CREATE TABLE project_innovation_sdgs (
  id bigint(20) auto_increment NOT NULL,
  innovation_id bigint(20) NULL,
  sdg_id bigint(20) NULL,
  id_phase bigint(20) NULL,
  CONSTRAINT innovation_sdg_pk PRIMARY KEY (id),
  CONSTRAINT innovation_sdg_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT innovation_sdg_project_innovations_FK FOREIGN KEY (innovation_id) REFERENCES project_innovations(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT innovation_sdg_sustainable_development_goals_FK FOREIGN KEY (sdg_id) REFERENCES sustainable_development_goals(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;