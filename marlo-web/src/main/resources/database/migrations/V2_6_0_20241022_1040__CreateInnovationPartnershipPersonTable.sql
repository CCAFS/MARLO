CREATE TABLE project_innovation_partnership_persons (
  id bigint(20) auto_increment NOT NULL,
  partnership_id bigint(20) NULL,
  user_id bigint(20) NULL,
  is_active tinyint(1) DEFAULT 1 NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
  created_by bigint(20) NOT NULL,
  modified_by bigint(20) NULL,
  modification_justification text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  CONSTRAINT innovation_partnership_persons_pk PRIMARY KEY (id),
  CONSTRAINT partnership_persons_innovation_partnerships_FK FOREIGN KEY (partnership_id) REFERENCES project_innovation_partnerships(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_partnership_persons_users_FK FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_partnership_persons_users_FK_1 FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_partnership_persons_users_FK_2 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
