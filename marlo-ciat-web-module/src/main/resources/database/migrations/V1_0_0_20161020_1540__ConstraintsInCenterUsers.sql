ALTER TABLE `center_users`
MODIFY COLUMN `center_id`  int(11) NOT NULL AFTER `user_id`;
COMMIT;

ALTER TABLE `center_users`
DROP INDEX `user_id`,
DROP INDEX `crp_id`;
COMMIT;

ALTER TABLE `center_users` ADD CONSTRAINT `fk_center_id` FOREIGN KEY (`center_id`) REFERENCES `research_centers` (`id`);

ALTER TABLE `center_users` ADD CONSTRAINT `fk_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `center_users` ADD CONSTRAINT `fk_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);

ALTER TABLE `center_users` ADD CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);