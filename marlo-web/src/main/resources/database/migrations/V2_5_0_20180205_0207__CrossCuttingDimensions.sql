DROP TABLE IF EXISTS `cross_cutting_dimensions`;
CREATE TABLE `cross_cutting_dimensions`
(
  `id`  bigint(20) NOT NULL AUTO_INCREMENT ,  
  `summarize` text null,
  `assets` text null,
  `is_active`  tinyint(1) NOT NULL ,
  `created_by`  bigint(20) NULL ,
  `modified_by`  bigint(20) NULL ,
  `active_since`  timestamp NULL ,
  `modification_justification`  text NULL,
  PRIMARY KEY (`id`),
  KEY `cross_cutting_index_created_by` (`created_by`) USING BTREE,
  KEY `cross_cutting_index_modified_by` (`modified_by`) USING BTREE, 
  CONSTRAINT `cross_cutting_fk_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `cross_cutting_fk_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)  
)ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;