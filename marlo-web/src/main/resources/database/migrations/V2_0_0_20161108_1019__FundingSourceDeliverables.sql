ALTER TABLE `deliverables` DROP FOREIGN KEY `deliverables_ibfk_7`;

ALTER TABLE `deliverables`
DROP COLUMN `funding_source_id`;

CREATE TABLE `deliverable_funding_sources` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NOT NULL ,
`funding_source_id`  bigint(20) NOT NULL ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NOT NULL ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`),
FOREIGN KEY (`funding_source_id`) REFERENCES `funding_sources` (`id`)
)
;

