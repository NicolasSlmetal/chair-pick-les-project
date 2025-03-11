CREATE TABLE tb_address (
    add_id           INTEGER NOT NULL,
    add_cep          CHAR(9) NOT NULL,
    add_name         VARCHAR(255) NOT NULL,
    add_street       VARCHAR(255) NOT NULL,
    add_number       INTEGER NOT NULL,
    add_country      VARCHAR(255) NOT NULL,
    add_street_type  VARCHAR(50) NOT NULL,
    add_neighborhood VARCHAR(255) NOT NULL,
    add_observation  VARCHAR(255) NOT NULL,
    add_default      INTEGER NOT NULL,
    add_city         VARCHAR(255) NOT NULL,
    add_state        VARCHAR(255) NOT NULL,
    add_customer_id  INTEGER NOT NULL
);

CREATE TABLE tb_cart (
    car_id                  INTEGER NOT NULL,
    car_item_entry_datetime TIMESTAMP NOT NULL,
    car_item_amount         INTEGER NOT NULL,
    car_item_price          NUMERIC(4,2) NOT NULL,
    car_item_status         VARCHAR(30) NOT NULL,
    car_customer_id         INTEGER NOT NULL,
    car_item_id             INTEGER NOT NULL
);

CREATE TABLE tb_category (
    cat_id   INTEGER NOT NULL,
    cat_name VARCHAR(100) NOT NULL
);

CREATE TABLE tb_chair (
    chr_id               INTEGER NOT NULL,
    chr_name             VARCHAR(255) NOT NULL,
    chr_sell_price       NUMERIC(4,2) NOT NULL,
    chr_description      VARCHAR(255) NOT NULL,
    chr_height           NUMERIC(3,2) NOT NULL,
    chr_width            NUMERIC(3,2) NOT NULL,
    chr_length           NUMERIC(3,2) NOT NULL,
    chr_weight           INTEGER NOT NULL,
    chr_average_rating   NUMERIC(2,2) NOT NULL,
    chr_pricing_group_id INTEGER NOT NULL
);

CREATE TABLE tb_chair_category (
    chc_id          INTEGER NOT NULL,
    chc_chair_id    INTEGER NOT NULL,
    chc_category_id INTEGER NOT NULL
);

CREATE TABLE tb_chair_status_change (
    csc_id         INTEGER NOT NULL,
    csc_reason     VARCHAR(255) NOT NULL,
    csc_status     VARCHAR(100) NOT NULL,
    csc_entry_date DATE NOT NULL,
    csc_chair_id   INTEGER NOT NULL
);

CREATE TABLE tb_coupon (
    cpn_id          INTEGER NOT NULL,
    cpn_type        VARCHAR(30) NOT NULL,
    cpn_value       NUMERIC(3,2) NOT NULL,
    cpn_customer_id INTEGER NOT NULL
);

CREATE TABLE tb_credit_brand (
    cbr_id   INTEGER NOT NULL,
    cbr_name VARCHAR(30) NOT NULL
);

CREATE TABLE tb_credit_card (
    cre_id           INTEGER NOT NULL,
    cre_number       CHAR(16) NOT NULL,
    cre_holder       VARCHAR(100) NOT NULL,
    cre_cvv          CHAR(3) NOT NULL,
    cre_default      INTEGER NOT NULL,
    cre_customer_id  INTEGER NOT NULL,
    cre_credit_brand INTEGER NOT NULL
);

CREATE TABLE tb_customer (
    cus_id         INTEGER NOT NULL,
    cus_name       VARCHAR(255) NOT NULL,
    cus_cpf        CHAR(11) NOT NULL,
    cus_genre      CHAR(1) NOT NULL,
    cus_active     INTEGER NOT NULL,
    cus_phone_type VARCHAR(11) NOT NULL,
    cus_phone_ddd  CHAR(2) NOT NULL,
    cus_phone      VARCHAR(9) NOT NULL,
    cus_born_date  DATE NOT NULL,
    cus_user_id    INTEGER NOT NULL
);

CREATE TABLE tb_delivery (
    del_id             INTEGER NOT NULL,
    del_status         VARCHAR(40) NOT NULL,
    del_amount         INTEGER NOT NULL,
    del_transporter    VARCHAR(50) NOT NULL,
    del_estimated_date DATE NOT NULL,
    del_order_id       INTEGER NOT NULL,
    del_item_id        INTEGER NOT NULL
);

