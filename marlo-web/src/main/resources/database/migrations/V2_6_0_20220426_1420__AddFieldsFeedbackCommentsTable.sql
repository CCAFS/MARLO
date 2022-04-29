ALTER TABLE `feedback_comments` 
ADD COLUMN `user_id` bigint(20) NULL AFTER `comment`,
ADD COLUMN `comment_date` timestamp NOT NULL AFTER `user_id`,
ADD UNIQUE INDEX `feedback_comments_id_2`(`user_id`) USING BTREE,
ADD CONSTRAINT `feedback_comments_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;