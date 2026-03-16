/**
 * menu.js — Menu page logic: load items, filter by category, add to cart
 */

const FALLBACK_ITEMS = [
  { id:1,  name:'Burrata & Heirloom Tomatoes', category:'Starters',  price:22, description:'Whipped burrata, heirloom tomatoes, black olive tapenade, sourdough crisps.', imageUrl:'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600&q=80' },
  { id:2,  name:'Charred Octopus',             category:'Starters',  price:28, description:'Grilled over applewood, smoked paprika aioli, pickled fennel, micro herbs.', imageUrl:'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=600&q=80' },
  { id:3,  name:'Bone Marrow Crostini',         category:'Starters',  price:19, description:'Roasted marrow, gremolata, sourdough toast, house-cured cornichons.', imageUrl:'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=600&q=80' },
  { id:4,  name:'Wood-Fire Ribeye',             category:'Mains',     price:58, description:'Prime-grade ribeye over applewood coals, chimichurri, roasted bone marrow.', imageUrl:'https://images.unsplash.com/photo-1558030006-450675393462?w=600&q=80' },
  { id:5,  name:'Smoked Duck Breast',           category:'Mains',     price:46, description:'Cherry-smoked Moulard duck, spiced plum reduction, wilted endive.', imageUrl:'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&q=80' },
  { id:6,  name:'Pan Roasted Halibut',          category:'Mains',     price:44, description:'Alaskan halibut, saffron beurre blanc, asparagus, caviar butter.', imageUrl:'https://images.unsplash.com/photo-1519984388953-d2406bc725e1?w=600&q=80' },
  { id:7,  name:'Wild Mushroom Risotto',        category:'Mains',     price:34, description:'Arborio rice, seasonal foraged mushrooms, black truffle, aged Parmesan.', imageUrl:'https://images.unsplash.com/photo-1476124369491-e7addf5db371?w=600&q=80' },
  { id:8,  name:'Chocolate Lava Cake',          category:'Desserts',  price:16, description:'Warm Valrhona chocolate, salted caramel, Madagascar vanilla ice cream.', imageUrl:'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=600&q=80' },
  { id:9,  name:'Crème Brûlée',                category:'Desserts',  price:14, description:'Classic Tahitian vanilla custard, caramelised demerara sugar, fresh berries.', imageUrl:'https://images.unsplash.com/photo-1488477181946-6428a0291777?w=600&q=80' },
  { id:10, name:'Aged Negroni',                 category:'Cocktails', price:18, description:'Barrel-aged Campari, sweet vermouth, Tanqueray gin, orange zest.', imageUrl:'https://images.unsplash.com/photo-1470337458703-46ad1756a187?w=600&q=80' },
  { id:11, name:'Smoked Old Fashioned',         category:'Cocktails', price:20, description:'Woodford Reserve, apple-smoked demerara, aromatic bitters, branded cherry.', imageUrl:'https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?w=600&q=80' },
  { id:12, name:'Grilled Caesar',               category:'Salads',    price:18, description:'Romaine hearts charred on the grill, house Caesar dressing, shaved Grana Padano.', imageUrl:'https://images.unsplash.com/photo-1546793665-c74683f339c1?w=600&q=80' },
];

let allItems = [];
let activeCategory = 'all';

async function initMenu() {
  try {
    allItems = await API.menu.getAll();
  } catch {
    allItems = FALLBACK_ITEMS;
  }
  buildFilters();
  renderMenu('all');
  updateCartUI();
}

function buildFilters() {
  const categories = [...new Set(allItems.map(i => i.category))];
  const filtersEl = document.getElementById('filters');
  categories.forEach(cat => {
    const btn = document.createElement('button');
    btn.className = 'filter-btn';
    btn.dataset.cat = cat;
    btn.textContent = cat;
    btn.addEventListener('click', () => selectCategory(cat));
    filtersEl.appendChild(btn);
  });
  // Wire up "All" button
  filtersEl.querySelector('[data-cat="all"]').addEventListener('click', () => selectCategory('all'));
}

