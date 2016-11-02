ALTER TABLE `deliverables`
ADD COLUMN `funding_source_id`  bigint(20) NULL AFTER `type_id`;

ALTER TABLE `deliverables` ADD CONSTRAINT `deliverables_ibfk_7` FOREIGN KEY (`funding_source_id`) REFERENCES `funding_sources` (`id`);