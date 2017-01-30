SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for crp_target_units
-- ----------------------------
DROP TABLE IF EXISTS `crp_target_units`;
CREATE TABLE `crp_target_units` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `target_unit_id` bigint(20) DEFAULT NULL,
  `crp_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `tu_crp_id` (`crp_id`),
  KEY `tu_target_unit_id` (`target_unit_id`),
  KEY `tu_created_by` (`created_by`),
  KEY `tu_modified_by` (`modified_by`),
  CONSTRAINT `tu_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `tu_crp_id` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`),
  CONSTRAINT `tu_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `tu_target_unit_id` FOREIGN KEY (`target_unit_id`) REFERENCES `srf_target_units` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crp_target_units
-- ----------------------------
INSERT INTO `crp_target_units` VALUES ('1', '-1', '1', '1', '2017-01-30 16:01:41', '1082', '1082', ' ');
INSERT INTO `crp_target_units` VALUES ('2', '1', '1', '1', '2017-01-30 16:03:02', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('3', '2', '1', '1', '2017-01-30 16:03:02', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('4', '3', '1', '1', '2017-01-30 16:03:01', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('5', '4', '1', '1', '2017-01-30 16:03:01', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('6', '5', '1', '1', '2017-01-30 16:03:01', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('7', '6', '1', '1', '2017-01-30 16:03:00', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('8', '7', '1', '1', '2017-01-30 16:03:00', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('9', '8', '1', '1', '2017-01-30 16:02:59', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('10', '9', '1', '1', '2017-01-30 16:02:59', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('11', '12', '1', '1', '2017-01-30 16:02:59', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('12', '13', '1', '1', '2017-01-30 16:02:58', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('13', '14', '1', '1', '2017-01-30 16:02:58', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('14', '15', '1', '1', '2017-01-30 16:02:57', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('15', '16', '1', '1', '2017-01-30 16:02:57', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('16', '17', '1', '1', '2017-01-30 16:02:57', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('17', '20', '1', '1', '2017-01-30 16:02:56', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('18', '23', '1', '1', '2017-01-30 16:02:56', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('19', '24', '1', '1', '2017-01-30 16:02:56', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('20', '25', '1', '1', '2017-01-30 16:02:55', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('21', '32', '1', '1', '2017-01-30 16:02:55', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('22', '35', '1', '1', '2017-01-30 16:02:54', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('23', '42', '1', '1', '2017-01-30 16:02:54', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('24', '44', '1', '1', '2017-01-30 16:02:54', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('25', '45', '1', '1', '2017-01-30 16:02:53', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('26', '-1', '5', '1', '2017-01-30 16:04:12', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('27', '2', '5', '1', '2017-01-30 16:05:25', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('28', '3', '5', '1', '2017-01-30 16:05:40', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('29', '35', '5', '1', '2017-01-30 16:05:55', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('30', '42', '5', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('31', '2', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('32', '5', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('33', '6', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('34', '9', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('35', '12', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('36', '13', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('37', '16', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('38', '17', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('39', '20', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('40', '23', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('41', '24', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('42', '25', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('43', '35', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
INSERT INTO `crp_target_units` VALUES ('44', '42', '4', '1', '2017-01-30 16:06:05', '1082', '1082', null);