function selectCategory(cat) {
  activeCategory = cat;
  document.querySelectorAll('.filter-btn').forEach(b => b.classList.toggle('active', b.dataset.cat === cat));
  renderMenu(cat);
}

function renderMenu(cat) {
  const items = cat === 'all' ? allItems : allItems.filter(i => i.category === cat);
  const grid = document.getElementById('menuGrid');

  if (cat === 'all') {
    // Group by category
    const grouped = {};
    items.forEach(item => {
      (grouped[item.category] = grouped[item.category] || []).push(item);
    });
    grid.style.display = 'block';
    grid.innerHTML = Object.entries(grouped).map(([category, dishes]) => `
      <h2 class="menu-section-title">${category}</h2>
      <div class="menu-grid">${dishes.map(menuItemCard).join('')}</div>
    `).join('');
  } else {
    grid.style.display = 'grid';
    grid.innerHTML = items.map(menuItemCard).join('');
  }
}

function menuItemCard(item) {
  const inCart = getCartQty(item.id);
  const imgStyle = item.imageUrl ? `style="background-image:url('${item.imageUrl}')"` : '';
  return `
    <div class="dish-card">
      <div class="dish-card__img" ${imgStyle}></div>
      <div class="dish-card__body">
        <p class="dish-card__category">${item.category}</p>
        <h3 class="dish-card__name">${item.name}</h3>
        <p class="dish-card__desc">${item.description || ''}</p>
        <div class="dish-card__footer">
          <span class="dish-card__price">$${Number(item.price).toFixed(2)}</span>
          ${inCart > 0
            ? `<div style="display:flex;align-items:center;gap:.5rem">
                <button class="dish-card__add" onclick="changeQty(${item.id},-1)">−</button>
                <span style="font-size:.9rem;min-width:1rem;text-align:center">${inCart}</span>
                <button class="dish-card__add" onclick="changeQty(${item.id},1)">+</button>
               </div>`
            : `<button class="dish-card__add" onclick="addToCart(${item.id})">+ Add</button>`
          }
        </div>
      </div>
    </div>`;
}

function addToCart(itemId) {
  const item = allItems.find(i => i.id === itemId);
  if (!item) return;
  const cart = getCart();
  const ex = cart.find(c => c.id === itemId);
  if (ex) { ex.qty += 1; } else { cart.push({ id: itemId, qty: 1, name: item.name, price: item.price }); }
  localStorage.setItem('cart', JSON.stringify(cart));
  showToast(`${item.name} added!`);
  updateCartUI();
  renderMenu(activeCategory);
}

function changeQty(itemId, delta) {
  const cart = getCart();
  const idx = cart.findIndex(c => c.id === itemId);
  if (idx === -1) return;
  cart[idx].qty += delta;
  if (cart[idx].qty <= 0) cart.splice(idx, 1);
  localStorage.setItem('cart', JSON.stringify(cart));
  updateCartUI();
  renderMenu(activeCategory);
}

function getCartQty(itemId) {
  return (getCart().find(c => c.id === itemId) || {}).qty || 0;
}

function getCart() {
  try { return JSON.parse(localStorage.getItem('cart')) || []; } catch { return []; }
}

function updateCartUI() {
  const cart = getCart();
  const total = cart.reduce((a, c) => a + c.qty, 0);
  const floatBtn = document.getElementById('cartFloat');
  const countEl  = document.getElementById('cartCount');
  if (floatBtn) { floatBtn.classList.toggle('visible', total > 0); }
  if (countEl)  { countEl.textContent = total; }
  const badge = document.getElementById('cartBadge');
  if (badge) { badge.textContent = total; badge.style.display = total ? 'inline' : 'none'; }
}

// Boot
initMenu();
