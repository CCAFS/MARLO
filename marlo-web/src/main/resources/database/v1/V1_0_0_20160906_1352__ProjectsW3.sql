CREATE TABLE `projects_bilateral_cofinancing` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`title`  text NULL ,
`start_date`  datetime NULL ,
`end_date`  datetime NULL ,
`agreement`  int NULL ,
`liason_institution`  bigint(20) NULL ,
`liason_user`  bigint(20) NULL ,
`donor`  bigint(20) NULL ,
`is_active`  tinyint(1) NOT NULL ,
`created_by`  bigint(20) NULL DEFAULT NULL ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`)
)
;
