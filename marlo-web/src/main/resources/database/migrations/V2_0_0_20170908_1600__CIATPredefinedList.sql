SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for center_nextuser_types
-- ----------------------------
DROP TABLE IF EXISTS `center_nextuser_types`;
CREATE TABLE `center_nextuser_types` (
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
  CONSTRAINT `fk_nextusertypes_types` FOREIGN KEY (`parent_type_id`) REFERENCES `center_nextuser_types` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_nextuser_types
-- ----------------------------
INSERT INTO `center_nextuser_types` VALUES ('1', 'Policy Makers', '2016-11-30 10:16:15', '1', '3', '3', ' ', null);
INSERT INTO `center_nextuser_types` VALUES ('2', 'General Public/Public Institutions', '2016-11-30 10:16:40', '1', '3', '3', ' ', null);
INSERT INTO `center_nextuser_types` VALUES ('3', 'Private Industry Actors/Individuals', '2017-05-19 08:50:16', '1', '3', '3', ' ', null);
INSERT INTO `center_nextuser_types` VALUES ('4', 'Ministry of Environment', '2016-11-30 10:17:02', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('5', 'Ministry of Agriculture', '2016-11-30 10:17:24', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('6', 'Land-use planners', '2016-11-30 10:18:47', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('7', 'Farmers and land managers', '2017-05-03 14:30:53', '0', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('8', 'local governments', '2016-11-30 10:18:46', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('9', 'national governments', '2016-11-30 10:18:45', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('10', 'Other Government Ministry', '2016-11-30 10:18:46', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('11', 'Land managers', '2017-09-08 15:13:20', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('12', 'NGOs', '2016-11-30 10:18:50', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('13', 'Local Community Members', '2017-05-03 14:31:37', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('14', 'Decision makers', '2017-05-03 14:36:09', '0', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('15', 'Development agencies', '2017-05-03 14:32:02', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('16', 'Donors', '2017-05-03 14:32:05', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('17', 'research and development organizations', '2017-05-03 14:36:20', '0', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('18', 'Public Scientific community', '2017-05-03 14:35:39', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('19', 'Producers/Farmers', '2017-05-19 08:51:36', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('20', 'Breeders (private)', '2017-05-19 08:51:45', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('21', 'SMEs', '2016-11-30 10:26:30', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('22', 'Product transformation/processing companies', '2017-05-19 08:51:52', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('23', 'Product Commercialization Companies', '2017-05-19 08:52:09', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('24', 'Input suppliers', '2017-05-19 08:52:14', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('25', 'Financial Institutions (private)', '2017-05-19 08:52:17', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('26', 'National Agriculture Research System (NARS)', '2017-05-03 14:32:16', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('27', 'Public Scientific Community', '2017-09-08 15:15:21', '0', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('28', 'Financial institutions (public)', '2017-05-03 14:33:03', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('29', 'Extension Agencies (public)', '2017-05-03 14:33:19', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('30', 'Breeders (public)', '2017-05-03 14:35:33', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('31', 'Research Partners (public)', '2017-05-03 14:35:52', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('32', 'Extension Agencies (private)', '2017-09-08 15:16:13', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('33', 'Research Partners (private)', '2017-09-08 15:16:12', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('34', 'Purchasing Companies', '2017-09-08 15:16:16', '1', '3', '3', ' ', '3');


-- ----------------------------
-- Table structure for center_target_units
-- ----------------------------
DROP TABLE IF EXISTS `center_target_units`;
CREATE TABLE `center_target_units` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_target_units_created_by_users_id` (`created_by`) USING BTREE,
  KEY `fk_target_units_modified_by_users_id` (`modified_by`) USING BTREE,
  CONSTRAINT `center_target_units_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_target_units_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_target_units
-- ----------------------------
INSERT INTO `center_target_units` VALUES ('-1', 'Not Applicable', '0', '3', '2016-09-19 16:35:08', '3', '');
INSERT INTO `center_target_units` VALUES ('1', 'Advanced Research Institutes', '1', '3', '2016-06-29 08:22:36', '843', '');
INSERT INTO `center_target_units` VALUES ('2', 'Agricultural Development Initiatives', '1', '3', '2016-07-05 17:48:58', '843', '');
INSERT INTO `center_target_units` VALUES ('3', 'Agricultural Research Initiatives', '1', '3', '2016-07-12 03:15:20', '843', '');
INSERT INTO `center_target_units` VALUES ('4', 'CGIAR Centers', '1', '3', '2016-07-18 12:41:42', '843', '');
INSERT INTO `center_target_units` VALUES ('5', 'Civil Society Organizations (CSO) ', '1', '3', '2016-07-24 22:08:04', '843', '');
INSERT INTO `center_target_units` VALUES ('6', 'Countries/ States', '1', '3', '2016-07-31 07:34:26', '843', '');
INSERT INTO `center_target_units` VALUES ('7', 'CRPs ', '1', '3', '2016-08-06 17:00:48', '843', '');
INSERT INTO `center_target_units` VALUES ('8', 'Decision Makers', '1', '3', '2016-08-13 02:27:10', '843', '');
INSERT INTO `center_target_units` VALUES ('9', 'Development Organizations', '0', '3', '2016-08-19 11:53:32', '843', '');
INSERT INTO `center_target_units` VALUES ('10', 'Farmer´s Groups', '0', '3', '2016-08-25 21:19:54', '3', 'a');
INSERT INTO `center_target_units` VALUES ('11', 'Farmers ', '1', '3', '2016-09-01 06:46:16', '843', '');
INSERT INTO `center_target_units` VALUES ('12', 'Farming households', '1', '3', '2016-09-07 16:12:38', '843', '');
INSERT INTO `center_target_units` VALUES ('13', 'Fisheries', '1', '3', '2016-09-14 01:39:00', '843', '');
INSERT INTO `center_target_units` VALUES ('14', 'Goverment Agencies', '1', '3', '2016-09-20 11:05:22', '843', '');
INSERT INTO `center_target_units` VALUES ('15', 'Hectares', '1', '3', '2016-09-26 20:31:44', '843', '');
INSERT INTO `center_target_units` VALUES ('16', 'Institutions', '1', '3', '2016-10-03 05:58:06', '843', '');
INSERT INTO `center_target_units` VALUES ('17', 'International Research Organizations', '1', '3', '2016-10-09 15:24:28', '843', '');
INSERT INTO `center_target_units` VALUES ('19', 'Livestock Dependent Communities', '1', '988', '2016-07-19 15:11:33', '843', '');
INSERT INTO `center_target_units` VALUES ('20', 'Market Buyers', '1', '988', '2016-07-22 09:21:54', '843', '');
INSERT INTO `center_target_units` VALUES ('21', 'Market Sellers', '1', '988', '2016-07-22 09:25:31', '843', '');
INSERT INTO `center_target_units` VALUES ('22', 'Markets', '1', '988', '2016-07-22 09:28:30', '843', '');
INSERT INTO `center_target_units` VALUES ('23', 'National Research and Extension Institutes (NAREs)', '1', '988', '2016-07-22 11:23:07', '843', '');
INSERT INTO `center_target_units` VALUES ('24', 'National Research Institutes (NARs)', '1', '988', '2016-07-22 11:23:36', '843', '');
INSERT INTO `center_target_units` VALUES ('25', 'National/State Organizations', '1', '988', '2016-07-22 11:58:38', '843', '');
INSERT INTO `center_target_units` VALUES ('26', 'Non- governamental Organizations (NGOs) ', '1', '988', '2016-07-22 12:03:40', '843', '');
INSERT INTO `center_target_units` VALUES ('27', 'Organizations and Institutions', '1', '988', '2016-07-22 12:04:27', '843', '');
INSERT INTO `center_target_units` VALUES ('28', 'Partners', '1', '988', '2016-07-22 12:21:28', '843', '');
INSERT INTO `center_target_units` VALUES ('29', 'Partnerships ', '1', '988', '2016-07-22 12:22:35', '843', '');
INSERT INTO `center_target_units` VALUES ('30', 'Persons', '1', '988', '2016-07-22 12:23:22', '843', '');
INSERT INTO `center_target_units` VALUES ('31', 'Policy Decisions', '1', '988', '2016-07-25 09:22:41', '843', '');
INSERT INTO `center_target_units` VALUES ('32', 'Policy Makers', '1', '988', '2016-07-25 09:24:10', '843', '');
INSERT INTO `center_target_units` VALUES ('33', 'Private Sector Organizations', '1', '988', '2016-07-25 09:26:31', '843', '');
INSERT INTO `center_target_units` VALUES ('35', 'Purchasing Companies', '1', '13', '2016-08-01 11:27:57', '843', '');
INSERT INTO `center_target_units` VALUES ('37', 'Recommendations Implemented', '1', '1061', '2016-08-03 12:32:23', '843', '');
INSERT INTO `center_target_units` VALUES ('38', 'Regional Bodies', '1', '1061', '2016-08-03 12:35:27', '843', '');
INSERT INTO `center_target_units` VALUES ('39', 'Researchers', '1', '843', '2016-08-25 07:52:47', '843', '');
INSERT INTO `center_target_units` VALUES ('40', 'Scientific Community', '0', '843', '2016-08-25 08:21:33', '843', '');
INSERT INTO `center_target_units` VALUES ('41', 'Small and Medium Enterprises (SMEs) ', '1', '843', '2016-08-25 08:23:54', '843', '');
INSERT INTO `center_target_units` VALUES ('42', 'Stakeholders', '1', '3', '2016-11-24 14:43:53', '3', '');
INSERT INTO `center_target_units` VALUES ('44', 'Subnational Public/Private Initiatives', '1', '3', '2016-11-25 13:30:44', '3', '');
INSERT INTO `center_target_units` VALUES ('45', 'Technology Developers', '1', '3', '2016-11-25 13:32:29', '3', '');
INSERT INTO `center_target_units` VALUES ('46', 'USD mio. new investment', '1', '3', '2017-05-03 14:41:13', '3', ' ');
INSERT INTO `center_target_units` VALUES ('47', 'Value Chain Actors', '1', '3', '2017-05-03 14:41:30', '3', ' ');
INSERT INTO `center_target_units` VALUES ('48', 'Wholesale companies', '1', '3', '2017-05-03 14:41:39', '3', ' ');
INSERT INTO `center_target_units` VALUES ('49', 'Women Farmer´s Groups', '1', '3', '2017-05-03 14:41:51', '3', ' ');
INSERT INTO `center_target_units` VALUES ('50', 'Other', '1', '3', '2017-05-03 14:41:58', '3', ' ');
INSERT INTO `center_target_units` VALUES ('51', 'Breeders', '1', '3', '2017-09-08 15:19:36', '3', ' ');
INSERT INTO `center_target_units` VALUES ('52', 'Development Agencies', '1', '3', '2017-09-08 15:19:45', '3', ' ');
INSERT INTO `center_target_units` VALUES ('53', 'Donors', '1', '3', '2017-09-08 15:19:55', '3', ' ');
INSERT INTO `center_target_units` VALUES ('54', 'Extension Agencies (private)', '1', '3', '2017-09-08 15:20:04', '3', ' ');
INSERT INTO `center_target_units` VALUES ('55', 'Extension Agencies (public)', '1', '3', '2017-09-08 15:20:12', '3', ' ');
INSERT INTO `center_target_units` VALUES ('56', 'Financial institutions (public)', '1', '3', '2017-09-08 15:20:25', '3', ' ');
INSERT INTO `center_target_units` VALUES ('57', 'Financial Institutions (private)', '1', '3', '2017-09-08 15:20:45', '3', ' ');
INSERT INTO `center_target_units` VALUES ('58', 'Input suppliers', '1', '3', '2017-09-08 15:20:55', '3', ' ');
INSERT INTO `center_target_units` VALUES ('59', 'Land managers', '1', '3', '2017-09-08 15:21:06', '3', ' ');
INSERT INTO `center_target_units` VALUES ('60', 'Land-use planners', '1', '3', '2017-09-08 15:21:14', '3', ' ');
INSERT INTO `center_target_units` VALUES ('61', 'Local Community Members', '1', '3', '2017-09-08 15:21:24', '3', ' ');
INSERT INTO `center_target_units` VALUES ('62', 'Local governments', '1', '3', '2017-09-08 15:21:32', '3', ' ');
INSERT INTO `center_target_units` VALUES ('63', 'Ministry of Environment', '1', '3', '2017-09-08 15:21:39', '3', ' ');
INSERT INTO `center_target_units` VALUES ('64', 'Ministry of Agriculture', '1', '3', '2017-09-08 15:21:47', '3', ' ');
INSERT INTO `center_target_units` VALUES ('65', 'Market Buyers', '1', '3', '2017-09-08 15:21:55', '3', ' ');
INSERT INTO `center_target_units` VALUES ('66', 'National Governments', '1', '3', '2017-09-08 15:22:02', '3', ' ');
INSERT INTO `center_target_units` VALUES ('67', 'Non-governmental Organizations (NGOs) ', '1', '3', '2017-09-08 15:22:19', '3', ' ');
INSERT INTO `center_target_units` VALUES ('68', 'Other Government Ministry', '1', '3', '2017-09-08 15:22:27', '3', ' ');
INSERT INTO `center_target_units` VALUES ('69', 'Producers', '1', '3', '2017-09-08 15:22:34', '3', ' ');
INSERT INTO `center_target_units` VALUES ('70', 'Product transformation/processing companies', '1', '3', '2017-09-08 15:22:41', '3', ' ');
INSERT INTO `center_target_units` VALUES ('71', 'Product Commercialization Companies', '1', '3', '2017-09-08 15:22:56', '3', ' ');
INSERT INTO `center_target_units` VALUES ('72', 'Public Scientific Community', '1', '3', '2017-09-08 15:23:03', '3', ' ');
INSERT INTO `center_target_units` VALUES ('73', 'Purchasing Companies', '1', '3', '2017-09-08 15:23:10', '3', ' ');
INSERT INTO `center_target_units` VALUES ('74', 'Researchers', '1', '3', '2017-09-08 15:23:18', '3', ' ');
INSERT INTO `center_target_units` VALUES ('75', 'Research Partners (private)', '1', '3', '2017-09-08 15:23:25', '3', ' ');
INSERT INTO `center_target_units` VALUES ('76', 'Research Partners (public)', '1', '3', '2017-09-08 15:23:37', '3', ' ');
INSERT INTO `center_target_units` VALUES ('77', 'Value Chain Actors', '1', '3', '2017-09-08 15:23:50', '3', ' ');
INSERT INTO `center_target_units` VALUES ('78', 'Wholesale companies', '1', '3', '2017-09-08 15:23:58', '3', ' ');
INSERT INTO `center_target_units` VALUES ('79', 'Women Farmer´s Groups', '1', '3', '2017-09-08 15:24:06', '3', ' ');