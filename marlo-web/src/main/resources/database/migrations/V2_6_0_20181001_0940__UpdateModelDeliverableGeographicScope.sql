SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `deliverables_info`
ADD COLUMN `geographic_scope_id`  bigint(20) NULL DEFAULT NULL AFTER `is_active`,
ADD COLUMN `region_id`  bigint(20) NULL DEFAULT NULL AFTER `geographic_scope_id`,
ADD INDEX `rep_ind_geographic_scope` (`geographic_scope_id`) USING BTREE ,
ADD INDEX `rep_ind_region` (`region_id`) USING BTREE ;

ALTER TABLE `deliverables_info` ADD CONSTRAINT `deliverables_info_ibfk_7` 
FOREIGN KEY (`geographic_scope_id`) 
REFERENCES `rep_ind_geographic_scopes` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `deliverables_info` ADD CONSTRAINT `deliverables_info_ibfk_8` 
FOREIGN KEY (`region_id`) 
REFERENCES `rep_ind_regions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Table structure for deliverable locations
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_locations`;
CREATE TABLE `deliverable_locations` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NOT NULL ,
`loc_element_id` bigint(20) NOT  NULL ,
`id_phase`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `deliverable_locations_ibfk_1` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `deliverable_locations_ibfk_2` FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `deliverable_locations_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `idx_deliverable_locations_id` (`id`) USING BTREE ,
INDEX `idx_deliverable_locations_deliverable_id` (`deliverable_id`) USING BTREE ,
INDEX `idx_deliverable_locations_id_phase` (`id_phase`) USING BTREE ,
INDEX `idx_deliverable_locations_loc_element_id` (`loc_element_id`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;