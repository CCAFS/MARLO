ALTER TABLE `deliverable_quality_checks`
ADD COLUMN `file_assurance`  bigint(20) NULL AFTER `quality_assurance`,
ADD COLUMN `link_assurance`  text NULL AFTER `file_assurance`,
ADD COLUMN `file_dictionary`  bigint(20) NULL AFTER `data_dictionary`,
ADD COLUMN `link_dictionary`  text NULL AFTER `file_dictionary`,
ADD COLUMN `file_tools`  bigint(20) NULL AFTER `data_tools`,
ADD COLUMN `link_tools`  text NULL AFTER `file_tools`;

ALTER TABLE `deliverable_quality_checks` ADD CONSTRAINT `quiality_file_assurance` FOREIGN KEY (`file_assurance`) REFERENCES `files` (`id`);

ALTER TABLE `deliverable_quality_checks` ADD CONSTRAINT `quality_file_dictionary` FOREIGN KEY (`file_dictionary`) REFERENCES `files` (`id`);

ALTER TABLE `deliverable_quality_checks` ADD CONSTRAINT `quality_file_tools` FOREIGN KEY (`file_tools`) REFERENCES `files` (`id`);