ALTER TABLE `center_impacts`
ADD COLUMN `subido_id`  bigint(20) NULL AFTER `impact_statement_id`;

ALTER TABLE `center_impacts` ADD CONSTRAINT `research_impacts_subido_id_fk` FOREIGN KEY (`subido_id`) REFERENCES `srf_sub_idos` (`id`);