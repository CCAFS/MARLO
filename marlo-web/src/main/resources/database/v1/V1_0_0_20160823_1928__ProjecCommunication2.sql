START TRANSACTION;

ALTER TABLE `project_outcomes` ADD FOREIGN KEY (`expected_unit`) REFERENCES `srf_target_units` (`id`);

ALTER TABLE `project_outcomes` ADD FOREIGN KEY (`achieved_unit`) REFERENCES `srf_target_units` (`id`);

ALTER TABLE `project_milestones` ADD FOREIGN KEY (`expected_unit`) REFERENCES `srf_target_units` (`id`);



 

COMMIT;