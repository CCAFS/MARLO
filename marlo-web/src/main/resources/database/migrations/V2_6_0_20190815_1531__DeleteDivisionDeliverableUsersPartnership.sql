ALTER TABLE `project_partner_persons`
ADD COLUMN `partner_division_id`  bigint(20) NULL AFTER `contact_type`;

ALTER TABLE `project_partner_persons` ADD FOREIGN KEY (`partner_division_id`) REFERENCES `partner_divisions` (`id`);