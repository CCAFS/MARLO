ALTER TABLE `center_deliverables` 
DROP FOREIGN KEY `deliverable_project_fk`;

ALTER TABLE `center_deliverables` 
CHANGE COLUMN `project_id` `project_id` bigint(20) NULL ,
ADD COLUMN `capdev_id` bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `center_deliverables` 
ADD CONSTRAINT `deliverable_project_fk`	 FOREIGN KEY (`project_id`)	 REFERENCES `center_projects` (`id`),
ADD CONSTRAINT `deliverable_capdev_fk`	 FOREIGN KEY (`capdev_id`)	 REFERENCES `capacity_development` (`id`);