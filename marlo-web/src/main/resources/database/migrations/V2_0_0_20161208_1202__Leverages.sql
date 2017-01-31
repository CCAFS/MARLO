/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-12-08 10:16:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `project_leverage`
-- ----------------------------
DROP TABLE IF EXISTS `project_leverage`;
CREATE TABLE `project_leverage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `title` varchar(500) NOT NULL,
  `institution` bigint(20) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `flagship` bigint(20) DEFAULT NULL,
  `budget` double DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `project_id` (`project_id`),
  KEY `modified_by` (`modified_by`),
  KEY `created_by` (`created_by`),
  KEY `institution` (`institution`),
  KEY `flagship` (`flagship`),
  CONSTRAINT `project_leverage_ibfk_5` FOREIGN KEY (`flagship`) REFERENCES `crp_programs` (`id`),
  CONSTRAINT `project_leverage_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `project_leverage_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_leverage_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_leverage_ibfk_4` FOREIGN KEY (`institution`) REFERENCES `institutions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_leverage
-- ----------------------------
INSERT INTO `project_leverage` VALUES ('1', '19', 'ICON', '116', '2015', '86', '50000', '1', '2016-02-16 04:15:24', '86', '86', '');
INSERT INTO `project_leverage` VALUES ('2', '58', 'Análisis integral de sistemas productivos en Colombia para la adaptación al cambio climático.', '126', '2015', '85', '230000', '0', '2016-02-18 16:04:11', '844', '54', '');
INSERT INTO `project_leverage` VALUES ('3', '58', 'test', '331', '2015', '87', '432', '0', '2016-02-18 16:04:11', '844', '54', '');
INSERT INTO `project_leverage` VALUES ('4', '34', 'ICRAF', '1143', '2015', '85', '20585', '1', '2016-02-18 16:18:50', '91', '91', '');
INSERT INTO `project_leverage` VALUES ('5', '34', 'IUCN', '792', '2015', '85', '5000', '1', '2016-02-18 16:18:50', '91', '91', '');
INSERT INTO `project_leverage` VALUES ('6', '34', 'SARI', '102', '2015', '85', '2500', '1', '2016-02-18 16:18:50', '91', '91', '');
INSERT INTO `project_leverage` VALUES ('7', '34', 'ISRA', '10', '2015', '85', '2500', '1', '2016-02-18 16:18:50', '91', '91', '');
INSERT INTO `project_leverage` VALUES ('8', '34', 'INERA', '110', '2015', '85', '1250', '1', '2016-02-18 16:18:50', '91', '91', '');
INSERT INTO `project_leverage` VALUES ('9', '34', 'INRAN', '249', '2015', '85', '1250', '1', '2016-02-18 16:18:50', '91', '91', '');
INSERT INTO `project_leverage` VALUES ('10', '111', 'NERC Knowledge Exchange Fellowship', '461', '2015', '86', '21625', '1', '2016-02-18 18:14:27', '50', '50', '');
INSERT INTO `project_leverage` VALUES ('11', '111', '', null, '2015', null, '0', '0', '2016-02-18 18:14:06', '50', '50', '');
INSERT INTO `project_leverage` VALUES ('12', '142', 'Stakeholder engagement for policy action to mainstream climate change in the coffee strategy', '1241', '2015', '87', '5000', '1', '2016-02-25 12:25:11', '271', '271', '');
INSERT INTO `project_leverage` VALUES ('13', '40', 'Intercomparision of agricultural models (sorghum)-ICRISAT', '391', '2015', '84', '20000', '1', '2016-02-19 13:44:21', '96', '96', '');
INSERT INTO `project_leverage` VALUES ('14', '40', 'Strengthening the Capacity of IGAD in Building Disaster Resilience in the Horn of Africa', '251', '2015', '84', '28000', '1', '2016-02-19 13:44:21', '96', '96', '');
INSERT INTO `project_leverage` VALUES ('15', '140', 'There is no leverage for this project', null, '2015', '84', '0', '1', '2016-02-20 16:12:32', '96', '96', '');
INSERT INTO `project_leverage` VALUES ('16', '49', 'Surveillance and early-warning systems for climate-sensitive diseases in Vietnam and Laos', '1103', '2015', '84', '0', '1', '2016-03-01 14:41:26', '157', '157', '');
INSERT INTO `project_leverage` VALUES ('17', '101', 'Enhancing ecosystem services of the Maulawin spring protected landscape (Philippine Tropical Forest Conservation Foundation, Inc).', '1084', '2015', '85', '43570', '1', '2016-03-01 16:05:30', '844', '844', '');
INSERT INTO `project_leverage` VALUES ('18', '49', 'Surveillance and early-warning systems for climate-sensitive diseases in Vietnam and Laos', '1104', '2015', '84', '0', '1', '2016-03-01 14:41:26', '157', '157', '');
INSERT INTO `project_leverage` VALUES ('19', '21', 'SNV supports assessment of co-benefits of AWD', '380', '2015', '86', '24000', '1', '2016-02-23 09:09:43', '108', '108', '');
INSERT INTO `project_leverage` VALUES ('20', '66', 'Community seedbanks in South Africa', '1226', '2015', '87', '140000', '1', '2016-03-03 13:23:09', '68', '68', '');
INSERT INTO `project_leverage` VALUES ('21', '141', '', '1146', '2015', '87', '0', '1', '2016-02-23 11:26:41', '271', '271', '');
INSERT INTO `project_leverage` VALUES ('22', '49', 'Surveillance and early-warning systems for climate-sensitive diseases in Vietnam and Laos', '1253', '2015', '87', '0', '1', '2016-03-01 14:41:26', '157', '157', '');
INSERT INTO `project_leverage` VALUES ('23', '49', 'Surveillance and early-warning systems for climate-sensitive diseases in Vietnam and Laos', '1254', '2015', '87', '0', '1', '2016-03-01 14:41:26', '157', '157', '');
INSERT INTO `project_leverage` VALUES ('24', '20', 'Total inkind contribution to the project', '685', '2015', '85', '50000', '1', '2016-02-24 03:26:27', '87', '87', '');
INSERT INTO `project_leverage` VALUES ('25', '1', 'Adaptation at Scale in Semi-Arid Regions (ASSAR) project', '1264', '2015', '87', '113300', '1', '2016-03-03 15:36:52', '199', '199', '');
INSERT INTO `project_leverage` VALUES ('26', '108', 'Climate Services for Africa', '156', '2015', '84', '479416', '1', '2016-03-02 12:45:34', '25', '25', '');
INSERT INTO `project_leverage` VALUES ('27', '108', 'Support to Africa Interact participants attending SBSTA 42 meeting in Bonn', '762', '2015', '87', '22486', '1', '2016-03-02 12:45:34', '25', '25', '');
INSERT INTO `project_leverage` VALUES ('28', '108', 'Working Session on Agriculture\\Landuse', '153', '2015', '87', '7761.6', '1', '2016-03-02 12:45:34', '25', '25', '');
INSERT INTO `project_leverage` VALUES ('29', '108', 'Working Session on Agriculture\\Landuse', '1256', '2015', '87', '16354', '1', '2016-03-02 12:45:34', '25', '25', '');
INSERT INTO `project_leverage` VALUES ('30', '39', 'FAUSIK (expected) from DfID in 2016', '50', '2015', '85', '300000', '1', '2016-02-26 11:14:27', '95', '95', '');
INSERT INTO `project_leverage` VALUES ('31', '118', '', null, '2015', null, '0', '0', '2016-02-26 15:35:10', '54', '54', '');
INSERT INTO `project_leverage` VALUES ('32', '118', 'RENACER PARA LAS COMUNIDADES INDÍGENAS EN NATAGAIMA TOLIMA – ADAPTACIÓN AL CAMBIO CLIMÁTICO Y SEGURI', '1255', '2015', '85', '40000', '1', '2016-02-26 15:42:49', '843', '843', '');
INSERT INTO `project_leverage` VALUES ('33', '247', 'test', '333', '2015', '84', '40000', '0', '2016-02-26 15:43:17', '843', '843', '');
INSERT INTO `project_leverage` VALUES ('34', '247', '', null, '2015', '85', '8.546546546465466e17', '0', '2016-02-26 15:44:49', '843', '843', '');
INSERT INTO `project_leverage` VALUES ('35', '247', '', null, '2015', null, '0', '0', '2016-02-26 15:44:49', '843', '843', '');
INSERT INTO `project_leverage` VALUES ('36', '8', 'Workshop on Climate Smart Agriculture in Asia', '1257', '2015', '87', '90562', '1', '2016-02-29 14:47:36', '181', '181', '');
INSERT INTO `project_leverage` VALUES ('37', '61', 'Promoting Climate Resilient Agriculture in Nepal', '1100', '2015', '85', '2500000', '1', '2016-03-03 03:33:39', '21', '21', '');
INSERT INTO `project_leverage` VALUES ('38', '101', 'Typhoon Glenda Relief (American Jewish Joint Distribution Committee -JDC)', '1084', '2015', '85', '13666', '1', '2016-03-01 16:05:30', '844', '844', '');
INSERT INTO `project_leverage` VALUES ('39', '48', '', null, '2015', null, '0', '0', '2016-03-01 14:18:36', '115', '115', '');
INSERT INTO `project_leverage` VALUES ('43', '101', 'Scaling out tested climate smart approaches at the municipal level in Guinyangan Quezon (Peace and Equity Foundation - PEF)', '1084', '2015', '85', '13666', '1', '2016-03-01 16:05:30', '844', '844', '');
INSERT INTO `project_leverage` VALUES ('44', '101', 'Bridging Relief and recovery towards resilience building in disaster affected areas in Panay (American Jewish Joint Distribution Committee-JDC)', '1084', '2015', '85', '123831', '1', '2016-03-01 16:05:30', '844', '844', '');
INSERT INTO `project_leverage` VALUES ('45', '6', 'Policy brief - Efficient Water Use Technologies: Their Adoption, Upscaling and Policy Implications', '162', '2015', '85', '2000', '1', '2016-03-03 14:14:04', '173', '173', '');
INSERT INTO `project_leverage` VALUES ('46', '6', 'Policy brief - Climate Change and Gender: Towards A Sustainable Technology-Friendly Future in Uganda', '1258', '2015', '87', '2500', '1', '2016-03-03 14:14:04', '173', '173', '');
INSERT INTO `project_leverage` VALUES ('47', '6', 'Development of agricultural sector climate change mainstreaming guidelines', '1241', '2015', '87', '28839', '1', '2016-03-03 14:14:04', '173', '173', '');
INSERT INTO `project_leverage` VALUES ('48', '6', 'Two regional consultative workshops (northern Uganda and WestNile) for development of agriculture sector climate change mainstreaming guidelines', '1241', '2015', '87', '0', '0', '2016-03-02 15:37:49', '173', '173', '');
INSERT INTO `project_leverage` VALUES ('49', '6', 'Scenario-guided review of the Agriculture Sector Strategy Program (ASSP), MAAIF, Uganda', '1259', '2015', '87', '2230', '1', '2016-03-03 14:14:04', '173', '173', '');
INSERT INTO `project_leverage` VALUES ('50', '63', 'ASEAN-CSR project', '862', '2015', '87', '10000', '1', '2016-03-02 21:00:26', '65', '65', '');
INSERT INTO `project_leverage` VALUES ('51', '63', 'GIZ consultation meeting', '1090', '2015', '87', '6500', '1', '2016-03-02 21:00:26', '65', '65', '');
INSERT INTO `project_leverage` VALUES ('52', '63', 'MoC Field sites', '1111', '2015', '87', '1000', '1', '2016-03-02 21:00:26', '65', '65', '');
INSERT INTO `project_leverage` VALUES ('53', '63', 'MAFF project Cambodia', '574', '2015', '87', '500000', '1', '2016-03-02 21:00:26', '65', '65', '');
INSERT INTO `project_leverage` VALUES ('54', '63', 'Oxfam Philippines scenarios workshop', '867', '2015', '87', '6000', '1', '2016-03-02 21:00:26', '65', '65', '');
INSERT INTO `project_leverage` VALUES ('55', '63', 'UNDP INDC Costa Rica workshop', '129', '2015', '87', '4500', '1', '2016-03-02 21:00:26', '65', '65', '');
INSERT INTO `project_leverage` VALUES ('56', '63', 'ACICAFOC local downscaling SAG plan Honduras', '875', '2015', '87', '27500', '1', '2016-03-02 21:00:26', '65', '65', '');
INSERT INTO `project_leverage` VALUES ('57', '125', 'CCAFS FAO CSA Practice Brief', '69', '2015', '87', '20000', '1', '2016-03-03 20:36:11', '842', '842', '');
INSERT INTO `project_leverage` VALUES ('58', '61', 'Scaling out climate smart agriculture in Nepal', '604', '2015', '85', '800000', '1', '2016-03-03 03:33:39', '21', '21', '');
INSERT INTO `project_leverage` VALUES ('59', '115', 'Infomediaries as complementary knowledge channels of CSA in the Philippines', '488', '2015', '85', '45302.21', '1', '2016-03-03 03:46:25', '193', '193', '');
INSERT INTO `project_leverage` VALUES ('60', '25', 'ICAR-National Innovation on Climate Resilient Agriculture (NICRA)-a large project that contributes to CCAFS. A ltter of leveraged funds from ICAR, Govt of India is available.', '17', '2015', '85', '300000', '1', '2016-03-03 05:58:46', '89', '89', '');
INSERT INTO `project_leverage` VALUES ('61', '100', 'Literature coding for governance indicators', '1173', '2015', '87', '10000', '1', '2016-03-04 06:07:21', '5', '5', '');
INSERT INTO `project_leverage` VALUES ('62', '100', 'Hosting a workshop on discourse and power', '198', '2015', '87', '2500', '1', '2016-03-04 06:07:21', '5', '5', '');
INSERT INTO `project_leverage` VALUES ('63', '54', 'Collaboration on various fields related to rice and climate change in Vietnam and the Philippines (joint proposal development, training modules on CSV for extension staff etc))', '1263', '2015', '85', '10000', '1', '2016-03-03 15:21:02', '108', '108', '');
INSERT INTO `project_leverage` VALUES ('64', '125', 'Gender in climate-smart agriculture : module 18 for gender in agriculture sourcebook.', '125', '2015', '87', '30000', '1', '2016-03-03 20:36:11', '842', '842', '');
INSERT INTO `project_leverage` VALUES ('65', '69', 'Preparing a new software package for the analysis of climatic data', '207', '2015', '87', '50000', '1', '2016-03-03 18:54:47', '16', '16', '');
INSERT INTO `project_leverage` VALUES ('66', '69', 'Preparing a new software package for the analysis of climatic data', '303', '2015', '87', '25000', '1', '2016-03-03 18:54:47', '16', '16', '');
INSERT INTO `project_leverage` VALUES ('67', '46', 'Spurring a Transformation for Agriculture through Remote Sensing (STARS)', '154', '2015', '84', '514785.24', '1', '2016-03-03 22:19:32', '78', '78', '');
INSERT INTO `project_leverage` VALUES ('68', '112', '', null, '2015', null, '0', '0', '2016-03-04 00:49:42', '18', '18', '');
INSERT INTO `project_leverage` VALUES ('69', '100', 'Co-financing working group workshop and coding through students', '323', '2015', '87', '15500', '1', '2016-03-04 06:07:21', '5', '5', '');
