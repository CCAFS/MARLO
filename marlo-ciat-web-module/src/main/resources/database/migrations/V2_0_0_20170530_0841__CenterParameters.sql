SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for center_parameters
-- ----------------------------
DROP TABLE IF EXISTS `center_parameters`;
CREATE TABLE `center_parameters` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key` varchar(500) DEFAULT NULL,
  `description` text,
  `format` int(11) DEFAULT NULL,
  `default_value` varchar(500) DEFAULT NULL,
  `category` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of center_parameters
-- ----------------------------
INSERT INTO `center_parameters` VALUES ('1', 'center_coord_role', 'Program Coordinator Role Id', '3', null, '1');
