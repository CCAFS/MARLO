alter table  crp_assumptions ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  crp_cluster_of_activities ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  crp_milestones ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  crp_outcome_sub_idos ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  crp_parameters ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  crp_program_leaders ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  crp_program_outcomes ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  crp_programs ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  crp_sites_leaders ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  crp_sub_idos_contributions ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  crp_users ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  crps ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  crps_sites_integration ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  srf_cross_cutting_issues ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  srf_idos ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  srf_slo_idos ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  srf_slo_indicator_targets ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  srf_slo_indicators ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  srf_slos ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  srf_sub_idos ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  srf_target_units ADD COLUMN `created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' AFTER `is_active`,
ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;

alter table  users ADD COLUMN `active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_by`,
ADD COLUMN `modified_by`  bigint(20) NOT NULL AFTER `active_since`,
ADD COLUMN `modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `modified_by`;