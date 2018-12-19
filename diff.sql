ALTER TABLE venpriceadjust_main ADD attach_original_name varchar(255);
ALTER TABLE venpriceadjust_main ADD attach_file_name varchar(255);
ALTER TABLE purchase_order_main DROP COLUMN money;
ALTER TABLE purchase_order_main DROP COLUMN sum;
ALTER TABLE notice ADD url varchar(255);

ALTER TABLE purchase_in_detail MODIFY tax_price DOUBLE;
ALTER TABLE purchase_in_detail MODIFY nat_price DOUBLE;
ALTER TABLE purchase_in_detail MODIFY nat_tax_price DOUBLE;
ALTER TABLE purchase_in_detail MODIFY price DOUBLE;
ALTER TABLE purchase_in_detail MODIFY material_price DOUBLE;
ALTER TABLE purchase_in_detail MODIFY material_tax_price DOUBLE;


ALTER TABLE purchase_in_main add verify_date datetime;

ALTER TABLE purchase_in_detail MODIFY tax_cost DOUBLE;
ALTER TABLE purchase_in_detail MODIFY nat_cost DOUBLE;
ALTER TABLE purchase_in_detail MODIFY nat_tax DOUBLE;
ALTER TABLE purchase_in_detail MODIFY nat_tax_cost DOUBLE;


ALTER TABLE statement_detail MODIFY closed_tax_price DOUBLE;
ALTER TABLE statement_detail MODIFY closed_tax_money DOUBLE;
ALTER TABLE statement_detail MODIFY closed_price DOUBLE;
ALTER TABLE statement_detail MODIFY closed_money DOUBLE;

DROP TABLE IF EXISTS `provide_class`;
CREATE TABLE `provide_class`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` int(11) NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `unit_provide`;
CREATE TABLE `unit_provide`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unit_id` int(11) NOT NULL,
  `provide_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Fixed;


DROP TABLE IF EXISTS `vendor_provide`;
CREATE TABLE `vendor_provide`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vendor_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `provide_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
