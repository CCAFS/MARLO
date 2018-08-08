ALTER TABLE `projects_info` DROP FOREIGN KEY `projects_info_ibfk_7`;

ALTER TABLE `projects_info` ADD CONSTRAINT `projects_info_ibfk_7` FOREIGN KEY (`liaison_institution_center`) REFERENCES `liaison_institutions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

