SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for srf_cross_cutting_issues
-- ----------------------------
DROP TABLE IF EXISTS `srf_cross_cutting_issues`;
CREATE TABLE `srf_cross_cutting_issues` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_srf_cross_cutting_issues_created_by_users_id` (`created_by`) USING BTREE,
  KEY `fk_srf_cross_cutting_issues_modified_by_users_id` (`modified_by`) USING BTREE,
  CONSTRAINT `srf_cross_cutting_issues_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `srf_cross_cutting_issues_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of srf_cross_cutting_issues
-- ----------------------------
INSERT INTO `srf_cross_cutting_issues` VALUES ('1', 'Climate Change', '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_cross_cutting_issues` VALUES ('2', 'Gender and Youth', '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_cross_cutting_issues` VALUES ('3', 'Policies and Institutions', '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_cross_cutting_issues` VALUES ('4', 'Capacity development', '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
