ALTER TABLE `feedback_qa_comments` 
MODIFY COLUMN `status` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'rejected, approved, clarification needed, pending';