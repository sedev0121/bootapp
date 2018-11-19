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