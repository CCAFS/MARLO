ALTER TABLE `project_locations` ADD CONSTRAINT `project_location_create_by_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);
ALTER TABLE `project_locations` ADD CONSTRAINT `project_location_modyfied_by_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);
