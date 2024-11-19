CREATE TABLE project_expected_study_partnerships (
  id bigint(20) auto_increment NOT NULL,
  expected_id bigint(20) NULL,
  institution_id bigint(20) NULL,
  id_phase bigint(20) NULL,
  expected_study_partner_type_id bigint(20) NULL,
  is_active tinyint(1) DEFAULT 1 NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
  created_by bigint(20) NOT NULL,
  modified_by bigint(20) NULL,
  modification_justification text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  CONSTRAINT study_partnerships_pk PRIMARY KEY (id),
  CONSTRAINT study_partnerships_studies_FK FOREIGN KEY (expected_id) REFERENCES project_expected_studies(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_partnerships_institutions_FK FOREIGN KEY (institution_id) REFERENCES institutions(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_partnerships_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_partnerships_study_partner_type_FK FOREIGN KEY (expected_study_partner_type_id) REFERENCES project_expected_study_partner_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_partnerships_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_partnerships_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
