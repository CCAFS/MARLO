SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rep_ind_stage_process
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_stage_process`;
CREATE TABLE `rep_ind_stage_process` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_stage_process
-- ----------------------------
INSERT INTO `rep_ind_stage_process` VALUES ('1', 'Stage 1 of outcome/impact case study', 'Research taken up by next user (decision maker or intermediary)');
INSERT INTO `rep_ind_stage_process` VALUES ('2', 'Stage 2 of outcome/impact case study', 'Policy/Law etc. Enacted ');
