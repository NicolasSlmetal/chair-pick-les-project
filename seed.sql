INSERT INTO tb_category VALUES
(1, 'Escritório'),
(2, 'Sala de Estar'),
(3, 'Sala de Jantar'),
(4, 'Quarto'),
(5, 'Cozinha'),
(6, 'Área Externa'),
(7, 'Infantil'),
(8, 'Home Office'),
(9, 'Comercial'),
(10, 'Industrial'),
(11, 'Luxo'),
(12, 'Minimalista'),
(13, 'Moderno'),
(14, 'Clássico'),
(15, 'Rústico'),
(16, 'Contemporâneo'),
(17, 'Vintage'),
(18, 'Escandinavo'),
(19, 'Boho Chic'),
(20, 'Art Déco'),
(21, 'Gamer');

INSERT INTO tb_pricing_group (pgr_name, pgr_percent_value) VALUES
('Básico', 1.10),
('Standard', 1.20),
('Premium', 1.50);


-- 1) Usuários
INSERT INTO tb_user (usr_email, usr_password, usr_type) VALUES
  ('cliente@exemplo.com', '$2a$12$tIcCw9rQ12KuVrzXd5KjKeNz5gViWTUA8.Y9VLz6KLnSPlSyQVjbC', 'CUSTOMER'),       -- senha: Abcd1234!
  ('admin@exemplo.com',   '$2a$12$LjTodtik34y.tREy0LaM6O3RsfG1XmzZ2oBjZ5Q8WVaLqUH1bbXiW', 'ADMIN'),        -- senha: Admin1234!
  ('vendas@exemplo.com',  '$2a$12$P2cUKY.7wgj6zB.UTAznWuotlfz8QYgyxnhZ.r2cO3brXb77GyX1i', 'SALES_MANAGER'); -- senha: Sales1234!

-- 2) Cliente
INSERT INTO tb_customer (cus_name, cus_cpf, cus_genre, cus_active, cus_phone_type, cus_phone_ddd, cus_phone, cus_born_date, cus_user_id)
VALUES (
  'João da Silva',
  '12345678901',
  'M',
  1,
  'CELL_PHONE',
  '11',
  '987654321',
  '1985-05-10',
  (SELECT usr_id FROM tb_user WHERE usr_email = 'cliente@exemplo.com')
);

-- 3) Endereço
INSERT INTO tb_address (
  add_cep, add_name, add_street, add_number, add_country, add_street_type,
  add_neighborhood, add_default, add_city, add_state, add_customer_id
) VALUES (
  '01234-567',
  'Endereço Principal',
  'Rua das Flores',
  100,
  'Brasil',
  'STREET',
  'Centro',
  1,
  'São Paulo',
  'SP',
  (SELECT cus_id FROM tb_customer WHERE cus_name = 'João da Silva')
);

-- 4) Cartão de crédito
INSERT INTO tb_credit_card (
  cre_number, cre_holder, cre_cvv, cre_default, cre_customer_id, cre_credit_brand
) VALUES (
  '4111111111111111',
  'JOÃO DA SILVA',
  '123',
  1,
  (SELECT cus_id FROM tb_customer WHERE cus_name = 'João da Silva'),
  1  -- VISA
);

-- 5) Cupons
INSERT INTO tb_coupon (cpn_type, cpn_value, cpn_customer_id) VALUES
  ('PROMOTIONAL', 10.00, (SELECT cus_id FROM tb_customer WHERE cus_name = 'João da Silva')),
  ('PROMOTIONAL', 20.00, (SELECT cus_id FROM tb_customer WHERE cus_name = 'João da Silva'));

