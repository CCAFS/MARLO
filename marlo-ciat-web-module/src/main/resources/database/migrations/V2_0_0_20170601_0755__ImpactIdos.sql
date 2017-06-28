ALTER TABLE `research_impacts`
ADD COLUMN `ido_id`  bigint(20) NULL AFTER `short_name`;
ALTER TABLE `research_impacts` ADD CONSTRAINT `research_impacts_ido_id_fk` FOREIGN KEY (`ido_id`) REFERENCES `srf_idos` (`id`);
