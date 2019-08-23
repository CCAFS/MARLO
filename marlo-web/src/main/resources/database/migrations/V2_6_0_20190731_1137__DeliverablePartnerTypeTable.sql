SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for deliverable_partner_type
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_partner_type`;
CREATE TABLE `deliverable_partner_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT;

-- ----------------------------
-- Records of deliverable_partner_type
-- ----------------------------
INSERT INTO `deliverable_partner_type` VALUES ('1', 'Resp');
INSERT INTO `deliverable_partner_type` VALUES ('2', 'Other');
SET FOREIGN_KEY_CHECKS=1;
