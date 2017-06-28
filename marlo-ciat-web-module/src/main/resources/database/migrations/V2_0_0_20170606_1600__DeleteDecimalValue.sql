ALTER TABLE `research_outcomes`
MODIFY COLUMN `value`  decimal(10,0) NULL DEFAULT NULL AFTER `year`,
MODIFY COLUMN `baseline`  decimal(10,0) NULL DEFAULT NULL AFTER `research_topic_id`;

ALTER TABLE `research_milestones`
MODIFY COLUMN `value`  decimal(10,0) NULL DEFAULT NULL AFTER `target_year`;

