SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for partnerships at project partner level
-- ----------------------------
DROP TABLE IF EXISTS `project_partner_partnerships`;
CREATE TABLE `project_partner_partnerships` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_partner`  bigint(20) NOT NULL ,
`main_area`  text NULL ,
`research_phase`  bigint(20) NULL ,
`geographic_scope`  bigint(20) NULL ,
`region`  bigint(20) NULL ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NOT NULL ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `project_partner_partnerships_ibfk_1` FOREIGN KEY (`project_partner`) REFERENCES `project_partners` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `project_partner_partnerships_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `project_partner_partnerships_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `project_partner_partnerships_ibfk_4` FOREIGN KEY (`research_phase`) REFERENCES `rep_ind_phase_research_partnerships` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `project_partner_partnerships_ibfk_5` FOREIGN KEY (`geographic_scope`) REFERENCES `rep_ind_geographic_scopes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `project_partner_partnerships_ibfk_6` FOREIGN KEY (`region`) REFERENCES `rep_ind_regions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `idx_partnership_id` (`id`) USING BTREE ,
INDEX `idx_partnership_researchPhase` (`research_phase`) USING BTREE ,
INDEX `idx_partnership_geographicScope` (`geographic_scope`) USING BTREE ,
INDEX `idx_partnership_region` (`region`) USING BTREE ,
INDEX `idx_partnership_project_partner` (`project_partner`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1
;

ALTER TABLE `project_partners`
ADD COLUMN `has_partnerships`  tinyint(1) NULL AFTER `responsibilities`;

-- ----------------------------
-- Table structure for partnerships location at project partner level
-- ----------------------------
DROP TABLE IF EXISTS `project_partner_partnership_locations`;
CREATE TABLE `project_partner_partnership_locations` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_partner_partnership`  bigint(20) NOT NULL ,
`location` bigint(20) NOT  NULL ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NOT NULL ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `project_partner_partnerships_location_ibfk_1` FOREIGN KEY (`project_partner_partnership`) REFERENCES `project_partner_partnerships` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `project_partner_partnerships_location_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `project_partner_partnerships_location_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `project_partner_partnerships_location_ibfk_4` FOREIGN KEY (`location`) REFERENCES `loc_elements` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `idx_partnership_location_id` (`id`) USING BTREE ,
INDEX `idx_partnership_location_partnerships_id` (`project_partner_partnership`) USING BTREE ,
INDEX `idx_partnership_location_location` (`location`) USING BTREE
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1
;