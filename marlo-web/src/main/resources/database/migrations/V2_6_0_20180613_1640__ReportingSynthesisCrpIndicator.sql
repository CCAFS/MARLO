SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rep_ind_synthesis_indicators
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_synthesis_indicators`;
CREATE TABLE `rep_ind_synthesis_indicators` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `indicator` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `is_marlo` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--------------------------------------------------------
-- ----------------------------
-- Records of rep_ind_synthesis_indicators
-- ----------------------------
INSERT INTO `rep_ind_synthesis_indicators` VALUES ('1', 'I1/I2','Projected uptake (women and men) /hectares from current CRP investments (for innovations at user-ready or scaling stage only – see indicator C1)','','Influence',0);
INSERT INTO `rep_ind_synthesis_indicators` VALUES ('2', 'I3','Number of policies/investments (etc) modified in 2017, informed by CGIAR research','','Influence',1);
INSERT INTO `rep_ind_synthesis_indicators` VALUES ('3', 'C1','Number of innovations by phase - new in 2017','','Control',1);
INSERT INTO `rep_ind_synthesis_indicators` VALUES ('4', 'C2', 'Number of formal partnerships in 2017, by purpose (ongoing + new)','','Control',1);
INSERT INTO `rep_ind_synthesis_indicators` VALUES ('5', 'C3','Participants in CGIAR activities 2017','(new +ongoing)','Control',1);
INSERT INTO `rep_ind_synthesis_indicators` VALUES ('6', 'C4','People trained in 2017','','Control',1);
INSERT INTO `rep_ind_synthesis_indicators` VALUES ('7', 'C5','Number of peer-reviewed publications','','Control',1);
INSERT INTO `rep_ind_synthesis_indicators` VALUES ('8', 'C6','Altmetrics','','Control',1);

-- ----------------------------
-- Table structure for report_synthesis_indicator_generals
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_indicator_generals`;
CREATE TABLE `report_synthesis_indicator_generals` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_indicator_generals_ibfk_1` FOREIGN KEY (`id`) REFERENCES `report_synthesis` (`id`),
  CONSTRAINT `report_synthesis_indicator_generals_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_indicator_generals_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for report_synthesis_indicators
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_indicators`;
CREATE TABLE `report_synthesis_indicators` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_indicator_general_id` bigint(20) NOT NULL,
  `rep_ind_synthesis_indicator_id` bigint(20) NOT NULL,
  `data` text,
  `comment` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  KEY `rep_ind_synthesis_indicator_id` (`rep_ind_synthesis_indicator_id`),
  KEY `report_synthesis_indicator_general_id` (`report_synthesis_indicator_general_id`),
  CONSTRAINT `report_synthesis_indicators_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_indicators_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_indicators_ibfk_3` FOREIGN KEY (`report_synthesis_indicator_general_id`) REFERENCES `report_synthesis_indicator_generals` (`id`),
  CONSTRAINT `report_synthesis_indicators_ibfk_4` FOREIGN KEY (`rep_ind_synthesis_indicator_id`) REFERENCES `rep_ind_synthesis_indicators` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table Permissions for Influence Indicator
-- ----------------------------

INSERT INTO permissions (permission, description, type)
VALUES ('crp:{0}:reportSynthesis:{1}:influence', 'Can edit in Annual Report Synthesis Influence Indicator', 1);
--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'FPL','FPM','PMU')
AND
p.permission = 'crp:{0}:reportSynthesis:{1}:influence';

-- ----------------------------
-- Table Permissions for Control Indicator
-- ----------------------------

INSERT INTO permissions (permission, description, type)
VALUES ('crp:{0}:reportSynthesis:{1}:control', 'Can edit in Annual Report Synthesis Control Indicator', 1);
--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'FPL','FPM','PMU')
AND
p.permission = 'crp:{0}:reportSynthesis:{1}:control';
