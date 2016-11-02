
ALTER TABLE `funding_sources` DROP FOREIGN KEY `funding_sources_ibfk_7`;
ALTER TABLE `funding_sources`
DROP COLUMN `liasion_instiution_id`,
ADD COLUMN `institution_id`  bigint(20) NULL AFTER `contact_person_email`,
DROP INDEX `liasion_instiution_id`;

ALTER TABLE `funding_sources` ADD FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;
