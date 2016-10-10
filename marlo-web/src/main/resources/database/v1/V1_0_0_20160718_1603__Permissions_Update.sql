/*
Navicat MySQL Data Transfer

Source Server         : LocalHost
Source Server Version : 50712
Source Host           : localhost:3306
Source Database       : ccafspr_marlo

Target Server Type    : MYSQL
Target Server Version : 50712
File Encoding         : 65001

Date: 2016-07-18 16:00:57
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `permission` varchar(255) NOT NULL,
  `description` text,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=425 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of permissions
-- ----------------------------
INSERT INTO `permissions` VALUES ('1', 'crp:{0}:*', 'Full privileges on all the platform', '0');
INSERT INTO `permissions` VALUES ('2', 'crp:{0}:project:*', 'Can update all the planning section contents', '0');
INSERT INTO `permissions` VALUES ('52', 'crp:{0}:project:{1}:budget:annualW1w2', 'Can update the W1/W2 budget in the project budget section in planning round', '1');
INSERT INTO `permissions` VALUES ('62', 'crp:{0}:summaries:*', 'Can update all the summaries section contents', '0');
INSERT INTO `permissions` VALUES ('63', 'crp:{0}:summaries:board:*', 'Can use all the functions in the summaries board', '0');
INSERT INTO `permissions` VALUES ('67', 'crp:{0}:project:{1}:partner:leader:canEdit', 'Can update the planning project partners leader', '1');
INSERT INTO `permissions` VALUES ('68', 'crp:{0}:project:{1}:partner:cordinator:canEdit', 'Can update the planning project partners cordinator', '1');
INSERT INTO `permissions` VALUES ('69', 'crp:{0}:project:{1}:description:*', 'Can update all the reporting section in project description.', '1');
INSERT INTO `permissions` VALUES ('70', 'crp:{0}:project:{1}:description:status', 'Can update all the reporting section in project description', '1');
INSERT INTO `permissions` VALUES ('73', 'crp:{0}:project:{1}:description:annualReportDonor', 'Can upload the report to the donor (bilateral project) in reporting round.', '1');
INSERT INTO `permissions` VALUES ('74', 'crp:{0}:project:{1}:description:bilateralContract', 'Can update the bilateral contract in reporting round.', '1');
INSERT INTO `permissions` VALUES ('75', 'crp:{0}:project:{1}:description:endDate', 'Can update the field end date in reporting round.', '1');
INSERT INTO `permissions` VALUES ('76', 'crp:{0}:project:{1}:description:flagships', 'Can update the flagships selections in reporting round.', '1');
INSERT INTO `permissions` VALUES ('77', 'crp:{0}:project:{1}:description:managementLiaison', 'Can update the ML in reporting round.', '1');
INSERT INTO `permissions` VALUES ('78', 'crp:{0}:project:{1}:description:regions', 'Can update the regions selections in reporting round.', '1');
INSERT INTO `permissions` VALUES ('79', 'crp:{0}:project:{1}:description:startDate', 'Can update the field start date in reporting round.', '1');
INSERT INTO `permissions` VALUES ('80', 'crp:{0}:project:{1}:description:statusDescription', 'Can update the project status in reporting round.', '1');
INSERT INTO `permissions` VALUES ('81', 'crp:{0}:project:{1}:description:summary', 'Can update the project summary in reporting round.', '1');
INSERT INTO `permissions` VALUES ('82', 'crp:{0}:project:{1}:description:title', 'Can update the project title in reporting round', '1');
INSERT INTO `permissions` VALUES ('83', 'crp:{0}:project:{1}:description:canEdit', 'Can update the project description section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('84', 'crp:{0}:project:{1}:description:workplan', 'Can upload the workplan (ccafs projects) in reporting round.', '1');
INSERT INTO `permissions` VALUES ('88', 'crp:{0}:project:{1}:partners:*', 'Can update everything on the partners section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('89', 'crp:{0}:project:{1}:partners:coordinator', 'Can update the project coordinator (PC) in reporting round.', '1');
INSERT INTO `permissions` VALUES ('90', 'crp:{0}:project:{1}:partners:leader', 'Can update the project leader (PL) in reporting round.', '1');
INSERT INTO `permissions` VALUES ('91', 'crp:{0}:project:{1}:partners:ppa', 'Can udpate the PPA partners in reporting round.', '1');
INSERT INTO `permissions` VALUES ('92', 'crp:{0}:project:{1}:partners:canEdit', 'Can update some content in project partners section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('93', 'crp:{0}:project:{1}:locations:*', 'Can update everything in project locations in reporting round.', '1');
INSERT INTO `permissions` VALUES ('94', 'crp:{0}:project:{1}:outcomes:*', 'Can update everything in project outcomes in reporting round.', '1');
INSERT INTO `permissions` VALUES ('96', 'crp:{0}:project:projectsList:*', 'Can use all the functions in the projects list section in reporting round.', '0');
INSERT INTO `permissions` VALUES ('97', 'crp:{0}:project:projectsList:addBilateralProject', 'Can use the \"add bilateral project\" button in the projects list section in reporting round.', '0');
INSERT INTO `permissions` VALUES ('98', 'crp:{0}:project:projectsList:addCoreProject', 'Can use the \"add core project\" button in the projects list section in reporting round.', '0');
INSERT INTO `permissions` VALUES ('99', 'crp:{0}:project:{1}:manage:deleteProject', 'Can use the \"Delete project\" button in any section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('100', 'crp:{0}:project:{1}:manage:submitProject', 'Can use the \"Submit project\" button in any section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('101', 'crp:{0}:project:{1}:ccafsOutcomes:*', 'Can update everything in the CCAFS outcomes section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('102', 'crp:{0}:project:{1}:otherContributions:*', 'Can update the other contributions section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('104', 'crp:{0}:project:{1}:outputs:*', 'Can update the Overview by MOGs (outputs) section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('105', 'crp:{0}:project:{1}:deliverable:*', 'Can update the Deliverable section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('106', 'crp:{0}:project:{1}:deliverablesList:*', 'Can update the Deliverables List section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('110', 'crp:{0}:project:{1}:activities:*', 'Can update the Project Activities section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('112', 'crp:{0}:project:{1}:caseStudies:*', 'Can update the Outcomes Case Studies section in the planning round.', '1');
INSERT INTO `permissions` VALUES ('118', 'crp:{0}:project:{1}:outcomes:canEdit', 'Can update project outcomes in reporting round', '1');
INSERT INTO `permissions` VALUES ('119', 'crp:{0}:project:{1}:outcomes:statement', 'Can update project statement in project outcomes in reporting round.', '1');
INSERT INTO `permissions` VALUES ('120', 'crp:{0}:project:{1}:outcomes:annualProgress', 'Can update annual progress in project outcomes in reporting round.', '1');
INSERT INTO `permissions` VALUES ('121', 'crp:{0}:project:{1}:outcomes:communicationEngagement', 'Can update communication engagement in project outcomes in reporting round.', '1');
INSERT INTO `permissions` VALUES ('122', 'crp:{0}:project:{1}:outcomes:uploadSummary', 'Can upload a summary file in project outcomes in reporting round.', '1');
INSERT INTO `permissions` VALUES ('130', 'crp:{0}:project:{1}:ccafsOutcomes:canEdit', 'Can make changes in the ccafs outcomes section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('131', 'crp:{0}:project:{1}:ccafsOutcomes:target', 'Can update the target value in ccafs outcomes section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('132', 'crp:{0}:project:{1}:ccafsOutcomes:achieved', 'Can update the target achieved in ccafs outcomes section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('133', 'crp:{0}:project:{1}:ccafsOutcomes:description', 'Can update the expected annual contribution in ccafs outcomes section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('134', 'crp:{0}:project:{1}:ccafsOutcomes:narrativeTargets', 'Can update the annual contribution achieved in ccafs outcomes section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('135', 'crp:{0}:project:{1}:ccafsOutcomes:gender', 'Can update the expected gender contribution in ccafs outcomes section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('136', 'crp:{0}:project:{1}:ccafsOutcomes:narrativeGender', 'Can update the gender contribution achieved in ccafs outcomes section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('146', 'crp:{0}:project:{1}:otherContributions:canEdit', 'Can make changes in the other contributions section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('147', 'crp:{0}:project:{1}:otherContributions:contribution', 'Can update how is contributing in other contributuions section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('148', 'crp:{0}:project:{1}:otherContributions:otherContributionIndicator', 'Can update the indicator in other contributions section un reporting round.', '1');
INSERT INTO `permissions` VALUES ('149', 'crp:{0}:project:{1}:otherContributions:otherContributionDescription', 'Can update the description of the contribution to the indicator in other contributions section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('150', 'crp:{0}:project:{1}:otherContributions:otherContributionTarget', 'Can update the target in other contributions section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('151', 'crp:{0}:project:{1}:otherContributions:additionalContribution', 'Can update the contribuition to another center activity in other contributions section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('152', 'crp:{0}:project:{1}:otherContributions:natureCollaboration', 'Can update the nature collaboration in other contributions section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('153', 'crp:{0}:project:{1}:otherContributions:explainAchieved', 'Can update the ahieved outcome in other contributions section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('154', 'crp:{0}:project:{1}:otherContributions:addCRPContribution', 'Can add new CRP in other contributions section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('160', 'crp:{0}:project:{1}:outputs:canEdit', 'Can make changes in the overview by mogs section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('161', 'crp:{0}:project:{1}:outputs:expectedAnnualContribution', 'Can update the expected annual contribution in overview by mogs section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('162', 'crp:{0}:project:{1}:outputs:socialInclusionDimmension', 'Can update the expected gender contribution in overview by mogs section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('163', 'crp:{0}:project:{1}:outputs:briefSummary', 'Can update the actual contribution in overview by mogs section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('164', 'crp:{0}:project:{1}:outputs:summaryGender', 'Can update the actual gender contribution in the overview by mogs section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('168', 'crp:{0}:project:{1}:deliverablesList:canEdit', 'Can make changes in the deliverables list section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('169', 'crp:{0}:project:{1}:deliverablesList:addDeliverable', 'Can add new deliverables in the deliverables list section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('170', 'crp:{0}:project:{1}:deliverablesList:removeOldDeliverables', 'Can remove deliverables in the deliverables list section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('174', 'crp:{0}:project:{1}:deliverable:canEdit', 'Can make changes in a particular deliverable in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('175', 'crp:{0}:project:{1}:deliverable:main', 'Can make changes in the main fields (title, start date) for a particular deliverable in reporting round.', '1');
INSERT INTO `permissions` VALUES ('176', 'crp:{0}:project:{1}:deliverable:other', 'Can make changes in the rest of the fields that are not part of \"main\" permission in reporting round.', '1');
INSERT INTO `permissions` VALUES ('177', 'crp:{0}:project:{1}:nextUsers:*', 'Can update the Next users section in the planning round.', '1');
INSERT INTO `permissions` VALUES ('178', 'crp:{0}:project:{1}:highlights:*', 'Can update the Project Highlights List section in the planning round.', '1');
INSERT INTO `permissions` VALUES ('182', 'crp:{0}:project:{1}:highlights:canEdit', 'Can make changes in the Project Highlights List section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('183', 'crp:{0}:project:{1}:highlights:addHighlight', 'Can add new Project Highlights in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('184', 'crp:{0}:project:{1}:highlights:removeHighlight', 'Can remove Project Highlights in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('185', 'crp:{0}:project:{1}:highlight:*', 'Can update the Project Highlight section in the planning round.', '1');
INSERT INTO `permissions` VALUES ('195', 'crp:{0}:project:{1}:activities:canEdit', 'Can make changes in the activities section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('196', 'crp:{0}:project:{1}:activities:title', 'Can update the title of the  activities section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('197', 'crp:{0}:project:{1}:activities:description', 'Can update the description of the activities section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('198', 'crp:{0}:project:{1}:activities:startDate', 'Can update the start date of the activities section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('199', 'crp:{0}:project:{1}:activities:endDate', 'Can update the end date of the activities section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('200', 'crp:{0}:project:{1}:activities:activityStatus', 'Can update the status of the activities section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('201', 'crp:{0}:project:{1}:activities:activityProgress', 'Can update the status justification of the activities section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('202', 'crp:{0}:project:{1}:activities:leader', 'Can update the leader of the activities section in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('203', 'crp:{0}:project:{1}:activities:addActivity', 'Can add new activities in the reporting round.', '1');
INSERT INTO `permissions` VALUES ('205', 'crp:{0}:project:{1}:budget:*', 'Can update everything on project budgets in reporting round', '1');
INSERT INTO `permissions` VALUES ('206', 'crp:{0}:project:{1}:budget:canEdit', 'Can update the planning project budget section in reporting round', '1');
INSERT INTO `permissions` VALUES ('207', 'crp:{0}:project:{1}:budget:annualBilateral', 'Can update the W3/Bilateral budget in the project budget section in reporting round', '1');
INSERT INTO `permissions` VALUES ('208', 'crp:{0}:project:{1}:budget:annualW1w2:canEdit', 'Can update the W1/W2 budget in the project budget section in reporting round', '1');
INSERT INTO `permissions` VALUES ('209', 'crp:{0}:project:{1}:budgetByMog:*', 'Can update everything in project budget by mog section in reporting round.', '1');
INSERT INTO `permissions` VALUES ('210', 'crp:{0}:project:{1}:leverages:*', 'Can update the Leverages section in the planning round.', '1');
INSERT INTO `permissions` VALUES ('211', 'crp:{0}:project:synthesis:crpIndicators:*', 'Can update everything on CRP Indicatoris in reporting round.', '0');
INSERT INTO `permissions` VALUES ('212', 'crp:{0}:project:synthesis:outcomeSynthesis:*', 'Can edit everything on Outcome Synthesis in reporting round.', '0');
INSERT INTO `permissions` VALUES ('213', 'crp:{0}:project:synthesis:outcomeSynthesis:canEdit', 'Can edit on Outcome Synthesis in reporting round.', '0');
INSERT INTO `permissions` VALUES ('214', 'crp:{0}:project:synthesis:outcomeSynthesis:rplSynthesis', 'Can edit on Regional Synthesis in reporting round.', '0');
INSERT INTO `permissions` VALUES ('215', 'crp:{0}:project:synthesis:outcomeSynthesis:fplSynthesis', 'Can edit on Flagship Synthesis in reporting round.', '0');
INSERT INTO `permissions` VALUES ('216', 'crp:{0}:project:synthesis:synthesisByMog:*', 'Can edit everything on Synthesis by MOG in reporting round.', '0');
INSERT INTO `permissions` VALUES ('217', 'crp:{0}:project:synthesis:synthesisByMog:canEdit', 'Can edit on Synthesis by MOG in reporting round.', '0');
INSERT INTO `permissions` VALUES ('218', 'crp:{0}:project:{1}:evaluation:*', 'Can update project Evaluation', '1');
INSERT INTO `permissions` VALUES ('219', 'crp:{0}:project:projectsEvaluation:*', 'Can update Evaluation projects section', '0');
INSERT INTO `permissions` VALUES ('220', 'crp:{0}:project:{1}:evaluation:canEdit', 'Can update project Evaluation', '1');
INSERT INTO `permissions` VALUES ('221', 'crp:{0}:project:{1}:evaluation:accessPL', 'Can view Project Leaders evaluations', '1');
INSERT INTO `permissions` VALUES ('222', 'crp:{0}:project:{1}:evaluation:accessRPL', 'Can view Regional Program evaluations', '1');
INSERT INTO `permissions` VALUES ('223', 'crp:{0}:project:{1}:evaluation:accessFPL', 'Can view Flagship Program evaluations', '1');
INSERT INTO `permissions` VALUES ('224', 'crp:{0}:project:{1}:evaluation:accessEE', 'Can view External evaluator evaluations', '1');
INSERT INTO `permissions` VALUES ('225', 'crp:{0}:project:{1}:evaluation:accessCU', 'Can view Coordination Unit evaluations', '1');
INSERT INTO `permissions` VALUES ('226', 'crp:{0}:project:{1}:evaluation:accessPD', 'Can view Program Director evaluations', '1');
INSERT INTO `permissions` VALUES ('419', '*', 'Super Admin Permission', '0');
INSERT INTO `permissions` VALUES ('420', 'superadmin:canEdit', 'Super admin can edit', '0');
INSERT INTO `permissions` VALUES ('421', 'crp:{0}:admin:*', 'Can edit crp admin', '0');
INSERT INTO `permissions` VALUES ('422', 'crp:{0}:admin:canAcess', 'Can view menu', '0');
INSERT INTO `permissions` VALUES ('423', 'crp:{0}:impactPathway:{1}:*', 'Can edit crp impactPathway', '3');
INSERT INTO `permissions` VALUES ('424', 'crp:{0}:impactPathway:{1}:canAcess', 'can view ImpactPathway Menu', '3');
