CREATE TABLE project_expected_study_primary_strategic_outcomes (
  id bigint(20) auto_increment NOT NULL,
  strategic_outcome_id bigint(20) NOT NULL,
  expected_study_id bigint(20) NOT NULL,
  id_phase bigint(20) NOT NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp NULL,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text NULL,
  CONSTRAINT study_strategic_outcomes_pk PRIMARY KEY (id),
  CONSTRAINT study_strategic_outcomes_primary_FK FOREIGN KEY (strategic_outcome_id) REFERENCES primary_alliance_strategic_outcomes(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_strategic_outcomes_studies_FK FOREIGN KEY (expected_study_id) REFERENCES project_expected_studies(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_strategic_outcomes_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_strategic_outcomes_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_strategic_outcomes_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;