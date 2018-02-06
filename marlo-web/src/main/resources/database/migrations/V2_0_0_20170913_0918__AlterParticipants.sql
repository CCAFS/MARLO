ALTER TABLE `participant` 
ADD COLUMN `other_institution` varchar(2) NULL AFTER `personal_email`,
ADD COLUMN `institutions_suggested` varchar(200) NULL AFTER `other_institution`;