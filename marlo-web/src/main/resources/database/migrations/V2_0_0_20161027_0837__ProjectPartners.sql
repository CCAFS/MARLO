ALTER TABLE `project_partner_persons`
ADD COLUMN `institution_id`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `project_partner_persons` ADD FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

