DROP TABLE IF EXISTS `cross_cutting_dimensions`;
CREATE TABLE `cross_cutting_dimensions`
(
  `id`  bigint(20) NOT NULL AUTO_INCREMENT ,
  `liaison_institution_id` bigint(20) NOT NULL,
  `summarize` text null,
  `assets` text null,
  `id_phase` bigint(20) NOT NULL,
  `is_active`  tinyint(1) NOT NULL ,
  `created_by`  bigint(20) NULL ,
  `modified_by`  bigint(20) NULL ,
  `active_since`  timestamp NULL ,
  `modification_justification`  text NULL,
  PRIMARY KEY (`id`),
  KEY `cross_cutting_index_created_by` (`created_by`) USING BTREE,
  KEY `cross_cutting_index_modified_by` (`modified_by`) USING BTREE,
  KEY `cross_cutting_index_id_phase` (`id_phase`) USING BTREE,
  KEY `cross_cutting_index_liaison_institution_id` (`liaison_institution_id`) USING BTREE,
  CONSTRAINT `cross_cutting_fk_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `cross_cutting_fk_id_phase` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),  
  CONSTRAINT `cross_cutting_fk_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `cross_cutting_fk_liaison_institution_id` FOREIGN KEY (`liaison_institution_id`) REFERENCES `liaison_institutions` (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;