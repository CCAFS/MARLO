alter table project_expected_study_info add has_lever_outcome_contribution tinyint(1) DEFAULT NULL;
alter table project_expected_study_info add has_nexus_contribution tinyint(1) DEFAULT NULL;
alter table project_expected_study_info add has_legacy_crp_contribution tinyint(1) DEFAULT NULL;

alter table project_expected_study_info add has_action_area_outcome_indicator_contribution tinyint(1) DEFAULT NULL;
alter table project_expected_study_info add has_impact_area_indicator_contribution tinyint(1) DEFAULT NULL;
alter table project_expected_study_info add has_initiative_contribution tinyint(1) DEFAULT NULL;