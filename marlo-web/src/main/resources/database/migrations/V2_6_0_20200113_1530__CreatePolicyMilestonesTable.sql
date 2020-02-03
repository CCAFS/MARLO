CREATE TABLE `policy_milestones` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT,
`crp_milestone_id`  bigint(20) NOT NULL ,
`policy_id`  bigint(20) NOT NULL,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NOT NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `policy_milestones_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `policy_milestones_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `policy_milestones_ibfk_3` FOREIGN KEY (`crp_milestone_id`) REFERENCES `crp_milestones` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `policy_milestones_ibfk_4` FOREIGN KEY (`policy_id`) REFERENCES `project_policies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `created_by` (`created_by`) USING BTREE ,
INDEX `modified_by` (`modified_by`) USING BTREE ,
INDEX `crp_milestone_id` (`crp_milestone_id`) USING BTREE ,
INDEX `policy_id` (`policy_id`) USING BTREE 
)
;
