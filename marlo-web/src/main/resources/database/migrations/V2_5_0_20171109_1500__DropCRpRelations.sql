ALTER TABLE `center_project_funding_sources` DROP FOREIGN KEY `center_project_funding_sources_ibfk_2`;

ALTER TABLE `center_project_funding_sources`
DROP COLUMN `crp_id`;

ALTER TABLE `crp_loc_element_types` DROP FOREIGN KEY `crp_loc_element_types_ibfk_1`;

ALTER TABLE `crp_loc_element_types`
DROP COLUMN `crp_id`;

ALTER TABLE `crp_ppa_partners` DROP FOREIGN KEY `crp_ppa_partners_ibfk_1`;

ALTER TABLE `crp_ppa_partners`
DROP COLUMN `crp_id`;

ALTER TABLE `crp_programs` DROP FOREIGN KEY `crp_programs_ibfk_1`;

ALTER TABLE `crp_programs`
DROP COLUMN `crp_id`;

ALTER TABLE `crp_sub_idos_contributions` DROP FOREIGN KEY `crp_sub_idos_contributions_ibfk_1`;

ALTER TABLE `crp_sub_idos_contributions`
DROP COLUMN `crp_id`;

ALTER TABLE `crp_target_units` DROP FOREIGN KEY `tu_crp_id`;

ALTER TABLE `crp_target_units`
DROP COLUMN `crp_id`;

ALTER TABLE `crps_sites_integration` DROP FOREIGN KEY `crps_sites_integration_ibfk_2`;

ALTER TABLE `crps_sites_integration` DROP FOREIGN KEY `crps_sites_integration_ibfk_5`;

ALTER TABLE `crps_sites_integration`
DROP COLUMN `crp_id`;

ALTER TABLE `deliverable_types` DROP FOREIGN KEY `deliverable_types_ibfk_2`;

ALTER TABLE `deliverable_types`
DROP COLUMN `crp_id`;

ALTER TABLE `deliverables` DROP FOREIGN KEY `deliverables_ibfk_7`;

ALTER TABLE `deliverables`
DROP COLUMN `crp_id`;

ALTER TABLE `funding_sources` DROP FOREIGN KEY `funding_sources_ibfk_5`;

ALTER TABLE `funding_sources`
DROP COLUMN `crp_id`;

ALTER TABLE `gender_types` DROP FOREIGN KEY `gender_types_ibfk_1`;

ALTER TABLE `gender_types`
DROP COLUMN `crp_id`;

ALTER TABLE `liaison_institutions` DROP FOREIGN KEY `liaison_institutions_ibfk_3`;

ALTER TABLE `liaison_institutions`
DROP COLUMN `crp_id`;

ALTER TABLE `liaison_users` DROP FOREIGN KEY `liaison_users_ibfk_3`;

ALTER TABLE `liaison_users`
DROP COLUMN `crp_id`;

ALTER TABLE `loc_element_types` DROP FOREIGN KEY `loc_element_types_ibfk_1`;

ALTER TABLE `loc_element_types`
DROP COLUMN `crp_id`;

ALTER TABLE `loc_elements` DROP FOREIGN KEY `loc_elements_ibfk_5`;

ALTER TABLE `loc_elements`
DROP COLUMN `crp_id`;

ALTER TABLE `phases` DROP FOREIGN KEY `phases_ibfk_1`;

ALTER TABLE `phases`
DROP COLUMN `crp_id`;

ALTER TABLE `projects` DROP FOREIGN KEY `projects_global_unit_fk`;

ALTER TABLE `projects` DROP FOREIGN KEY `projects_ibfk_5`;

ALTER TABLE `projects`
DROP COLUMN `crp_id`,
DROP COLUMN `global_unit_id`;