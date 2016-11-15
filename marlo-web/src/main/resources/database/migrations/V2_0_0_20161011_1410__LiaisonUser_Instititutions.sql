SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `liaison_institutions`
-- ----------------------------
DROP TABLE IF EXISTS `liaison_institutions`;
CREATE TABLE `liaison_institutions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `institution_id` bigint(20) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `acronym` varchar(255) DEFAULT NULL,
  `crp_program` bigint(20) DEFAULT NULL,
  `crp_id` bigint(20) DEFAULT '1',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FK_liaison_institutions_institutions_idx` (`institution_id`) USING BTREE,
  KEY `crp_program` (`crp_program`) USING BTREE,
  KEY `crp_id` (`crp_id`) USING BTREE,
  CONSTRAINT `liaison_institutions_ibfk_1` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `liaison_institutions_ibfk_2` FOREIGN KEY (`crp_program`) REFERENCES `crp_programs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `liaison_institutions_ibfk_3` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of liaison_institutions
-- ----------------------------
BEGIN;
INSERT INTO `liaison_institutions` VALUES ('1', '114', 'Coordinating Unit', 'CU', null, '1', '1'), 
('2', '46', 'Flagship 2', 'F2', '85', '1', '1'), 
('3', '100', 'Flagship 4', 'F4', '84', '1', '1'),
('4', '1053', 'Flagship 3', 'F3', '86', '1', '1'), 
('5', '66', 'Flagship 1', 'F1', '87', '1', '1'), 
('6', '66', 'East Africa Region', 'RP EA', '89', '1', '1'), ('7', '46', 'Latin America Region', 'RP LAM', '88', '1', '1'), ('8', '172', 'South Asia Region', 'RP SAs', '92', '1', '1'), ('9', '5', 'South East Asia Region', 'RP SEA', '91', '1', '1'), ('10', '103', 'West Africa Region', 'RP WA', '90', '1', '1'), ('11', '52', 'Africa Rice Center', 'AfricaRice', null, null, '1'), ('12', '49', 'Bioversity International', 'BI', null, null, '1'), ('13', '46', 'Centro Internacional de Agricultura Tropical', 'CIAT', null, null, '1'), ('14', '115', 'Center for International Forestry Research', 'CIFOR', null, null, '1'), ('15', '50', 'International Maize and Wheat Improvement Center', 'CIMMYT', null, null, '1'), ('16', '67', 'Centro Internacional de la Papa', 'CIP', null, null, '1'), ('17', '51', 'International Center for Agricultural Research in the Dry Areas', 'ICARDA', null, null, '1'), ('18', '88', 'World Agroforestry Centre', 'ICRAF', null, null, '1'), ('19', '103', 'International Crops Research Institute for the Semi-Arid Tropics', 'ICRISAT', null, null, '1'), ('20', '89', 'International Food Policy Research Institute', 'IFPRI', null, null, '1'), ('21', '45', 'International Institute of Tropical Agriculture', 'IITA', null, null, '1'), ('22', '66', 'International Livestock Research Institute', 'ILRI', null, null, '1'), ('23', '5', 'International Rice Research Institute', 'IRRI', null, null, '1'), ('24', '172', 'International Water Management Institute', 'IWMI', null, null, '1'), ('25', '99', 'WorldFish Center', 'WorldFish', null, null, '1'), ('34', null, 'Coordinating Unit', 'CU', null, '3', '1'), ('35', null, 'Coordinating Unit', 'CU', null, '4', '1'), ('36', null, 'Coodrinating Unit', 'CU', null, '5', '1'), ('37', null, 'Coodrinating Unit', 'CU', null, '7', '1'), ('38', null, 'Food Systems for Healthier Diets', 'F1', '93', '5', '1'), ('39', null, 'Biofortification', 'F2', '94', '5', '1'), ('40', null, 'Food Safety', 'F3', '95', '5', '1'), ('41', null, 'Supporting Policies, Programs and Enabling Action through Research', 'F4', '96', '5', '1'), ('42', null, 'Improving Human Health', 'F5', '97', '5', '1'), ('43', null, 'Technological Innovation and Sustainable Intensification', 'F1', '98', '3', '1'), ('44', null, 'Economywide Factors Affecting Agricultural Growth and Rural Transformation', 'F2', '99', '3', '1'), ('45', null, 'Inclusive and Efficient Value Chains', 'F3', '100', '3', '1'), ('46', null, 'Social Protection for Agriculture and Resilience', 'F4', '101', '3', '1'), ('47', null, 'Livestock Genetics', 'F1', '102', '7', '1'), ('48', null, 'Livestock Health', 'F2', '103', '7', '1'), ('49', null, 'Livestock Feeds and Forages', 'F3', '104', '7', '1'), ('50', null, 'Livestock and the Environment', 'F4', '105', '7', '1'), ('51', null, 'Livestock and Livelihoods and Agri-Food Systems', 'F5', '106', '7', '1');
COMMIT;

-- ----------------------------
-- Table structure for `liaison_users`
-- ----------------------------
DROP TABLE IF EXISTS `liaison_users`;
CREATE TABLE `liaison_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT 'This field is a link to the table users',
  `institution_id` bigint(20) NOT NULL COMMENT 'This field is a link to the table liaison_institutions',
  `crp_id` bigint(20) NOT NULL DEFAULT '1',
  `is_active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_liaison_users_users__idx` (`user_id`) USING BTREE,
  KEY `FK_liaison_users_institutions__idx` (`institution_id`) USING BTREE,
  KEY `crp_id` (`crp_id`) USING BTREE,
  CONSTRAINT `liaison_users_ibfk_1` FOREIGN KEY (`institution_id`) REFERENCES `liaison_institutions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `liaison_users_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `liaison_users_ibfk_3` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of liaison_users
