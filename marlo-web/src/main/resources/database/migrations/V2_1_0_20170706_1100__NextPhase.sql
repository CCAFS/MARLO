ALTER TABLE `phases`
ADD COLUMN `next_phase`  bigint(20) NULL AFTER `visible`;

ALTER TABLE `phases` ADD FOREIGN KEY (`next_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

