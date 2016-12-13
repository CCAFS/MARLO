ALTER TABLE `case_study_projects`
ADD COLUMN `creator`  tinyint(1) NOT NULL DEFAULT 1 AFTER `case_study_id`;

