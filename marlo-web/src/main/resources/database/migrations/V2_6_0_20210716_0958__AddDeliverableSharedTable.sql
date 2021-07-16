CREATE TABLE `project_deliverable_shared` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_id`  bigint(20) NULL ,
`deliverable_id`  bigint(20) NULL ,
`id_phase`  bigint(20) NULL ,
`created_by`  bigint(20) NOT NULL ,
`active_since`  time NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `project_deliverable_shared_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT `project_deliverable_shared_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT `project_deliverable_shared_ibfk_3` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `project_deliverable_shared_ibfk_4` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `project_deliverable_shared_ibfk_5` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `deliverable_id` (`deliverable_id`) USING BTREE ,
INDEX `project_id` (`project_id`) USING BTREE ,
INDEX `phase_id` (`id_phase`) USING BTREE ,
INDEX `created_by_idx` (`created_by`) USING BTREE ,
INDEX `modified_by_idx` (`modified_by`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
ROW_FORMAT=DYNAMIC
;

