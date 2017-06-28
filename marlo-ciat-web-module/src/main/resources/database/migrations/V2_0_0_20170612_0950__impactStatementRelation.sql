ALTER TABLE `research_impacts` DROP FOREIGN KEY `research_impacts_ido_id_fk`;

ALTER TABLE `research_impacts`
CHANGE COLUMN `ido_id` `impact_statement_id`  bigint(20) NULL DEFAULT NULL AFTER `short_name`;

ALTER TABLE `research_impacts` ADD CONSTRAINT `research_impacts_statement_id_fk` FOREIGN KEY (`impact_statement_id`) REFERENCES `research_impact_statement` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `research_impacts`
DROP COLUMN `other_ido`;

