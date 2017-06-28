ALTER TABLE `research_areas`
ADD COLUMN `color`  varchar(8) NULL AFTER `acronym`;

ALTER TABLE `research_impacts`
ADD COLUMN `color`  varchar(8) NULL AFTER `target_year`;

ALTER TABLE `research_topics`
ADD COLUMN `color`  varchar(8) NULL AFTER `research_program_id`;