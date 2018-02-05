CREATE TABLE `powb_evidence_planned_studies` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`powb_evidence_id`  bigint(20) NOT NULL ,
`planned_topic`  text NULL ,
`geographic_scope`  int(11) NULL ,
`sub_ido_id`  bigint(20) NULL ,
`slo_indicator_id`  bigint(20) NULL ,
`comments`  text NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `powb_planned_studies_evidence_id_fk` FOREIGN KEY (`powb_evidence_id`) REFERENCES `powb_evidences` (`id`),
CONSTRAINT `powb_planned_studies_sub_idos_id_fk` FOREIGN KEY (`sub_ido_id`) REFERENCES `srf_sub_idos` (`id`),
CONSTRAINT `powb_planned_studies_srf_indicators_id_fk` FOREIGN KEY (`slo_indicator_id`) REFERENCES `srf_slo_indicators` (`id`),
CONSTRAINT `powb_planned_studies_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `powb_planned_studies_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;

