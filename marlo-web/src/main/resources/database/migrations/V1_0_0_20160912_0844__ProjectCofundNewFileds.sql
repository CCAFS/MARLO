START TRANSACTION;
ALTER TABLE `projects_bilateral_cofinancing`
ADD COLUMN `finance_code`  text NULL AFTER `crp_id`,
ADD COLUMN `contact_person_name`  text NULL AFTER `finance_code`,
ADD COLUMN `contact_person_email`  text NULL AFTER `contact_person_name`;
COMMIT;