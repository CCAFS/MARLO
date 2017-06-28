ALTER TABLE `research_outcomes`
MODIFY COLUMN `baseline`  decimal(10,2) NULL DEFAULT NULL AFTER `research_topic_id`;
