ALTER TABLE `roles`
MODIFY COLUMN `description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `id`,
MODIFY COLUMN `acronym`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `description`;

