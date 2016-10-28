ALTER TABLE `funding_sources`
ADD COLUMN `crp_id`  bigint(20) NULL AFTER `type`;

ALTER TABLE `funding_sources` ADD CONSTRAINT `funding_sources_ibfk_5` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`);