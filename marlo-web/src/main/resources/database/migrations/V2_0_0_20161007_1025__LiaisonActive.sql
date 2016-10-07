ALTER TABLE `liaison_users`
ADD COLUMN `is_active`  tinyint(1) NOT NULL AFTER `crp_id`;

ALTER TABLE `liaison_institutions`
ADD COLUMN `is_active`  tinyint(1) NOT NULL DEFAULT 1 AFTER `crp_id`;

