SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ip_liaison_institutions
-- ----------------------------
DROP TABLE IF EXISTS `ip_liaison_institutions`;
CREATE TABLE `ip_liaison_institutions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `institution_id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `acronym` varchar(255) DEFAULT NULL,
  `ip_program` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ip_liaison_institutions_institutions_idx` (`institution_id`) USING BTREE,
  CONSTRAINT `ip_liaison_institutions_ibfk_1` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ip_liaison_institutions
-- ----------------------------
INSERT INTO `ip_liaison_institutions` VALUES ('1', '114', 'Coordinating Unit', 'CU', null);
INSERT INTO `ip_liaison_institutions` VALUES ('2', '46', 'Flagship 1', 'F1', '1');
INSERT INTO `ip_liaison_institutions` VALUES ('3', '100', 'Flagship 2', 'F2', '2');
INSERT INTO `ip_liaison_institutions` VALUES ('4', '1053', 'Flagship 3', 'F3', '3');
INSERT INTO `ip_liaison_institutions` VALUES ('5', '66', 'Flagship 4', 'F4', '4');
INSERT INTO `ip_liaison_institutions` VALUES ('6', '66', 'East Africa Region', 'RP EA', '6');
INSERT INTO `ip_liaison_institutions` VALUES ('7', '46', 'Latin America Region', 'RP LAM', '5');
INSERT INTO `ip_liaison_institutions` VALUES ('8', '172', 'South Asia Region', 'RP SAs', '8');
INSERT INTO `ip_liaison_institutions` VALUES ('9', '5', 'South East Asia Region', 'RP SEA', '9');
INSERT INTO `ip_liaison_institutions` VALUES ('10', '103', 'West Africa Region', 'RP WA', '7');
INSERT INTO `ip_liaison_institutions` VALUES ('11', '52', 'Africa Rice Center', 'AfricaRice', null);
INSERT INTO `ip_liaison_institutions` VALUES ('12', '49', 'Bioversity International', 'BI', null);
INSERT INTO `ip_liaison_institutions` VALUES ('13', '46', 'Centro Internacional de Agricultura Tropical', 'CIAT', null);
INSERT INTO `ip_liaison_institutions` VALUES ('14', '115', 'Center for International Forestry Research', 'CIFOR', null);
INSERT INTO `ip_liaison_institutions` VALUES ('15', '50', 'International Maize and Wheat Improvement Center', 'CIMMYT', null);
INSERT INTO `ip_liaison_institutions` VALUES ('16', '67', 'Centro Internacional de la Papa', 'CIP', null);
INSERT INTO `ip_liaison_institutions` VALUES ('17', '51', 'International Center for Agricultural Research in the Dry Areas', 'ICARDA', null);
INSERT INTO `ip_liaison_institutions` VALUES ('18', '88', 'World Agroforestry Centre', 'ICRAF', null);
INSERT INTO `ip_liaison_institutions` VALUES ('19', '103', 'International Crops Research Institute for the Semi-Arid Tropics', 'ICRISAT', null);
INSERT INTO `ip_liaison_institutions` VALUES ('20', '89', 'International Food Policy Research Institute', 'IFPRI', null);
INSERT INTO `ip_liaison_institutions` VALUES ('21', '45', 'International Institute of Tropical Agriculture', 'IITA', null);
INSERT INTO `ip_liaison_institutions` VALUES ('22', '66', 'International Livestock Research Institute', 'ILRI', null);
INSERT INTO `ip_liaison_institutions` VALUES ('23', '5', 'International Rice Research Institute', 'IRRI', null);
INSERT INTO `ip_liaison_institutions` VALUES ('24', '172', 'International Water Management Institute', 'IWMI', null);
INSERT INTO `ip_liaison_institutions` VALUES ('25', '99', 'WorldFish Center', 'WorldFish', null);
