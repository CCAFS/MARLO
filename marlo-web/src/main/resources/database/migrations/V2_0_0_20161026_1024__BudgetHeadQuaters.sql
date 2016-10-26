update 
project_budgets pp  INNER JOIN institutions ins on ins.id=pp.institution_id and ins.headquarter is not null
set pp.institution_id=ins.headquarter
where pp.is_active=1;

 update 
project_partners pp  INNER JOIN institutions ins on ins.id=pp.institution_id and ins.headquarter is not null
set pp.institution_id=ins.headquarter
where pp.is_active=1;

/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-10-26 10:35:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `project_branches`
-- ----------------------------
DROP TABLE IF EXISTS `project_branches`;
CREATE TABLE `project_branches` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_partner_id` bigint(20) NOT NULL COMMENT 'Foreign key to projects table',
  `institution_id` bigint(20) NOT NULL COMMENT 'Foreign key to institutions table',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_project_partners_projects_idx` (`project_partner_id`) USING BTREE,
  KEY `FK_project_partners_institutions_idx` (`institution_id`) USING BTREE,
  KEY `fk_project_partners_users_created_by_idx` (`created_by`) USING BTREE,
  KEY `fk_project_partners_users_modified_by_idx` (`modified_by`) USING BTREE,
  CONSTRAINT `project_branches_ibfk_2` FOREIGN KEY (`project_partner_id`) REFERENCES `project_partners` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `project_branches_ibfk_1` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `project_branches_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `project_branches_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_branches
