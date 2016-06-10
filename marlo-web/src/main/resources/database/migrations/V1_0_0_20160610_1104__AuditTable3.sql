ALTER TABLE `auditlog`
ADD COLUMN `user_id`  bigint(20) NOT NULL AFTER `Entity_json`;

