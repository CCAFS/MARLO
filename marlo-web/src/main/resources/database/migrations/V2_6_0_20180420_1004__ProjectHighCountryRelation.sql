ALTER TABLE `project_highlights_country` ADD CONSTRAINT `project_highlights_country_ibfk_3` FOREIGN KEY (`id_country`) REFERENCES `loc_elements` (`id`);