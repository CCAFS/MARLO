CREATE TABLE `projects` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`name`  text NULL ,
`short_name`  text NULL ,
`status_id`  int(11) NULL ,
`start_date`  timestamp NULL ,
`end_date`  timestamp NULL ,
`project_leader_id`  bigint(20) NULL ,
`contact_person_id`  bigint(20) NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_sice`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`)
)
;