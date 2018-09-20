SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for powb_ind_following_milestones
-- ----------------------------
DROP TABLE IF EXISTS `powb_ind_following_milestones`;
CREATE TABLE `powb_ind_following_milestones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of powb_ind_following_milestones
-- ----------------------------
INSERT INTO `powb_ind_following_milestones` VALUES ('1', 'Identical to proposal');
INSERT INTO `powb_ind_following_milestones` VALUES ('2', 'Reworded/ rephrased from proposal');
INSERT INTO `powb_ind_following_milestones` VALUES ('3', 'New/ changed');
------------------------------------------
-- ----------------------------
-- Table structure for powb_ind_milestone_risks
-- ----------------------------
DROP TABLE IF EXISTS `powb_ind_milestone_risks`;
CREATE TABLE `powb_ind_milestone_risks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of powb_ind_milestone_risks
-- ----------------------------
INSERT INTO `powb_ind_milestone_risks` VALUES ('1', 'Research/science', 'Inherent risk in unknown cutting edge research or science');
INSERT INTO `powb_ind_milestone_risks` VALUES ('2', 'Finalcial', 'Funding not fully confirmed or at risk of being cut');
INSERT INTO `powb_ind_milestone_risks` VALUES ('3', 'Partnership  ', 'Risk that partners won’t be able to deliver a key piece on time');
INSERT INTO `powb_ind_milestone_risks` VALUES ('4', 'Internal resources ', 'Risk that key staff, infrastructure or equipment not available at time needed');
INSERT INTO `powb_ind_milestone_risks` VALUES ('5', 'Weather ', 'For example,  drought or heavy rain affecting field trials');
INSERT INTO `powb_ind_milestone_risks` VALUES ('6', 'External environment (political, economic, legal, market) ', 'E.g. risk of non-delivery due to conflict, economic/market changes  ');
INSERT INTO `powb_ind_milestone_risks` VALUES ('7', 'Other ', null);
------------------------------------------------
-- ----------------------------
-- Table structure for powb_ind_dac_milestone_markers
-- ----------------------------
DROP TABLE IF EXISTS `powb_ind_assesment_risk`;
CREATE TABLE `powb_ind_assesment_risk` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of powb_ind_dac_milestone_markers
-- ----------------------------
INSERT INTO `powb_ind_assesment_risk` VALUES ('1', 'Low');
INSERT INTO `powb_ind_assesment_risk` VALUES ('2', 'Medium');
INSERT INTO `powb_ind_assesment_risk` VALUES ('3', 'High');

