ALTER TABLE `center_project_funding_sources`
ADD COLUMN `global_unit_id`  bigint(20) NULL AFTER `code`;

ALTER TABLE `center_project_funding_sources` ADD CONSTRAINT `center_project_funding_sources_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `crp_loc_element_types`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `crp_loc_element_types` ADD CONSTRAINT `crp_loc_element_types_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `crp_ppa_partners`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `crp_ppa_partners` ADD CONSTRAINT `crp_ppa_partners_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `crp_programs`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `crp_programs` ADD CONSTRAINT `crp_programs_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `crp_sub_idos_contributions`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `crp_sub_idos_contributions` ADD CONSTRAINT `crp_sub_idos_contributions_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `crp_target_units`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `crp_target_units` ADD CONSTRAINT `crp_target_units_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `crp_users`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `crp_users` ADD CONSTRAINT `crp_users_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `crps_sites_integration`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `crps_sites_integration` ADD CONSTRAINT `crps_sites_integration_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `custom_parameters`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `custom_parameters` ADD CONSTRAINT `custom_parameters_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `deliverable_types`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `deliverable_types` ADD CONSTRAINT `deliverable_types_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `deliverables`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `deliverables` ADD CONSTRAINT `deliverables_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `funding_sources`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `funding_sources` ADD CONSTRAINT `funding_sources_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `gender_types`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `gender_types` ADD CONSTRAINT `gender_types_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `liaison_institutions`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `liaison_institutions` ADD CONSTRAINT `liaison_institutions_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `liaison_users`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `liaison_users` ADD CONSTRAINT `liaison_users_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `loc_element_types`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `loc_element_types` ADD CONSTRAINT `loc_element_types_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `loc_elements`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `loc_elements` ADD CONSTRAINT `loc_elements_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `phases`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `phases` ADD CONSTRAINT `phases_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `projects`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `projects` ADD CONSTRAINT `projects_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

ALTER TABLE `roles`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `roles` ADD CONSTRAINT `roles_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);