ALTER TABLE `research_impacts`
ADD COLUMN `short_name`  varchar(15) NULL AFTER `description`;

ALTER TABLE `research_topics`
ADD COLUMN `short_name`  varchar(15) NULL AFTER `research_topic`;

ALTER TABLE `research_outcomes`
ADD COLUMN `short_name`  varchar(15) NULL AFTER `description`;

ALTER TABLE `research_outputs`
ADD COLUMN `short_name`  varchar(15) NULL AFTER `title`;

