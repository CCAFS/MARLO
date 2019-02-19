ALTER TABLE `project_outcome_indicators`
ADD COLUMN `value_reporting`  double(10,2) NULL AFTER `created_by`,
ADD COLUMN `achieved_narrative`  text NULL AFTER `value_reporting`;

