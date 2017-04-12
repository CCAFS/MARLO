UPDATE deliverables set division = null;
ALTER TABLE `deliverables`
CHANGE COLUMN `division` `division_id`  bigint(20) NULL DEFAULT NULL AFTER `crp_id`;
ALTER TABLE `deliverables` ADD CONSTRAINT `deliverables_division_fk` FOREIGN KEY (`division_id`) REFERENCES `partner_divisions` (`id`);