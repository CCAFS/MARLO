ALTER TABLE `deliverable_partnerships`
CHANGE COLUMN `division` `division_id`  bigint(20) NULL DEFAULT NULL AFTER `modification_justification`;

ALTER TABLE `deliverable_partnerships` ADD CONSTRAINT `deliverable_division_id_fk` FOREIGN KEY (`division_id`) REFERENCES `partner_divisions` (`id`);