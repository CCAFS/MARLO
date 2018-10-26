SET FOREIGN_KEY_CHECKS = 0;
drop table if exists deliverable_rules;
CREATE TABLE `deliverable_rules` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `description` text NULL,
  PRIMARY KEY (`id`),  
  INDEX `id_index` (`id`) USING BTREE 
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

drop table if exists deliverable_type_rules;
CREATE TABLE `deliverable_type_rules` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deliverable_type` bigint(20) NOT NULL,
  `deliverable_rule` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
CONSTRAINT `deliverable_type_rules_fk1` FOREIGN KEY (`deliverable_type`) REFERENCES `deliverable_types` (`id`),
CONSTRAINT `deliverable_type_rules_fk2` FOREIGN KEY (`deliverable_rule`) REFERENCES `deliverable_rules` (`id`),
UNIQUE INDEX `deliverable_type_rule_index` (`deliverable_type`, `deliverable_rule`) USING BTREE 
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
