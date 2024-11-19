CREATE TABLE project_innovation_reference_datasets (
  id bigint auto_increment NOT NULL,
  project_innovation_id bigint NULL,
  reference text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  id_phase bigint NULL,
  link text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  is_external_author tinyint(1) NULL,
  is_active tinyint(1) DEFAULT 1 NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
  created_by bigint NOT NULL,
  modified_by bigint NULL,
  modification_justification text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  CONSTRAINT innovation_reference_datasets_pk PRIMARY KEY (id),
  CONSTRAINT innovation_reference_datasets_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT innovation_reference_datasets_FK FOREIGN KEY (project_innovation_id) REFERENCES project_innovations(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT innovation_reference_datasets_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT innovation_reference_datasets_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
