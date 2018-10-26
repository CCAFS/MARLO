ALTER TABLE `project_expected_study_info`
CHANGE COLUMN `references` `references_text`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `elaboration_outcome_impact_statement`;
