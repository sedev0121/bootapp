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


/* 2019-06-07 */
alter table box add column inventory_name varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;
alter table box add column inventory_specs varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;
alter table box add column vendor_code varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;
alter table box add column vendor_name varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;
alter table box add column type int(1) NULL DEFAULT NULL;
alter table box add column delivery_number varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;

update box a left join inventory b on a.inventory_code=b.code set a.inventory_name=b.name, a.inventory_specs=b.specs;
update box a left join delivery_main b on a.delivery_code=b.code left join vendor c on b.vendor_code=c.code set a.vendor_code=c.code, a.vendor_name=c.name, a.delivery_number=b.deliver_number;
update box set type=1 where delivery_code is not null;

/* 2019-06-13 second password */
alter table account add column second_password varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;
update account set second_password='$2a$10$CBrThLk9FmVJxhlkFsHYu.jSvEVubybN62TaruleZyt5z8eOIEm66' where role='ROLE_VENDOR';


/* 2019-06-14 triger */
DROP TABLE IF EXISTS `box_history`;
CREATE TABLE `box_history`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `box_id` int(11) NULL DEFAULT NULL,
  `box_class_id` int(11) NULL DEFAULT NULL,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `bind_date` datetime(0) NULL DEFAULT NULL,
  `bind_property` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `quantity` double(255, 2) NULL DEFAULT NULL,
  `used` int(1) NULL DEFAULT NULL,
  `state` int(1) NULL DEFAULT NULL,
  `inventory_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `delivery_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `inventory_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `inventory_specs` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `vendor_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `vendor_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `type` int(1) NULL DEFAULT NULL,
  `delivery_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `operation_date` datetime(0) NULL DEFAULT NULL,
  `operation` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

DROP TRIGGER IF EXISTS update_trigger;
DROP TRIGGER IF EXISTS insert_trigger;
DROP TRIGGER IF EXISTS delete_trigger;  

delimiter # 
create trigger update_trigger after update on box
   for each row
   begin
   insert into box_history(box_id, box_class_id, code, bind_date, bind_property, quantity, used, state, inventory_code, delivery_code, inventory_name, inventory_specs, vendor_code, vendor_name, type, delivery_number, operation_date, operation) values (new.id, new.box_class_id, new.code, new.bind_date, new.bind_property, new.quantity, new.used, new.state, new.inventory_code, new.delivery_code, new.inventory_name, new.inventory_specs, new.vendor_code, new.vendor_name, new.type, new.delivery_number, now(), 'update');
   end#   
	 
delimiter # 	 
create trigger insert_trigger after insert on box
   for each row
   begin
   insert into box_history(box_id, box_class_id, code, bind_date, bind_property, quantity, used, state, inventory_code, delivery_code, inventory_name, inventory_specs, vendor_code, vendor_name, type, delivery_number, operation_date, operation) values (new.id, new.box_class_id, new.code, new.bind_date, new.bind_property, new.quantity, new.used, new.state, new.inventory_code, new.delivery_code, new.inventory_name, new.inventory_specs, new.vendor_code, new.vendor_name, new.type, new.delivery_number, now(), 'insert');
   end#    

delimiter # 
create trigger delete_trigger before delete on box
   for each row
   begin
   insert into box_history(box_id, box_class_id, code, bind_date, bind_property, quantity, used, state, inventory_code, delivery_code, inventory_name, inventory_specs, vendor_code, vendor_name, type, delivery_number, operation_date, operation) values (old.id, old.box_class_id, old.code, old.bind_date, old.bind_property, old.quantity, old.used, old.state, old.inventory_code, old.delivery_code, old.inventory_name, old.inventory_specs, old.vendor_code, old.vendor_name, old.type, old.delivery_number, now(), 'delete');
   end#  
delimiter ; 


alter table inventory add column extra_rate float(11, 2) NULL DEFAULT 0.00;


/* 2019-06-18 */
alter table delivery_detail add column cancel_date datetime(0) NULL DEFAULT NULL;
alter table delivery_detail add column cancel_reason varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;
alter table delivery_detail add column cancel_quantity double(20, 2) NULL DEFAULT NULL;
alter table delivery_detail add column cancel_confirm_date datetime(0) NULL DEFAULT NULL;




/******************************* SRM_V30 *******************************/
/* 2019-07-19 */
alter table purchase_order_detail add column package_quantity double(16,6) NULL DEFAULT NULL;
alter table purchase_order_detail add column delivered_package_quantity double(16,6) NULL DEFAULT NULL;
alter table delivery_detail add column delivered_package_quantity double(16,6) NULL DEFAULT NULL;

/****************************** SRM_V32 ********************************/
/* 2019-07-17 */
DROP TABLE IF EXISTS vendor_provide;
DROP TABLE IF EXISTS unit;
DROP TABLE IF EXISTS unit_provide;
DROP TABLE IF EXISTS provide_class;
DROP TABLE IF EXISTS negotiation_main;
DROP TABLE IF EXISTS negotiation_detail;
DROP TABLE IF EXISTS warning;
DROP TABLE IF EXISTS measurement_unit;


