ALTER TABLE `project_lp6_contribution`
MODIFY COLUMN `narrative`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `contribution`,
MODIFY COLUMN `working_across_flagships`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `is_working_across_flagships`,
MODIFY COLUMN `undertaking_efforts_leading`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `is_undertaking_efforts_leading`,
MODIFY COLUMN `providing_pathways_narrative`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `undertaking_efforts_leading`,
MODIFY COLUMN `top_three_partnerships`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `is_providing_pathways`,
MODIFY COLUMN `undertaking_efforts_csa`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `is_undertaking_efforts_csa`,
MODIFY COLUMN `initiative_related`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `is_initiative_related`,
MODIFY COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `modified_by`;