CREATE TABLE tb_insert_log (
    ilo_table    VARCHAR(20) NOT NULL,
    ilo_datetime TIMESTAMP NOT NULL,
    ilo_user_id  INTEGER NOT NULL,
    ilo_row_id   INTEGER NOT NULL
);

CREATE TABLE tb_item (
    itm_id          INTEGER NOT NULL,
    itm_entry_date  DATE NOT NULL,
    itm_amount      INTEGER NOT NULL,
    itm_unit_cost   NUMERIC(4,2) NOT NULL,
    itm_reserved    INTEGER,
    itm_chair_id    INTEGER NOT NULL,
    itm_supplier_id INTEGER NOT NULL
);

CREATE TABLE tb_item_swap (
    its_id          INTEGER NOT NULL,
    its_amount      INTEGER NOT NULL,
    its_status      VARCHAR(30) NOT NULL,
    its_total_value NUMERIC(4,2) NOT NULL,
    its_item_id     INTEGER NOT NULL,
    its_order_id    INTEGER NOT NULL
);

CREATE TABLE tb_order (
    ord_id                  INTEGER NOT NULL,
    ord_status              VARCHAR(30) NOT NULL,
    ord_created_date        DATE NOT NULL,
    ord_updated_date        DATE NOT NULL,
    ord_total_amount        INTEGER NOT NULL,
    ord_total_value         INTEGER NOT NULL,
    ord_customer_id         INTEGER NOT NULL,
    ord_billing_address_id  INTEGER NOT NULL,
    ord_delivery_address_id INTEGER NOT NULL
);

CREATE TABLE tb_order_coupon (
    ocp_id         INTEGER NOT NULL,
    ocp_paid_value NUMERIC(4,2) NOT NULL,
    ocp_coupon_id  INTEGER NOT NULL,
    ocp_order_id   INTEGER NOT NULL
);

CREATE TABLE tb_order_credit_card (
    occ_id             INTEGER NOT NULL,
    occ_paid_value     NUMERIC(4,2) NOT NULL,
    occ_order_id       INTEGER NOT NULL,
    occ_credit_card_id INTEGER NOT NULL
);

CREATE TABLE tb_order_item (
    ori_id          INTEGER NOT NULL,
    ori_status      VARCHAR(50) NOT NULL,
    ori_amount      INTEGER NOT NULL,
    ori_sell_price  NUMERIC(4,2) NOT NULL,
    ori_freight_tax NUMERIC(3,2) NOT NULL,
    ori_item_id     INTEGER NOT NULL,
    ori_order_id    INTEGER NOT NULL
);

CREATE TABLE tb_price_change_request (
    pcr_id              INTEGER NOT NULL,
    pcr_requested_price NUMERIC(3,2) NOT NULL,
    pcr_status          VARCHAR(30) NOT NULL,
    pcr_chair_id        INTEGER NOT NULL
);

CREATE TABLE tb_pricing_group (
    pgr_id            INTEGER NOT NULL,
    pgr_name          VARCHAR(30) NOT NULL,
    pgr_percent_value NUMERIC(3,2) NOT NULL
);

CREATE TABLE tb_supplier (
    sup_id   INTEGER NOT NULL,
    sup_name VARCHAR(100) NOT NULL
);

CREATE TABLE tb_update_log (
    ulo_table     VARCHAR(20) NOT NULL,
    ulo_datetime  TIMESTAMP NOT NULL,
    ulo_column    VARCHAR(50) NOT NULL,
    ulo_row_id    INTEGER NOT NULL,
    ulo_old_value VARCHAR(255),
    ulo_new_value VARCHAR(255) NOT NULL,
    ulo_user_id   INTEGER NOT NULL
);

CREATE TABLE tb_user (
    usr_id       INTEGER NOT NULL,
    usr_email    VARCHAR(255) NOT NULL,
    usr_password VARCHAR(255) NOT NULL,
    usr_type     VARCHAR(30) NOT NULL
);

-- Comentários (mantidos em posição original)
COMMENT ON COLUMN tb_chair.chr_height IS 'Centimetros.';
COMMENT ON COLUMN tb_chair.chr_width IS 'Centimetros.';

