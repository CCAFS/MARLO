ALTER TABLE `feedback_comments` 
MODIFY COLUMN `comment_date` timestamp NULL AFTER `user_id`;

ALTER TABLE `feedback_qa_comments` 
MODIFY COLUMN `comment_date` timestamp NULL AFTER `user_id`;