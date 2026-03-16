/**
 * order.js — Cart display and order placement
 */

const TAX_RATE = 0.0875;  // 8.75% NYC tax
let orderType = 'DINE_IN';

function setType(type) {
  orderType = type;
  ['DINE_IN','TAKEOUT','DELIVERY'].forEach(t => {
    const btn = document.getElementById(`btn${t.split('_').map((w,i) => i===0 ? w[0]+w.slice(1).toLowerCase() : w[0]+w.slice(1).toLowerCase()).join('')}`);
  });
  document.getElementById('btnDineIn').classList.toggle('active', type === 'DINE_IN');
  document.getElementById('btnTakeout').classList.toggle('active', type === 'TAKEOUT');
  document.getElementById('btnDelivery').classList.toggle('active', type === 'DELIVERY');
  document.getElementById('deliveryFields').style.display = type === 'DELIVERY' ? 'block' : 'none';
  document.getElementById('tableField').style.display = type === 'DINE_IN' ? 'block' : 'none';
}

function getCart() {
  try { return JSON.parse(localStorage.getItem('cart')) || []; } catch { return []; }
}

function saveCart(cart) { localStorage.setItem('cart', JSON.stringify(cart)); }

function renderCartSummary() {
  const cart = getCart();
  const el = document.getElementById('cartSummary');

  if (cart.length === 0) {
    el.innerHTML = `
      <div class="empty-cart">
        <p style="font-size:2.5rem">🛒</p>
        <p style="margin-top:1rem">Your cart is empty.</p>
        <p style="margin-top:.5rem"><a href="menu.html">Browse the menu</a> to get started.</p>
      </div>`;
    return;
  }

  const subtotal = cart.reduce((s, i) => s + (i.price * i.qty), 0);
  const tax      = subtotal * TAX_RATE;
  const total    = subtotal + tax;

  el.innerHTML = `
    <ul class="cart-items">
      ${cart.map(item => `
        <li class="cart-item">
          <div class="cart-item__info">
            <p class="cart-item__name">${item.name}</p>
            <p class="cart-item__price">$${Number(item.price).toFixed(2)} each</p>
          </div>
          <div class="cart-item__qty">
            <button class="qty-btn" onclick="changeCartQty(${item.id}, -1)">−</button>
            <span>${item.qty}</span>
            <button class="qty-btn" onclick="changeCartQty(${item.id}, +1)">+</button>
          </div>
          <span style="font-family:var(--ff-serif);min-width:3rem;text-align:right">
            $${(item.price * item.qty).toFixed(2)}
          </span>
        </li>`).join('')}
    </ul>
    <div class="cart-totals">
      <div class="cart-row"><span>Subtotal</span><span>$${subtotal.toFixed(2)}</span></div>
      <div class="cart-row"><span>Tax (8.75%)</span><span>$${tax.toFixed(2)}</span></div>
      <div class="cart-row total"><span>Total</span><span>$${total.toFixed(2)}</span></div>
    </div>`;
}

function changeCartQty(itemId, delta) {
  const cart = getCart();
  const idx = cart.findIndex(i => i.id === itemId);
  if (idx === -1) return;
  cart[idx].qty += delta;
  if (cart[idx].qty <= 0) cart.splice(idx, 1);
  saveCart(cart);
  renderCartSummary();
}

async function placeOrder() {
  const cart = getCart();
  if (cart.length === 0) {
    showAlert('Please add items to your cart first.', 'error');
    return;
  }

  const firstName = document.getElementById('firstName').value.trim();
  const lastName  = document.getElementById('lastName').value.trim();
  const email     = document.getElementById('email').value.trim();
  const phone     = document.getElementById('phone').value.trim();

  if (!firstName || !lastName || !email) {
    showAlert('Please fill in your name and email.', 'error');
    return;
  }

  const btn = document.getElementById('placeOrderBtn');
  btn.textContent = 'Placing Order…';
  btn.disabled = true;

  const payload = {
    customerName:  `${firstName} ${lastName}`,
    customerEmail: email,
    customerPhone: phone,
    orderType,
    tableNumber:   document.getElementById('tableNumber')?.value || null,
    deliveryAddress: document.getElementById('address')?.value || null,
    notes:         document.getElementById('notes').value,
    items: cart.map(i => ({ menuItemId: i.id, quantity: i.qty, unitPrice: i.price })),
  };

  try {
    const order = await API.orders.place(payload);
    // Success
    localStorage.removeItem('cart');
    document.getElementById('orderForm').style.display    = 'none';
    document.getElementById('orderSuccess').style.display = 'block';
    document.getElementById('orderId').textContent        = `#${order.id || order.orderId || 'N/A'}`;
  } catch (err) {
    showAlert(`Could not place order: ${err.message}. (Demo mode: showing confirmation anyway)`, 'error');
    // Demo fallback
    setTimeout(() => {
      localStorage.removeItem('cart');
      document.getElementById('orderForm').style.display    = 'none';
      document.getElementById('orderSuccess').style.display = 'block';
      document.getElementById('orderId').textContent        = '#DEMO-' + Math.floor(Math.random() * 10000);
    }, 1500);
  } finally {
    btn.textContent = 'Place Order';
    btn.disabled    = false;
  }
}

function showAlert(msg, type) {
  const el = document.getElementById('alertBox');
  el.textContent = msg;
  el.className = `alert alert--${type}`;
}

// Init
renderCartSummary();
