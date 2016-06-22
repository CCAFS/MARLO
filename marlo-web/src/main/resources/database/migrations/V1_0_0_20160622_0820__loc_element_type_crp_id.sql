ALTER TABLE `loc_element_types`
ADD COLUMN `crp_id`  bigint(20) NULL AFTER `parent_id`;

ALTER TABLE `loc_element_types` ADD CONSTRAINT `fk_crp_id_loc_element_type` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`);