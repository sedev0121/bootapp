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


DROP TABLE IF EXISTS `notice_vendor`;
DROP TABLE IF EXISTS `permission_group_function_unit`;

DROP TABLE IF EXISTS `function`;
CREATE TABLE `function`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of function
-- ----------------------------
INSERT INTO `function` VALUES (1, '询价管理');
INSERT INTO `function` VALUES (2, '报价管理');
INSERT INTO `function` VALUES (3, '订单管理');
INSERT INTO `function` VALUES (4, '出货看板');
INSERT INTO `function` VALUES (5, '出入库单据');
INSERT INTO `function` VALUES (6, '对账单管理');
INSERT INTO `function` VALUES (7, '基础资料');
INSERT INTO `function` VALUES (8, '报表中心');
INSERT INTO `function` VALUES (9, '公告通知');


DROP TABLE IF EXISTS `action`;
CREATE TABLE `action`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of action
-- ----------------------------
INSERT INTO `action` VALUES (1, '查看列表');
INSERT INTO `action` VALUES (2, '新建/发布');
INSERT INTO `action` VALUES (3, '删除');
INSERT INTO `action` VALUES (4, '审核/退回');
INSERT INTO `action` VALUES (5, '通过/拒绝');
INSERT INTO `action` VALUES (6, '归档');
INSERT INTO `action` VALUES (7, '新建');
INSERT INTO `action` VALUES (8, '发布');
INSERT INTO `action` VALUES (9, '供应商档案');
INSERT INTO `action` VALUES (10, '新建供应商');
INSERT INTO `action` VALUES (11, '物料档案');
INSERT INTO `action` VALUES (12, '物料价格查询');


DROP TABLE IF EXISTS `function_action`;
CREATE TABLE `function_action`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `function_id` bigint(20) NULL DEFAULT NULL,
  `action_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK6gpnrgxvuhi6ibpse9pjjln7a`(`action_id`) USING BTREE,
  INDEX `FK8g1u0nym8shhaq5bk66bhu36x`(`function_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 27 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of function_action
-- ----------------------------
INSERT INTO `function_action` VALUES (1, 1, 1);
INSERT INTO `function_action` VALUES (2, 1, 2);
INSERT INTO `function_action` VALUES (3, 1, 3);
INSERT INTO `function_action` VALUES (4, 1, 4);
INSERT INTO `function_action` VALUES (5, 1, 5);
INSERT INTO `function_action` VALUES (6, 1, 6);
INSERT INTO `function_action` VALUES (7, 2, 1);
INSERT INTO `function_action` VALUES (8, 2, 5);
INSERT INTO `function_action` VALUES (9, 3, 1);
INSERT INTO `function_action` VALUES (10, 3, 2);
INSERT INTO `function_action` VALUES (11, 4, 1);
INSERT INTO `function_action` VALUES (12, 5, 1);
INSERT INTO `function_action` VALUES (13, 6, 1);
INSERT INTO `function_action` VALUES (14, 6, 2);
INSERT INTO `function_action` VALUES (15, 6, 3);
INSERT INTO `function_action` VALUES (16, 6, 4);
INSERT INTO `function_action` VALUES (17, 2, 4);
INSERT INTO `function_action` VALUES (18, 2, 6);
INSERT INTO `function_action` VALUES (19, 7, 9);
INSERT INTO `function_action` VALUES (20, 8, 1);
INSERT INTO `function_action` VALUES (21, 9, 1);
INSERT INTO `function_action` VALUES (22, 9, 7);
INSERT INTO `function_action` VALUES (23, 9, 8);
INSERT INTO `function_action` VALUES (24, 7, 10);
INSERT INTO `function_action` VALUES (25, 7, 11);
INSERT INTO `function_action` VALUES (26, 7, 12);



DROP TABLE IF EXISTS `vendor_provide`;
CREATE TABLE `vendor_provide`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vendor_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `provide_id` int(11) NOT NULL,
  `unit_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


### 2019-02-14 ###
alter table vendor_provide DROP column unit_id;
alter table vendor DROP column unit_id;

DROP TABLE IF EXISTS `master`;
CREATE TABLE `master`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `item_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `key_index`(`item_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;