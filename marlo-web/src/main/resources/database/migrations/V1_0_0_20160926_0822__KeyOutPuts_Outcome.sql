ALTER TABLE `crp_cluster_key_outputs`
ADD COLUMN `contribution`  double NULL AFTER `id`;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `crp_cluster_key_outputs_outcome`
-- ----------------------------
DROP TABLE IF EXISTS `crp_cluster_key_outputs_outcome`;
CREATE TABLE `crp_cluster_key_outputs_outcome` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key_output_id` bigint(20) NOT NULL,
  `outcome_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  KEY `key_output_id` (`key_output_id`),
  KEY `outcome_id` (`outcome_id`),
  CONSTRAINT `crp_cluster_key_outputs_outcome_ibfk_5` FOREIGN KEY (`outcome_id`) REFERENCES `crp_program_outcomes` (`id`),
  CONSTRAINT `crp_cluster_key_outputs_outcome_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `crp_cluster_key_outputs_outcome_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `crp_cluster_key_outputs_outcome_ibfk_4` FOREIGN KEY (`key_output_id`) REFERENCES `crp_cluster_key_outputs` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

