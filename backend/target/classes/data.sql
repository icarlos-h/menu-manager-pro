-- ============================
-- RESET (opcional)
-- ============================
DELETE FROM unit_products;
DELETE FROM unit_categories;
DELETE FROM products;
DELETE FROM categories;
DELETE FROM users;
DELETE FROM units;

-- ============================
-- UNITS
-- ============================
INSERT INTO units (name, phone, instagram, google_maps_url, ifood_url, keeta_url, food99_url)
VALUES
('Unidade Centro', '+55 11 99999-9999', '@unidadecentro',
 'https://maps.google.com/?q=unidade+centro', 'https://ifood.com.br/unidade-centro', NULL, NULL),

('Unidade Zona Sul', '+55 11 98888-8888', '@unidadezonasul',
 NULL, NULL, 'https://keeta.com/unidade-zonasul', 'https://99food.com/unidade-zonasul');


-- ============================
-- CATEGORIES (globais)
-- ============================
INSERT INTO categories (name, sort_order, active)
VALUES
('Burgers', 1, TRUE),
('Bebidas', 2, TRUE),
('Sobremesas', 3, TRUE);

-- ============================
-- PRODUCTS (globais)
-- (assumindo que categories ficaram 1,2,3 nessa ordem)
-- ============================
INSERT INTO products (category_id, name, description, base_price, photo_url, weight_grams)
VALUES
(1, 'Cheeseburger', 'Pão, carne, queijo', 24.90, NULL, 250),
(1, 'Bacon Burger', 'Bacon crocante + cheddar', 29.90, NULL, 280),
(2, 'Refrigerante Lata', '350ml', 6.50, NULL, 350),
(3, 'Brownie', 'Brownie com calda', 12.00, NULL, 120),
(3, 'Sorvete', 'Copo 200ml', 10.00, NULL, 200);

-- ============================
-- UNIT_CATEGORIES (por unidade)
-- (assumindo units ficaram 1 e 2)
-- ============================
-- Unidade 1 ativa tudo
INSERT INTO unit_categories (unit_id, category_id, active)
VALUES
(1, 1, TRUE),
(1, 2, TRUE),
(1, 3, TRUE);

-- Unidade 2 não trabalha com sobremesas
INSERT INTO unit_categories (unit_id, category_id, active)
VALUES
(2, 1, TRUE),
(2, 2, TRUE),
(2, 3, FALSE);

-- ============================
-- UNIT_PRODUCTS (por unidade)
-- (assumindo products ficaram 1..5 na ordem acima)
-- ============================
-- Unidade 1: 5 produtos (um com override)
INSERT INTO unit_products (unit_id, product_id, active, price_override)
VALUES
(1, 1, TRUE, NULL),
(1, 2, TRUE, 31.90),
(1, 3, TRUE, NULL),
(1, 4, TRUE, NULL),
(1, 5, FALSE, NULL);

-- Unidade 2: só burgers e bebida (sem sobremesas)
INSERT INTO unit_products (unit_id, product_id, active, price_override)
VALUES
(2, 1, TRUE, 26.90),
(2, 2, TRUE, NULL),
(2, 3, TRUE, NULL),
(2, 4, FALSE, NULL),
(2, 5, FALSE, NULL);

-- ============================
-- USERS
-- ============================
INSERT INTO users (email, password, role, unit_id, active)
VALUES
('admin@menumgr.com', '$2b$10$tBQVZpDut33Wegp.YTky7un3tmixtdfUaFVvezOpBAe4u/ppZxbnO', 'ADMIN', NULL, TRUE),
('unit1@menumgr.com', '$2b$10$6h8/V50vZKhx5kaPBMCPxuWF1AKLAz0bVC80wuMJQegM/wgmVS3Ma', 'UNIT', 1, TRUE);