/* 2019-07-22 */
alter table account DROP column wangwang;
alter table account DROP column yahoo;
alter table account DROP column weixin;
alter table account DROP column qq;
alter table account DROP column gtalk;
alter table account DROP column entry_time;

/* 2019-07-25 */
alter table purchase_order_detail DROP column nat_price;
alter table purchase_order_detail DROP column nat_tax_price;
alter table purchase_order_detail DROP column nat_money;
alter table purchase_order_detail DROP column nat_sum;

INSERT INTO `action` VALUES (12, '收货');
INSERT INTO `function_action` VALUES (20, 7, 12);

INSERT INTO `function` VALUES (8, '对账单管理');
INSERT INTO `action` VALUES (13, '新建/提交');
INSERT INTO `action` VALUES (14, '审核');
INSERT INTO `action` VALUES (15, '发布');
INSERT INTO `action` VALUES (16, '撤回');
INSERT INTO `action` VALUES (17, '审批');
INSERT INTO `action` VALUES (18, '传递ERP');

INSERT INTO `function_action` VALUES (21, 8, 1); /*对账单 查看列表 */
INSERT INTO `function_action` VALUES (22, 8, 13); /*对账单 新建/提交 */
INSERT INTO `function_action` VALUES (23, 8, 14); /*对账单 审核 */
INSERT INTO `function_action` VALUES (24, 8, 15); /*对账单 发布 */
INSERT INTO `function_action` VALUES (25, 8, 16); /*对账单 撤回 */
INSERT INTO `function_action` VALUES (26, 8, 17); /*对账单 审批 */
INSERT INTO `function_action` VALUES (27, 8, 18); /*对账单 传递ERP */

/* 2019-07-29 */
INSERT INTO `function` VALUES (9, '出货看板');
INSERT INTO `function_action` VALUES (30, 9, 1); /*出货看板 查看列表*/

DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `make_id` int(11) NULL DEFAULT NULL,
  `make_date` datetime(0) NULL DEFAULT NULL,
  `statement_date` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

DROP TABLE IF EXISTS `task_log`;
CREATE TABLE `task_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NULL DEFAULT NULL,
  `vendor_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `state` int(1) NULL DEFAULT NULL,
  `failed_reason` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `statement_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

DROP TABLE IF EXISTS `attach_file`;
CREATE TABLE `attach_file`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `row_no` int(11) NULL DEFAULT NULL,
  `filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `original_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `date` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

DROP TABLE IF EXISTS `purchase_in_main`;
CREATE TABLE `purchase_in_main`  (
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `bredvouch` int(1) NULL DEFAULT NULL,
  `vendor_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `company_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `store_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `date` datetime(0) NULL DEFAULT NULL,
  `verify_date` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`code`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `purchase_in_detail`;
CREATE TABLE `purchase_in_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `row_no` int(11) NOT NULL,
  `inventory_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `quantity` double(20, 4) NULL DEFAULT NULL,
  `price` double NULL DEFAULT NULL,
  `cost` double(20, 2) NULL DEFAULT NULL,
  `tax` double(20, 2) NULL DEFAULT NULL,
  `tax_price` double NULL DEFAULT NULL,
  `tax_rate` double(20, 2) NULL DEFAULT NULL,
  `tax_cost` double NULL DEFAULT NULL,
  `state` int(1) NOT NULL DEFAULT 0,
  `auto_id` int(20) NULL DEFAULT NULL,
  `po_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `po_row_no` int(20) NULL DEFAULT NULL,
  `delivery_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `delivery_row_no` int(11) NULL DEFAULT NULL,
  `sync_date` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 62 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `contract_main`;
