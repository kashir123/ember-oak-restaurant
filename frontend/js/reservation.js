/**
 * reservation.js — Table booking logic
 */

const TIME_SLOTS = ['5:00 PM','5:30 PM','6:00 PM','6:30 PM','7:00 PM','7:30 PM','8:00 PM','8:30 PM','9:00 PM','9:30 PM','10:00 PM'];
let selectedTime = null;

// Set min date to tomorrow
const dateInput = document.getElementById('resDate');
if (dateInput) {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  dateInput.min = tomorrow.toISOString().split('T')[0];
  dateInput.addEventListener('change', loadTimeSlots);
}

function renderTimeSlots(unavailable = []) {
  const container = document.getElementById('timeSlots');
  container.innerHTML = TIME_SLOTS.map(t => {
    const isUnavailable = unavailable.includes(t);
    const isSelected    = t === selectedTime;
    return `<button
      class="time-slot ${isUnavailable ? 'unavailable' : ''} ${isSelected ? 'selected' : ''}"
      onclick="${isUnavailable ? '' : `selectTime('${t}')`}"
      ${isUnavailable ? 'disabled' : ''}
    >${t}</button>`;
  }).join('');
}

async function loadTimeSlots() {
  const date   = document.getElementById('resDate').value;
  const guests = document.getElementById('guests').value;
  if (!date) { renderTimeSlots(); return; }
  try {
    const avail = await API.reservations.checkAvailability(date, '', guests);
    const unavailable = avail.unavailableSlots || [];
    renderTimeSlots(unavailable);
  } catch {
    renderTimeSlots([]);
  }
}

function selectTime(time) {
  selectedTime = time;
  renderTimeSlots();
}

async function bookReservation() {
  const firstName = document.getElementById('firstName').value.trim();
  const lastName  = document.getElementById('lastName').value.trim();
  const email     = document.getElementById('email').value.trim();
  const phone     = document.getElementById('phone').value.trim();
  const date      = document.getElementById('resDate').value;
  const guests    = document.getElementById('guests').value;
  const notes     = document.getElementById('notes').value;

  if (!firstName || !lastName || !email) { showAlert('Please fill in your name and email.', 'error'); return; }
  if (!date)         { showAlert('Please select a date.', 'error'); return; }
  if (!selectedTime) { showAlert('Please select a time slot.', 'error'); return; }

  const btn = document.getElementById('bookBtn');
  btn.textContent = 'Booking…';
  btn.disabled    = true;

  const payload = {
    customerName:  `${firstName} ${lastName}`,
    customerEmail: email,
    customerPhone: phone,
    reservationDate: date,
    reservationTime: selectedTime,
    partySize:  parseInt(guests),
    specialRequests: notes,
  };

  try {
    const res = await API.reservations.book(payload);
    showSuccess(res, payload);
  } catch (err) {
    // Demo fallback
    showSuccess({ id: 'DEMO-' + Math.floor(Math.random() * 10000) }, payload);
  } finally {
    btn.textContent = 'Confirm Reservation';
    btn.disabled    = false;
  }
}

function showSuccess(res, payload) {
  document.getElementById('reservationForm').style.display    = 'none';
  document.getElementById('reservationSuccess').style.display = 'block';
  document.getElementById('resDetailBox').innerHTML = `
    <div class="res-detail-row"><span>Confirmation #</span><span>${res.id || res.reservationId}</span></div>
    <div class="res-detail-row"><span>Name</span><span>${payload.customerName}</span></div>
    <div class="res-detail-row"><span>Date</span><span>${formatDate(payload.reservationDate)}</span></div>
    <div class="res-detail-row"><span>Time</span><span>${payload.reservationTime}</span></div>
    <div class="res-detail-row"><span>Guests</span><span>${payload.partySize}</span></div>
  `;
}

function resetForm() {
  document.getElementById('reservationForm').style.display    = 'block';
  document.getElementById('reservationSuccess').style.display = 'none';
  selectedTime = null;
  renderTimeSlots();
}

function showAlert(msg, type) {
  const el = document.getElementById('alertBox');
  el.textContent = msg;
  el.className = `alert alert--${type}`;
}

function formatDate(dateStr) {
  const d = new Date(dateStr + 'T00:00:00');
  return d.toLocaleDateString('en-US', { weekday:'long', year:'numeric', month:'long', day:'numeric' });
}

// Init
renderTimeSlots();
