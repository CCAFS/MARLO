SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_efficiencies
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_efficiencies`;
CREATE TABLE `report_synthesis_efficiencies` (
  `id` bigint(20) NOT NULL,
  `description` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_efficiencies_ibfk_1` FOREIGN KEY (`id`) REFERENCES `report_synthesis` (`id`),
  CONSTRAINT `report_synthesis_efficiencies_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_efficiencies_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table Permissions for Efficiency
-- ----------------------------

INSERT INTO permissions (permission, description, type)
VALUES ('crp:{0}:reportSynthesis:{1}:efficiency', 'Can edit in Annual Report Synthesis Efficiency', 1);
--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'FPL','FPM','PMU')
AND
p.permission = 'crp:{0}:reportSynthesis:{1}:efficiency';