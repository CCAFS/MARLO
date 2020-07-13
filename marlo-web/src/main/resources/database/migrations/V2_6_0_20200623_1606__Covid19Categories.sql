/*create table categories*/
CREATE TABLE
IF NOT EXISTS `project_impacts_categories` (
	`id` BIGINT (20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `description` text,
	`is_active` TINYINT (1) NOT NULL DEFAULT '1',
	`active_since` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`created_by` BIGINT (20) DEFAULT NULL,
	`modified_by` BIGINT (20) DEFAULT NULL,
	`modification_justification` text,
	PRIMARY KEY (`id`),
	CONSTRAINT `project_impacts_categories_users1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT `project_impacts_categories_users2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);


/*fill table categories*/
INSERT INTO `project_impacts_categories` (`id`, `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES ('1', 'No impact', 'Activities unaffected and expect to achieve deliverables as planned', '1', CURRENT_TIMESTAMP, 1082, NULL, NULL);
INSERT INTO `project_impacts_categories` (`id`, `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES ('2', 'Minor re-scheduling', 'Activities have had to be postponed, but still expect to achieve deliverables by end of year', '1', CURRENT_TIMESTAMP, 1082, NULL, NULL);
INSERT INTO `project_impacts_categories` (`id`, `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES ('3', 'Major re-scheduling', 'Activities have had to be postponed and 2020 deliverables will be achieved only in 2021', '1', CURRENT_TIMESTAMP, 1082, NULL, NULL);
INSERT INTO `project_impacts_categories` (`id`, `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES ('4', 'Partial cancellation', 'Some activities have had to be cancelled and some deliverables will not be achieved', '1', CURRENT_TIMESTAMP, 1082, NULL, NULL);
INSERT INTO `project_impacts_categories` (`id`, `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES ('5', 'Full cancellation', 'All activities have had to be cancelled', '1', CURRENT_TIMESTAMP, 1082, NULL, NULL);

/*create colum categories*/
ALTER TABLE `project_impacts` ADD project_impact_category_id BIGINT (20) NULL;

ALTER TABLE `project_impacts` ADD CONSTRAINT project_impact_categories
FOREIGN KEY (project_impact_category_id) REFERENCES project_impacts_categories(id);