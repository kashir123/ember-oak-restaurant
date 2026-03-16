/**
 * api.js — Centralized API communication layer
 * All calls go through the API Gateway (port 8080)
 */

const API = (() => {
  // API Gateway base URL — update for production
  const BASE = window.ENV_API_BASE || 'http://localhost:8080/api';

  /**
   * Generic fetch wrapper with error handling
   */
  async function request(path, options = {}) {
    const url = `${BASE}${path}`;
    const token = localStorage.getItem('authToken');

    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
        ...(options.headers || {}),
      },
      ...options,
    };

    try {
      const res = await fetch(url, config);
      if (!res.ok) {
        let msg = `HTTP ${res.status}`;
        try { const err = await res.json(); msg = err.message || msg; } catch (_) {}
        throw new Error(msg);
      }
      if (res.status === 204) return null;
      return await res.json();
    } catch (err) {
      console.error(`[API] ${options.method || 'GET'} ${path}`, err);
      throw err;
    }
  }

  // ── Auth / User Service ──────────────────────────────────────
  const auth = {
    register: (data) => request('/users/register', { method: 'POST', body: JSON.stringify(data) }),
    login:    (data) => request('/users/login',    { method: 'POST', body: JSON.stringify(data) }),
    profile:  ()     => request('/users/profile'),
    logout:   ()     => { localStorage.removeItem('authToken'); localStorage.removeItem('currentUser'); },
  };

  // ── Menu Service ─────────────────────────────────────────────
  const menu = {
    getAll:      (category) => request(`/menu/items${category ? '?category=' + category : ''}`),
    getById:     (id)       => request(`/menu/items/${id}`),
    getCategories: ()       => request('/menu/categories'),
    create:      (data)     => request('/menu/items', { method: 'POST', body: JSON.stringify(data) }),
    update:      (id, data) => request(`/menu/items/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete:      (id)       => request(`/menu/items/${id}`, { method: 'DELETE' }),
  };

  // ── Order Service ────────────────────────────────────────────
  const orders = {
    place:  (data) => request('/orders', { method: 'POST', body: JSON.stringify(data) }),
    getAll: ()     => request('/orders'),
    getById:(id)   => request(`/orders/${id}`),
    cancel: (id)   => request(`/orders/${id}/cancel`, { method: 'PUT' }),
    getMyOrders: () => request('/orders/my'),
  };

  // ── Reservation Service ──────────────────────────────────────
  const reservations = {
    book:      (data) => request('/reservations', { method: 'POST', body: JSON.stringify(data) }),
    getAll:    ()     => request('/reservations'),
    getById:   (id)   => request(`/reservations/${id}`),
    cancel:    (id)   => request(`/reservations/${id}/cancel`, { method: 'PUT' }),
    getMyReservations: () => request('/reservations/my'),
    checkAvailability: (date, time, guests) =>
      request(`/reservations/availability?date=${date}&time=${time}&guests=${guests}`),
  };

  // ── Helpers ───────────────────────────────────────────────────
  function isLoggedIn() { return !!localStorage.getItem('authToken'); }
  function getCurrentUser() {
    try { return JSON.parse(localStorage.getItem('currentUser')); }
    catch { return null; }
  }
  function saveAuth(token, user) {
    localStorage.setItem('authToken', token);
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  return { auth, menu, orders, reservations, isLoggedIn, getCurrentUser, saveAuth };
})();
