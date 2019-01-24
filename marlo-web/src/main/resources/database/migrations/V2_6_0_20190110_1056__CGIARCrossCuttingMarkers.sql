SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for cgiar_cross_cutting_markers
-- ----------------------------
DROP TABLE IF EXISTS `cgiar_cross_cutting_markers`;
CREATE TABLE `cgiar_cross_cutting_markers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of cgiar_cross_cutting_markers
-- ----------------------------
INSERT INTO `cgiar_cross_cutting_markers` VALUES ('1', 'Gender');
INSERT INTO `cgiar_cross_cutting_markers` VALUES ('2', 'Youth');
INSERT INTO `cgiar_cross_cutting_markers` VALUES ('3', 'CapDev');
INSERT INTO `cgiar_cross_cutting_markers` VALUES ('4', 'Climate Change');
SET FOREIGN_KEY_CHECKS=1;
