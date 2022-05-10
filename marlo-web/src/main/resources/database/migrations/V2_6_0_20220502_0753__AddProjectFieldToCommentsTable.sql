ALTER TABLE `feedback_qa_comments` 
ADD COLUMN `project_id` bigint(20) NULL AFTER `parent_id`,
ADD INDEX `feedback_qa_comments_FK_5`(`project_id`) USING BTREE,
ADD CONSTRAINT `feedback_qa_comments_FK_4` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;