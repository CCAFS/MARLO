SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rep_ind_stage_studies
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_stage_studies`;
CREATE TABLE `rep_ind_stage_studies` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_stage_studies
-- ----------------------------
INSERT INTO `rep_ind_stage_studies` VALUES ('1', 'Stage 1', '(sphere of influence) CGIAR research (and related activities) has contributed to changed discourse and/or behavior among key actors (related to the theory of change).  Examples of evidence:  outcome mapping study, media analysis, e-mail correspondence. ');
INSERT INTO `rep_ind_stage_studies` VALUES ('2', 'Stage 2', '(sphere of influence) CGIAR research (and related activities) has contributed to documented policy and practice change by key actors.  This may include changes such as income, nutrient intake etc. in the sphere of influence (e.g. project level).  Example of evidence: a study of adoption and effects, commissioned at project level. ');
INSERT INTO `rep_ind_stage_studies` VALUES ('3', 'Stage 3', '(sphere of interest) Policy and/or practice changes influenced by CGIAR research (and related activities) has led to impacts at scale or beyond the direct CGIAR sphere of influence.  Example of evidence:  ex-post Impact Assessment');
