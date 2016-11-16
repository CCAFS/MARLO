ALTER TABLE `deliverables`
ADD COLUMN `cross_cutting_gender`  tinyint(1) NULL DEFAULT NULL AFTER `create_date`,
ADD COLUMN `cross_cutting_youth`  tinyint(1) NULL DEFAULT NULL AFTER `cross_cutting_gender`,
ADD COLUMN `cross_cutting_capacity`  tinyint(1) NULL DEFAULT NULL AFTER `cross_cutting_youth`,
ADD COLUMN `cross_cutting_na`  tinyint(1) NULL DEFAULT NULL AFTER `cross_cutting_capacity`;

CREATE TABLE `deliverable_gender_levels` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NOT NULL ,
`gender_level`  bigint(20) NOT NULL ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NOT NULL ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`),
FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;

