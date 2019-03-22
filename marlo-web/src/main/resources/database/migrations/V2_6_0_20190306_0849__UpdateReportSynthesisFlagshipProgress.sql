/* Add AR2018 flagship progress fields */
/* interesting_points */
ALTER TABLE `report_synthesis_flagship_progress`
ADD COLUMN `overall_progress`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'AR2018 field' AFTER `id`;

ALTER TABLE `report_synthesis_flagship_progress`
ADD COLUMN `progress_by_flagships`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'AR2018 field' AFTER `overall_progress`;

ALTER TABLE `report_synthesis_flagship_progress`
ADD COLUMN `detailed_annex`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'AR2018 field' AFTER `progress_by_flagships`;

ALTER TABLE `report_synthesis_flagship_progress`
ADD COLUMN `expanded_research_areas`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'AR2018 field' AFTER `detailed_annex`;

ALTER TABLE `report_synthesis_flagship_progress`
ADD COLUMN `dropped_research_lines`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'AR2018 field' AFTER `expanded_research_areas`;

ALTER TABLE `report_synthesis_flagship_progress`
ADD COLUMN `changed_direction`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'AR2018 field' AFTER `dropped_research_lines`;

ALTER TABLE `report_synthesis_flagship_progress`
ADD COLUMN `altmetric_score`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'AR2018 field' AFTER `changed_direction`;