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