-- Constraints (PKs e FKs) agrupadas no final
-- Chaves Primárias
ALTER TABLE tb_address ADD CONSTRAINT pk_tb_address PRIMARY KEY (add_id);
ALTER TABLE tb_cart ADD CONSTRAINT pk_tb_cart PRIMARY KEY (car_id);
ALTER TABLE tb_category ADD CONSTRAINT pk_tb_category PRIMARY KEY (cat_id);
ALTER TABLE tb_chair ADD CONSTRAINT pk_tb_chair PRIMARY KEY (chr_id);
ALTER TABLE tb_chair_category ADD CONSTRAINT pk_tb_chair_category PRIMARY KEY (chc_id);
ALTER TABLE tb_chair_status_change ADD CONSTRAINT pk_tb_chair_status_change PRIMARY KEY (csc_id);
ALTER TABLE tb_coupon ADD CONSTRAINT pk_tb_coupon PRIMARY KEY (cpn_id);
ALTER TABLE tb_credit_brand ADD CONSTRAINT pk_tb_credit_brand PRIMARY KEY (cbr_id);
ALTER TABLE tb_credit_card ADD CONSTRAINT pk_tb_credit_card PRIMARY KEY (cre_id);
ALTER TABLE tb_customer ADD CONSTRAINT pk_tb_customer PRIMARY KEY (cus_id);
ALTER TABLE tb_delivery ADD CONSTRAINT pk_tb_delivery PRIMARY KEY (del_id);
ALTER TABLE tb_item ADD CONSTRAINT pk_tb_item PRIMARY KEY (itm_id);
ALTER TABLE tb_item_swap ADD CONSTRAINT pk_tb_item_swap PRIMARY KEY (its_id);
ALTER TABLE tb_order ADD CONSTRAINT pk_tb_order PRIMARY KEY (ord_id);
ALTER TABLE tb_order_coupon ADD CONSTRAINT pk_tb_order_coupon PRIMARY KEY (ocp_id);
ALTER TABLE tb_order_credit_card ADD CONSTRAINT pk_tb_order_credit_card PRIMARY KEY (occ_id);
ALTER TABLE tb_order_item ADD CONSTRAINT pk_tb_order_item PRIMARY KEY (ori_id);
ALTER TABLE tb_price_change_request ADD CONSTRAINT pk_tb_price_change_request PRIMARY KEY (pcr_id);
ALTER TABLE tb_pricing_group ADD CONSTRAINT pk_tb_pricing_group PRIMARY KEY (pgr_id);
ALTER TABLE tb_supplier ADD CONSTRAINT pk_tb_supplier PRIMARY KEY (sup_id);
ALTER TABLE tb_user ADD CONSTRAINT pk_tb_user PRIMARY KEY (usr_id);

ALTER TABLE tb_order
    ADD CONSTRAINT fk_order_billing_address FOREIGN KEY (ord_billing_address_id) REFERENCES tb_address (add_id),
    ADD CONSTRAINT fk_order_delivery_address FOREIGN KEY (ord_delivery_address_id) REFERENCES tb_address (add_id);

ALTER TABLE tb_chair_category
    ADD CONSTRAINT fk_chair_category_category FOREIGN KEY (chc_category_id) REFERENCES tb_category (cat_id),
    ADD CONSTRAINT fk_chair_category_chair FOREIGN KEY (chc_chair_id) REFERENCES tb_chair (chr_id);

ALTER TABLE tb_chair_status_change
    ADD CONSTRAINT fk_chair_status_chair FOREIGN KEY (csc_chair_id) REFERENCES tb_chair (chr_id);

ALTER TABLE tb_item
    ADD CONSTRAINT fk_item_chair FOREIGN KEY (itm_chair_id) REFERENCES tb_chair (chr_id),
    ADD CONSTRAINT fk_item_supplier FOREIGN KEY (itm_supplier_id) REFERENCES tb_supplier (sup_id);

ALTER TABLE tb_price_change_request
    ADD CONSTRAINT fk_price_change_chair FOREIGN KEY (pcr_chair_id) REFERENCES tb_chair (chr_id);

ALTER TABLE tb_order_coupon
    ADD CONSTRAINT fk_order_coupon_coupon FOREIGN KEY (ocp_coupon_id) REFERENCES tb_coupon (cpn_id),
    ADD CONSTRAINT fk_order_coupon_order FOREIGN KEY (ocp_order_id) REFERENCES tb_order (ord_id);

