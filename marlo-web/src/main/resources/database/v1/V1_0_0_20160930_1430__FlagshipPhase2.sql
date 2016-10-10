START TRANSACTION;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for crp_programs
-- ----------------------------
DROP TABLE IF EXISTS `crp_programs`;
CREATE TABLE `crp_programs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `acronym` varchar(50) NOT NULL,
  `crp_id` bigint(20) NOT NULL,
  `program_type` int(11) NOT NULL,
  `color` varchar(8) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `crp_programs_ibfk_1` (`crp_id`) USING BTREE,
  KEY `fk_crp_programs_created_by_users_id` (`created_by`) USING BTREE,
  KEY `fk_crp_programs_modified_by_users_id` (`modified_by`) USING BTREE,
  CONSTRAINT `crp_programs_ibfk_1` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`),
  CONSTRAINT `crp_programs_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `crp_programs_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crp_programs
-- ----------------------------
INSERT INTO `crp_programs` VALUES ('84', 'Climate services and safety nets', 'F4', '1', '1', '#8139B6', '1', '843', '2016-09-30 13:49:15', '844', '');
INSERT INTO `crp_programs` VALUES ('85', 'Climate-Smart Technologies and Practices', 'F2', '1', '1', '#f39c12', '1', '843', '2016-09-08 08:18:56', '1', '');
INSERT INTO `crp_programs` VALUES ('86', 'Low emissions development', 'F3', '1', '1', '#71CE48', '1', '843', '2016-09-08 08:18:51', '1', '');
INSERT INTO `crp_programs` VALUES ('87', 'Priorities and Policies for CSA', 'F1', '1', '1', '#4B91D7', '1', '843', '2016-09-08 08:19:01', '1', '');
INSERT INTO `crp_programs` VALUES ('88', 'Latin America', 'LAM', '1', '2', '#5F688B', '1', '843', '2016-07-11 10:30:34', '843', '');
INSERT INTO `crp_programs` VALUES ('89', 'East Africa', 'EA', '1', '2', '#6267B0', '1', '843', '2016-07-11 10:30:34', '843', '');
INSERT INTO `crp_programs` VALUES ('90', 'West Africa', 'WA', '1', '2', '#0ECF3B', '1', '843', '2016-07-11 10:33:34', '843', '');
INSERT INTO `crp_programs` VALUES ('91', 'Southeast Asia', 'SEA', '1', '2', '#F54423', '1', '843', '2016-07-11 10:33:34', '843', '');
INSERT INTO `crp_programs` VALUES ('92', 'South Asia', 'SAs', '1', '2', '#956611', '1', '843', '2016-07-11 10:33:34', '843', '');
INSERT INTO `crp_programs` VALUES ('93', 'Food Systems for Healthier Diets', 'F1', '5', '1', '', '1', '988', '2016-07-22 12:12:26', '988', '');
INSERT INTO `crp_programs` VALUES ('94', 'Biofortification', 'F2', '5', '1', '', '1', '988', '2016-07-22 12:50:19', '988', '');
INSERT INTO `crp_programs` VALUES ('95', 'Food Safety', 'F3', '5', '1', '', '1', '988', '2016-07-22 09:00:32', '988', '');
INSERT INTO `crp_programs` VALUES ('96', 'Supporting Policies, Programs and Enabling Action through Research', 'F4', '5', '1', '', '1', '988', '2016-07-22 09:00:32', '988', '');
INSERT INTO `crp_programs` VALUES ('97', 'Improving Human Health', 'F5', '5', '1', '', '1', '988', '2016-07-25 09:58:51', '988', '');
INSERT INTO `crp_programs` VALUES ('98', 'Technological Innovation and Sustainable Intensification', 'F1', '3', '1', '#f39c12', '1', '1061', '2016-08-03 13:14:14', '1061', '');
INSERT INTO `crp_programs` VALUES ('99', 'Economywide Factors Affecting Agricultural Growth and Rural Transformation', 'F2', '3', '1', '#D45890', '1', '1061', '2016-08-03 10:49:18', '1061', '');
INSERT INTO `crp_programs` VALUES ('100', 'Inclusive and Efficient Value Chains', 'F3', '3', '1', '#8e44ad', '1', '1061', '2016-08-03 10:49:18', '1061', '');
INSERT INTO `crp_programs` VALUES ('101', 'Social Protection for Agriculture and Resilience', 'F4', '3', '1', '#d35400', '1', '1061', '2016-08-03 10:49:18', '1061', '');
INSERT INTO `crp_programs` VALUES ('102', 'Livestock Genetics', 'F1', '7', '1', '#96C92C', '1', '1087', '2016-08-10 08:54:37', '1087', '');
INSERT INTO `crp_programs` VALUES ('103', 'Livestock Health', 'F2', '7', '1', '#F55858', '1', '926', '2016-08-10 09:13:55', '1087', '');
INSERT INTO `crp_programs` VALUES ('104', 'Livestock Feeds and Forages', 'F3', '7', '1', '#8e44ad', '1', '926', '2016-08-10 09:13:55', '1087', '');
INSERT INTO `crp_programs` VALUES ('105', 'Livestock and the Environment', 'F4', '7', '1', '#f1c40f', '1', '926', '2016-08-10 09:13:55', '1087', '');
INSERT INTO `crp_programs` VALUES ('106', 'Livestock and Livelihoods and Agri-Food Systems', 'F5', '7', '1', '#2c3e50', '1', '926', '2016-08-10 09:13:55', '1087', '');
INSERT INTO `crp_programs` VALUES ('107', '', '', '1', '1', '#8e44ad', '0', '1106', '2016-08-29 13:48:46', '1106', '');
INSERT INTO `crp_programs` VALUES ('108', '', '', '1', '1', '#DB4B22', '0', '844', '2016-08-30 13:12:14', '844', '');
INSERT INTO `crp_programs` VALUES ('109', '', '', '1', '1', '#9960F0', '0', '1106', '2016-08-30 15:49:34', '1106', '');
INSERT INTO `crp_programs` VALUES ('110', 'Climate services and safety nets', 'F4', '1', '1', '#E6F3F2', '0', '1106', '2016-08-30 15:55:34', '1106', '');
INSERT INTO `crp_programs` VALUES ('111', '', '', '1', '1', '#8FF6A4', '0', '1106', '2016-08-30 15:56:30', '1106', '');

COMMIT;