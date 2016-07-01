ALTER TABLE `auditlog`
MODIFY COLUMN `transaction_id`  text NOT NULL AFTER `principal`;

