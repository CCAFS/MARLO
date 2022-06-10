ALTER TABLE `feedback_qa_comments` 
ADD COLUMN `user_approval_id` BIGINT(20) NULL AFTER `parent_field_description`,
ADD INDEX `feedback_qa_comments_FK_6` (`user_approval_id` ASC) INVISIBLE;

ALTER TABLE `feedback_qa_comments` 
ADD CONSTRAINT `feedback_qa_comments_FK_5`
  FOREIGN KEY (`user_approval_id`)
  REFERENCES `users` (`id`)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT;