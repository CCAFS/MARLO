ALTER TABLE `projects` ADD CONSTRAINT `project_status_fk` FOREIGN KEY (`status_id`) REFERENCES `project_status` (`id`);

ALTER TABLE `projects` ADD CONSTRAINT `project_leader_fk` FOREIGN KEY (`project_leader_id`) REFERENCES `users` (`id`);

ALTER TABLE `projects` ADD CONSTRAINT `project_contact_fk` FOREIGN KEY (`contact_person_id`) REFERENCES `users` (`id`);

ALTER TABLE `projects` ADD CONSTRAINT `project_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `projects` ADD CONSTRAINT `project_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);

