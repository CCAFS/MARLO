ALTER TABLE `report_synthesis_melia_evaluations`
ADD COLUMN `actions`  text NULL AFTER `status`,
ADD COLUMN `comments`  text NULL AFTER `text_when`;