-- 6) Cadeiras
INSERT INTO tb_chair (
  chr_name, chr_sell_price, chr_description, chr_height, chr_width,
  chr_length, chr_weight, chr_pricing_group_id, chr_average_rating
) VALUES
  ('Cadeira Office Basic',        199.90, 'Cadeira simples para escritório.',        100.0, 50.0, 50.0, 7.5, 1, 5.0),
  ('Cadeira Gamer XP',            499.90, 'Cadeira gamer com apoio de braço.',      120.0, 60.0, 60.0, 15.0, 3, 4.5),
  ('Cadeira Diretor Lux',         899.90, 'Cadeira de couro para diretoria.',       130.0, 65.0, 65.0, 18.0, 3, 3.3),
  ('Cadeira Infantil Color',      149.90, 'Cadeira colorida para crianças.',        80.0, 40.0, 40.0, 5.0, 1, 4.2),
  ('Cadeira de Jantar Clássica',  299.90, 'Cadeira elegante para sala de jantar.',  95.0, 45.0, 45.0, 6.0, 2, 4.3),
  ('Banco Industrial',            399.90, 'Banco de estrutura metálica.',           75.0, 40.0, 40.0, 10.0, 2, 4.2),
  ('Poltrona de Leitura',         599.90, 'Poltrona confortável para leitura.',     110.0, 70.0, 70.0, 20.0, 3, 2.2),
  ('Cadeira Espera',              249.90, 'Cadeiras para sala de espera.',          90.0, 50.0, 50.0, 8.0, 2, 4.0),
  ('Cadeira Dobrável',            129.90, 'Cadeira prática e dobrável.',            85.0, 45.0, 45.0, 6.5, 1, 4.1),
  ('Cadeira de Balanço',          549.90, 'Cadeira de balanço para área externa.',  115.0, 60.0, 60.0, 12.0, 3, 3.8),
  ('Cadeira Executiva Mesh',      799.90, 'Cadeira executiva com encosto mesh.',    125.0, 60.0, 65.0, 16.0, 3, 4.0),
  ('Cadeira de Praia',            199.90, 'Cadeira leve para praia.',               90.0, 55.0, 55.0, 4.0, 1, 4.5),
  ('Cadeira Minimalista',         349.90, 'Design minimalista em madeira.',         100.0, 50.0, 50.0, 9.0, 2, 1.4),
  ('Cadeira Boho Chic',           459.90, 'Estilo boho com detalhes em fibra.',     105.0, 55.0, 55.0, 10.0, 2, 3.5),
  ('Cadeira Vintage Metal',       379.90, 'Design vintage com estrutura metálica.', 95.0, 50.0, 50.0, 11.0, 2, 2.8),
  ('Cadeira Rústica Fazenda',     429.90, 'Madeira maciça estilo rústico.',         100.0, 55.0, 55.0, 14.0, 2, 5.0),
  ('Cadeira Contemporânea',       519.90, 'Linha contemporânea para sala.',         98.0, 52.0, 52.0, 9.5, 3, 4.7),
  ('Cadeira Art Déco',            639.90, 'Detalhes art déco sofisticados.',        105.0, 58.0, 58.0, 12.5, 3, 4.3),
  ('Banco Alto Bar',              299.90, 'Banco alto para balcão.',                110.0, 40.0, 40.0, 8.5, 2, 4.3),
  ('Poltrona Escandinava',        689.90, 'Estilo escandinavo em madeira clara.',   102.0, 60.0, 60.0, 13.0, 3, 4.8);

-- 7) Fornecedor
INSERT INTO tb_supplier (sup_name) VALUES ('Fornecedor Padrão');

-- 8) Itens em estoque
INSERT INTO tb_item (itm_amount, itm_unit_cost, itm_chair_id, itm_supplier_id)
SELECT 20, chr_sell_price / 2, chr_id, 1 FROM tb_chair ORDER BY chr_id LIMIT 20;


INSERT INTO tb_chair_category (chc_chair_id, chc_category_id) VALUES
(1, 1),  (1, 8),  (1, 13), -- Cadeira Office Basic
(2, 21), (2, 8),  (2, 13), -- Cadeira Gamer XP
(3, 1),  (3, 11), (3, 9),  -- Cadeira Diretor Lux
(4, 7),  (4, 13),          -- Cadeira Infantil Color
(5, 3),  (5, 14), (5, 13), -- Cadeira de Jantar Clássica
(6, 10), (6, 15), (6, 9),  -- Banco Industrial
(7, 2),  (7, 16), (7, 11), -- Poltrona de Leitura
(8, 9),  (8, 1),           -- Cadeira Espera
(9, 6),  (9, 9),           -- Cadeira Dobrável
(10, 6), (10, 15), (10, 14), -- Cadeira de Balanço
(11, 1), (11, 8), (11, 13), -- Cadeira Executiva Mesh
(12, 6), (12, 9),          -- Cadeira de Praia
(13, 12), (13, 13),        -- Cadeira Minimalista
(14, 19), (14, 2),         -- Cadeira Boho Chic
(15, 17), (15, 10),        -- Cadeira Vintage Metal
(16, 15), (16, 4),         -- Cadeira Rústica Fazenda
(17, 16), (17, 2),         -- Cadeira Contemporânea
(18, 20), (18, 11),        -- Cadeira Art Déco
(19, 3),  (19, 10),        -- Banco Alto Bar
(20, 18), (20, 12), (20, 2); -- Poltrona Escandinava


