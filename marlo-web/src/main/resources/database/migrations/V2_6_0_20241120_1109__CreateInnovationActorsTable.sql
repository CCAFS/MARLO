CREATE TABLE project_innovation_actors (
  id bigint auto_increment NOT NULL,
  innovation_id bigint NOT NULL,
  actor_id bigint NOT NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  created_by bigint NULL,
  modified_by bigint NULL,
  modification_justification text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  id_phase bigint NULL,
  is_women_youth tinyint(1) NULL,
  is_women_not_youth tinyint(1) NULL,
  is_men_youth tinyint(1) NULL,
  is_men_not_youth tinyint(1) NULL,
  is_nonbinary_youth tinyint(1) NULL,
  is_nonbinary_not_youth tinyint(1) NULL,
  CONSTRAINT project_innovation_actors_pk PRIMARY KEY (id),
  CONSTRAINT project_innovation_actors_project_innovations_FK FOREIGN KEY (innovation_id) REFERENCES project_innovations(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_actors_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_actors_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_actors_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_actors_actors_FK FOREIGN KEY (actor_id) REFERENCES actors(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;