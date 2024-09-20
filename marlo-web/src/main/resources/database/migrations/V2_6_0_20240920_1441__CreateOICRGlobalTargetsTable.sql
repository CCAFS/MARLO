CREATE TABLE project_expected_study_global_targets (
  id bigint(20) auto_increment NOT NULL,
  expected_id bigint(20) NULL,
  global_target_id bigint(20) NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  id_phase bigint(20) NULL,
  CONSTRAINT study_global_targets_pk PRIMARY KEY (id),
  CONSTRAINT study_global_targets_studies_FK FOREIGN KEY (expected_id) REFERENCES project_expected_studies(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_global_targets_global_targets_FK FOREIGN KEY (global_target_id) REFERENCES global_targets(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_global_targets_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_global_targets_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_global_targets_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;