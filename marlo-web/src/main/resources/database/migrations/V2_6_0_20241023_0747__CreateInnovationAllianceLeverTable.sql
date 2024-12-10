CREATE TABLE project_innovation_alliance_levers (
  id bigint(20) auto_increment NOT NULL,
  innovation_id bigint(20) NOT NULL,
  alliance_lever_id bigint(20) NOT NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  id_phase bigint(20) NULL,
  lever_comments text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  CONSTRAINT project_innovation_alliance_levers_pk PRIMARY KEY (id),
  CONSTRAINT project_innovation_alliance_levers_alliance_levers_FK FOREIGN KEY (alliance_lever_id) REFERENCES alliance_levers(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_alliance_levers_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_alliance_levers_project_innovations_FK FOREIGN KEY (innovation_id) REFERENCES project_innovations(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_alliance_levers_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_alliance_levers_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
