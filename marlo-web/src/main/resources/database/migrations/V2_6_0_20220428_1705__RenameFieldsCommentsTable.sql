ALTER TABLE `feedback_qa_comments` 
CHANGE COLUMN `object_id` `parent_id` bigint(20) NULL DEFAULT NULL AFTER `screen_id`,
ADD COLUMN `field_value` text NULL AFTER `approval_date`;