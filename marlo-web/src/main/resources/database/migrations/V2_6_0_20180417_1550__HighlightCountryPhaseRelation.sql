ALTER TABLE `project_highlights_country` DROP FOREIGN KEY `project_highlights_country_ibfk_1`;

ALTER TABLE `project_highlights_country` ADD CONSTRAINT `project_highlights_country_ibfk_1` FOREIGN KEY (`project_highlights_id`) REFERENCES `project_highlights` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `project_highlights_country` ADD CONSTRAINT `project_highlights_country_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;