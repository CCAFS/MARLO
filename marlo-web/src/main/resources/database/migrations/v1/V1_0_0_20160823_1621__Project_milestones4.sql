START TRANSACTION;

ALTER TABLE `project_milestones`
CHANGE COLUMN `expetcted_gender` `expected_gender`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `narrative_gender`;



 

COMMIT;