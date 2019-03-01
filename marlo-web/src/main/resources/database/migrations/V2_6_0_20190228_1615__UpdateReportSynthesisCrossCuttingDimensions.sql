ALTER TABLE `report_synthesis_cross_cutting_dimensions`
ADD COLUMN `gender_research_findings`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `id`;

ALTER TABLE `report_synthesis_cross_cutting_dimensions`
ADD COLUMN `gender_learned`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `gender_research_findings`;

ALTER TABLE `report_synthesis_cross_cutting_dimensions`
ADD COLUMN `gender_problems_arisen`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `gender_learned`;

ALTER TABLE `report_synthesis_cross_cutting_dimensions`
ADD COLUMN `youth_contribution`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `gender_problems_arisen`;

ALTER TABLE `report_synthesis_cross_cutting_dimensions`
ADD COLUMN `youth_research_findings`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `youth_contribution`;

ALTER TABLE `report_synthesis_cross_cutting_dimensions`
ADD COLUMN `youth_learned`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `youth_research_findings`;

ALTER TABLE `report_synthesis_cross_cutting_dimensions`
ADD COLUMN `youth_problems_arisen`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `youth_learned`;

ALTER TABLE `report_synthesis_cross_cutting_dimensions`
ADD COLUMN `cap_dev_key_achievements`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `youth_problems_arisen`;

ALTER TABLE `report_synthesis_cross_cutting_dimensions`
ADD COLUMN `climate_change_key_achievements`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `cap_dev_key_achievements`;