ALTER TABLE `center_areas`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `center_areas` ADD CONSTRAINT `center_areas_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `center_custom_parameters`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `center_custom_parameters` ADD CONSTRAINT `center_custom_parameters_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `center_leaders`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `center_leaders` ADD CONSTRAINT `center_leaders_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `center_objectives`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `center_objectives` ADD CONSTRAINT `center_objectives_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `center_roles`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `center_roles` ADD CONSTRAINT `center_roles_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `center_users`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `center_users` ADD CONSTRAINT `center_users_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);