ALTER TABLE tb_credit_card
    ADD CONSTRAINT fk_credit_card_brand FOREIGN KEY (cre_credit_brand) REFERENCES tb_credit_brand (cbr_id),
    ADD CONSTRAINT fk_credit_card_customer FOREIGN KEY (cre_customer_id) REFERENCES tb_customer (cus_id);

ALTER TABLE tb_order_credit_card
    ADD CONSTRAINT fk_order_credit_card_card FOREIGN KEY (occ_credit_card_id) REFERENCES tb_credit_card (cre_id),
    ADD CONSTRAINT fk_order_credit_card_order FOREIGN KEY (occ_order_id) REFERENCES tb_order (ord_id);

ALTER TABLE tb_address
    ADD CONSTRAINT fk_address_customer FOREIGN KEY (add_customer_id) REFERENCES tb_customer (cus_id);

ALTER TABLE tb_cart
    ADD CONSTRAINT fk_cart_customer FOREIGN KEY (car_customer_id) REFERENCES tb_customer (cus_id),
    ADD CONSTRAINT fk_cart_item FOREIGN KEY (car_item_id) REFERENCES tb_item (itm_id);

ALTER TABLE tb_coupon
    ADD CONSTRAINT fk_coupon_customer FOREIGN KEY (cpn_customer_id) REFERENCES tb_customer (cus_id);

ALTER TABLE tb_order
    ADD CONSTRAINT fk_order_customer FOREIGN KEY (ord_customer_id) REFERENCES tb_customer (cus_id);

ALTER TABLE tb_delivery
    ADD CONSTRAINT fk_delivery_item FOREIGN KEY (del_item_id) REFERENCES tb_item (itm_id),
    ADD CONSTRAINT fk_delivery_order FOREIGN KEY (del_order_id) REFERENCES tb_order (ord_id);

ALTER TABLE tb_item_swap
    ADD CONSTRAINT fk_item_swap_item FOREIGN KEY (its_item_id) REFERENCES tb_item (itm_id),
    ADD CONSTRAINT fk_item_swap_order FOREIGN KEY (its_order_id) REFERENCES tb_order (ord_id);

ALTER TABLE tb_order_item
    ADD CONSTRAINT fk_order_item_item FOREIGN KEY (ori_item_id) REFERENCES tb_item (itm_id),
    ADD CONSTRAINT fk_order_item_order FOREIGN KEY (ori_order_id) REFERENCES tb_order (ord_id);

ALTER TABLE tb_chair
    ADD CONSTRAINT fk_chair_pricing_group FOREIGN KEY (chr_pricing_group_id) REFERENCES tb_pricing_group (pgr_id);

ALTER TABLE tb_customer
    ADD CONSTRAINT fk_customer_user FOREIGN KEY (cus_user_id) REFERENCES tb_user (usr_id);

ALTER TABLE tb_user
ALTER COLUMN usr_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_customer
ALTER COLUMN cus_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_address
ALTER COLUMN add_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_cart
ALTER COLUMN car_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_category
ALTER COLUMN cat_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_chair
ALTER COLUMN chr_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_chair_category
ALTER COLUMN chc_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_chair_status_change
ALTER COLUMN csc_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_coupon
ALTER COLUMN cpn_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_credit_brand
ALTER COLUMN cbr_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_credit_card
ALTER COLUMN cre_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_delivery
ALTER COLUMN del_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_item
ALTER COLUMN itm_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_item_swap
ALTER COLUMN its_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_order
ALTER COLUMN ord_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_order_coupon
ALTER COLUMN ocp_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_order_credit_card
ALTER COLUMN occ_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_order_item
ALTER COLUMN ori_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_price_change_request
ALTER COLUMN pcr_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_pricing_group
ALTER COLUMN pgr_id ADD GENERATED BY DEFAULT AS IDENTITY;

ALTER TABLE tb_supplier
ALTER COLUMN sup_id ADD GENERATED BY DEFAULT AS IDENTITY;

INSERT INTO tb_credit_brand (cbr_id, cbr_name) VALUES (1, 'VISA');
INSERT INTO tb_credit_brand (cbr_id, cbr_name) VALUES (2, 'MASTERCARD');
