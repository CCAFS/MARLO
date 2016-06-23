CREATE TABLE `crp_cluster_activity_leaders` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`user_id`  bigint(20) NOT NULL ,
`cluster_activity_id`  bigint(20) NOT NULL ,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
PRIMARY KEY (`id`),
FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)  ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`cluster_activity_id`) REFERENCES `crp_cluster_of_activities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)
;

