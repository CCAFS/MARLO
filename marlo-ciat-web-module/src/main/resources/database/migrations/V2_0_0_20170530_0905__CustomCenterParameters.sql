SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for center_custom_parameters
-- ----------------------------
DROP TABLE IF EXISTS `center_custom_parameters`;
CREATE TABLE `center_custom_parameters` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parameter_id` bigint(20) NOT NULL,
  `center_id` int(11) NOT NULL,
  `value` varchar(500) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `custom_parameters_paramater_id_fk` (`parameter_id`),
  KEY `custom_parameters_center_id_fk` (`center_id`),
  KEY `custom_parameters_created_fk` (`created_by`),
  KEY `custom_parameters_modified_fk` (`modified_by`),
  CONSTRAINT `custom_parameters_center_id_fk` FOREIGN KEY (`center_id`) REFERENCES `research_centers` (`id`),
  CONSTRAINT `custom_parameters_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `custom_parameters_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `custom_parameters_paramater_id_fk` FOREIGN KEY (`parameter_id`) REFERENCES `center_parameters` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_custom_parameters
-- ----------------------------
INSERT INTO `center_custom_parameters` VALUES ('1', '1', '1', '2', '1', '2017-05-30 09:09:42', '3', '3', ' ');
