SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for intellectual_assets
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_intellectual_assets`;
CREATE TABLE `deliverable_intellectual_assets` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deliverable` bigint(20) NOT NULL,
  `phase` bigint(20) NOT NULL,
  `has_patent_pvp` tinyint(1) NULL DEFAULT NULL,
  `applicant` text NULL,
  `type` int(11) NULL,
  `title` text NULL,
  `additional_information` text NULL,
  `link` text NULL,
  `public_communication` text NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_intellectual_assets_id`(`id`) USING BTREE,
  INDEX `idx_intellectual_assets_deliverable`(`deliverable`) USING BTREE,
  INDEX `idx_intellectual_assets_phase`(`phase`) USING BTREE,
  CONSTRAINT `intellectual_assets_ibfk_1` FOREIGN KEY (`deliverable`) REFERENCES `deliverables` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `intellectual_assets_ibfk_2` FOREIGN KEY (`phase`) REFERENCES `phases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;