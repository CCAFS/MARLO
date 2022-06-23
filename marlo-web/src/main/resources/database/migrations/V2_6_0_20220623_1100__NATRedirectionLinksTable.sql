CREATE TABLE `nat_redirection_links` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `indicator_name` text DEFAULT NULL,
  `indicator_id` bigint DEFAULT NULL,
  `redirection_url` text DEFAULT NULL,
  PRIMARY KEY (id),
  INDEX `nat_redirection_links_id` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;