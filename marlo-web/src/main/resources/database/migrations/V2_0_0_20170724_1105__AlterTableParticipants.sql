ALTER TABLE `participant` 
ADD COLUMN `middle_name` varchar(200) NULL AFTER `modification_justification`,
ADD COLUMN `personal_email` varchar(200) NULL AFTER `middle_name`;