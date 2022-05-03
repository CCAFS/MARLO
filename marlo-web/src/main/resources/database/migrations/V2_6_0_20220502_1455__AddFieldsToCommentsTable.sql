ALTER TABLE `feedback_qa_comments` 
ADD COLUMN `link` text NULL AFTER `field_value`,
ADD COLUMN `field_description` text NULL AFTER `link`;