CREATE TABLE project_expected_study_crp_outcomes (
  id bigint(20) NOT NULL,
  expected_id bigint(20) NOT NULL,
  crp_outcome_id bigint(20) NOT NULL,
  id_phase bigint(20) NULL,
  CONSTRAINT `PRIMARY` PRIMARY KEY (id),
  CONSTRAINT project_expected_study_crp_outcomes_FK FOREIGN KEY (id) REFERENCES crp_program_outcomes(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_expected_study_crp_outcomes_FK_1 FOREIGN KEY (expected_id) REFERENCES project_expected_studies(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_expected_study_crp_outcomes_FK_2 FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;
