ALTER TABLE `permissions`
MODIFY COLUMN `description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `permission`;

