ALTER TABLE `project_crosscuting_themes`
ADD COLUMN `impact_assessment`  tinyint(1) NULL AFTER `big_data`;

UPDATE project_crosscuting_themes set impact_assessment=0;