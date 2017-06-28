SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for target_units
-- ----------------------------
DROP TABLE IF EXISTS `target_units`;
CREATE TABLE `target_units` (
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
  CONSTRAINT `target_units_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `target_units_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of target_units
-- ----------------------------
INSERT INTO `target_units` VALUES ('-1', 'Not Applicable', '0', '3', '2016-09-19 16:35:08', '3', '');
INSERT INTO `target_units` VALUES ('1', 'Advanced Research Institutes', '1', '3', '2016-06-29 08:22:36', '843', '');
INSERT INTO `target_units` VALUES ('2', 'Agricultural Development Initiatives', '1', '3', '2016-07-05 17:48:58', '843', '');
INSERT INTO `target_units` VALUES ('3', 'Agricultural Research Initiatives', '1', '3', '2016-07-12 03:15:20', '843', '');
INSERT INTO `target_units` VALUES ('4', 'CGIAR Centers', '1', '3', '2016-07-18 12:41:42', '843', '');
INSERT INTO `target_units` VALUES ('5', 'Civil Society Organizations (CSO) ', '1', '3', '2016-07-24 22:08:04', '843', '');
INSERT INTO `target_units` VALUES ('6', 'Countries/ States', '1', '3', '2016-07-31 07:34:26', '843', '');
INSERT INTO `target_units` VALUES ('7', 'CRPs ', '1', '3', '2016-08-06 17:00:48', '843', '');
INSERT INTO `target_units` VALUES ('8', 'Decision Makers', '1', '3', '2016-08-13 02:27:10', '843', '');
INSERT INTO `target_units` VALUES ('9', 'Development Organizations', '1', '3', '2016-08-19 11:53:32', '843', '');
INSERT INTO `target_units` VALUES ('10', 'Farmer´s Groups', '0', '3', '2016-08-25 21:19:54', '3', 'a');
INSERT INTO `target_units` VALUES ('11', 'Farmers ', '1', '3', '2016-09-01 06:46:16', '843', '');
INSERT INTO `target_units` VALUES ('12', 'Farming households', '1', '3', '2016-09-07 16:12:38', '843', '');
INSERT INTO `target_units` VALUES ('13', 'Fisheries', '1', '3', '2016-09-14 01:39:00', '843', '');
INSERT INTO `target_units` VALUES ('14', 'Goverment Agencies', '1', '3', '2016-09-20 11:05:22', '843', '');
INSERT INTO `target_units` VALUES ('15', 'Hectares', '1', '3', '2016-09-26 20:31:44', '843', '');
INSERT INTO `target_units` VALUES ('16', 'Institutions', '1', '3', '2016-10-03 05:58:06', '843', '');
INSERT INTO `target_units` VALUES ('17', 'International Research Organizations', '1', '3', '2016-10-09 15:24:28', '843', '');
INSERT INTO `target_units` VALUES ('19', 'Livestock Dependent Communities', '1', '988', '2016-07-19 15:11:33', '843', '');
INSERT INTO `target_units` VALUES ('20', 'Market Buyers', '1', '988', '2016-07-22 09:21:54', '843', '');
INSERT INTO `target_units` VALUES ('21', 'Market Sellers', '1', '988', '2016-07-22 09:25:31', '843', '');
INSERT INTO `target_units` VALUES ('22', 'Markets', '1', '988', '2016-07-22 09:28:30', '843', '');
INSERT INTO `target_units` VALUES ('23', 'National Research and Extension Institutes (NAREs)', '1', '988', '2016-07-22 11:23:07', '843', '');
INSERT INTO `target_units` VALUES ('24', 'National Research Institutes (NARs)', '1', '988', '2016-07-22 11:23:36', '843', '');
INSERT INTO `target_units` VALUES ('25', 'National/State Organizations', '1', '988', '2016-07-22 11:58:38', '843', '');
INSERT INTO `target_units` VALUES ('26', 'Non- governamental Organizations (NGOs) ', '1', '988', '2016-07-22 12:03:40', '843', '');
INSERT INTO `target_units` VALUES ('27', 'Organizations and Institutions', '1', '988', '2016-07-22 12:04:27', '843', '');
INSERT INTO `target_units` VALUES ('28', 'Partners', '1', '988', '2016-07-22 12:21:28', '843', '');
INSERT INTO `target_units` VALUES ('29', 'Partnerships ', '1', '988', '2016-07-22 12:22:35', '843', '');
INSERT INTO `target_units` VALUES ('30', 'Persons', '1', '988', '2016-07-22 12:23:22', '843', '');
INSERT INTO `target_units` VALUES ('31', 'Policy Decisions', '1', '988', '2016-07-25 09:22:41', '843', '');
INSERT INTO `target_units` VALUES ('32', 'Policy Makers', '1', '988', '2016-07-25 09:24:10', '843', '');
INSERT INTO `target_units` VALUES ('33', 'Private Sector Organizations', '1', '988', '2016-07-25 09:26:31', '843', '');
INSERT INTO `target_units` VALUES ('35', 'Purchasing Companies', '1', '13', '2016-08-01 11:27:57', '843', '');
INSERT INTO `target_units` VALUES ('37', 'Recommendations Implemented', '1', '1061', '2016-08-03 12:32:23', '843', '');
INSERT INTO `target_units` VALUES ('38', 'Regional Bodies', '1', '1061', '2016-08-03 12:35:27', '843', '');
INSERT INTO `target_units` VALUES ('39', 'Researchers', '1', '843', '2016-08-25 07:52:47', '843', '');
INSERT INTO `target_units` VALUES ('40', 'Scientific Community', '1', '843', '2016-08-25 08:21:33', '843', '');
INSERT INTO `target_units` VALUES ('41', 'Small and Medium Enterprises (SMEs) ', '1', '843', '2016-08-25 08:23:54', '843', '');
INSERT INTO `target_units` VALUES ('42', 'Stakeholders', '1', '3', '2016-11-24 14:43:53', '3', '');
INSERT INTO `target_units` VALUES ('44', 'Subnational Public/Private Initiatives', '1', '3', '2016-11-25 13:30:44', '3', '');
INSERT INTO `target_units` VALUES ('45', 'Technology Developers', '1', '3', '2016-11-25 13:32:29', '3', '');
INSERT INTO `target_units` VALUES ('46', 'USD mio. new investment', '1', '3', '2017-05-03 14:41:13', '3', ' ');
INSERT INTO `target_units` VALUES ('47', 'Value Chain Actors', '1', '3', '2017-05-03 14:41:30', '3', ' ');
INSERT INTO `target_units` VALUES ('48', 'Wholesale companies', '1', '3', '2017-05-03 14:41:39', '3', ' ');
INSERT INTO `target_units` VALUES ('49', 'Women Farmer´s Groups', '1', '3', '2017-05-03 14:41:51', '3', ' ');
INSERT INTO `target_units` VALUES ('50', 'Other', '1', '3', '2017-05-03 14:41:58', '3', ' ');
