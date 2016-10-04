ALTER TABLE `crp_program_countries` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `crp_program_countries` ADD FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)  ON DELETE CASCADE ON UPDATE CASCADE;

