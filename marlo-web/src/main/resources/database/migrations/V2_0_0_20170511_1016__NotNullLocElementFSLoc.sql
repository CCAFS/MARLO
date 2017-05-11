ALTER TABLE `funding_source_locations`
MODIFY COLUMN `loc_element_id`  bigint(20) NULL AFTER `funding_source_id`;