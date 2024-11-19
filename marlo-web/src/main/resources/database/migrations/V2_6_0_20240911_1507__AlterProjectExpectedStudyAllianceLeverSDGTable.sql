DROP TABLE project_expected_study_alliance_levers;

CREATE TABLE project_expected_study_sdg_alliance_levers (
  id bigint(20) auto_increment NOT NULL,
  expected_id bigint(20) NOT NULL,
  alliance_lever_id bigint(20) NOT NULL,
  sdg_contribution_id bigint(20) NOT NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  id_phase bigint(20) NULL,
  is_primary tinyint(1) NULL,
  CONSTRAINT expected_study_alliance_levers_pk PRIMARY KEY (id),
  CONSTRAINT expected_study_alliance_levers_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT expected_study_alliance_levers_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT expected_study_alliance_levers_project_expected_studies_FK FOREIGN KEY (expected_id) REFERENCES project_expected_studies(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT expected_study_alliance_levers_alliance_levers_FK FOREIGN KEY (alliance_lever_id) REFERENCES alliance_levers(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT expected_study_alliance_levers_sdg_contributions_FK FOREIGN KEY (sdg_contribution_id) REFERENCES sdg_contributions(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT expected_study_alliance_levers_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;