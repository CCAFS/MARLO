SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for deliverable_data_sharing
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_data_sharing`;
CREATE TABLE `deliverable_data_sharing` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deliverable_id` bigint(20) NOT NULL,
  `institutional_repository` bit(1) DEFAULT NULL,
  `link_institutional_repository` varchar(500) DEFAULT NULL,
  `ccfas_host_greater` bit(1) DEFAULT NULL,
  `link_ccfas_host_greater` varchar(500) DEFAULT NULL,
  `ccfas_host_smaller` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `deliverable_id` (`deliverable_id`) USING BTREE,
  CONSTRAINT `deliverable_data_sharing_ibfk_1` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deliverable_data_sharing
-- ----------------------------
