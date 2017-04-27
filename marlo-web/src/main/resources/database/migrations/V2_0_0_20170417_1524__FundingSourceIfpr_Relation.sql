update funding_sources set division =null;

ALTER TABLE `funding_sources`
CHANGE COLUMN `division` `division_id`  bigint(20) NULL DEFAULT NULL ;

ALTER TABLE `funding_sources` ADD CONSTRAINT `funding_sources_division_id_fk` FOREIGN KEY (`division_id`) REFERENCES `partner_divisions` (`id`);