ALTER TABLE `feedback_qa_comments` 
ADD COLUMN `user_id` bigint(20) NULL AFTER `reply_id`,
ADD COLUMN `comment_date` timestamp NOT NULL AFTER `user_id`,
ADD INDEX `feedback_qa_comments_FK_4`(`user_id`) USING BTREE,
ADD CONSTRAINT `feedback_qa_comments_FK_3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;