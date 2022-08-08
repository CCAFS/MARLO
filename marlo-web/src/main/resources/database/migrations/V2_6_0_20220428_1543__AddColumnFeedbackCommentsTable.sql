ALTER TABLE `feedback_qa_comments` 
MODIFY COLUMN `status` tinyint(1) NULL DEFAULT NULL AFTER `comment`,
ADD COLUMN `approval_date` timestamp NULL AFTER `comment_date`;