INSERT INTO tb_order (ord_status, ord_created_date, ord_updated_date, ord_total_amount, ord_total_value, ord_customer_id, ord_billing_address_id, ord_delivery_address_id) VALUES
('APPROVED', '2025-01-10', '2025-01-10 10:00:00', 3, 1344.67, 1, 1, 1), -- Order 1
('APPROVED', '2025-01-20', '2025-01-20 15:00:00', 2, 909.30, 1, 1, 1), -- Order 2
('APPROVED', '2025-02-05', '2025-02-05 11:30:00', 1, 415.25, 1, 1, 1), -- Order 3
('APPROVED', '2025-02-28', '2025-02-28 09:45:00', 4, 1715.15, 1, 1, 1), -- Order 4
('APPROVED', '2025-03-10', '2025-03-10 14:20:00', 2, 878.20, 1, 1, 1), -- Order 5
('APPROVED', '2025-03-25', '2025-03-25 16:10:00', 3, 1436.95, 1, 1, 1), -- Order 6
('APPROVED', '2025-04-08', '2025-04-08 12:00:00', 5, 2418.00, 1, 1, 1), -- Order 7
('APPROVED', '2025-04-22', '2025-04-22 17:30:00', 2, 957.90, 1, 1, 1), -- Order 8
('APPROVED', '2025-05-05', '2025-05-05 10:50:00', 3, 1412.15, 1, 1, 1), -- Order 9
('APPROVED', '2025-05-25', '2025-05-25 14:00:00', 1, 420.00, 1, 1, 1), -- Order 10
('APPROVED', '2025-06-10', '2025-06-10 09:00:00', 4, 1850.45, 1, 1, 1), -- Order 11
('APPROVED', '2025-06-20', '2025-06-20 13:15:00', 2, 870.00, 1, 1, 1), -- Order 12
('APPROVED', '2025-06-30', '2025-06-30 16:40:00', 3, 1395.90, 1, 1, 1), -- Order 13
('APPROVED', '2025-07-01', '2025-07-01 11:20:00', 5, 2475.25, 1, 1, 1), -- Order 14
('APPROVED', '2025-07-02', '2025-07-02 12:00:00', 2, 928.15, 1, 1, 1); -- Order 15

-- Ordem 1: 3 itens, produtos 1,2,4
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 1, 415.25, 15.00, 1, 1),
('APPROVED', 1, 454.05, 15.00, 2, 1),
('APPROVED', 1, 445.37, 15.00, 4, 1);

-- Ordem 2: 2 itens, produto 1 e 3
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 2, 402.50, 15.00, 1, 2),
('APPROVED', 1, 439.80, 15.00, 3, 2);

-- Ordem 3: 1 item, produto 2
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 1, 400.25, 15.00, 2, 3);

-- Ordem 4: 4 itens, produtos 1,2,3,4
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 2, 410.00, 15.00, 1, 4),
('APPROVED', 1, 420.50, 15.00, 2, 4),
('APPROVED', 1, 385.40, 15.00, 3, 4),
('APPROVED', 1, 384.25, 15.00, 4, 4);

-- Ordem 5: 2 itens, produtos 3 e 4
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 1, 395.10, 15.00, 3, 5),
('APPROVED', 1, 468.10, 15.00, 4, 5);

-- Ordem 6: 3 itens, produtos 1 e 2
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 2, 405.00, 15.00, 1, 6),
('APPROVED', 1, 451.95, 15.00, 2, 6);

-- Ordem 7: 5 itens, produtos 1 e 3
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 3, 400.50, 15.00, 1, 7),
('APPROVED', 2, 432.00, 15.00, 3, 7);

-- Ordem 8: 2 itens, produto 2
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 2, 452.45, 15.00, 2, 8);

-- Ordem 9: 3 itens, produtos 1 e 4
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 1, 402.10, 15.00, 1, 9),
('APPROVED', 2, 472.65, 15.00, 4, 9);

-- Ordem 10: 1 item, produto 1
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 1, 405.00, 15.00, 1, 10);

-- Ordem 11: 4 itens, produtos 3 e 4
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 3, 395.00, 15.00, 3, 11),
('APPROVED', 1, 436.45, 15.00, 4, 11);

-- Ordem 12: 2 itens, produto 2
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 2, 420.00, 15.00, 2, 12);

-- Ordem 13: 3 itens, produtos 1 e 4
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 1, 412.00, 15.00, 1, 13),
('APPROVED', 2, 474.95, 15.00, 4, 13);

-- Ordem 14: 5 itens, produtos 1 e 2
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 3, 404.25, 15.00, 1, 14),
('APPROVED', 2, 433.50, 15.00, 2, 14);

-- Ordem 15: 2 itens, produto 3 e 4
INSERT INTO tb_order_item (ori_status, ori_amount, ori_sell_price, ori_freight_tax, ori_item_id, ori_order_id) VALUES
('APPROVED', 1, 397.65, 15.00, 3, 15),
('APPROVED', 1, 475.50, 15.00, 4, 15);

-- PAGAMENTOS (somente cartão de crédito, mesmo para todas as ordens)
INSERT INTO tb_order_credit_card (occ_paid_value, occ_order_id, occ_credit_card_id) VALUES
(1344.67, 1, 1),
(909.30, 2, 1),
(415.25, 3, 1),
(1715.15, 4, 1),
(878.20, 5, 1),
(1436.95, 6, 1),
(2418.00, 7, 1),
(957.90, 8, 1),
(1412.15, 9, 1),
(420.00, 10, 1),
(1850.45, 11, 1),
(870.00, 12, 1),
(1395.90, 13, 1),
(2475.25, 14, 1),
(928.15, 15, 1);


