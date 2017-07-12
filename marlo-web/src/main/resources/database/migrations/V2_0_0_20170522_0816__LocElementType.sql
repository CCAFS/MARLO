ALTER TABLE `project_locations`
MODIFY COLUMN `loc_element_id`  bigint(20) NULL AFTER `project_id`,
ADD COLUMN `loc_element_type_id`  bigint(20) NULL AFTER `project_id`;

