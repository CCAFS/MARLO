CREATE TABLE project_innovation_alliance_organizations (
  id bigint auto_increment NOT NULL,
  project_innovation_id bigint NULL,
  id_phase bigint NULL,
  institution_type_id bigint NULL,
  organization_name text NULL,
  is_scaling_partner tinyint(1) NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  created_by bigint NULL,
  modified_by bigint NULL,
  modification_justification text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  CONSTRAINT project_innovation_alliance_organizations_pk PRIMARY KEY (id),
  CONSTRAINT project_innovation_alliance_organizations_FK FOREIGN KEY (project_innovation_id) REFERENCES project_innovations(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_institution_types_FK FOREIGN KEY (institution_type_id) REFERENCES institution_types(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_alliance_organizations_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_alliance_organizations_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_alliance_organizations_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;