ALTER TABLE `projects`
ADD COLUMN `is_global`  tinyint(1) NOT NULL AFTER `contact_person_id`;