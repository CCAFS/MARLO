-- ENH-HOMEPAGE-BANNER-001
-- Homepage banner content, entered by an administrator from the /admin module.
-- One row per Global Unit: the banner is homepage chrome, not phased reporting data, so it carries
-- no phase reference and takes part in no forward replication.

CREATE TABLE `homepage_banners` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`global_unit_id`  bigint(20) NOT NULL ,
`title`  varchar(500) NULL DEFAULT NULL ,
`description`  text NULL ,
`image_file_name`  varchar(255) NULL DEFAULT NULL ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' ,
`modified_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
UNIQUE KEY `uk_homepage_banners_global_unit` (`global_unit_id`),
CONSTRAINT `fk_homepage_banners_global_unit` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `fk_homepage_banners_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `fk_homepage_banners_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)ENGINE=InnoDB
;

-- Seed the content that until now lived in global.properties as dashboard.cluster.title and
-- dashboard.cluster.description, so the AICCRA homepage reads the same after this deploy as before it.
-- Resolved by acronym because global unit ids are not stable across environments; the WHERE also makes
-- this a no-op on a database that has no AICCRA row yet.
-- image_file_name stays NULL on purpose: the illustration in use is a CDN asset, not an uploaded file,
-- so an administrator uploads it once from the new admin section after deploy.
INSERT INTO `homepage_banners`
  (`global_unit_id`, `title`, `description`, `image_file_name`, `modification_justification`)
SELECT gu.`id`,
       'What is a Cluster?',
       'A cluster is defined as the group of AICCRA main activities led by each AICCRA Country Leader (Ghana, Mali, Senegal, Ethiopia, Kenya and Zambia), AICCRA Regional Leaders (Western Africa and Eastern & Southern Africa), and AICCRA Thematic leaders (Theme 1, Theme 2, Theme 3, and Theme 4). In each cluster, participants are involved as leaders, coordinators and collaborators with specific budget allocations for each AICCRA main activity with a set of deliverables and contributions towards our performance indicators.',
       NULL,
       'Seeded from global.properties by ENH-HOMEPAGE-BANNER-001'
FROM `global_units` gu
WHERE gu.`acronym` = 'AICCRA'
;
