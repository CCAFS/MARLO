ALTER TABLE `research_impacts`
MODIFY COLUMN `short_name`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `description`;

ALTER TABLE `research_topics`
MODIFY COLUMN `short_name`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `research_topic`;

ALTER TABLE `research_outcomes`
MODIFY COLUMN `short_name`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `description`;

ALTER TABLE `research_outputs`
MODIFY COLUMN `short_name`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `title`;

