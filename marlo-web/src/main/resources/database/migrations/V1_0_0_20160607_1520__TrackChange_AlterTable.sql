update crp_parameters Set modified_by=1;
update crp_program_leaders Set modified_by=1;
update crp_programs Set modified_by=1;
update crp_users Set modified_by=1;
update crps Set modified_by=1;
----------------------------------------------------------------------------------------------------------------------------
alter table  crp_assumptions ADD CONSTRAINT fk_crp_assumptions_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  crp_cluster_of_activities ADD CONSTRAINT fk_crp_cluster_of_activities_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  crp_milestones ADD CONSTRAINT fk_crp_milestones_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  crp_outcome_sub_idos ADD CONSTRAINT fk_crp_outcome_sub_idos_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  crp_parameters ADD CONSTRAINT fk_crp_parameters_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  crp_program_leaders ADD CONSTRAINT fk_crp_program_leaders_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  crp_program_outcomes ADD CONSTRAINT fk_crp_program_outcomes_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  crp_programs ADD CONSTRAINT fk_crp_programs_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  crp_sites_leaders ADD CONSTRAINT fk_crp_sites_leaders_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  crp_sub_idos_contributions ADD CONSTRAINT fk_crp_sub_idos_contributions_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  crp_users ADD CONSTRAINT fk_crp_users_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  crps ADD CONSTRAINT fk_crps_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  crps_sites_integration ADD CONSTRAINT fk_crps_sites_integration_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  srf_cross_cutting_issues ADD CONSTRAINT fk_srf_cross_cutting_issues_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  srf_idos ADD CONSTRAINT fk_srf_idos_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  srf_slo_idos ADD CONSTRAINT fk_srf_slo_idos_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  srf_slo_indicator_targets ADD CONSTRAINT fk_srf_slo_indicator_targets_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  srf_slo_indicators ADD CONSTRAINT fk_srf_slo_indicators_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  srf_slos ADD CONSTRAINT fk_srf_slos_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  srf_sub_idos ADD CONSTRAINT fk_srf_sub_idos_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

alter table  srf_target_units ADD CONSTRAINT fk_srf_target_units_created_by_users_id FOREIGN KEY (created_by) REFERENCES users(id);

------------------------------------------------------------------------------------------------------------------------------------------------

alter table  crp_assumptions ADD CONSTRAINT fk_crp_assumptions_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  crp_cluster_of_activities ADD CONSTRAINT fk_crp_cluster_of_activities_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  crp_milestones ADD CONSTRAINT fk_crp_milestones_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  crp_outcome_sub_idos ADD CONSTRAINT fk_crp_outcome_sub_idos_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  crp_parameters ADD CONSTRAINT fk_crp_parameters_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  crp_program_leaders ADD CONSTRAINT fk_crp_program_leaders_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  crp_program_outcomes ADD CONSTRAINT fk_crp_program_outcomes_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  crp_programs ADD CONSTRAINT fk_crp_programs_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  crp_sites_leaders ADD CONSTRAINT fk_crp_sites_leaders_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  crp_sub_idos_contributions ADD CONSTRAINT fk_crp_sub_idos_contributions_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  crp_users ADD CONSTRAINT fk_crp_users_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  crps ADD CONSTRAINT fk_crps_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  crps_sites_integration ADD CONSTRAINT fk_crps_sites_integration_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  srf_cross_cutting_issues ADD CONSTRAINT fk_srf_cross_cutting_issues_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  srf_idos ADD CONSTRAINT fk_srf_idos_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  srf_slo_idos ADD CONSTRAINT fk_srf_slo_idos_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  srf_slo_indicator_targets ADD CONSTRAINT fk_srf_slo_indicator_targets_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  srf_slo_indicators ADD CONSTRAINT fk_srf_slo_indicators_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  srf_slos ADD CONSTRAINT fk_srf_slos_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  srf_sub_idos ADD CONSTRAINT fk_srf_sub_idos_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);

alter table  srf_target_units ADD CONSTRAINT fk_srf_target_units_modified_by_users_id FOREIGN KEY (modified_by) REFERENCES users(id);