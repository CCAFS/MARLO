ALTER TABLE `submissions`
ADD COLUMN `unsubmit_user_id`  bigint(20) NULL AFTER `year`,
ADD COLUMN `unsubmit`  tinyint(1) NULL AFTER `unsubmit_user_id`,
ADD COLUMN `unsubmit_justification`  text NULL AFTER `unsubmit`;

ALTER TABLE `submissions` ADD FOREIGN KEY (`unsubmit_user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

