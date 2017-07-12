ALTER TABLE `funding_source_locations`
ADD COLUMN `loc_element_type_id`  bigint(20) NULL AFTER `loc_element_id`;

ALTER TABLE `funding_source_locations` ADD CONSTRAINT `fs_location_elemet_type_fk` FOREIGN KEY (`loc_element_type_id`) REFERENCES `loc_element_types` (`id`);

