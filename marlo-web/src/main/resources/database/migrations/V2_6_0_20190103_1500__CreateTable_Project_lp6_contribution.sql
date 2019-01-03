CREATE TABLE IF NOT EXISTS `project_lp6_contribution` (
  `id` BIGINT(20) NOT NULL,
  `project_id` BIGINT(20) NOT NULL,
  `phase_id` BIGINT(20) NOT NULL,
  `contribution` TINYINT(1) NOT NULL,
  `is_active` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`));
