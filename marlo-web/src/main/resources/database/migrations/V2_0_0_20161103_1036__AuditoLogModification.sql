ALTER TABLE `auditlog`
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `relation_name`;

