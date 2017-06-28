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
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of nextuser_types
-- ----------------------------
INSERT INTO `nextuser_types` VALUES ('1', 'Policy Makers', '2016-11-30 10:16:15', '1', '3', '3', ' ', null);
INSERT INTO `nextuser_types` VALUES ('2', 'General Public/Public Institutions', '2016-11-30 10:16:40', '1', '3', '3', ' ', null);
INSERT INTO `nextuser_types` VALUES ('3', 'Agriculture Industry Actors', '2016-11-30 10:16:47', '1', '3', '3', ' ', null);
INSERT INTO `nextuser_types` VALUES ('4', 'Ministry of Environment', '2016-11-30 10:17:02', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('5', 'Ministry of Agriculture', '2016-11-30 10:17:24', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('6', 'Land-use planners', '2016-11-30 10:18:47', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('7', 'Farmers and land managers', '2017-05-03 14:30:53', '0', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('8', 'local governments', '2016-11-30 10:18:46', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('9', 'national governments', '2016-11-30 10:18:45', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('10', 'Other Government Ministry', '2016-11-30 10:18:46', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('11', 'land managers', '2016-11-30 10:18:44', '1', '3', '3', ' ', '1');
INSERT INTO `nextuser_types` VALUES ('12', 'NGOs', '2016-11-30 10:18:50', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('13', 'Local Community Members', '2017-05-03 14:31:37', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('14', 'Decision makers', '2017-05-03 14:36:09', '0', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('15', 'Development agencies', '2017-05-03 14:32:02', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('16', 'Donors', '2017-05-03 14:32:05', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('17', 'research and development organizations', '2017-05-03 14:36:20', '0', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('18', 'Public Scientific community', '2017-05-03 14:35:39', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('19', 'producers', '2016-11-30 10:26:07', '1', '3', '3', ' ', '3');
INSERT INTO `nextuser_types` VALUES ('20', 'Breeders and farmers', '2016-11-30 10:26:18', '1', '3', '3', ' ', '3');
INSERT INTO `nextuser_types` VALUES ('21', 'SMEs', '2016-11-30 10:26:30', '1', '3', '3', ' ', '3');
INSERT INTO `nextuser_types` VALUES ('22', 'NARs', '2016-11-30 10:26:41', '1', '3', '3', ' ', '3');
INSERT INTO `nextuser_types` VALUES ('23', 'Purchasing companies', '2016-11-30 10:26:55', '1', '3', '3', ' ', '3');
INSERT INTO `nextuser_types` VALUES ('24', 'farmers', '2016-11-30 10:27:10', '1', '3', '3', ' ', '3');
INSERT INTO `nextuser_types` VALUES ('25', 'private sector', '2016-11-30 10:27:16', '1', '3', '3', ' ', '3');
INSERT INTO `nextuser_types` VALUES ('26', 'National Agriculture Research System (NARS)', '2017-05-03 14:32:16', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('27', 'Public Scientific Community', '2017-05-03 14:32:38', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('28', 'Financial institutions (public)', '2017-05-03 14:33:03', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('29', 'Extension Agencies (public)', '2017-05-03 14:33:19', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('30', 'Breeders (public)', '2017-05-03 14:35:33', '1', '3', '3', ' ', '2');
INSERT INTO `nextuser_types` VALUES ('31', 'Research Partners (public)', '2017-05-03 14:35:52', '1', '3', '3', ' ', '2');