CREATE TABLE `contract_main`  (
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `date` datetime(0) NOT NULL,
  `start_date` datetime(0) NOT NULL,
  `end_date` datetime(0) NOT NULL,
  `type` int(1) NOT NULL DEFAULT 1,
  `kind` int(1) NOT NULL,
  `company_id` int(255) NULL DEFAULT NULL,
  `vendor_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `state` int(1) NOT NULL,
  `tax_rate` float(16, 0) NOT NULL DEFAULT 17,
  `project_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `quantity_type` int(1) NOT NULL,
  `price_type` int(1) NOT NULL,
  `base_price` double(16, 2) NULL DEFAULT NULL,
  `floating_price` double(16, 2) NULL DEFAULT NULL,
  `pay_mode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `memo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `make_id` int(11) NULL DEFAULT NULL,
  `make_date` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`code`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `contract_detail`;
CREATE TABLE `contract_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `row_no` int(11) NOT NULL,
  `inventory_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `quantity` double(16, 4) NULL DEFAULT NULL,
  `tax_price` double(16, 4) NULL DEFAULT NULL,
  `memo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `floating_direction` int(1) NULL DEFAULT NULL,
  `floating_price` double(16, 4) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 30 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `statement_main`;
CREATE TABLE `statement_main`  (
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `date` datetime(0) NULL DEFAULT NULL,
  `type` int(1) NOT NULL DEFAULT 1 COMMENT '1:采购对账, 2:委外对账',
  `company_id` int(255) NULL DEFAULT NULL,
  `vendor_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `state` int(1) NULL DEFAULT NULL,
  `tax_rate` float(16, 0) NOT NULL DEFAULT 17,
  `make_id` int(11) NULL DEFAULT NULL,
  `make_date` datetime(0) NULL DEFAULT NULL,
  `review_id` int(11) NULL DEFAULT NULL,
  `review_date` datetime(0) NULL DEFAULT NULL,
  `deploy_id` int(11) NULL DEFAULT NULL,
  `deploy_date` datetime(0) NULL DEFAULT NULL,
  `cancel_id` int(11) NULL DEFAULT NULL,
  `cancel_date` datetime(0) NULL DEFAULT NULL,
  `confirm_id` int(11) NULL DEFAULT NULL,
  `confirm_date` datetime(0) NULL DEFAULT NULL,
  `invoice_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '0个字节 (04113256；04113257；04113258；04113259)',
  `invoice_state` int(1) NULL DEFAULT NULL COMMENT '发票状态(已开发票/发票已退回/发票已审核/已传递ERP)',
  `invoice_type` int(1) NULL DEFAULT 1 COMMENT '发票类型(专用发票/普通发票）',
  `invoice_make_id` int(11) NULL DEFAULT NULL,
  `invoice_make_date` datetime(0) NULL DEFAULT NULL,
  `erp_invoice_make_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `erp_invoice_make_date` datetime(0) NULL DEFAULT NULL,
  `invoice_confirm_id` int(11) NULL DEFAULT NULL,
  `invoice_confirm_date` datetime(0) NULL DEFAULT NULL,
  `invoice_cancel_id` int(11) NULL DEFAULT NULL,
  `invoice_cancel_date` datetime(0) NULL DEFAULT NULL,
  `task_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `cost_sum` double(16, 2) NULL DEFAULT NULL,
  `tax_cost_sum` double(16, 2) NULL DEFAULT NULL,
  `adjust_cost_sum` double(16, 2) NULL DEFAULT NULL,
  `tax_sum` double(16, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`code`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `statement_detail`;
CREATE TABLE `statement_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `row_no` int(11) NULL DEFAULT NULL,
  `pi_detail_id` int(11) NULL DEFAULT NULL,
  `adjust_tax_cost` double(16, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;



/* 2019-08-05 */
alter table purchase_order_main add column contract_code varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;
alter table purchase_order_main add column base_price double(16, 2) NULL DEFAULT NULL;

alter table purchase_order_detail add column price_from int(1) NOT NULL DEFAULT 0;
alter table purchase_order_detail add column contract_code varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;
alter table purchase_order_detail add column base_price double(16, 2) NULL DEFAULT NULL;

/* 2019-08-06 */
alter table statement_main modify column tax_rate int(2) NOT NULL DEFAULT 16;


/* 2019-08-12 */
alter table statement_detail add column tax_rate int(2) NOT NULL DEFAULT 16;

INSERT INTO `function` VALUES (11, '合同管理');
INSERT INTO `function_action` VALUES (41, 11, 1); 
INSERT INTO `function_action` VALUES (42, 11, 13);  


/* 2019-08-13 */
INSERT INTO `function` VALUES (10, '询价管理');
INSERT INTO `action` VALUES (19, '新建/发布');
INSERT INTO `action` VALUES (20, '通过/拒绝');
INSERT INTO `action` VALUES (21, '审核/退回');
INSERT INTO `action` VALUES (22, '归档');
INSERT INTO `function_action` VALUES (31, 10, 1); 
INSERT INTO `function_action` VALUES (32, 10, 19); 
INSERT INTO `function_action` VALUES (33, 10, 20); 
INSERT INTO `function_action` VALUES (34, 10, 21); 
INSERT INTO `function_action` VALUES (35, 10, 22); 

alter table venpriceadjust_main DROP column attach_original_name;
alter table venpriceadjust_main DROP column attach_file_name;

INSERT INTO `function` VALUES (12, '采购动态');
INSERT INTO `function_action` VALUES (51, 12, 1); 
INSERT INTO `function_action` VALUES (52, 12, 2); 
INSERT INTO `function_action` VALUES (53, 12, 15); 

DROP TABLE IF EXISTS `message`;
alter table notice DROP column to_unit_account;
alter table notice DROP column vendor_code_list;
alter table notice DROP column to_all_vendor;
alter table notice DROP column create_unit;
alter table notice DROP column account_id_list;

/* 2019-08-15 */
INSERT INTO `function` VALUES (13, '账单任务');
INSERT INTO `function_action` VALUES (61, 13, 1); 

/* 2019-08-19 */
alter table notice add column class_id int(1) NULL;
DROP TABLE IF EXISTS `notice_class`;
CREATE TABLE `notice_class`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

/* 2019-08-23 */
alter table notice modify COLUMN content longtext NULL DEFAULT NULL;