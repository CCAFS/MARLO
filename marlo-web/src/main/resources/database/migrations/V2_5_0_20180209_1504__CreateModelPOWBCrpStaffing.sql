DROP TABLE IF EXISTS `powb_crp_staffing`;

CREATE TABLE `powb_crp_staffing` (
`id`  bigint(20) NOT NULL ,
`staffing_issues`  text NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `fk_powb_crp_staffing_id_fk` FOREIGN KEY (`id`) REFERENCES `powb_synthesis` (`id`),
CONSTRAINT `fk_powb_crp_staffing_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `fk_powb_crp_staffing_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
);
DROP TABLE IF EXISTS `powb_crp_staffing_categories`;

CREATE TABLE `powb_crp_staffing_categories` (
`id`  bigint(20) NOT NULL ,
`category`  text NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `fk_powb_crp_staffing_categories_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `fk_powb_crp_staffing_categories_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
);
DROP TABLE IF EXISTS `powb_synthesis_crp_staffing_category`;

CREATE TABLE `powb_synthesis_crp_staffing_category` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT,
`powb_synthesis_id`  bigint(20) NOT NULL ,
`powb_crp_staffing_category_id`  bigint(20) NOT NULL ,
`female` double NULL ,
`male` double NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `fk1_powb_synthesis_crp_staffing_category` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `fk2_powb_synthesis_crp_staffing_category` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
CONSTRAINT `fk3_powb_synthesis_crp_staffing_category` FOREIGN KEY (`powb_synthesis_id`) REFERENCES `powb_synthesis` (`id`),
CONSTRAINT `fk4_powb_synthesis_crp_staffing_category` FOREIGN KEY (`powb_crp_staffing_category_id`) REFERENCES `powb_crp_staffing_categories` (`id`)
);