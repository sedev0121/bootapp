ALTER TABLE venpriceadjust_main ADD attach_original_name varchar(255);
ALTER TABLE venpriceadjust_main ADD attach_file_name varchar(255);

ALTER TABLE purchase_order_main DROP COLUMN money;
ALTER TABLE purchase_order_main DROP COLUMN sum;