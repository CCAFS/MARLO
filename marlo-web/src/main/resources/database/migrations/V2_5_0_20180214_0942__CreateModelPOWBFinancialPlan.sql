SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `powb_financial_plan`;
CREATE TABLE `powb_financial_plan` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`financial_plan_issues`  text NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `fk_powb_financial_plan_id_fk` FOREIGN KEY (`id`) REFERENCES `powb_synthesis` (`id`),
CONSTRAINT `fk_powb_financial_plan_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `fk_powb_financial_plan_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
);

DROP TABLE IF EXISTS `powb_expenditure_areas`;
CREATE TABLE `powb_expenditure_areas` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT,
`expenditure_area`  text NOT NULL ,
`is_expenditure`  tinyint(1) NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `fk_powb_expenditure_areas_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `fk_powb_expenditure_areas_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
);

DROP TABLE IF EXISTS `powb_financial_expenditure`;
CREATE TABLE `powb_financial_expenditure` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT,
`powb_synthesis_id`  bigint(20) NOT NULL ,
`powb_expenditure_area_id`  bigint(20) NOT NULL ,
`w1w2_percentage` double NOT NULL ,
`comments` text NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `fk1_powb_financial_expenditure` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `fk2_powb_financial_expenditure` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
CONSTRAINT `fk3_powb_financial_expenditure` FOREIGN KEY (`powb_synthesis_id`) REFERENCES `powb_synthesis` (`id`),
CONSTRAINT `fk4_powb_financial_expenditure` FOREIGN KEY (`powb_expenditure_area_id`) REFERENCES `powb_expenditure_areas` (`id`)
);


DROP TABLE IF EXISTS `powb_financial_planned_budget`;
CREATE TABLE `powb_financial_planned_budget` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT,
`powb_synthesis_id`  bigint(20) NULL ,
`powb_expenditure_area_id`  bigint(20) NULL ,
`liaison_institution_id`  bigint(20) NOT NULL ,
`w1w2` double NOT NULL ,
`w3_bilateral` double NOT NULL ,
`comments` text NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `fk1_powb_financial_planned_budget` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `fk2_powb_financial_planned_budget` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
CONSTRAINT `fk3_powb_financial_planned_budget` FOREIGN KEY (`powb_synthesis_id`) REFERENCES `powb_synthesis` (`id`),
CONSTRAINT `fk4_powb_financial_planned_budget` FOREIGN KEY (`powb_expenditure_area_id`) REFERENCES `powb_expenditure_areas` (`id`),
CONSTRAINT `fk5_powb_financial_planned_budget` FOREIGN KEY (`liaison_institution_id`) REFERENCES `liaison_institutions` (`id`)
);