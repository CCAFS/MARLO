 START TRANSACTION;
ALTER TABLE `projects_bilateral_cofinancing` ADD FOREIGN KEY (`liason_institution`) REFERENCES `liaison_institutions` (`id`);

ALTER TABLE `projects_bilateral_cofinancing` ADD FOREIGN KEY (`liason_user`) REFERENCES `liaison_users` (`id`);

ALTER TABLE `projects_bilateral_cofinancing` ADD FOREIGN KEY (`donor`) REFERENCES `institutions` (`id`);

ALTER TABLE `projects_bilateral_cofinancing` ADD FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `projects_bilateral_cofinancing` ADD FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);

commit;