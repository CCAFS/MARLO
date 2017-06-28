SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for nextuser_types
-- ----------------------------
DROP TABLE IF EXISTS `nextuser_types`;
CREATE TABLE `nextuser_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  `parent_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_nextusertypes_created_by` (`created_by`),
  KEY `fk_nextusertypes_modified_by` (`modified_by`),
  KEY `fk_nextusertypes_types` (`parent_type_id`),
  CONSTRAINT `fk_nextusertypes_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_nextusertypes_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_nextusertypes_types` FOREIGN KEY (`parent_type_id`) REFERENCES `nextuser_types` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of nextuser_types
-- ----------------------------
INSERT INTO `nextuser_types` VALUES ('1', 'Policy Makers', '2016-11-30 10:16:15', '1', '3', '3', ' ', null);
INSERT INTO `nextuser_types` VALUES ('2', 'General Public/Public Institutions', '2016-11-30 10:16:40', '1', '3', '3', ' ', null);
INSERT INTO `nextuser_types` VALUES ('3', 'Agriculture Industry Actors', '2016-11-30 10:16:47', '1', '3', '3', ' ', null);
INSERT INTO `nextuser_types` VALUES ('4', 'Ministry of Environment', '2016-11-30 10:17:02', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('5', 'Ministry of Agriculture', '2016-11-30 10:17:24', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('6', 'Land-use planners', '2016-11-30 10:18:47', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('7', 'Farmers and land managers', '2016-11-30 10:18:46', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('8', 'local governments', '2016-11-30 10:18:46', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('9', 'national governments', '2016-11-30 10:18:45', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('10', 'Other Government Ministry', '2016-11-30 10:18:46', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('11', 'land managers', '2016-11-30 10:18:44', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('12', 'NGOs', '2016-11-30 10:18:50', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('13', 'Community Members', '2016-11-30 10:24:57', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('14', 'decision makers', '2016-11-30 10:25:10', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('15', 'development agencies', '2016-11-30 10:25:21', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('16', 'donors', '2016-11-30 10:25:32', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('17', 'research and development organizations', '2016-11-30 10:25:43', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('18', 'Scientific community', '2016-11-30 10:25:54', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('19', 'producers', '2016-11-30 10:26:07', '1', '3', '3', ' ', '3');
INSERT INTO `nextuser_types` VALUES ('20', 'Breeders and farmers', '2016-11-30 10:26:18', '1', '3', '3', ' ', '3');
INSERT INTO `nextuser_types` VALUES ('21', 'SMEs', '2016-11-30 10:26:30', '1', '3', '3', ' ', '3');
INSERT INTO `nextuser_types` VALUES ('22', 'NARs', '2016-11-30 10:26:41', '1', '3', '3', ' ', '3');
INSERT INTO `nextuser_types` VALUES ('23', 'Purchasing companies', '2016-11-30 10:26:55', '1', '3', '3', ' ', '3');
INSERT INTO `nextuser_types` VALUES ('24', 'farmers', '2016-11-30 10:27:10', '1', '3', '3', ' ', '3');
INSERT INTO `nextuser_types` VALUES ('25', 'private sector', '2016-11-30 10:27:16', '1', '3', '3', ' ', '3');
