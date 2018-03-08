DELETE FROM powb_evidence_planned_studies;

ALTER TABLE `powb_evidence_planned_studies` DROP FOREIGN KEY `powb_planned_studies_srf_indicators_id_fk`;

ALTER TABLE `powb_evidence_planned_studies` DROP FOREIGN KEY `powb_planned_studies_sub_idos_id_fk`;

ALTER TABLE `powb_evidence_planned_studies`
DROP COLUMN `planned_topic`,
DROP COLUMN `geographic_scope`,
DROP COLUMN `sub_ido_id`,
DROP COLUMN `slo_indicator_id`,
DROP COLUMN `comments`,
ADD COLUMN `project_expected_studies_id`  bigint(20) NOT NULL AFTER `powb_evidence_id`;

ALTER TABLE `powb_evidence_planned_studies` ADD CONSTRAINT `powb_planned_studies_project_studies_fk` FOREIGN KEY (`project_expected_studies_id`) REFERENCES `project_expected_studies` (`id`);