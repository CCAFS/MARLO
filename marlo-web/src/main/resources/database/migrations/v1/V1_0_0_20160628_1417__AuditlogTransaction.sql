ALTER TABLE `auditlog`
ADD COLUMN `transaction_id`  bigint NOT NULL AFTER `user_id`;