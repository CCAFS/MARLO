ALTER TABLE `feedback_qa_replies` 
DROP INDEX `feedback_comments_id_2`,
ADD INDEX `feedback_comments_id_2`(`user_id`) USING BTREE;