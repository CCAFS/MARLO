ALTER TABLE `users`
CHANGE COLUMN `is_ccafs_user` `is_cgiar_user`  tinyint(1) NOT NULL DEFAULT 0 AFTER `password`;
