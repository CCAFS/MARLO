ALTER TABLE `research_milestones` ADD CONSTRAINT `fk_milestones_target_unit` FOREIGN KEY (`target_unit_id`) REFERENCES `target_units` (`id`);