-- ----------------------------
BEGIN;
INSERT INTO `liaison_users` VALUES ('1', '122', '1', '1', '1'), ('2', '132', '1', '1', '1'), ('3', '28', '2', '1', '1'), ('4', '17', '2', '1', '1'), ('5', '29', '3', '1', '1'), ('7', '31', '4', '1', '1'), ('8', '32', '4', '1', '1'), ('9', '7', '5', '1', '1'), ('10', '5', '5', '1', '1'), ('11', '24', '6', '1', '1'), ('12', '25', '6', '1', '1'), ('13', '19', '7', '1', '1'), ('14', '18', '7', '1', '1'), ('15', '20', '8', '1', '1'), ('16', '21', '8', '1', '1'), ('17', '26', '9', '1', '1'), ('18', '193', '9', '1', '1'), ('19', '22', '10', '1', '1'), ('20', '61', '12', '1', '1'), ('21', '66', '13', '1', '1'), ('22', '69', '13', '1', '1'), ('23', '119', '14', '1', '1'), ('24', '88', '15', '1', '1'), ('25', '67', '16', '1', '1'), ('26', '84', '19', '1', '1'), ('27', '162', '19', '1', '1'), ('28', '51', '5', '1', '1'), ('29', '52', '20', '1', '1'), ('30', '81', '21', '1', '1'), ('31', '82', '22', '1', '1'), ('32', '108', '23', '1', '1'), ('33', '275', '24', '1', '1'), ('34', '83', '18', '1', '1'), ('35', '101', '25', '1', '1'), ('36', '27', '9', '1', '1'), ('37', '131', '1', '1', '1'), ('38', '1', '13', '1', '1'), ('39', '50', '4', '1', '1'), ('40', '294', '1', '1', '1'), ('42', '16', '5', '1', '1'), ('43', '243', '12', '1', '1'), ('45', '271', '21', '1', '1'), ('46', '247', '12', '1', '1'), ('48', '1002', '25', '1', '1'), ('49', '847', '11', '1', '1'), ('50', '848', '17', '1', '1'), ('51', '158', '18', '1', '1'), ('52', '852', '3', '1', '1'), ('53', '855', '10', '1', '1'), ('54', '14', '1', '1', '1'), ('55', '863', '12', '1', '1'), ('57', '842', '1', '1', '1'), ('58', '3', '2', '1', '1'), ('59', '1051', '3', '1', '1'), ('60', '179', '6', '1', '1'), ('61', '22', '6', '1', '1'), ('62', '186', '10', '1', '1'), ('63', '1024', '14', '1', '1'), ('64', '1025', '14', '1', '1'), ('65', '199', '19', '1', '1'), ('66', '484', '19', '1', '1'), ('69', '1054', '19', '1', '1'), ('70', '1056', '14', '1', '1'), ('71', '1058', '3', '1', '1'), ('77', '28', '3', '1', '1'), ('78', '17', '3', '1', '1'), ('79', '29', '5', '1', '1'), ('80', '7', '2', '1', '1'), ('81', '1068', '38', '5', '1'), ('82', '1069', '39', '5', '1'), ('83', '99', '40', '5', '1'), ('84', '1070', '41', '5', '1'), ('85', '1071', '42', '5', '1'), ('86', '79', '43', '3', '1'), ('87', '1082', '44', '3', '1'), ('88', '1083', '44', '3', '1'), ('89', '1084', '45', '3', '1'), ('90', '1085', '45', '3', '1'), ('91', '1086', '46', '3', '1'), ('92', '1091', '48', '7', '1'), ('93', '1093', '49', '7', '1'), ('94', '1090', '47', '7', '1'), ('95', '82', '50', '7', '1'), ('96', '304', '51', '7', '1'), ('97', '17', '3', '1', '1'), ('98', '988', '36', '5', '1'), ('99', '1060', '36', '5', '1'), ('100', '1065', '36', '5', '1'), ('101', '1066', '36', '5', '1'), ('102', '1067', '36', '5', '1'), ('103', '1079', '34', '3', '1'), ('104', '1064', '34', '3', '1'), ('105', '1061', '34', '3', '1'), ('106', '1080', '34', '3', '1'), ('107', '1081', '34', '3', '1'), ('108', '1087', '37', '7', '1'), ('109', '926', '37', '7', '1'), ('110', '1088', '37', '7', '1');
INSERT INTO `liaison_users` VALUES ('111', '1089', '37', '7', '1');
COMMIT;