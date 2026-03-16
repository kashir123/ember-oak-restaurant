/**
 * main.js — Home page interactions & shared utilities
 */

// ── Navbar scroll behavior ────────────────────────────────────
const navbar = document.getElementById('navbar');
if (navbar) {
  window.addEventListener('scroll', () => {
    navbar.classList.toggle('scrolled', window.scrollY > 60);
  });
}

// ── Hamburger menu ────────────────────────────────────────────
const hamburger = document.getElementById('hamburger');
if (hamburger) {
  hamburger.addEventListener('click', () => {
    const links = document.querySelector('.nav__links');
    links && links.classList.toggle('nav__links--open');
  });
}

// ── Load featured dishes on home page ────────────────────────
const featuredGrid = document.getElementById('featuredDishes');
if (featuredGrid) {
  loadFeaturedDishes();
}

async function loadFeaturedDishes() {
  try {
    const items = await API.menu.getAll();
    const featured = items.slice(0, 3);
    featuredGrid.innerHTML = featured.map(renderDishCard).join('');
  } catch (err) {
    // Fallback sample data when backend unavailable
    const fallback = [
      { id: 1, name: 'Wood-Fire Ribeye', category: 'Mains', description: 'Prime-grade ribeye over applewood coals, chimichurri, roasted bone marrow.', price: 58, imageUrl: 'https://images.unsplash.com/photo-1558030006-450675393462?w=600&q=80' },
      { id: 2, name: 'Burrata & Heirloom', category: 'Starters', description: 'Whipped burrata, heirloom tomatoes, black olive tapenade, sourdough crisps.', price: 22, imageUrl: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600&q=80' },
      { id: 3, name: 'Smoked Duck Breast', category: 'Mains', description: 'Cherry-smoked Moulard duck, spiced plum reduction, wilted endive.', price: 46, imageUrl: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&q=80' },
    ];
    featuredGrid.innerHTML = fallback.map(renderDishCard).join('');
  }
}

function renderDishCard(item) {
  const imgStyle = item.imageUrl
    ? `style="background-image:url('${item.imageUrl}')"`
    : '';
  return `
    <div class="dish-card" onclick="window.location='pages/menu.html'">
      <div class="dish-card__img" ${imgStyle}></div>
      <div class="dish-card__body">
        <p class="dish-card__category">${item.category || 'Specialty'}</p>
        <h3 class="dish-card__name">${item.name}</h3>
        <p class="dish-card__desc">${item.description || ''}</p>
        <div class="dish-card__footer">
          <span class="dish-card__price">$${Number(item.price).toFixed(2)}</span>
          <button class="dish-card__add" onclick="event.stopPropagation();addToCart(${item.id})">+ Add</button>
        </div>
      </div>
    </div>`;
}

// ── Cart (localStorage) ───────────────────────────────────────
function addToCart(itemId) {
  const cart = getCart();
  const existing = cart.find(c => c.id === itemId);
  if (existing) { existing.qty += 1; }
  else           { cart.push({ id: itemId, qty: 1 }); }
  localStorage.setItem('cart', JSON.stringify(cart));
  showToast('Item added to your order!');
  updateCartBadge();
}

function getCart() {
  try { return JSON.parse(localStorage.getItem('cart')) || []; }
  catch { return []; }
}

function updateCartBadge() {
  const badge = document.getElementById('cartBadge');
  if (!badge) return;
  const total = getCart().reduce((a, c) => a + c.qty, 0);
  badge.textContent = total;
  badge.style.display = total ? 'inline-flex' : 'none';
}

// ── Toast notification ────────────────────────────────────────
function showToast(msg, type = 'success') {
  let toast = document.getElementById('toast');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'toast';
    toast.style.cssText = `
      position:fixed;bottom:2rem;right:2rem;z-index:9999;
      background:var(--clr-accent);color:var(--clr-bg);
      padding:1rem 1.5rem;border-radius:6px;font-size:.88rem;
      opacity:0;transition:.3s;pointer-events:none;font-family:var(--ff-sans);
    `;
    document.body.appendChild(toast);
  }
  if (type === 'error') toast.style.background = '#dc5050';
  else toast.style.background = 'var(--clr-accent)';
  toast.textContent = msg;
  toast.style.opacity = '1';
  setTimeout(() => { toast.style.opacity = '0'; }, 3000);
}

// ── Scroll-reveal animation ───────────────────────────────────
const observer = new IntersectionObserver((entries) => {
  entries.forEach(e => {
    if (e.isIntersecting) {
      e.target.style.opacity = '1';
      e.target.style.transform = 'translateY(0)';
    }
  });
}, { threshold: 0.1 });

document.querySelectorAll('.section__title, .dish-card, .experience__item').forEach(el => {
  el.style.cssText += 'opacity:0;transform:translateY(20px);transition:opacity .6s,transform .6s;';
  observer.observe(el);
});

// Initialise
updateCartBadge();
