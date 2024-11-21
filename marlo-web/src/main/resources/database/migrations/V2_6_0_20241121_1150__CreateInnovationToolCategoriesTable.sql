CREATE TABLE project_innovation_tool_categories (
  id bigint auto_increment NOT NULL,
  tool_category_id bigint NOT NULL,
  innovation_id bigint NOT NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  created_by bigint NULL,
  modified_by bigint NULL,
  modification_justification text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  id_phase bigint NULL,
  other_narrative text NULL,
  CONSTRAINT project_innovation_tool_category_pk PRIMARY KEY (id),
  CONSTRAINT project_innovation_tool_category_project_innovations_FK FOREIGN KEY (innovation_id) REFERENCES project_innovations(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_tool_category_tool_function_categories_FK FOREIGN KEY (tool_category_id) REFERENCES tool_function_categories(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_tool_category_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_tool_category_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_tool_category_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;