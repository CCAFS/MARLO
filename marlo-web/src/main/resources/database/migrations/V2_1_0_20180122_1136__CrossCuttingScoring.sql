SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `cross_cutting_scoring`
-- ----------------------------
DROP TABLE IF EXISTS `cross_cutting_scoring`;

CREATE TABLE `cross_cutting_scoring` (
  `id` bigint(20) NOT NULL,
  `description` TEXT NOT NULL,
  `complete_description` TEXT  NULL,
  `crp_id` bigint(20) NULL,
  PRIMARY KEY (`id`),
  KEY `crp_id` (`crp_id`),
  CONSTRAINT `crosscut_fk1` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`)
 )ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8;
 
 
  
-- ----------------------------
-- Records of cross_cutting_scoring
-- ----------------------------
INSERT INTO `cross_cutting_scoring` VALUES (0, '0-Not Targeted', 'Not Targeted',null);
INSERT INTO `cross_cutting_scoring` VALUES (1, '1-Significant', 'Significant',null);
INSERT INTO `cross_cutting_scoring` VALUES (2, '2-Principal', 'Principal',null);


ALTER TABLE deliverables_info ADD cross_cutting_score_gender bigint(20) null; 
ALTER TABLE deliverables_info ADD cross_cutting_score_youth bigint(20) null;
ALTER TABLE deliverables_info ADD cross_cutting_score_capacity bigint(20) null;
 
  
  