DROP TABLE IF EXISTS `participant`;

CREATE TABLE `participant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` bigint(20) NOT NULL,
  `name` varchar(200) NOT NULL,
  `last_name` varchar(200) NOT NULL,
  `gender` int(11) NOT NULL,
  `citizenship` VARCHAR(200) NOT NULL,
  `country_of_residence` varchar(200) NULL,
  `highest_degree` varchar(200) NULL,
  `institution` varchar(200) NULL,
  `email` varchar(200) NOT NULL,
  `reference` varchar(200) NULL,
  `supervisor` varchar(200) NULL,
  `fellowship` varchar(200) NULL,
  `is_active` tinyint(1) NOT NULL,
  `acive_since` timestamp NULL,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) NULL,
  `modification_justification` text NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `participant_created_fk`  FOREIGN KEY (`created_by`)  REFERENCES `users` (`id`),
  CONSTRAINT `participant_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`));
  