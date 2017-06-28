ALTER TABLE `research_outcomes`
ADD COLUMN `baseline`  numeric(25,0) NULL AFTER `research_topic_id`,
ADD COLUMN `is_impact_pathway`  tinyint(1) NULL AFTER `baseline`;

UPDATE `research_outcomes`
set is_impact_pathway = 1;