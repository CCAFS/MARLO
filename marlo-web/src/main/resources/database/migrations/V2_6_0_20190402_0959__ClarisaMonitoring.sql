SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for clarisa_monitoring
-- ----------------------------
DROP TABLE IF EXISTS `clarisa_monitoring`;
CREATE TABLE `clarisa_monitoring` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `global_unit_id` bigint(20) DEFAULT NULL,
  `service_name` text,
  `service_args` text,
  `service_type` text,
  `date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of clarisa_monitoring
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
