ALTER TABLE `users`
MODIFY COLUMN `is_ccafs_user`  tinyint(1) NOT NULL DEFAULT 0 AFTER `password`,
MODIFY COLUMN `is_active`  tinyint(1) NOT NULL DEFAULT 1 AFTER `created_by`;

