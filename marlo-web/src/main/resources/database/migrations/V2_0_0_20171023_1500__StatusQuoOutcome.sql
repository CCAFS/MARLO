ALTER TABLE `center_monitoring_outcomes`
DROP COLUMN `narrative`,
ADD COLUMN `status_quo`  text NULL AFTER `year`,
ADD COLUMN `ciat_role`  text NULL AFTER `status_quo`,
ADD COLUMN `what_changed`  text NULL AFTER `ciat_role`;