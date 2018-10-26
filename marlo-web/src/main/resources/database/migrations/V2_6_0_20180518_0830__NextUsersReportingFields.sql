ALTER TABLE `project_nextusers`
ADD COLUMN `knowledge_report`  text NULL AFTER `strategies`,
ADD COLUMN `strategies_report`  text NULL AFTER `knowledge_report`;