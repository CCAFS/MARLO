ALTER TABLE `funding_sources`
ADD COLUMN `liasion_instiution_id`  bigint(20) NULL AFTER `contact_person_email`;

ALTER TABLE `funding_sources` ADD FOREIGN KEY (`liasion_instiution_id`) REFERENCES `liaison_institutions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

