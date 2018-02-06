CREATE TABLE `ad_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `login` varchar(200) NULL,
  `first_name` varchar(200) NULL,
  `middle_name` varchar(200) NULL,
  `last_name` varchar(200) NULL,
  `email` varchar(100) NULL,
  `is_active` tinyint(1) NULL,
  `created_by` bigint(20) NULL,
  `modified_by` bigint(20) NULL,
  `active_since` timestamp NULL,
  `modification_justification` text NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `ad_user_created_by_fk`	FOREIGN KEY (`created_by`)	 REFERENCES `users` (`id`),
  CONSTRAINT `ad_user_modified_by_fk`   FOREIGN KEY (`modified_by`)  REFERENCES `users` (`id`)
  );