DROP TABLE IF EXISTS `company`;
CREATE TABLE `company`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `erp_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of company
-- ----------------------------
INSERT INTO `company` VALUES (1, '创新电器', 1);
INSERT INTO `company` VALUES (2, '谈桥工厂', 2);
INSERT INTO `company` VALUES (3, '马桥工厂', 3);

DROP TABLE IF EXISTS `permission_dimension`;
CREATE TABLE `permission_dimension`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of permission_dimension
-- ----------------------------
INSERT INTO `permission_dimension` VALUES (1, '业务实体');
INSERT INTO `permission_dimension` VALUES (2, '库房');
INSERT INTO `permission_dimension` VALUES (3, '采购员');
INSERT INTO `permission_dimension` VALUES (4, '供应商');
INSERT INTO `permission_dimension` VALUES (5, '商品分类');


DROP TABLE IF EXISTS `permission_group_dimension`;
CREATE TABLE `permission_group_dimension`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `dimension_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of permission_group_dimension
-- ----------------------------
INSERT INTO `permission_group_dimension` VALUES (1, 7, 1);
INSERT INTO `permission_group_dimension` VALUES (2, 7, 2);
INSERT INTO `permission_group_dimension` VALUES (3, 4, 4);
INSERT INTO `permission_group_dimension` VALUES (4, 6, 3);
INSERT INTO `permission_group_dimension` VALUES (5, 6, 1);
INSERT INTO `permission_group_dimension` VALUES (6, 5, 1);
INSERT INTO `permission_group_dimension` VALUES (7, 5, 2);
INSERT INTO `permission_group_dimension` VALUES (8, 5, 5);
INSERT INTO `permission_group_dimension` VALUES (9, 4, 1);
INSERT INTO `permission_group_dimension` VALUES (10, 3, 2);
INSERT INTO `permission_group_dimension` VALUES (11, 4, 4);
INSERT INTO `permission_group_dimension` VALUES (12, 5, 4);

DROP TABLE IF EXISTS `permission_user_scope`;
CREATE TABLE `permission_user_scope`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `dimension_id` int(11) NOT NULL,
  `target_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 167 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of permission_user_scope
-- ----------------------------
INSERT INTO `permission_user_scope` VALUES (152, 18, 4, 1, '1');
INSERT INTO `permission_user_scope` VALUES (153, 18, 4, 1, '3');
INSERT INTO `permission_user_scope` VALUES (154, 18, 2, 1, '2');
INSERT INTO `permission_user_scope` VALUES (155, 18, 2, 2, '16');
INSERT INTO `permission_user_scope` VALUES (156, 18, 2, 2, '21');
INSERT INTO `permission_user_scope` VALUES (157, 18, 2, 3, '3');
INSERT INTO `permission_user_scope` VALUES (158, 18, 2, 3, '1');
INSERT INTO `permission_user_scope` VALUES (159, 18, 2, 5, '03');
INSERT INTO `permission_user_scope` VALUES (160, 18, 2, 5, '1');
INSERT INTO `permission_user_scope` VALUES (161, 18, 2, 4, '010001');
INSERT INTO `permission_user_scope` VALUES (162, 18, 2, 4, '020001');
INSERT INTO `permission_user_scope` VALUES (163, 18, 5, 1, '1');
INSERT INTO `permission_user_scope` VALUES (164, 18, 5, 1, '2');
INSERT INTO `permission_user_scope` VALUES (165, 18, 5, 1, '3');
INSERT INTO `permission_user_scope` VALUES (166, 18, 7, 1, '1');



alter table account add column company_id int(11) NULL DEFAULT 1;
alter table account add column unitname varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;


TRUNCATE TABLE function;
ALTER TABLE function AUTO_INCREMENT = 1;
INSERT INTO `function` VALUES (1, '权限管理');
INSERT INTO `function` VALUES (2, '采购员用户管理');
INSERT INTO `function` VALUES (3, '供应商用户管理');
INSERT INTO `function` VALUES (4, '基础资料');
INSERT INTO `function` VALUES (5, '箱码管理');
INSERT INTO `function` VALUES (6, '订单管理');
INSERT INTO `function` VALUES (7, '发货单管理');

TRUNCATE TABLE action;
ALTER TABLE action AUTO_INCREMENT = 1;
INSERT INTO `action` VALUES (1, '查看列表');
INSERT INTO `action` VALUES (2, '新建/修改');
INSERT INTO `action` VALUES (3, '删除');
INSERT INTO `action` VALUES (4, '发布');
INSERT INTO `action` VALUES (5, '提交');
INSERT INTO `action` VALUES (6, '确认/退回');
INSERT INTO `action` VALUES (7, '关闭');
INSERT INTO `action` VALUES (8, '行取消');
INSERT INTO `action` VALUES (9, '生成箱码');
INSERT INTO `action` VALUES (10, '启用/停用');
INSERT INTO `action` VALUES (11, '解绑');

TRUNCATE TABLE function_action;
ALTER TABLE function_action AUTO_INCREMENT = 1;
INSERT INTO `function_action` VALUES (1, 1, 1);
INSERT INTO `function_action` VALUES (2, 1, 2);
INSERT INTO `function_action` VALUES (3, 2, 1);
INSERT INTO `function_action` VALUES (4, 2, 2);
INSERT INTO `function_action` VALUES (5, 2, 3);
INSERT INTO `function_action` VALUES (6, 3, 1);
INSERT INTO `function_action` VALUES (7, 3, 2);
INSERT INTO `function_action` VALUES (8, 3, 3);
INSERT INTO `function_action` VALUES (9, 4, 1);
INSERT INTO `function_action` VALUES (10, 5, 1);
INSERT INTO `function_action` VALUES (11, 5, 9);
INSERT INTO `function_action` VALUES (12, 5, 10);
INSERT INTO `function_action` VALUES (13, 5, 11);
INSERT INTO `function_action` VALUES (14, 6, 1);
INSERT INTO `function_action` VALUES (15, 6, 4);
INSERT INTO `function_action` VALUES (16, 6, 7);
INSERT INTO `function_action` VALUES (17, 6, 8);
INSERT INTO `function_action` VALUES (18, 7, 1);
INSERT INTO `function_action` VALUES (19, 7, 6);

alter table box modify COLUMN box_class_id int(11) NULL DEFAULT NULL;
alter table box add column inventory_code varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;
alter table box add column delivery_code varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;
alter table box DROP column delivery_detail_id;

alter table delivery_detail DROP column deliver_number;
alter table delivery_main add column deliver_number varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;


alter table purchase_order_detail add column count_per_box int(11) NULL DEFAULT NULL;
update purchase_order_detail a left join purchase_order_main b on a.code=b.code left join inventory c on a.inventory_code=c.code set a.count_per_box=c.count_per_box where b.srmstate>0;


alter table delivery_main add column type varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;