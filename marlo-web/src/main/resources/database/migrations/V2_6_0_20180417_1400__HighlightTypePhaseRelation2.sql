ALTER TABLE `project_highlights_types` DROP FOREIGN KEY `project_highlights_types_ibfk_1`;

ALTER TABLE `project_highlights_types` DROP FOREIGN KEY `project_highlights_types_ibfk_2`;

ALTER TABLE `project_highlights_types` ADD CONSTRAINT `project_highlights_types_ibfk_1` FOREIGN KEY (`project_highlights_id`) REFERENCES `project_highlights` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `project_highlights_types` ADD CONSTRAINT `project_highlights_types_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

