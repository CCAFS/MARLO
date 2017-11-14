/*modification_justification will be reject_justification*/
ALTER TABLE `partner_requests` CHANGE `modification_justification` `reject_justification` text NULL;

/* Add offices columns*/
ALTER TABLE `partner_requests` 
ADD `modification_justification` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`,
ADD `rejected_by` BIGINT (20) NULL AFTER `modification_justification`,
ADD `is_office` TINYINT (1) DEFAULT '0' AFTER `web_page`,
ADD `institution_id` BIGINT (20) NULL AFTER `is_office`,
ADD `request_source` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `institution_id`,
ADD CONSTRAINT `p_request_rejected_by` FOREIGN KEY (`rejected_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
ADD CONSTRAINT `p_request_institution_id` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;