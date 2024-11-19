CREATE TABLE project_expected_study_alliance_levers_outcomes (
  id bigint(20) auto_increment NOT NULL,
  expected_id bigint(20) NOT NULL,
  alliance_lever_id bigint(20) NOT NULL,
  lever_outcome_id bigint(20) NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  id_phase bigint(20) NULL,
  lever_comments text NULL,
  CONSTRAINT study_alliance_levers_outcomes_pk PRIMARY KEY (id),
  CONSTRAINT study_alliance_levers_outcomes_studies_FK FOREIGN KEY (expected_id) REFERENCES project_expected_studies(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_alliance_levers_outcomes_alliance_levers_FK FOREIGN KEY (alliance_lever_id) REFERENCES alliance_levers(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_alliance_alliance_lever_outcomes_FK FOREIGN KEY (lever_outcome_id) REFERENCES alliance_lever_outcomes(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_alliance_levers_outcomes_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_alliance_levers_outcomes_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_alliance_levers_outcomes_phases_FK FOREIGN KEY (id) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;