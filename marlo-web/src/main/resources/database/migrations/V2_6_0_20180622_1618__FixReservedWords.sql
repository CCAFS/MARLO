ALTER TABLE `report_synthesis_melia_evaluations`
CHANGE COLUMN `whom` `text_whon`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `status`,
CHANGE COLUMN `when` `text_when`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `text_whon`;