-- ----------------------------
INSERT INTO `project_branches` VALUES ('1', '514', '1170', '1', '2015-09-15 21:39:10', '193', '193', '');
INSERT INTO `project_branches` VALUES ('2', '902', '1170', '1', '2015-11-05 09:00:20', '450', '17', '');
INSERT INTO `project_branches` VALUES ('3', '515', '1171', '1', '2015-09-15 21:39:10', '193', '193', '');
INSERT INTO `project_branches` VALUES ('4', '3', '1212', '1', '2015-09-15 21:39:10', '7', '173', 'I have made changes in some sections as follows: \r\n1. John Hebert Ainembabazi moved on from IITA.\r\n2. ANSAF is not a budget holder');
INSERT INTO `project_branches` VALUES ('5', '22', '1212', '1', '2015-09-15 21:39:10', '24', '95', '');
INSERT INTO `project_branches` VALUES ('6', '29', '1212', '1', '2015-09-15 21:39:10', '24', '96', '');
INSERT INTO `project_branches` VALUES ('7', '38', '1212', '1', '2015-09-15 21:39:10', '24', '100', '');
INSERT INTO `project_branches` VALUES ('8', '999', '1212', '1', '2015-11-19 14:45:46', '162', '162', '');
INSERT INTO `project_branches` VALUES ('9', '104', '1221', '1', '2015-09-15 21:39:10', '27', '1052', '');
INSERT INTO `project_branches` VALUES ('10', '510', '1221', '1', '2015-09-15 21:39:10', '193', '193', '');
INSERT INTO `project_branches` VALUES ('11', '25', '1215', '1', '2015-09-15 21:39:10', '19', '53', '');
INSERT INTO `project_branches` VALUES ('12', '113', '1215', '1', '2015-09-15 21:39:10', '19', '61', '');
INSERT INTO `project_branches` VALUES ('13', '863', '1215', '1', '2015-10-30 10:16:12', '243', '61', '');
INSERT INTO `project_branches` VALUES ('14', '535', '1220', '1', '2015-09-15 21:39:10', '90', '1052', '');
INSERT INTO `project_branches` VALUES ('15', '94', '650', '1', '2015-09-15 21:39:10', '20', '85', '');
INSERT INTO `project_branches` VALUES ('16', '103', '650', '1', '2015-09-15 21:39:10', '20', '89', '');
INSERT INTO `project_branches` VALUES ('17', '120', '650', '1', '2015-09-15 21:39:10', '20', '89', '');
INSERT INTO `project_branches` VALUES ('18', '139', '650', '1', '2015-09-15 21:39:10', '28', '3', '');
INSERT INTO `project_branches` VALUES ('19', '387', '650', '1', '2015-09-15 21:39:10', '21', '21', '');
INSERT INTO `project_branches` VALUES ('20', '715', '650', '1', '2015-09-15 21:39:10', '88', '88', '');
INSERT INTO `project_branches` VALUES ('21', '738', '650', '1', '2015-09-15 21:39:10', '88', '88', '');
INSERT INTO `project_branches` VALUES ('22', '980', '650', '1', '2015-11-18 09:43:15', '88', '88', '');
INSERT INTO `project_branches` VALUES ('23', '981', '650', '1', '2015-11-18 09:58:50', '88', '88', '');
INSERT INTO `project_branches` VALUES ('24', '982', '650', '1', '2015-11-18 10:01:19', '88', '88', '');
INSERT INTO `project_branches` VALUES ('25', '997', '650', '1', '2015-11-19 14:45:46', '162', '162', '');
INSERT INTO `project_branches` VALUES ('26', '109', '680', '1', '2015-09-15 21:39:10', '24', '96', '');
INSERT INTO `project_branches` VALUES ('27', '119', '680', '1', '2015-09-15 21:39:10', '24', '100', '');
INSERT INTO `project_branches` VALUES ('28', '140', '680', '1', '2015-09-15 21:39:10', '28', '3', 'Updating budget 2016 as per Julian Rivera revision.');
INSERT INTO `project_branches` VALUES ('29', '268', '680', '1', '2015-09-15 21:39:10', '208', '880', '');
INSERT INTO `project_branches` VALUES ('30', '418', '680', '1', '2015-09-15 21:39:10', '29', '78', '');
INSERT INTO `project_branches` VALUES ('31', '449', '680', '1', '2015-09-15 21:39:10', '168', '95', '');
INSERT INTO `project_branches` VALUES ('32', '491', '680', '1', '2015-09-15 21:39:10', '162', '162', '');
INSERT INTO `project_branches` VALUES ('33', '727', '680', '1', '2015-09-15 21:39:10', '88', '96', '');
INSERT INTO `project_branches` VALUES ('34', '998', '680', '1', '2015-11-19 14:45:46', '162', '162', '');
INSERT INTO `project_branches` VALUES ('35', '701', '1178', '1', '2015-09-15 21:39:10', '73', '844', 'Remove Girvetz, Evan duplicated on Partner 2');
INSERT INTO `project_branches` VALUES ('36', '898', '1178', '1', '2015-11-05 08:13:33', '450', '17', '');
INSERT INTO `project_branches` VALUES ('37', '1057', '1178', '1', '2016-08-29 08:14:16', '521', '521', '');
INSERT INTO `project_branches` VALUES ('38', '167', '792', '1', '2015-09-15 21:39:10', '199', '199', '');
INSERT INTO `project_branches` VALUES ('39', '417', '792', '1', '2015-09-15 21:39:10', '78', '78', '');
INSERT INTO `project_branches` VALUES ('40', '700', '792', '1', '2015-09-15 21:39:10', '91', '91', '');
INSERT INTO `project_branches` VALUES ('41', '97', '1213', '1', '2015-09-15 21:39:10', '27', '86', '');
INSERT INTO `project_branches` VALUES ('42', '118', '1213', '1', '2015-09-15 21:39:10', '27', '965', '');
INSERT INTO `project_branches` VALUES ('43', '34', '1223', '1', '2015-09-15 21:39:10', '19', '53', '');
INSERT INTO `project_branches` VALUES ('44', '886', '1223', '1', '2015-11-04 11:46:48', '82', '82', '');
INSERT INTO `project_branches` VALUES ('45', '537', '1222', '1', '2015-09-15 21:39:10', '90', '1052', '');
INSERT INTO `project_branches` VALUES ('46', '234', '873', '1', '2015-09-15 21:39:10', '70', '65', '');
INSERT INTO `project_branches` VALUES ('47', '40', '1143', '1', '2015-09-15 21:39:10', '27', '965', '');
INSERT INTO `project_branches` VALUES ('48', '122', '1143', '1', '2015-09-15 21:39:10', '27', '871', '');
INSERT INTO `project_branches` VALUES ('49', '185', '1143', '1', '2015-09-15 21:39:10', '79', '856', '');
INSERT INTO `project_branches` VALUES ('50', '511', '1143', '1', '2015-09-15 21:39:10', '193', '193', '');
INSERT INTO `project_branches` VALUES ('51', '520', '1143', '1', '2015-09-15 21:39:10', '26', '193', '');
INSERT INTO `project_branches` VALUES ('52', '538', '1143', '1', '2015-09-15 21:39:10', '90', '1052', '');
INSERT INTO `project_branches` VALUES ('53', '27', '1219', '1', '2015-09-15 21:39:10', '19', '61', '');
INSERT INTO `project_branches` VALUES ('54', '117', '1232', '1', '2015-09-15 21:39:10', '27', '115', '');
INSERT INTO `project_branches` VALUES ('55', '880', '1232', '1', '2015-11-04 03:43:42', '193', '193', '');
INSERT INTO `project_branches` VALUES ('56', '15', '1085', '1', '2015-09-15 21:39:10', '20', '87', '');
INSERT INTO `project_branches` VALUES ('57', '31', '1085', '1', '2015-09-15 21:39:10', '20', '97', '');
INSERT INTO `project_branches` VALUES ('58', '42', '1085', '1', '2015-09-15 21:39:10', '20', '89', '');
INSERT INTO `project_branches` VALUES ('59', '96', '1085', '1', '2015-09-15 21:39:10', '27', '52', '');
INSERT INTO `project_branches` VALUES ('60', '114', '1085', '1', '2015-09-15 21:39:10', '29', '98', '');
INSERT INTO `project_branches` VALUES ('61', '711', '1085', '1', '2015-09-15 21:39:10', '77', '878', '');
INSERT INTO `project_branches` VALUES ('62', '388', '1099', '1', '2015-09-15 21:39:10', '21', '21', '');
INSERT INTO `project_branches` VALUES ('63', '555', '1176', '1', '2015-09-15 21:39:10', '108', '108', '');
INSERT INTO `project_branches` VALUES ('64', '57', '1042', '1', '2015-09-15 21:39:10', '24', '25', '');
INSERT INTO `project_branches` VALUES ('65', '441', '1042', '1', '2015-09-15 21:39:10', '86', '82', '');
INSERT INTO `project_branches` VALUES ('66', '685', '1042', '1', '2015-09-15 21:39:10', '22', '22', '');
INSERT INTO `project_branches` VALUES ('67', '512', '1116', '1', '2015-09-15 21:39:10', '193', '193', '');
INSERT INTO `project_branches` VALUES ('68', '540', '1116', '1', '2015-09-15 21:39:10', '90', '1052', '');
INSERT INTO `project_branches` VALUES ('69', '556', '1116', '1', '2015-09-15 21:39:10', '108', '108', '');
INSERT INTO `project_branches` VALUES ('70', '107', '1214', '1', '2015-09-15 21:39:10', '22', '94', '');
INSERT INTO `project_branches` VALUES ('71', '391', '843', '1', '2015-09-15 21:39:10', '189', '21', '');
INSERT INTO `project_branches` VALUES ('72', '66', '1106', '1', '2015-09-15 21:39:10', '24', '96', '');
INSERT INTO `project_branches` VALUES ('73', '238', '599', '1', '2015-09-15 21:39:10', '70', '65', '');
INSERT INTO `project_branches` VALUES ('74', '918', '1238', '1', '2015-11-10 14:01:56', '158', '158', '');
INSERT INTO `project_branches` VALUES ('75', '1041', '1260', '1', '2016-03-03 15:07:29', '1009', '1009', '');
INSERT INTO `project_branches` VALUES ('76', '293', '1090', '1', '2015-09-15 21:39:10', '68', '68', '');
INSERT INTO `project_branches` VALUES ('77', '823', '1090', '1', '2015-10-27 16:05:49', '243', '243', '');
INSERT INTO `project_branches` VALUES ('78', '565', '1154', '1', '2015-09-15 21:39:10', '108', '108', '');
INSERT INTO `project_branches` VALUES ('79', '860', '1231', '1', '2015-10-30 06:47:52', '108', '108', '');
INSERT INTO `project_branches` VALUES ('80', '1052', '1269', '1', '2016-08-29 07:50:56', '521', '521', '');
INSERT INTO `project_branches` VALUES ('81', '333', '9', '1', '2015-09-15 21:39:10', '62', '62', '');
INSERT INTO `project_branches` VALUES ('82', '1031', '9', '1', '2016-02-25 16:43:56', '987', '987', '');
INSERT INTO `project_branches` VALUES ('83', '933', '86', '1', '2015-11-11 17:57:03', '842', '294', '');
INSERT INTO `project_branches` VALUES ('84', '228', '430', '1', '2015-09-15 21:39:10', '175', '1009', '');
INSERT INTO `project_branches` VALUES ('85', '544', '1065', '1', '2015-09-15 21:39:10', '115', '115', '');
INSERT INTO `project_branches` VALUES ('86', '543', '1066', '1', '2015-09-15 21:39:10', '115', '115', '');
INSERT INTO `project_branches` VALUES ('87', '875', '1066', '1', '2015-11-03 08:17:24', '193', '193', '');
INSERT INTO `project_branches` VALUES ('88', '542', '1067', '1', '2015-09-15 21:39:10', '115', '115', '');
INSERT INTO `project_branches` VALUES ('89', '37', '1071', '1', '2015-09-15 21:39:10', '27', '115', '');
INSERT INTO `project_branches` VALUES ('90', '16', '103', '1', '2015-09-15 21:39:10', '20', '89', '');
INSERT INTO `project_branches` VALUES ('91', '49', '103', '1', '2015-09-15 21:39:10', '7', '65', '');
INSERT INTO `project_branches` VALUES ('92', '73', '103', '1', '2015-09-15 21:39:10', '29', '29', '');
INSERT INTO `project_branches` VALUES ('93', '81', '103', '1', '2015-09-15 21:39:10', '7', '199', '');
INSERT INTO `project_branches` VALUES ('94', '93', '103', '1', '2015-09-15 21:39:10', '20', '84', '');
INSERT INTO `project_branches` VALUES ('95', '145', '103', '1', '2015-09-15 21:39:10', '22', '22', '');
INSERT INTO `project_branches` VALUES ('96', '194', '103', '1', '2015-09-15 21:39:10', '66', '173', 'I have made changes in some sections as follows: \r\n1. John Hebert Ainembabazi moved on from IITA.\r\n2. ANSAF is not a budget holder');
INSERT INTO `project_branches` VALUES ('97', '272', '103', '1', '2015-09-15 21:39:10', '208', '880', '');
INSERT INTO `project_branches` VALUES ('98', '381', '103', '1', '2015-09-15 21:39:10', '234', '89', '');
INSERT INTO `project_branches` VALUES ('99', '390', '103', '1', '2015-09-15 21:39:10', '21', '21', '');
INSERT INTO `project_branches` VALUES ('100', '516', '103', '1', '2015-09-15 21:39:10', '26', '193', '');
INSERT INTO `project_branches` VALUES ('101', '934', '103', '1', '2015-11-11 19:59:01', '22', '22', '');
INSERT INTO `project_branches` VALUES ('102', '115', '1216', '1', '2015-09-15 21:39:10', '22', '78', '');
INSERT INTO `project_branches` VALUES ('103', '153', '1216', '1', '2015-09-15 21:39:10', '24', '162', '');
INSERT INTO `project_branches` VALUES ('104', '887', '1216', '1', '2015-11-04 13:35:15', '162', '162', '');
INSERT INTO `project_branches` VALUES ('105', '28', '1217', '1', '2015-09-15 21:39:10', '24', '96', '');
INSERT INTO `project_branches` VALUES ('106', '331', '1051', '1', '2015-09-15 21:39:10', '60', '58', '');
INSERT INTO `project_branches` VALUES ('107', '270', '51', '1', '2015-09-15 21:39:10', '208', '880', '');
