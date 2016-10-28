ALTER TABLE `funding_sources`
MODIFY COLUMN `type`  bigint(20) NULL DEFAULT NULL AFTER `center_type`;

ALTER TABLE `funding_sources` ADD CONSTRAINT `funding_sources_ibfk_6` FOREIGN KEY (`type`) REFERENCES `budget_types` (`id`);