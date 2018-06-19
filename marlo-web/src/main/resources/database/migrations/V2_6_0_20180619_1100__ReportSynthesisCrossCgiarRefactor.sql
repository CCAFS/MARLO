ALTER TABLE `report_synthesis_cross_cgiar_collaborations` DROP FOREIGN KEY `report_synthesis_cross_cgiar_collaborations_ibfk_3`;

ALTER TABLE `report_synthesis_cross_cgiar_collaborations`
DROP COLUMN `crp_program_id`,
ADD COLUMN `flagship`  text NULL AFTER `global_unit_id`;