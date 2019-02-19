ALTER TABLE `rep_ind_type_activities` 
ADD COLUMN `is_formal` tinyint(1) NOT NULL DEFAULT 0 AFTER `definition`;

UPDATE `rep_ind_type_activities` SET `is_formal` = 1 WHERE `id` = 1;
UPDATE `rep_ind_type_activities` SET `is_formal` = 1 WHERE `id` = 2;
UPDATE `rep_ind_type_activities` SET `is_formal` = 1 WHERE `id` = 3;
UPDATE `rep_ind_type_activities` SET `is_formal` = 1 WHERE `id` = 4;
