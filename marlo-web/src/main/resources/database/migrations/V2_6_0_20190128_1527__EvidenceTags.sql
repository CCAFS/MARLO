SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for evidence_tags
-- ----------------------------
DROP TABLE IF EXISTS `evidence_tags`;
CREATE TABLE `evidence_tags` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of evidence_tags
-- ----------------------------
INSERT INTO `evidence_tags` VALUES ('1', 'New Outcomes Impact Case');
INSERT INTO `evidence_tags` VALUES ('2', 'Update Outcomes Impact Case at same level of maturity (revised)');
INSERT INTO `evidence_tags` VALUES ('3', 'Updated Outcome Impact Case at new level maturity');

SET FOREIGN_KEY_CHECKS=1;
