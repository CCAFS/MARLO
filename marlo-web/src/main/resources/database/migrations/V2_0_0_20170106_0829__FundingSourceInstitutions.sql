SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `funding_source_institutions`
-- ----------------------------
DROP TABLE IF EXISTS `funding_source_institutions`;
CREATE TABLE `funding_source_institutions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `funding_source_id` bigint(20) NOT NULL,
  `institution_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `funding_source_id` (`funding_source_id`),
  KEY `institution_id` (`institution_id`),
  CONSTRAINT `funding_source_institutions_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`),
  CONSTRAINT `funding_source_institutions_ibfk_1` FOREIGN KEY (`funding_source_id`) REFERENCES `funding_sources` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

delete from funding_source_institutions;

INSERT INTO funding_source_institutions (funding_source_id,institution_id)
select id,institution_id from funding_sources where institution_id is not null;