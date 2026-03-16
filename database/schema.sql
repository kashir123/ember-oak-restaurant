-- ============================================================
-- Ember & Oak Restaurant System — Database Schema
-- Run this script on your PostgreSQL instance
-- ============================================================

-- Create databases
CREATE DATABASE restaurant_users;
CREATE DATABASE restaurant_menu;
CREATE DATABASE restaurant_orders;
CREATE DATABASE restaurant_reservations;

-- ── USERS DB ────────────────────────────────────────────────
\c restaurant_users;

CREATE TABLE IF NOT EXISTS users (
    id           BIGSERIAL PRIMARY KEY,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    phone        VARCHAR(20),
    role         VARCHAR(20)  NOT NULL DEFAULT 'CUSTOMER',
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);

-- ── MENU DB ─────────────────────────────────────────────────
\c restaurant_menu;

CREATE TABLE IF NOT EXISTS menu_items (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(200)   NOT NULL,
    description TEXT,
    price       NUMERIC(10,2)  NOT NULL,
    category    VARCHAR(100),
    image_url   VARCHAR(500),
    available   BOOLEAN        NOT NULL DEFAULT TRUE,
    featured    BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_menu_category  ON menu_items(category);
CREATE INDEX idx_menu_available ON menu_items(available);

-- Seed menu items
INSERT INTO menu_items (name, description, price, category, available, featured) VALUES
('Burrata & Heirloom Tomatoes','Whipped burrata, heirloom tomatoes, black olive tapenade, sourdough crisps.',22.00,'Starters',true,false),
('Charred Octopus','Grilled over applewood, smoked paprika aioli, pickled fennel, micro herbs.',28.00,'Starters',true,false),
('Bone Marrow Crostini','Roasted marrow, gremolata, sourdough toast, house-cured cornichons.',19.00,'Starters',true,false),
('Wood-Fire Ribeye','Prime-grade ribeye over applewood coals, chimichurri, roasted bone marrow.',58.00,'Mains',true,true),
('Smoked Duck Breast','Cherry-smoked Moulard duck, spiced plum reduction, wilted endive.',46.00,'Mains',true,true),
('Pan Roasted Halibut','Alaskan halibut, saffron beurre blanc, asparagus, caviar butter.',44.00,'Mains',true,false),
('Wild Mushroom Risotto','Arborio rice, seasonal foraged mushrooms, black truffle, aged Parmesan.',34.00,'Mains',true,false),
('Grilled Caesar Salad','Romaine hearts charred on the grill, house Caesar dressing, Grana Padano.',18.00,'Salads',true,false),
('Chocolate Lava Cake','Warm Valrhona chocolate, salted caramel, Madagascar vanilla ice cream.',16.00,'Desserts',true,false),
('Creme Brulee','Classic Tahitian vanilla custard, caramelised demerara sugar, fresh berries.',14.00,'Desserts',true,false),
('Aged Negroni','Barrel-aged Campari, sweet vermouth, Tanqueray gin, orange zest.',18.00,'Cocktails',true,false),
('Smoked Old Fashioned','Woodford Reserve, apple-smoked demerara, aromatic bitters, branded cherry.',20.00,'Cocktails',true,false);

-- ── ORDERS DB ───────────────────────────────────────────────
\c restaurant_orders;

CREATE TABLE IF NOT EXISTS orders (
    id               BIGSERIAL PRIMARY KEY,
    customer_name    VARCHAR(200),
    customer_email   VARCHAR(255),
    customer_phone   VARCHAR(20),
    order_type       VARCHAR(20)   NOT NULL DEFAULT 'DINE_IN',
    status           VARCHAR(30)   NOT NULL DEFAULT 'PENDING',
    delivery_address TEXT,
    table_number     INTEGER,
    notes            TEXT,
    total_amount     NUMERIC(10,2),
    created_at       TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS order_items (
    id             BIGSERIAL PRIMARY KEY,
    order_id       BIGINT        NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    menu_item_id   BIGINT,
    menu_item_name VARCHAR(200),
    quantity       INTEGER       NOT NULL DEFAULT 1,
    unit_price     NUMERIC(10,2) NOT NULL
);

CREATE INDEX idx_orders_email  ON orders(customer_email);
CREATE INDEX idx_orders_status ON orders(status);

-- ── RESERVATIONS DB ─────────────────────────────────────────
\c restaurant_reservations;

CREATE TABLE IF NOT EXISTS reservations (
    id                BIGSERIAL PRIMARY KEY,
    customer_name     VARCHAR(200),
    customer_email    VARCHAR(255),
    customer_phone    VARCHAR(20),
    reservation_date  DATE          NOT NULL,
    reservation_time  VARCHAR(20)   NOT NULL,
    party_size        INTEGER       NOT NULL,
    special_requests  TEXT,
    status            VARCHAR(30)   NOT NULL DEFAULT 'CONFIRMED',
    created_at        TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_res_email  ON reservations(customer_email);
CREATE INDEX idx_res_date   ON reservations(reservation_date);
CREATE INDEX idx_res_status ON reservations(status);
