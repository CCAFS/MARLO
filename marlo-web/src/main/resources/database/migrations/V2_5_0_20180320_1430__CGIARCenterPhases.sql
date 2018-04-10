SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for phases
-- ----------------------------
DROP TABLE IF EXISTS `phases`;
CREATE TABLE `phases` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` text CHARACTER SET utf8 NOT NULL,
  `year` int(11) NOT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `editable` tinyint(1) DEFAULT '1',
  `visible` tinyint(1) DEFAULT '1',
  `next_phase` bigint(20) DEFAULT NULL,
  `global_unit_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `next_phase` (`next_phase`) USING BTREE,
  KEY `phases_global_unit_fk` (`global_unit_id`) USING BTREE,
  KEY `id` (`id`) USING BTREE,
  CONSTRAINT `phases_ibfk_1` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`),
  CONSTRAINT `phases_ibfk_2` FOREIGN KEY (`next_phase`) REFERENCES `phases` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=185 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of phases
-- ----------------------------
INSERT INTO `phases` VALUES ('1', 'Planning', '2017', '2016-10-01', '2016-11-30', '0', '1', '11', '1');
INSERT INTO `phases` VALUES ('2', 'Planning', '2017', '2016-10-01', '2016-11-30', '0', '1', '14', '5');
INSERT INTO `phases` VALUES ('3', 'Reporting', '2016', '2017-01-15', '2017-03-15', '0', '0', '1', '1');
INSERT INTO `phases` VALUES ('4', 'Planning', '2017', '2016-10-01', '2016-11-30', '0', '1', '12', '3');
INSERT INTO `phases` VALUES ('5', 'Planning', '2017', '2017-01-01', '2017-12-31', '0', '1', '13', '4');
INSERT INTO `phases` VALUES ('6', 'Planning', '2017', '2017-01-01', '2017-01-30', '0', '1', '15', '7');
INSERT INTO `phases` VALUES ('7', 'Planning', '2017', '2017-01-01', '2017-12-31', '0', '1', '17', '21');
INSERT INTO `phases` VALUES ('8', 'Planning', '2017', '2017-01-01', '2017-12-31', '0', '1', '18', '22');
INSERT INTO `phases` VALUES ('10', 'Planning', '2017', '2016-10-01', '2016-11-30', '0', '1', '16', '11');
INSERT INTO `phases` VALUES ('11', 'Reporting', '2017', '2018-05-21', '2018-07-06', '0', '0', '26', '1');
INSERT INTO `phases` VALUES ('12', 'Reporting', '2017', '2018-01-15', '2018-03-15', '0', '0', '27', '3');
INSERT INTO `phases` VALUES ('13', 'Reporting', '2017', '2018-01-15', '2018-03-15', '0', '0', '28', '4');
INSERT INTO `phases` VALUES ('14', 'Reporting', '2017', '2018-01-15', '2018-03-15', '0', '0', '29', '5');
INSERT INTO `phases` VALUES ('15', 'Reporting', '2017', '2018-01-15', '2018-03-15', '0', '0', '30', '7');
INSERT INTO `phases` VALUES ('16', 'Reporting', '2017', '2018-01-15', '2018-03-15', '0', '0', '31', '11');
INSERT INTO `phases` VALUES ('17', 'Reporting', '2017', '2018-01-15', '2018-03-15', '0', '0', '32', '21');
INSERT INTO `phases` VALUES ('18', 'Reporting', '2017', '2018-01-15', '2018-03-15', '0', '0', '33', '22');
INSERT INTO `phases` VALUES ('20', 'Reporting', '2017', '2018-01-15', '2018-03-15', '0', '0', '35', '24');
INSERT INTO `phases` VALUES ('21', 'Reporting', '2017', '2018-01-15', '2018-03-15', '0', '0', '36', '25');
INSERT INTO `phases` VALUES ('26', 'Planning', '2018', '2018-01-31', '2018-03-27', '1', '1', '41', '1');
INSERT INTO `phases` VALUES ('27', 'Planning', '2018', '2018-01-31', '2018-03-27', '1', '1', '42', '3');
INSERT INTO `phases` VALUES ('28', 'Planning', '2018', '2018-02-01', '2018-04-13', '1', '1', '43', '4');
INSERT INTO `phases` VALUES ('29', 'Planning', '2018', '2018-02-07', '2018-03-15', '1', '1', '44', '5');
INSERT INTO `phases` VALUES ('30', 'Planning', '2018', '2018-01-31', '2018-03-12', '1', '1', '45', '7');
INSERT INTO `phases` VALUES ('31', 'Planning', '2018', '2018-01-31', '2018-04-14', '1', '1', '46', '11');
INSERT INTO `phases` VALUES ('32', 'Planning', '2018', '2018-02-01', '2018-04-02', '1', '1', '47', '21');
INSERT INTO `phases` VALUES ('33', 'Planning', '2018', '2018-02-01', '2018-04-02', '1', '1', '48', '22');
INSERT INTO `phases` VALUES ('35', 'Planning', '2018', '2018-01-31', '2018-03-31', '1', '1', '50', '24');
INSERT INTO `phases` VALUES ('36', 'Planning', '2018', '2018-01-31', '2017-11-30', '0', '1', '51', '25');
INSERT INTO `phases` VALUES ('41', 'Reporting', '2018', '2019-01-15', '2019-03-15', '0', '0', '56', '1');
INSERT INTO `phases` VALUES ('42', 'Reporting', '2018', '2019-01-15', '2019-03-15', '0', '0', '57', '3');
INSERT INTO `phases` VALUES ('43', 'Reporting', '2018', '2019-01-15', '2019-03-15', '0', '0', '58', '4');
INSERT INTO `phases` VALUES ('44', 'Reporting', '2018', '2019-01-15', '2019-03-15', '0', '0', '59', '5');
INSERT INTO `phases` VALUES ('45', 'Reporting', '2018', '2019-01-15', '2019-03-15', '0', '0', '60', '7');
INSERT INTO `phases` VALUES ('46', 'Reporting', '2018', '2019-01-15', '2019-03-15', '0', '1', '61', '11');
INSERT INTO `phases` VALUES ('47', 'Reporting', '2018', '2019-01-15', '2019-03-15', '0', '0', '62', '21');
INSERT INTO `phases` VALUES ('48', 'Reporting', '2018', '2019-01-15', '2019-03-15', '0', '0', '63', '22');
INSERT INTO `phases` VALUES ('49', 'Reporting', '2018', '2019-01-15', '2019-03-15', '0', '0', '79', '23');
INSERT INTO `phases` VALUES ('50', 'Reporting', '2018', '2019-01-15', '2019-03-15', '0', '0', '65', '24');
INSERT INTO `phases` VALUES ('51', 'Reporting', '2018', '2019-01-15', '2019-03-15', '0', '0', '66', '25');
INSERT INTO `phases` VALUES ('56', 'Planning', '2019', '2018-10-01', '2018-11-30', '0', '0', '71', '1');
INSERT INTO `phases` VALUES ('57', 'Planning', '2019', '2018-10-01', '2018-11-30', '0', '0', '72', '3');
INSERT INTO `phases` VALUES ('58', 'Planning', '2019', '2018-10-01', '2018-11-30', '0', '0', '73', '4');
INSERT INTO `phases` VALUES ('59', 'Planning', '2019', '2018-10-01', '2018-11-30', '0', '0', '74', '5');
INSERT INTO `phases` VALUES ('60', 'Planning', '2019', '2018-10-01', '2018-11-30', '0', '0', '75', '7');
INSERT INTO `phases` VALUES ('61', 'Planning', '2019', '2018-10-01', '2018-11-30', '0', '0', '76', '11');
INSERT INTO `phases` VALUES ('62', 'Planning', '2019', '2018-10-01', '2018-11-30', '0', '0', '77', '21');
INSERT INTO `phases` VALUES ('63', 'Planning', '2019', '2018-10-01', '2018-11-30', '0', '0', '78', '22');
INSERT INTO `phases` VALUES ('65', 'Planning', '2019', '2018-10-01', '2018-11-30', '0', '0', '80', '24');
INSERT INTO `phases` VALUES ('66', 'Planning', '2019', '2018-10-01', '2018-11-30', '0', '0', '81', '25');
INSERT INTO `phases` VALUES ('71', 'Reporting', '2019', '2020-01-15', '2020-03-15', '0', '0', '86', '1');
INSERT INTO `phases` VALUES ('72', 'Reporting', '2019', '2020-01-15', '2020-03-15', '0', '0', '87', '3');
INSERT INTO `phases` VALUES ('73', 'Reporting', '2019', '2020-01-15', '2020-03-15', '0', '0', '88', '4');
INSERT INTO `phases` VALUES ('74', 'Reporting', '2019', '2020-01-15', '2020-03-15', '0', '0', '89', '5');
INSERT INTO `phases` VALUES ('75', 'Reporting', '2019', '2020-01-15', '2020-03-15', '0', '0', '90', '7');
INSERT INTO `phases` VALUES ('76', 'Reporting', '2019', '2020-01-15', '2020-03-15', '0', '0', '91', '11');
INSERT INTO `phases` VALUES ('77', 'Reporting', '2019', '2020-01-15', '2020-03-15', '0', '0', '92', '21');
INSERT INTO `phases` VALUES ('78', 'Reporting', '2019', '2020-01-15', '2020-03-15', '0', '0', '93', '22');
INSERT INTO `phases` VALUES ('79', 'Reporting', '2019', '2020-01-15', '2020-03-15', '0', '0', '109', '23');
INSERT INTO `phases` VALUES ('80', 'Reporting', '2019', '2020-01-15', '2020-03-15', '0', '0', '95', '24');
INSERT INTO `phases` VALUES ('81', 'Reporting', '2019', '2020-01-15', '2020-03-15', '0', '0', '96', '25');
INSERT INTO `phases` VALUES ('86', 'Planning', '2020', '2019-10-01', '2019-11-30', '0', '0', '101', '1');
INSERT INTO `phases` VALUES ('87', 'Planning', '2020', '2019-10-01', '2019-11-30', '0', '0', '102', '3');
INSERT INTO `phases` VALUES ('88', 'Planning', '2020', '2019-10-01', '2019-11-30', '0', '0', '103', '4');
INSERT INTO `phases` VALUES ('89', 'Planning', '2020', '2019-10-01', '2019-11-30', '0', '0', '104', '5');
INSERT INTO `phases` VALUES ('90', 'Planning', '2020', '2019-10-01', '2019-11-30', '0', '0', '105', '7');
INSERT INTO `phases` VALUES ('91', 'Planning', '2020', '2019-10-01', '2019-11-30', '0', '0', '106', '11');
INSERT INTO `phases` VALUES ('92', 'Planning', '2020', '2019-10-01', '2019-11-30', '0', '0', '107', '21');
INSERT INTO `phases` VALUES ('93', 'Planning', '2020', '2019-10-01', '2019-11-30', '0', '0', '108', '22');
INSERT INTO `phases` VALUES ('95', 'Planning', '2020', '2019-10-01', '2019-11-30', '0', '0', '110', '24');
INSERT INTO `phases` VALUES ('96', 'Planning', '2020', '2019-10-01', '2019-11-30', '0', '0', '111', '25');
INSERT INTO `phases` VALUES ('101', 'Reporting', '2020', '2021-01-15', '2021-03-15', '0', '0', '116', '1');
INSERT INTO `phases` VALUES ('102', 'Reporting', '2020', '2021-01-15', '2021-03-15', '0', '0', '117', '3');
INSERT INTO `phases` VALUES ('103', 'Reporting', '2020', '2021-01-15', '2021-03-15', '0', '0', '118', '4');
INSERT INTO `phases` VALUES ('104', 'Reporting', '2020', '2021-01-15', '2021-03-15', '0', '0', '119', '5');
INSERT INTO `phases` VALUES ('105', 'Reporting', '2020', '2021-01-15', '2021-03-15', '0', '0', '120', '7');
INSERT INTO `phases` VALUES ('106', 'Reporting', '2020', '2021-01-15', '2021-03-15', '0', '0', '121', '11');
INSERT INTO `phases` VALUES ('107', 'Reporting', '2020', '2021-01-15', '2021-03-15', '0', '0', '122', '21');
INSERT INTO `phases` VALUES ('108', 'Reporting', '2020', '2021-01-15', '2021-03-15', '0', '0', '123', '22');
INSERT INTO `phases` VALUES ('109', 'Reporting', '2020', '2021-01-15', '2021-03-15', '0', '0', '139', '23');
INSERT INTO `phases` VALUES ('110', 'Reporting', '2020', '2021-01-15', '2021-03-15', '0', '0', '125', '24');
INSERT INTO `phases` VALUES ('111', 'Reporting', '2020', '2021-01-15', '2021-03-15', '0', '0', '126', '25');
INSERT INTO `phases` VALUES ('116', 'Planning', '2021', '2020-10-01', '2020-11-30', '0', '0', '131', '1');
INSERT INTO `phases` VALUES ('117', 'Planning', '2021', '2020-10-01', '2020-11-30', '0', '0', '132', '3');
INSERT INTO `phases` VALUES ('118', 'Planning', '2021', '2020-10-01', '2020-11-30', '0', '0', '133', '4');
INSERT INTO `phases` VALUES ('119', 'Planning', '2021', '2020-10-01', '2020-11-30', '0', '0', '134', '5');
INSERT INTO `phases` VALUES ('120', 'Planning', '2021', '2020-10-01', '2020-11-30', '0', '0', '135', '7');
INSERT INTO `phases` VALUES ('121', 'Planning', '2021', '2020-10-01', '2020-11-30', '0', '0', '136', '11');
INSERT INTO `phases` VALUES ('122', 'Planning', '2021', '2020-10-01', '2020-11-30', '0', '0', '137', '21');
INSERT INTO `phases` VALUES ('123', 'Planning', '2021', '2020-10-01', '2020-11-30', '0', '0', '138', '22');
INSERT INTO `phases` VALUES ('125', 'Planning', '2021', '2020-10-01', '2020-11-30', '0', '0', '140', '24');
INSERT INTO `phases` VALUES ('126', 'Planning', '2021', '2020-10-01', '2020-11-30', '0', '0', '141', '25');
INSERT INTO `phases` VALUES ('131', 'Reporting', '2021', '2022-01-15', '2022-03-15', '0', '0', '146', '1');
INSERT INTO `phases` VALUES ('132', 'Reporting', '2021', '2022-01-15', '2022-03-15', '0', '0', '147', '3');
INSERT INTO `phases` VALUES ('133', 'Reporting', '2021', '2022-01-15', '2022-03-15', '0', '0', '148', '4');
INSERT INTO `phases` VALUES ('134', 'Reporting', '2021', '2022-01-15', '2022-03-15', '0', '0', '149', '5');
INSERT INTO `phases` VALUES ('135', 'Reporting', '2021', '2022-01-15', '2022-03-15', '0', '0', '150', '7');
INSERT INTO `phases` VALUES ('136', 'Reporting', '2021', '2022-01-15', '2022-03-15', '0', '0', '151', '11');
INSERT INTO `phases` VALUES ('137', 'Reporting', '2021', '2022-01-15', '2022-03-15', '0', '0', '152', '21');
INSERT INTO `phases` VALUES ('138', 'Reporting', '2021', '2022-01-15', '2022-03-15', '0', '0', '153', '22');
INSERT INTO `phases` VALUES ('139', 'Reporting', '2021', '2022-01-15', '2022-03-15', '0', '0', '169', '23');
INSERT INTO `phases` VALUES ('140', 'Reporting', '2021', '2022-01-15', '2022-03-15', '0', '0', '155', '24');
INSERT INTO `phases` VALUES ('141', 'Reporting', '2021', '2022-01-15', '2022-03-15', '0', '0', '156', '25');
INSERT INTO `phases` VALUES ('146', 'Planning', '2022', '2021-10-01', '2021-11-30', '0', '0', '161', '1');
INSERT INTO `phases` VALUES ('147', 'Planning', '2022', '2021-10-01', '2021-11-30', '0', '0', '162', '3');
INSERT INTO `phases` VALUES ('148', 'Planning', '2022', '2021-10-01', '2021-11-30', '0', '0', '163', '4');
INSERT INTO `phases` VALUES ('149', 'Planning', '2022', '2021-10-01', '2021-11-30', '0', '0', '164', '5');
INSERT INTO `phases` VALUES ('150', 'Planning', '2022', '2021-10-01', '2021-11-30', '0', '0', '165', '7');
INSERT INTO `phases` VALUES ('151', 'Planning', '2022', '2021-10-01', '2021-11-30', '0', '0', '166', '11');
INSERT INTO `phases` VALUES ('152', 'Planning', '2022', '2021-10-01', '2021-11-30', '0', '0', '167', '21');
INSERT INTO `phases` VALUES ('153', 'Planning', '2022', '2021-10-01', '2021-11-30', '0', '0', '168', '22');
INSERT INTO `phases` VALUES ('155', 'Planning', '2022', '2021-10-01', '2021-11-30', '0', '0', '170', '24');
INSERT INTO `phases` VALUES ('156', 'Planning', '2022', '2021-10-01', '2021-11-30', '0', '0', '171', '25');
INSERT INTO `phases` VALUES ('161', 'Reporting', '2022', '2023-01-15', '2023-03-15', '0', '0', null, '1');
INSERT INTO `phases` VALUES ('162', 'Reporting', '2022', '2023-01-15', '2023-03-15', '0', '0', null, '3');
INSERT INTO `phases` VALUES ('163', 'Reporting', '2022', '2023-01-15', '2023-03-15', '0', '0', null, '4');
INSERT INTO `phases` VALUES ('164', 'Reporting', '2022', '2023-01-15', '2023-03-15', '0', '0', null, '5');
INSERT INTO `phases` VALUES ('165', 'Reporting', '2022', '2023-01-15', '2023-03-15', '0', '0', null, '7');
INSERT INTO `phases` VALUES ('166', 'Reporting', '2022', '2023-01-15', '2023-03-15', '0', '0', null, '11');
INSERT INTO `phases` VALUES ('167', 'Reporting', '2022', '2023-01-15', '2023-03-15', '0', '0', null, '21');
INSERT INTO `phases` VALUES ('168', 'Reporting', '2022', '2023-01-15', '2023-03-15', '0', '0', null, '22');
INSERT INTO `phases` VALUES ('169', 'Reporting', '2022', '2023-01-15', '2023-03-15', '0', '0', null, '23');
INSERT INTO `phases` VALUES ('170', 'Reporting', '2022', '2023-01-15', '2023-03-15', '0', '0', null, '24');
INSERT INTO `phases` VALUES ('171', 'Reporting', '2022', '2023-01-15', '2023-03-15', '0', '0', null, '25');
INSERT INTO `phases` VALUES ('172', 'Planning', '2017', '2016-10-01', '2016-11-30', '0', '1', '173', '29');
INSERT INTO `phases` VALUES ('173', 'Reporting', '2016', '2017-01-15', '2017-03-15', '0', '0', '174', '29');
INSERT INTO `phases` VALUES ('174', 'Reporting', '2017', '2018-05-21', '2018-07-06', '0', '0', '175', '29');
INSERT INTO `phases` VALUES ('175', 'Planning', '2018', '2018-01-31', '2018-03-27', '1', '1', '176', '29');
INSERT INTO `phases` VALUES ('176', 'Reporting', '2018', '2019-01-15', '2019-03-15', '0', '0', '177', '29');
INSERT INTO `phases` VALUES ('177', 'Planning', '2019', '2018-10-01', '2018-11-30', '0', '0', '178', '29');
INSERT INTO `phases` VALUES ('178', 'Reporting', '2019', '2020-01-15', '2020-03-15', '0', '0', '179', '29');
INSERT INTO `phases` VALUES ('179', 'Planning', '2020', '2019-10-01', '2019-11-30', '0', '0', '180', '29');
INSERT INTO `phases` VALUES ('180', 'Reporting', '2020', '2021-01-15', '2021-03-15', '0', '0', '181', '29');
INSERT INTO `phases` VALUES ('181', 'Planning', '2021', '2020-10-01', '2020-11-30', '0', '0', '182', '29');
INSERT INTO `phases` VALUES ('182', 'Reporting', '2021', '2022-01-15', '2022-03-15', '0', '0', '183', '29');
INSERT INTO `phases` VALUES ('183', 'Planning', '2022', '2021-10-01', '2021-11-30', '0', '0', '184', '29');
INSERT INTO `phases` VALUES ('184', 'Reporting', '2022', '2023-01-15', '2023-03-15', '0', '0', null, '29');
