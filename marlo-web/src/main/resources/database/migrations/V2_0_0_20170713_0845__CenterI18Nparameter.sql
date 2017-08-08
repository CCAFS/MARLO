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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of center_parameters
-- ----------------------------
INSERT INTO `center_parameters` VALUES ('1', 'center_coord_role', 'Program Coordinator Role Id', '3', null, '1');
INSERT INTO `center_parameters` VALUES ('2', 'center_custom_file', 'Custom I18N file', '4', null, '3');


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
  CONSTRAINT `custom_parameters_center_id_fk` FOREIGN KEY (`center_id`) REFERENCES `centers` (`id`),
  CONSTRAINT `custom_parameters_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `custom_parameters_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `custom_parameters_paramater_id_fk` FOREIGN KEY (`parameter_id`) REFERENCES `center_parameters` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_custom_parameters
-- ----------------------------
INSERT INTO `center_custom_parameters` VALUES ('1', '1', '1', '2', '1', '2017-05-30 09:09:42', '3', '3', ' ');
INSERT INTO `center_custom_parameters` VALUES ('2', '2', '1', 'ciat', '1', '2017-07-13 08:29:06', '3', '3', null);

