ALTER TABLE `research_output_partner_persons`
ADD COLUMN `user_id`  bigint(20) NULL AFTER `output_partner_id`;

ALTER TABLE `research_output_partner_persons` ADD CONSTRAINT `person_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);