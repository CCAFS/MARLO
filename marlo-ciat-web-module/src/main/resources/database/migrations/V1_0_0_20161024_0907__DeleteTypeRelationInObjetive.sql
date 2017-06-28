ALTER TABLE `research_objectives` DROP FOREIGN KEY `fk_objective_type`;

ALTER TABLE `research_objectives`
DROP COLUMN `type_id`;