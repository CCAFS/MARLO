ALTER TABLE `projects`
MODIFY COLUMN `type`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `end_date`;