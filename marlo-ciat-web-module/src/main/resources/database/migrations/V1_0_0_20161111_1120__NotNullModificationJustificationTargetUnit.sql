ALTER TABLE `target_units`
MODIFY COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `modified_by`;