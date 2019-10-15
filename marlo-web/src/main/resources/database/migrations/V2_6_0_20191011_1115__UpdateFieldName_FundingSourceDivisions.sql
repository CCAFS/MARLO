ALTER TABLE `funding_source_divisions`
CHANGE COLUMN `phase_id` `id_phase`  bigint(20) NULL DEFAULT NULL AFTER `division_id`;

