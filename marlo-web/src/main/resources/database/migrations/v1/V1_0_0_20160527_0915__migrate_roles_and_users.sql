/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50626
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2016-05-27 10:33:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `crp_users`
-- ----------------------------
DROP TABLE IF EXISTS `crp_users`;
CREATE TABLE `crp_users` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`user_id`  bigint(20) NOT NULL ,
`crp_id`  bigint(20) NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `user_id` (`user_id`) USING BTREE ,
INDEX `crp_id` (`crp_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=514

;

-- ----------------------------
-- Records of crp_users
-- ----------------------------
BEGIN;
INSERT INTO `crp_users` VALUES ('3', '1', '1', '1'), ('4', '2', '1', '1'), ('5', '3', '1', '1'), ('6', '5', '1', '1'), ('7', '7', '1', '1'), ('8', '13', '1', '1'), ('9', '14', '1', '1'), ('10', '16', '1', '1'), ('11', '17', '1', '1'), ('12', '18', '1', '1'), ('13', '19', '1', '1'), ('14', '20', '1', '1'), ('15', '21', '1', '1'), ('16', '22', '1', '1'), ('17', '23', '1', '1'), ('18', '24', '1', '1'), ('19', '25', '1', '1'), ('20', '26', '1', '1'), ('21', '27', '1', '1'), ('22', '28', '1', '1'), ('23', '29', '1', '1'), ('24', '31', '1', '1'), ('25', '32', '1', '1'), ('26', '50', '1', '1'), ('27', '51', '1', '1'), ('28', '52', '1', '1'), ('29', '53', '1', '1'), ('30', '54', '1', '1'), ('31', '55', '1', '1'), ('32', '56', '1', '1'), ('33', '57', '1', '1'), ('34', '58', '1', '1'), ('35', '59', '1', '1'), ('36', '60', '1', '1'), ('37', '61', '1', '1'), ('38', '62', '1', '1'), ('39', '63', '1', '1'), ('40', '64', '1', '1'), ('41', '65', '1', '1'), ('42', '66', '1', '1'), ('43', '67', '1', '1'), ('44', '68', '1', '1'), ('45', '69', '1', '1'), ('46', '72', '1', '1'), ('47', '73', '1', '1'), ('48', '77', '1', '1'), ('49', '78', '1', '1'), ('50', '79', '1', '1'), ('51', '80', '1', '1'), ('52', '81', '1', '1'), ('53', '82', '1', '1'), ('54', '83', '1', '1'), ('55', '84', '1', '1'), ('56', '85', '1', '1'), ('57', '86', '1', '1'), ('58', '87', '1', '1'), ('59', '88', '1', '1'), ('60', '89', '1', '1'), ('61', '90', '1', '1'), ('62', '91', '1', '1'), ('63', '92', '1', '1'), ('64', '93', '1', '1'), ('65', '94', '1', '1'), ('66', '95', '1', '1'), ('67', '96', '1', '1'), ('68', '97', '1', '1'), ('69', '98', '1', '1'), ('70', '99', '1', '1'), ('71', '100', '1', '1'), ('72', '101', '1', '1'), ('73', '102', '1', '1'), ('74', '103', '1', '1'), ('75', '104', '1', '1'), ('76', '105', '1', '1'), ('77', '106', '1', '1'), ('78', '107', '1', '1'), ('79', '108', '1', '1'), ('80', '109', '1', '1'), ('81', '110', '1', '1'), ('82', '111', '1', '1'), ('83', '112', '1', '1'), ('84', '113', '1', '1'), ('85', '114', '1', '1'), ('86', '115', '1', '1'), ('87', '116', '1', '1'), ('88', '117', '1', '1'), ('89', '118', '1', '1'), ('90', '119', '1', '1'), ('91', '120', '1', '1'), ('92', '122', '1', '1'), ('93', '123', '1', '1'), ('94', '124', '1', '1'), ('95', '127', '1', '1'), ('96', '128', '1', '1'), ('97', '130', '1', '1'), ('98', '131', '1', '1'), ('99', '132', '1', '1'), ('100', '133', '1', '1'), ('101', '135', '1', '1'), ('102', '136', '1', '1');
INSERT INTO `crp_users` VALUES ('103', '137', '1', '1'), ('104', '139', '1', '1'), ('105', '140', '1', '1'), ('106', '141', '1', '1'), ('107', '142', '1', '1'), ('108', '143', '1', '1'), ('109', '144', '1', '1'), ('110', '145', '1', '1'), ('111', '150', '1', '1'), ('112', '151', '1', '1'), ('113', '153', '1', '1'), ('114', '154', '1', '1'), ('115', '157', '1', '1'), ('116', '158', '1', '1'), ('117', '160', '1', '1'), ('118', '162', '1', '1'), ('119', '167', '1', '1'), ('120', '168', '1', '1'), ('121', '169', '1', '1'), ('122', '170', '1', '1'), ('123', '172', '1', '1'), ('124', '173', '1', '1'), ('125', '174', '1', '1'), ('126', '175', '1', '1'), ('127', '176', '1', '1'), ('128', '177', '1', '1'), ('129', '178', '1', '1'), ('130', '179', '1', '1'), ('131', '180', '1', '1'), ('132', '181', '1', '1'), ('133', '182', '1', '1'), ('134', '183', '1', '1'), ('135', '184', '1', '1'), ('136', '186', '1', '1'), ('137', '187', '1', '1'), ('138', '188', '1', '1'), ('139', '189', '1', '1'), ('140', '190', '1', '1'), ('141', '191', '1', '1'), ('142', '193', '1', '1'), ('143', '194', '1', '1'), ('144', '195', '1', '1'), ('145', '196', '1', '1'), ('146', '197', '1', '1'), ('147', '199', '1', '1'), ('148', '201', '1', '1'), ('149', '206', '1', '1'), ('150', '208', '1', '1'), ('151', '212', '1', '1'), ('152', '223', '1', '1'), ('153', '224', '1', '1'), ('154', '226', '1', '1'), ('155', '227', '1', '1'), ('156', '228', '1', '1'), ('157', '230', '1', '1'), ('158', '231', '1', '1'), ('159', '232', '1', '1'), ('160', '233', '1', '1'), ('161', '234', '1', '1'), ('162', '235', '1', '1'), ('163', '236', '1', '1'), ('164', '241', '1', '1'), ('165', '243', '1', '1'), ('166', '244', '1', '1'), ('167', '245', '1', '1'), ('168', '246', '1', '1'), ('169', '247', '1', '1'), ('170', '248', '1', '1'), ('171', '249', '1', '1'), ('172', '250', '1', '1'), ('173', '251', '1', '1'), ('174', '252', '1', '1'), ('175', '254', '1', '1'), ('176', '255', '1', '1'), ('177', '256', '1', '1'), ('178', '257', '1', '1'), ('179', '258', '1', '1'), ('180', '259', '1', '1'), ('181', '263', '1', '1'), ('182', '270', '1', '1'), ('183', '271', '1', '1'), ('184', '272', '1', '1'), ('185', '273', '1', '1'), ('186', '275', '1', '1'), ('187', '276', '1', '1'), ('188', '277', '1', '1'), ('189', '285', '1', '1'), ('190', '286', '1', '1'), ('191', '287', '1', '1'), ('192', '288', '1', '1'), ('193', '293', '1', '1'), ('194', '294', '1', '1'), ('195', '295', '1', '1'), ('196', '296', '1', '1'), ('197', '297', '1', '1'), ('198', '298', '1', '1'), ('199', '300', '1', '1'), ('200', '302', '1', '1'), ('201', '303', '1', '1'), ('202', '304', '1', '1');
INSERT INTO `crp_users` VALUES ('203', '306', '1', '1'), ('204', '307', '1', '1'), ('205', '308', '1', '1'), ('206', '316', '1', '1'), ('207', '317', '1', '1'), ('208', '318', '1', '1'), ('209', '319', '1', '1'), ('210', '349', '1', '1'), ('211', '361', '1', '1'), ('212', '364', '1', '1'), ('213', '366', '1', '1'), ('214', '367', '1', '1'), ('215', '385', '1', '1'), ('216', '386', '1', '1'), ('217', '387', '1', '1'), ('218', '417', '1', '1'), ('219', '421', '1', '1'), ('220', '430', '1', '1'), ('221', '449', '1', '1'), ('222', '450', '1', '1'), ('223', '470', '1', '1'), ('224', '472', '1', '1'), ('225', '473', '1', '1'), ('226', '474', '1', '1'), ('227', '475', '1', '1'), ('228', '478', '1', '1'), ('229', '492', '1', '1'), ('230', '508', '1', '1'), ('231', '521', '1', '1'), ('232', '527', '1', '1'), ('233', '532', '1', '1'), ('234', '544', '1', '1'), ('235', '557', '1', '1'), ('236', '564', '1', '1'), ('237', '565', '1', '1'), ('238', '566', '1', '1'), ('239', '567', '1', '1'), ('240', '586', '1', '1'), ('241', '588', '1', '1'), ('242', '589', '1', '1'), ('243', '606', '1', '1'), ('244', '607', '1', '1'), ('245', '609', '1', '1'), ('246', '827', '1', '1'), ('247', '830', '1', '1'), ('248', '831', '1', '1'), ('249', '832', '1', '1'), ('250', '833', '1', '1'), ('251', '842', '1', '1'), ('252', '843', '1', '1'), ('253', '844', '1', '1'), ('254', '847', '1', '1'), ('255', '848', '1', '1'), ('256', '852', '1', '1'), ('257', '855', '1', '1'), ('258', '856', '1', '1'), ('259', '857', '1', '1'), ('260', '858', '1', '1'), ('261', '862', '1', '1'), ('262', '863', '1', '1'), ('263', '864', '1', '1'), ('264', '869', '1', '1'), ('265', '871', '1', '1'), ('266', '872', '1', '1'), ('267', '878', '1', '1'), ('268', '880', '1', '1'), ('269', '881', '1', '1'), ('270', '905', '1', '1'), ('271', '910', '1', '1'), ('272', '913', '1', '1'), ('273', '915', '1', '1'), ('274', '917', '1', '1'), ('275', '918', '1', '1'), ('276', '922', '1', '1'), ('277', '923', '1', '1'), ('278', '924', '1', '1'), ('279', '925', '1', '1'), ('280', '926', '1', '1'), ('281', '928', '1', '1'), ('282', '948', '1', '1'), ('283', '952', '1', '1'), ('284', '955', '1', '1'), ('285', '957', '1', '1'), ('286', '961', '1', '1'), ('287', '963', '1', '1'), ('288', '965', '1', '1'), ('289', '987', '1', '1'), ('290', '988', '1', '1'), ('291', '992', '1', '1'), ('292', '994', '1', '1'), ('293', '996', '1', '1'), ('294', '998', '1', '1'), ('295', '999', '1', '1'), ('296', '1002', '1', '1'), ('297', '1007', '1', '1'), ('298', '1009', '1', '1'), ('299', '1014', '1', '1'), ('300', '1025', '1', '1'), ('301', '1051', '1', '1'), ('302', '1052', '1', '1');
INSERT INTO `crp_users` VALUES ('303', '1053', '1', '1'), ('304', '1054', '1', '1'), ('305', '1056', '1', '1'), ('306', '1057', '1', '1');
COMMIT;

-- ----------------------------
-- Table structure for `crps`
-- ----------------------------
DROP TABLE IF EXISTS `crps`;
CREATE TABLE `crps` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`name`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`acronym`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=7

;

-- ----------------------------
-- Records of crps
-- ----------------------------
BEGIN;
INSERT INTO `crps` VALUES ('1', 'CCFAS', 'ccafs', '1'), ('3', 'PIM', 'pim', '1'), ('4', 'WLE', 'wle', '1'), ('5', 'A4NH', 'a4nh', '1'), ('6', 'Admin', 'Admin', '0');
COMMIT;

-- ----------------------------
-- Table structure for `permissions`
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`permission`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`type`  int(11) NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=422

;

-- ----------------------------
-- Records of permissions
-- ----------------------------
BEGIN;
INSERT INTO `permissions` VALUES ('1', 'crp:{0}:*', 'Full privileges on all the platform', '0'), ('2', 'crp:{0}:project:*', 'Can update all the planning section contents', '0'), ('52', 'crp:{0}:project:{1}:budget:annualW1w2', 'Can update the W1/W2 budget in the project budget section in planning round', '1'), ('62', 'crp:{0}:summaries:*', 'Can update all the summaries section contents', '0'), ('63', 'crp:{0}:summaries:board:*', 'Can use all the functions in the summaries board', '0'), ('67', 'crp:{0}:project:{1}:partner:leader:canEdit', 'Can update the planning project partners leader', '1'), ('68', 'crp:{0}:project:{1}:partner:cordinator:canEdit', 'Can update the planning project partners cordinator', '1'), ('69', 'crp:{0}:project:{1}:description:*', 'Can update all the reporting section in project description.', '1'), ('70', 'crp:{0}:project:{1}:description:status', 'Can update all the reporting section in project description', '1'), ('73', 'crp:{0}:project:{1}:description:annualReportDonor', 'Can upload the report to the donor (bilateral project) in reporting round.', '1'), ('74', 'crp:{0}:project:{1}:description:bilateralContract', 'Can update the bilateral contract in reporting round.', '1'), ('75', 'crp:{0}:project:{1}:description:endDate', 'Can update the field end date in reporting round.', '1'), ('76', 'crp:{0}:project:{1}:description:flagships', 'Can update the flagships selections in reporting round.', '1'), ('77', 'crp:{0}:project:{1}:description:managementLiaison', 'Can update the ML in reporting round.', '1'), ('78', 'crp:{0}:project:{1}:description:regions', 'Can update the regions selections in reporting round.', '1'), ('79', 'crp:{0}:project:{1}:description:startDate', 'Can update the field start date in reporting round.', '1'), ('80', 'crp:{0}:project:{1}:description:statusDescription', 'Can update the project status in reporting round.', '1'), ('81', 'crp:{0}:project:{1}:description:summary', 'Can update the project summary in reporting round.', '1'), ('82', 'crp:{0}:project:{1}:description:title', 'Can update the project title in reporting round', '1'), ('83', 'crp:{0}:project:{1}:description:canEdit', 'Can update the project description section in reporting round.', '1'), ('84', 'crp:{0}:project:{1}:description:workplan', 'Can upload the workplan (ccafs projects) in reporting round.', '1'), ('88', 'crp:{0}:project:{1}:partners:*', 'Can update everything on the partners section in reporting round.', '1'), ('89', 'crp:{0}:project:{1}:partners:coordinator', 'Can update the project coordinator (PC) in reporting round.', '1'), ('90', 'crp:{0}:project:{1}:partners:leader', 'Can update the project leader (PL) in reporting round.', '1'), ('91', 'crp:{0}:project:{1}:partners:ppa', 'Can udpate the PPA partners in reporting round.', '1'), ('92', 'crp:{0}:project:{1}:partners:canEdit', 'Can update some content in project partners section in reporting round.', '1'), ('93', 'crp:{0}:project:{1}:locations:*', 'Can update everything in project locations in reporting round.', '1'), ('94', 'crp:{0}:project:{1}:outcomes:*', 'Can update everything in project outcomes in reporting round.', '1'), ('96', 'crp:{0}:project:projectsList:*', 'Can use all the functions in the projects list section in reporting round.', '0'), ('97', 'crp:{0}:project:projectsList:addBilateralProject', 'Can use the \"add bilateral project\" button in the projects list section in reporting round.', '0'), ('98', 'crp:{0}:project:projectsList:addCoreProject', 'Can use the \"add core project\" button in the projects list section in reporting round.', '0'), ('99', 'crp:{0}:project:{1}:manage:deleteProject', 'Can use the \"Delete project\" button in any section in reporting round.', '1'), ('100', 'crp:{0}:project:{1}:manage:submitProject', 'Can use the \"Submit project\" button in any section in reporting round.', '1'), ('101', 'crp:{0}:project:{1}:ccafsOutcomes:*', 'Can update everything in the CCAFS outcomes section in the reporting round.', '1'), ('102', 'crp:{0}:project:{1}:otherContributions:*', 'Can update the other contributions section in the reporting round.', '1'), ('104', 'crp:{0}:project:{1}:outputs:*', 'Can update the Overview by MOGs (outputs) section in the reporting round.', '1'), ('105', 'crp:{0}:project:{1}:deliverable:*', 'Can update the Deliverable section in the reporting round.', '1'), ('106', 'crp:{0}:project:{1}:deliverablesList:*', 'Can update the Deliverables List section in the reporting round.', '1'), ('110', 'crp:{0}:project:{1}:activities:*', 'Can update the Project Activities section in the reporting round.', '1'), ('112', 'crp:{0}:project:{1}:caseStudies:*', 'Can update the Outcomes Case Studies section in the planning round.', '1'), ('118', 'crp:{0}:project:{1}:outcomes:canEdit', 'Can update project outcomes in reporting round', '1'), ('119', 'crp:{0}:project:{1}:outcomes:statement', 'Can update project statement in project outcomes in reporting round.', '1'), ('120', 'crp:{0}:project:{1}:outcomes:annualProgress', 'Can update annual progress in project outcomes in reporting round.', '1'), ('121', 'crp:{0}:project:{1}:outcomes:communicationEngagement', 'Can update communication engagement in project outcomes in reporting round.', '1'), ('122', 'crp:{0}:project:{1}:outcomes:uploadSummary', 'Can upload a summary file in project outcomes in reporting round.', '1'), ('130', 'crp:{0}:project:{1}:ccafsOutcomes:canEdit', 'Can make changes in the ccafs outcomes section in reporting round.', '1'), ('131', 'crp:{0}:project:{1}:ccafsOutcomes:target', 'Can update the target value in ccafs outcomes section in reporting round.', '1'), ('132', 'crp:{0}:project:{1}:ccafsOutcomes:achieved', 'Can update the target achieved in ccafs outcomes section in reporting round.', '1'), ('133', 'crp:{0}:project:{1}:ccafsOutcomes:description', 'Can update the expected annual contribution in ccafs outcomes section in reporting round.', '1'), ('134', 'crp:{0}:project:{1}:ccafsOutcomes:narrativeTargets', 'Can update the annual contribution achieved in ccafs outcomes section in reporting round.', '1'), ('135', 'crp:{0}:project:{1}:ccafsOutcomes:gender', 'Can update the expected gender contribution in ccafs outcomes section in reporting round.', '1'), ('136', 'crp:{0}:project:{1}:ccafsOutcomes:narrativeGender', 'Can update the gender contribution achieved in ccafs outcomes section in reporting round.', '1'), ('146', 'crp:{0}:project:{1}:otherContributions:canEdit', 'Can make changes in the other contributions section in reporting round.', '1'), ('147', 'crp:{0}:project:{1}:otherContributions:contribution', 'Can update how is contributing in other contributuions section in reporting round.', '1'), ('148', 'crp:{0}:project:{1}:otherContributions:otherContributionIndicator', 'Can update the indicator in other contributions section un reporting round.', '1'), ('149', 'crp:{0}:project:{1}:otherContributions:otherContributionDescription', 'Can update the description of the contribution to the indicator in other contributions section in reporting round.', '1'), ('150', 'crp:{0}:project:{1}:otherContributions:otherContributionTarget', 'Can update the target in other contributions section in reporting round.', '1'), ('151', 'crp:{0}:project:{1}:otherContributions:additionalContribution', 'Can update the contribuition to another center activity in other contributions section in reporting round.', '1'), ('152', 'crp:{0}:project:{1}:otherContributions:natureCollaboration', 'Can update the nature collaboration in other contributions section in reporting round.', '1'), ('153', 'crp:{0}:project:{1}:otherContributions:explainAchieved', 'Can update the ahieved outcome in other contributions section in reporting round.', '1'), ('154', 'crp:{0}:project:{1}:otherContributions:addCRPContribution', 'Can add new CRP in other contributions section in reporting round.', '1'), ('160', 'crp:{0}:project:{1}:outputs:canEdit', 'Can make changes in the overview by mogs section in the reporting round.', '1'), ('161', 'crp:{0}:project:{1}:outputs:expectedAnnualContribution', 'Can update the expected annual contribution in overview by mogs section in reporting round.', '1'), ('162', 'crp:{0}:project:{1}:outputs:socialInclusionDimmension', 'Can update the expected gender contribution in overview by mogs section in the reporting round.', '1'), ('163', 'crp:{0}:project:{1}:outputs:briefSummary', 'Can update the actual contribution in overview by mogs section in the reporting round.', '1'), ('164', 'crp:{0}:project:{1}:outputs:summaryGender', 'Can update the actual gender contribution in the overview by mogs section in the reporting round.', '1'), ('168', 'crp:{0}:project:{1}:deliverablesList:canEdit', 'Can make changes in the deliverables list section in reporting round.', '1'), ('169', 'crp:{0}:project:{1}:deliverablesList:addDeliverable', 'Can add new deliverables in the deliverables list section in reporting round.', '1'), ('170', 'crp:{0}:project:{1}:deliverablesList:removeOldDeliverables', 'Can remove deliverables in the deliverables list section in reporting round.', '1'), ('174', 'crp:{0}:project:{1}:deliverable:canEdit', 'Can make changes in a particular deliverable in the reporting round.', '1'), ('175', 'crp:{0}:project:{1}:deliverable:main', 'Can make changes in the main fields (title, start date) for a particular deliverable in reporting round.', '1'), ('176', 'crp:{0}:project:{1}:deliverable:other', 'Can make changes in the rest of the fields that are not part of \"main\" permission in reporting round.', '1'), ('177', 'crp:{0}:project:{1}:nextUsers:*', 'Can update the Next users section in the planning round.', '1'), ('178', 'crp:{0}:project:{1}:highlights:*', 'Can update the Project Highlights List section in the planning round.', '1'), ('182', 'crp:{0}:project:{1}:highlights:canEdit', 'Can make changes in the Project Highlights List section in the reporting round.', '1'), ('183', 'crp:{0}:project:{1}:highlights:addHighlight', 'Can add new Project Highlights in the reporting round.', '1'), ('184', 'crp:{0}:project:{1}:highlights:removeHighlight', 'Can remove Project Highlights in the reporting round.', '1'), ('185', 'crp:{0}:project:{1}:highlight:*', 'Can update the Project Highlight section in the planning round.', '1'), ('195', 'crp:{0}:project:{1}:activities:canEdit', 'Can make changes in the activities section in the reporting round.', '1'), ('196', 'crp:{0}:project:{1}:activities:title', 'Can update the title of the  activities section in the reporting round.', '1'), ('197', 'crp:{0}:project:{1}:activities:description', 'Can update the description of the activities section in the reporting round.', '1'), ('198', 'crp:{0}:project:{1}:activities:startDate', 'Can update the start date of the activities section in the reporting round.', '1'), ('199', 'crp:{0}:project:{1}:activities:endDate', 'Can update the end date of the activities section in the reporting round.', '1'), ('200', 'crp:{0}:project:{1}:activities:activityStatus', 'Can update the status of the activities section in the reporting round.', '1'), ('201', 'crp:{0}:project:{1}:activities:activityProgress', 'Can update the status justification of the activities section in the reporting round.', '1'), ('202', 'crp:{0}:project:{1}:activities:leader', 'Can update the leader of the activities section in the reporting round.', '1'), ('203', 'crp:{0}:project:{1}:activities:addActivity', 'Can add new activities in the reporting round.', '1'), ('205', 'crp:{0}:project:{1}:budget:*', 'Can update everything on project budgets in reporting round', '1'), ('206', 'crp:{0}:project:{1}:budget:canEdit', 'Can update the planning project budget section in reporting round', '1'), ('207', 'crp:{0}:project:{1}:budget:annualBilateral', 'Can update the W3/Bilateral budget in the project budget section in reporting round', '1'), ('208', 'crp:{0}:project:{1}:budget:annualW1w2:canEdit', 'Can update the W1/W2 budget in the project budget section in reporting round', '1'), ('209', 'crp:{0}:project:{1}:budgetByMog:*', 'Can update everything in project budget by mog section in reporting round.', '1'), ('210', 'crp:{0}:project:{1}:leverages:*', 'Can update the Leverages section in the planning round.', '1'), ('211', 'crp:{0}:project:synthesis:crpIndicators:*', 'Can update everything on CRP Indicatoris in reporting round.', '0'), ('212', 'crp:{0}:project:synthesis:outcomeSynthesis:*', 'Can edit everything on Outcome Synthesis in reporting round.', '0'), ('213', 'crp:{0}:project:synthesis:outcomeSynthesis:canEdit', 'Can edit on Outcome Synthesis in reporting round.', '0'), ('214', 'crp:{0}:project:synthesis:outcomeSynthesis:rplSynthesis', 'Can edit on Regional Synthesis in reporting round.', '0'), ('215', 'crp:{0}:project:synthesis:outcomeSynthesis:fplSynthesis', 'Can edit on Flagship Synthesis in reporting round.', '0'), ('216', 'crp:{0}:project:synthesis:synthesisByMog:*', 'Can edit everything on Synthesis by MOG in reporting round.', '0'), ('217', 'crp:{0}:project:synthesis:synthesisByMog:canEdit', 'Can edit on Synthesis by MOG in reporting round.', '0');
INSERT INTO `permissions` VALUES ('218', 'crp:{0}:project:{1}:evaluation:*', 'Can update project Evaluation', '1'), ('219', 'crp:{0}:project:projectsEvaluation:*', 'Can update Evaluation projects section', '0'), ('220', 'crp:{0}:project:{1}:evaluation:canEdit', 'Can update project Evaluation', '1'), ('221', 'crp:{0}:project:{1}:evaluation:accessPL', 'Can view Project Leaders evaluations', '1'), ('222', 'crp:{0}:project:{1}:evaluation:accessRPL', 'Can view Regional Program evaluations', '1'), ('223', 'crp:{0}:project:{1}:evaluation:accessFPL', 'Can view Flagship Program evaluations', '1'), ('224', 'crp:{0}:project:{1}:evaluation:accessEE', 'Can view External evaluator evaluations', '1'), ('225', 'crp:{0}:project:{1}:evaluation:accessCU', 'Can view Coordination Unit evaluations', '1'), ('226', 'crp:{0}:project:{1}:evaluation:accessPD', 'Can view Program Director evaluations', '1'), ('419', '*', 'Super Admin Permission', '0'), ('420', 'superadmin:canEdit', 'Super admin can edit', '0'), ('421', 'crp:{0}:canEdit', 'Can edit crp admin', '0');
COMMIT;

-- ----------------------------
-- Table structure for `role_permissions`
-- ----------------------------
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`role_id`  bigint(20) NOT NULL ,
`permission_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `roles_permission_roles_idx` (`role_id`) USING BTREE ,
INDEX `roles_permission_user_permission_idx` (`permission_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=613

;

-- ----------------------------
-- Records of role_permissions
-- ----------------------------
BEGIN;
INSERT INTO `role_permissions` VALUES ('1', '1', '1'), ('84', '2', '62'), ('140', '2', '67'), ('141', '4', '67'), ('142', '10', '67'), ('143', '2', '68'), ('144', '4', '68'), ('145', '10', '68'), ('146', '7', '68'), ('163', '11', '62'), ('164', '11', '67'), ('165', '11', '68'), ('181', '12', '62'), ('182', '12', '67'), ('183', '12', '68'), ('197', '2', '69'), ('199', '4', '69'), ('206', '7', '73'), ('207', '7', '70'), ('208', '7', '80'), ('209', '7', '83'), ('216', '9', '73'), ('217', '9', '70'), ('218', '9', '80'), ('219', '9', '83'), ('223', '2', '89'), ('224', '2', '90'), ('225', '2', '92'), ('229', '4', '89'), ('230', '4', '90'), ('231', '4', '92'), ('234', '7', '89'), ('235', '7', '92'), ('237', '9', '92'), ('240', '10', '91'), ('241', '10', '92'), ('246', '2', '93'), ('247', '4', '93'), ('248', '7', '93'), ('249', '9', '93'), ('269', '2', '100'), ('270', '4', '100'), ('271', '7', '100'), ('349', '2', '118'), ('350', '4', '118'), ('351', '7', '118'), ('352', '9', '118'), ('353', '2', '120'), ('354', '4', '120'), ('355', '7', '120'), ('356', '9', '120'), ('357', '2', '121'), ('358', '4', '121'), ('359', '7', '121'), ('360', '9', '121'), ('361', '2', '122'), ('362', '4', '122'), ('363', '7', '122'), ('364', '9', '122'), ('381', '2', '130'), ('382', '4', '130'), ('383', '7', '130'), ('384', '9', '130'), ('385', '2', '132'), ('386', '4', '132'), ('387', '7', '132'), ('388', '9', '132'), ('389', '2', '134'), ('390', '4', '134'), ('391', '7', '134'), ('392', '9', '134'), ('393', '2', '136'), ('394', '4', '136'), ('395', '7', '136'), ('396', '9', '136'), ('413', '2', '146'), ('414', '4', '146'), ('415', '7', '146'), ('416', '9', '146'), ('417', '2', '148'), ('418', '4', '148'), ('419', '7', '148'), ('420', '9', '148'), ('421', '2', '149'), ('422', '4', '149'), ('423', '7', '149'), ('424', '9', '149'), ('425', '2', '150'), ('426', '4', '150'), ('427', '7', '150'), ('428', '9', '150'), ('429', '2', '151'), ('430', '4', '151'), ('431', '7', '151'), ('432', '9', '151'), ('433', '2', '153'), ('434', '4', '153'), ('435', '7', '153'), ('436', '9', '153'), ('449', '2', '160');
INSERT INTO `role_permissions` VALUES ('450', '4', '160'), ('451', '7', '160'), ('452', '9', '160'), ('453', '2', '163'), ('454', '4', '163'), ('455', '7', '163'), ('456', '9', '163'), ('457', '2', '164'), ('458', '4', '164'), ('459', '7', '164'), ('460', '9', '164'), ('469', '2', '168'), ('470', '4', '168'), ('471', '7', '168'), ('472', '9', '168'), ('473', '2', '169'), ('474', '4', '169'), ('475', '7', '169'), ('476', '9', '169'), ('489', '2', '174'), ('490', '4', '174'), ('491', '7', '174'), ('492', '9', '174'), ('493', '2', '175'), ('494', '2', '176'), ('495', '4', '176'), ('496', '7', '176'), ('497', '9', '176'), ('502', '2', '182'), ('503', '4', '182'), ('504', '7', '182'), ('505', '9', '182'), ('506', '2', '183'), ('507', '4', '183'), ('508', '7', '183'), ('509', '9', '183'), ('510', '2', '184'), ('511', '4', '184'), ('512', '7', '184'), ('513', '9', '184'), ('546', '2', '110'), ('547', '4', '110'), ('548', '7', '195'), ('549', '9', '195'), ('550', '7', '199'), ('551', '9', '199'), ('552', '7', '200'), ('553', '9', '200'), ('554', '7', '201'), ('555', '9', '201'), ('556', '7', '202'), ('557', '9', '202'), ('558', '7', '203'), ('559', '9', '203'), ('569', '10', '52'), ('575', '4', '211'), ('576', '12', '211'), ('577', '11', '213'), ('578', '12', '213'), ('579', '11', '214'), ('580', '12', '215'), ('581', '11', '217'), ('582', '12', '217'), ('584', '11', '211'), ('591', '7', '220'), ('592', '11', '220'), ('593', '12', '220'), ('595', '13', '220'), ('596', '14', '220'), ('597', '11', '221'), ('598', '12', '221'), ('600', '14', '221'), ('601', '12', '222'), ('602', '14', '222'), ('603', '14', '223'), ('604', '11', '224'), ('605', '12', '224'), ('607', '14', '224'), ('608', '12', '225'), ('609', '14', '225'), ('610', '1', '421'), ('611', '17', '419'), ('612', '17', '420');
COMMIT;

-- ----------------------------
-- Table structure for `roles`
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`acronym`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`crp_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `crp_id` (`crp_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=18

;

-- ----------------------------
-- Records of roles
-- ----------------------------
BEGIN;
INSERT INTO `roles` VALUES ('1', 'CCFAS Admin', 'Admin', '1'), ('2', 'Management Liaison', 'ML', '1'), ('4', 'Contact point', 'CP', '1'), ('7', 'Project leader', 'PL', '1'), ('8', 'Guest', 'G', '1'), ('9', 'Project coordinator', 'PC', '1'), ('10', 'Finance person', 'FM', '1'), ('11', 'Regional Program Leaders', 'RPL', '1'), ('12', 'Flagship Leaders', 'FPL', '1'), ('13', 'External Evaluator', 'E', '1'), ('14', 'Program Management Unit', 'PMU', '1'), ('15', 'Site Integration Leader', 'SL', '1'), ('16', 'Data Manager', 'DM', '1'), ('17', 'Super Admin', 'SuperAdmin', '6');
COMMIT;

-- ----------------------------
-- Table structure for `user_roles`
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`user_id`  bigint(20) NOT NULL ,
`role_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `user_id` (`user_id`) USING BTREE ,
INDEX `role_id` (`role_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1059

;

-- ----------------------------
-- Records of user_roles
-- ----------------------------
BEGIN;
INSERT INTO `user_roles` VALUES ('1', '1', '1'), ('8', '5', '2'), ('9', '7', '2'), ('16', '16', '2'), ('18', '17', '2'), ('19', '18', '2'), ('20', '19', '2'), ('21', '20', '2'), ('22', '21', '2'), ('23', '22', '2'), ('24', '22', '7'), ('26', '24', '2'), ('27', '25', '2'), ('28', '26', '2'), ('30', '28', '2'), ('31', '29', '2'), ('32', '29', '7'), ('33', '30', '2'), ('34', '31', '2'), ('36', '32', '2'), ('37', '50', '2'), ('38', '51', '2'), ('39', '52', '7'), ('40', '53', '7'), ('41', '54', '7'), ('42', '55', '7'), ('43', '56', '7'), ('45', '58', '7'), ('46', '59', '7'), ('47', '60', '7'), ('48', '61', '7'), ('49', '62', '7'), ('50', '63', '7'), ('51', '64', '7'), ('52', '65', '7'), ('53', '66', '7'), ('54', '67', '7'), ('55', '68', '7'), ('61', '73', '7'), ('65', '77', '7'), ('66', '78', '7'), ('67', '79', '7'), ('68', '80', '7'), ('69', '81', '7'), ('70', '82', '7'), ('71', '83', '7'), ('72', '84', '7'), ('73', '85', '7'), ('74', '86', '7'), ('75', '87', '7'), ('77', '89', '7'), ('78', '90', '7'), ('82', '94', '7'), ('83', '95', '7'), ('84', '96', '7'), ('85', '97', '7'), ('86', '98', '7'), ('87', '99', '7'), ('88', '100', '7'), ('89', '101', '7'), ('90', '102', '7'), ('91', '103', '7'), ('93', '105', '7'), ('94', '106', '7'), ('95', '107', '7'), ('96', '108', '7'), ('97', '109', '7'), ('98', '110', '7'), ('99', '111', '7'), ('100', '112', '7'), ('101', '113', '7'), ('102', '114', '7'), ('104', '116', '7'), ('105', '117', '7'), ('106', '118', '7'), ('107', '119', '7'), ('124', '132', '7'), ('132', '141', '7'), ('134', '143', '7'), ('135', '144', '7'), ('136', '145', '7'), ('137', '150', '7'), ('138', '151', '7'), ('144', '158', '7'), ('147', '162', '7'), ('154', '172', '7'), ('155', '173', '7'), ('171', '189', '7'), ('175', '193', '2'), ('212', '241', '7'), ('229', '259', '7'), ('240', '270', '7'), ('777', '3', '1'), ('785', '66', '4'), ('789', '844', '1'), ('791', '843', '1'), ('800', '13', '1'), ('819', '52', '4'), ('820', '61', '4'), ('821', '67', '4');
INSERT INTO `user_roles` VALUES ('822', '69', '4'), ('823', '82', '4'), ('824', '83', '4'), ('825', '108', '4'), ('826', '119', '4'), ('827', '162', '4'), ('828', '243', '4'), ('829', '247', '4'), ('830', '271', '4'), ('834', '88', '4'), ('836', '847', '4'), ('837', '848', '4'), ('838', '275', '4'), ('839', '450', '9'), ('840', '294', '2'), ('841', '145', '9'), ('842', '158', '4'), ('843', '142', '9'), ('845', '852', '2'), ('846', '182', '7'), ('848', '855', '2'), ('849', '833', '7'), ('851', '288', '9'), ('852', '856', '9'), ('853', '14', '1'), ('854', '478', '7'), ('855', '857', '1'), ('856', '858', '7'), ('857', '361', '7'), ('860', '206', '7'), ('862', '302', '7'), ('863', '863', '4'), ('864', '864', '7'), ('867', '199', '9'), ('869', '182', '9'), ('873', '871', '9'), ('874', '250', '7'), ('875', '869', '7'), ('878', '271', '7'), ('880', '862', '7'), ('882', '872', '9'), ('883', '880', '9'), ('885', '881', '9'), ('886', '872', '7'), ('887', '61', '9'), ('888', '115', '7'), ('890', '7', '7'), ('891', '5', '9'), ('892', '158', '9'), ('894', '179', '9'), ('896', '285', '7'), ('898', '153', '7'), ('899', '73', '9'), ('901', '54', '9'), ('903', '905', '9'), ('904', '174', '7'), ('906', '910', '7'), ('908', '5', '1'), ('910', '915', '9'), ('911', '178', '7'), ('915', '88', '7'), ('920', '842', '2'), ('921', '294', '7'), ('922', '842', '9'), ('923', '924', '9'), ('924', '924', '7'), ('925', '925', '9'), ('926', '925', '7'), ('927', '557', '7'), ('928', '56', '9'), ('929', '19', '7'), ('930', '170', '9'), ('932', '926', '7'), ('937', '132', '2'), ('938', '31', '7'), ('940', '271', '9'), ('941', '948', '7'), ('942', '63', '9'), ('943', '952', '9'), ('944', '928', '9'), ('945', '72', '7'), ('948', '878', '9'), ('954', '91', '9'), ('957', '470', '7'), ('961', '955', '7'), ('963', '957', '7'), ('964', '176', '9'), ('965', '69', '9'), ('966', '963', '7'), ('968', '965', '9'), ('970', '119', '9'), ('977', '367', '9'), ('981', '288', '7'), ('986', '987', '7'), ('993', '994', '7'), ('994', '1002', '4'), ('995', '136', '9'), ('996', '17', '12'), ('997', '28', '12'), ('998', '29', '12');
INSERT INTO `user_roles` VALUES ('999', '30', '12'), ('1000', '852', '12'), ('1001', '31', '12'), ('1002', '32', '12'), ('1003', '50', '12'), ('1004', '5', '12'), ('1005', '7', '12'), ('1006', '16', '12'), ('1007', '51', '12'), ('1008', '24', '11'), ('1009', '25', '11'), ('1010', '18', '11'), ('1011', '19', '11'), ('1012', '20', '11'), ('1013', '21', '11'), ('1014', '26', '11'), ('1015', '193', '11'), ('1016', '22', '11'), ('1017', '855', '11'), ('1019', '286', '1'), ('1020', '160', '9'), ('1021', '133', '9'), ('1022', '24', '9'), ('1023', '60', '9'), ('1024', '1007', '9'), ('1025', '181', '7'), ('1026', '996', '7'), ('1027', '104', '7'), ('1028', '157', '7'), ('1029', '567', '7'), ('1032', '174', '9'), ('1033', '107', '9'), ('1036', '1014', '9'), ('1042', '1009', '9'), ('1046', '194', '9'), ('1047', '28', '7'), ('1049', '57', '9'), ('1053', '1051', '2'), ('1054', '1052', '9'), ('1055', '179', '11'), ('1056', '186', '2'), ('1057', '186', '11'), ('1058', '844', '14');
COMMIT;

-- ----------------------------
-- Auto increment value for `crp_users`
-- ----------------------------
ALTER TABLE `crp_users` AUTO_INCREMENT=514;

-- ----------------------------
-- Auto increment value for `crps`
-- ----------------------------
ALTER TABLE `crps` AUTO_INCREMENT=7;

-- ----------------------------
-- Auto increment value for `permissions`
-- ----------------------------
ALTER TABLE `permissions` AUTO_INCREMENT=422;

-- ----------------------------
-- Auto increment value for `role_permissions`
-- ----------------------------
ALTER TABLE `role_permissions` AUTO_INCREMENT=613;

-- ----------------------------
-- Auto increment value for `roles`
-- ----------------------------
ALTER TABLE `roles` AUTO_INCREMENT=18;

-- ----------------------------
-- Auto increment value for `user_roles`
-- ----------------------------
ALTER TABLE `user_roles` AUTO_INCREMENT=1059;
