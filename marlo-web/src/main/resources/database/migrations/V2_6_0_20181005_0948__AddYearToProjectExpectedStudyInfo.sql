/* Add year to study info */
ALTER TABLE `project_expected_study_info`
ADD COLUMN `year`  int(11) NULL DEFAULT NULL AFTER `study_type_id`;

/* Migrate year from study to study info */
UPDATE project_expected_study_info si
INNER JOIN project_expected_studies s ON s.id = si.project_expected_study_id
SET si.`year` = s.`year`;