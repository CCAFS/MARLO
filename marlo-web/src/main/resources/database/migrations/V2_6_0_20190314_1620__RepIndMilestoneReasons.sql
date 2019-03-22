SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rep_ind_milestone_reasons
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_milestone_reasons`;
CREATE TABLE `rep_ind_milestone_reasons` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_milestone_reasons
-- ----------------------------
INSERT INTO `rep_ind_milestone_reasons` VALUES ('1', '1. Research/science', 'inherent risk in unknown cutting-edge research or science');
INSERT INTO `rep_ind_milestone_reasons` VALUES ('2', '2. Financial', 'funding delayed and/or cut');
INSERT INTO `rep_ind_milestone_reasons` VALUES ('3', '3. Partnership', 'partners were not able to deliver a key piece on time');
INSERT INTO `rep_ind_milestone_reasons` VALUES ('4', '4. Internal resources', 'key staff, infrastructure or equipment was not available at the time\r\nneeded');
INSERT INTO `rep_ind_milestone_reasons` VALUES ('5', '5. Weather', 'for example, drought or heavy rain affecting field trials');
INSERT INTO `rep_ind_milestone_reasons` VALUES ('6', '6. External environment (political, economic, legal, market)', 'e.g. conflict, economic/market\r\nchanges');
INSERT INTO `rep_ind_milestone_reasons` VALUES ('7', '7. Other', null);
SET FOREIGN_KEY_CHECKS=1;
