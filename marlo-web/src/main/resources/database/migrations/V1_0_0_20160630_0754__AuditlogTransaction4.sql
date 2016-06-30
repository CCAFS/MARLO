ALTER TABLE `auditlog`
ADD COLUMN `relation_name`  varchar(500) NULL AFTER `transaction_id`;

