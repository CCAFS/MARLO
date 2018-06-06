SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_partner_partnership_research_phases
-- ----------------------------
DROP TABLE IF EXISTS `project_partner_partnership_research_phases`;
CREATE TABLE `project_partner_partnership_research_phases` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_partner_partnership_id` bigint(20) NOT NULL,
  `rep_ind_phase_research_partnership_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_partnership_research_phases_id`(`id`) USING BTREE,
  INDEX `idx_partnership_research_phases_partnership_id`(`project_partner_partnership_id`) USING BTREE,
  INDEX `idx_partnership_research_phases_phase_research_partnership_id`(`rep_ind_phase_research_partnership_id`) USING BTREE,
  INDEX `idx_partnership_research_phases_created_by`(`created_by`) USING BTREE,
  INDEX `idx_partnership_research_phases_modified_by`(`modified_by`) USING BTREE,
  CONSTRAINT `project_partner_partnership_research_phases_ibfk_1` FOREIGN KEY (`project_partner_partnership_id`) REFERENCES `project_partner_partnerships` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `project_partner_partnership_research_phases_ibfk_2` FOREIGN KEY (`rep_ind_phase_research_partnership_id`) REFERENCES `rep_ind_phase_research_partnerships` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `project_partner_partnership_research_phases_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `project_partner_partnership_research_phases_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Migrate research phases from project_partner_partnerships -> project_partner_partnership_research_phases


-- ----------------------------

INSERT INTO project_partner_partnership_research_phases 
(project_partner_partnership_id,rep_ind_phase_research_partnership_id,is_active,active_since,created_by,modified_by,modification_justification)
SELECT
  ppp.id as "project_partner_partnership_id",
  ppp.research_phase as "rep_ind_phase_research_partnership_id",
  ppp.is_active,
  ppp.active_since,
  ppp.created_by,
  ppp.modified_by,
  "Migrated in MARLO to set many to one relation on research phases partnerships (20180522)" as "modification_justification"
FROM
  project_partner_partnerships ppp
WHERE
  ppp.research_phase IS NOT NULL;

-- ----------------------------
-- Change table structure for project_partner_partnerships
-- ----------------------------

ALTER TABLE `project_partner_partnerships` DROP FOREIGN KEY `project_partner_partnerships_ibfk_4`;
ALTER TABLE `project_partner_partnerships` DROP FOREIGN KEY `project_partner_partnerships_ibfk_5`;
ALTER TABLE `project_partner_partnerships` DROP FOREIGN KEY `project_partner_partnerships_ibfk_6`;

ALTER TABLE `project_partner_partnerships`
DROP COLUMN `research_phase`,
DROP INDEX `idx_partnership_researchPhase`;

ALTER TABLE `project_partner_partnerships` ADD CONSTRAINT `project_partner_partnerships_ibfk_4` FOREIGN KEY (`geographic_scope`) REFERENCES `rep_ind_geographic_scopes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `project_partner_partnerships` ADD CONSTRAINT `project_partner_partnerships_ibfk_5` FOREIGN KEY (`region`) REFERENCES `rep_ind_regions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;


