CREATE TABLE project_innovation_functions (
	id bigint(20) auto_increment NOT NULL,
	title TEXT NULL,
	is_active tinyint(1) DEFAULT 1 NOT NULL,
	active_since timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
	created_by bigint NOT NULL,
	modified_by bigint NULL,
	modification_justification text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
	CONSTRAINT innovation_function_pk PRIMARY KEY (id),
	CONSTRAINT innovation_function_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT innovation_function_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
