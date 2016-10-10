CREATE TABLE `crp_ppa_partners` (
  `crp_id` bigint(20) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `institution_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `crp_id_ppa_fk` (`crp_id`),
  KEY `institution_id_ppa_fk` (`institution_id`),
  CONSTRAINT `crp_id_ppa_fk` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`),
  CONSTRAINT `institution_id_ppa_fk` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;