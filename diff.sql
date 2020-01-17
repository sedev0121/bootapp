DROP TABLE IF EXISTS `purchase_order_detail_backup`;
DROP TABLE IF EXISTS `warning`;
DROP TABLE IF EXISTS `vendor_provide`;
DROP TABLE IF EXISTS `unit_provide`;
DROP TABLE IF EXISTS unit;
DROP TABLE IF EXISTS provide_class;
DROP TABLE IF EXISTS negotiation_main;
DROP TABLE IF EXISTS negotiation_detail;
DROP TABLE IF EXISTS measurement_unit;
DROP TABLE IF EXISTS `delivery_main_copy1`;

alter table box modify column quantity decimal(16, 2) NULL DEFAULT NULL;
alter table box_history modify column `quantity` decimal(16, 2) NULL DEFAULT NULL;

alter table contract_detail modify column quantity decimal(16, 4) NULL DEFAULT NULL;
alter table contract_detail modify column tax_price decimal(16, 4) NULL DEFAULT NULL;
alter table contract_detail modify column floating_price decimal(16, 4) NULL DEFAULT NULL;

alter table contract_main modify column `base_price` decimal(16, 2) NULL DEFAULT NULL;
alter table contract_main modify column `floating_price` decimal(16, 2) NULL DEFAULT NULL;
alter table contract_main modify column tax_rate decimal(16, 0) NOT NULL DEFAULT 13;


alter table delivery_detail modify column `delivered_quantity` decimal(20, 2) NULL DEFAULT NULL;
alter table delivery_detail modify column `accepted_quantity` decimal(20, 2) NULL DEFAULT NULL;
alter table delivery_detail modify column `cancel_quantity` decimal(20, 2) NULL DEFAULT NULL;
alter table delivery_detail modify column `delivered_package_quantity` decimal(16, 6) NULL DEFAULT NULL;


alter table inventory modify column `iimptaxrate` decimal(20, 2) NULL DEFAULT NULL;
alter table inventory modify column extra_rate decimal(11, 2) NULL DEFAULT 0.00;

alter table purchase_in_detail modify column `quantity` decimal(20, 4) NULL DEFAULT NULL;
alter table purchase_in_detail modify column `price` decimal(20, 6) NULL DEFAULT NULL;
alter table purchase_in_detail modify column `cost` decimal(20, 2) NULL DEFAULT NULL;
alter table purchase_in_detail modify column `tax` decimal(20, 2) NULL DEFAULT NULL;
alter table purchase_in_detail modify column `tax_price` decimal(20, 6) NULL DEFAULT NULL;
alter table purchase_in_detail modify column `tax_rate` decimal(20, 2) NULL DEFAULT NULL;
alter table purchase_in_detail modify column `tax_cost` decimal NULL DEFAULT NULL;
alter table purchase_in_detail modify column `bill_quantity` decimal(20, 2) NULL DEFAULT NULL;



alter table purchase_order_detail modify column `quantity` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `price` decimal(16, 6) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `tax_price` decimal(16, 6) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `money` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `sum` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `nat_price` decimal(16, 6) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `nat_tax_price` decimal(16, 6) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `nat_money` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `nat_sum` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `prepay_money` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `tax_rate` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `backed_quantity` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `arrived_quantity` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `invoiced_quantity` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `invoiced_money` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `confirmed_quantity` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `delivered_quantity` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `package_quantity` decimal(16, 6) NULL DEFAULT NULL;
alter table purchase_order_detail modify column `base_price` decimal(16, 2) NULL DEFAULT NULL;



alter table purchase_order_main modify column `tax_rate` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_main modify column `exchange_rate` decimal(16, 2) NULL DEFAULT NULL;
alter table purchase_order_main modify column `base_price` decimal(16, 2) NULL DEFAULT NULL;


alter table statement_detail modify column `adjust_tax_cost` decimal(16, 2) NULL DEFAULT NULL;

alter table statement_main modify column `cost_sum` decimal(16, 2) NULL DEFAULT NULL;
alter table statement_main modify column `tax_cost_sum` decimal(16, 2) NULL DEFAULT NULL;
alter table statement_main modify column `adjust_cost_sum` decimal(16, 2) NULL DEFAULT NULL;
alter table statement_main modify column `tax_sum` decimal(16, 2) NULL DEFAULT NULL;


alter table venpriceadjust_detail modify column `itaxrate` decimal(20, 2) NULL DEFAULT NULL;
alter table venpriceadjust_detail modify column `iunitprice` decimal(20, 6) NULL DEFAULT NULL;
alter table venpriceadjust_detail modify column `itaxunitprice` decimal(20, 6) NULL DEFAULT NULL;
alter table venpriceadjust_detail modify column `fminquantity` decimal(20, 4) NULL DEFAULT NULL;
alter table venpriceadjust_detail modify column `fmaxquantity` decimal(20, 4) NULL DEFAULT NULL;





alter table statement_detail add column `price` decimal(20, 6) NULL DEFAULT NULL;
alter table statement_detail add column `cost` decimal(20, 2) NULL DEFAULT NULL;
alter table statement_detail add column `tax_price` decimal(20, 6) NULL DEFAULT NULL;
alter table statement_detail add column `tax_cost` decimal(10, 0) NULL DEFAULT NULL;

update statement_detail a left join purchase_in_detail b on a.pi_detail_id=b.id set a.price=b.price, a.cost=b.cost, a.tax_price=b.tax_price, a.tax_cost=b.tax_cost;


--2020-01-17
ALTER TABLE purchase_in_detail DROP COLUMN erp_changed;
