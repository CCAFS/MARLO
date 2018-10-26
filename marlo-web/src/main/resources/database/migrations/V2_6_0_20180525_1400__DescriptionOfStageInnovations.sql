SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rep_ind_degree_innovation
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_degree_innovation`;
CREATE TABLE `rep_ind_degree_innovation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for rep_ind_contribution_of_crp
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_contribution_of_crp`;
CREATE TABLE `rep_ind_contribution_of_crp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--------------------------------------------------------
-- ----------------------------
-- Records of rep_ind_contribution_of_crp
-- ----------------------------
INSERT INTO `rep_ind_contribution_of_crp` VALUES ('1', 'Sole Contribution');
INSERT INTO `rep_ind_contribution_of_crp` VALUES ('2', 'Lead Contribution');
INSERT INTO `rep_ind_contribution_of_crp` VALUES ('3', 'Partial Contribution');

-- ----------------------------
-- Records of rep_ind_degree_innovation
-- ----------------------------
INSERT INTO `rep_ind_degree_innovation` VALUES ('1', 'Novel');
INSERT INTO `rep_ind_degree_innovation` VALUES ('2', 'Adaptive');

-----------------------------------------------------------------------------

ALTER TABLE `project_innovation_info`
CHANGE COLUMN `novel` `description_stage`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `project_expected_studies_id`,
ADD COLUMN `rep_ind_contribution_crp_id`  bigint(20) NULL AFTER `rep_ind_region_id`,
ADD COLUMN `rep_ind_degree_innovation_id`  bigint(20) NULL AFTER `rep_ind_contribution_crp_id`;

ALTER TABLE `project_innovation_info` ADD FOREIGN KEY (`rep_ind_degree_innovation_id`) REFERENCES `rep_ind_degree_innovation` (`id`);

ALTER TABLE `project_innovation_info` ADD FOREIGN KEY (`rep_ind_contribution_crp_id`) REFERENCES `rep_ind_contribution_of_crp` (`id`);
