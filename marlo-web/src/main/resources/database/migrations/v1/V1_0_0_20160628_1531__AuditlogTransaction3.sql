ALTER TABLE `auditlog`
ADD COLUMN `principal`  bigint  AFTER `user_id`;