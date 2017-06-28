ALTER TABLE `project_funding_sources`
DROP COLUMN `ocs_code`,
DROP COLUMN `donor`,
DROP COLUMN `sync`,
ADD COLUMN `crp_id`  bigint(20) NULL AFTER `project_id`;

ALTER TABLE `project_funding_sources` ADD CONSTRAINT `project_funding_crp_fk` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`);

