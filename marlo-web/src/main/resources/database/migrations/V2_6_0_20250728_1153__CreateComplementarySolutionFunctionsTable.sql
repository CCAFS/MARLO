CREATE TABLE project_innovation_complementary_solution_functions (
	id bigint auto_increment NOT NULL,
	complementary_solution_id bigint NULL,
	project_innovation_function_id bigint NULL,
	is_active tinyint(1) DEFAULT 1 NOT NULL,
	active_since timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
	created_by bigint NOT NULL,
	modified_by bigint NULL,
	modification_justification text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
	id_phase bigint NULL,
	CONSTRAINT `PRIMARY` PRIMARY KEY (id),
	CONSTRAINT complementary_solution_phases_FK_ FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT complementary_solution_u_FK_2 FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT complementary_solution_u_FK_1_ FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT complementary_solution_functions_FK FOREIGN KEY (complementary_solution_id) REFERENCES project_innovation_complementary_solutions(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT project_innovation_functions_FK FOREIGN KEY (project_innovation_function_id) REFERENCES project_innovation_functions(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci
COMMENT='';
CREATE INDEX complementary_solution_innovation_functions_FK USING BTREE ON project_innovation_complementary_solution_functions (project_innovation_function_id);
CREATE INDEX complementary_solution_rep_ind_innovation_types_FK USING BTREE ON project_innovation_complementary_solution_functions (complementary_solution_id);
CREATE INDEX complementary_solution_users_FK USING BTREE ON project_innovation_complementary_solution_functions (created_by);
CREATE INDEX complementary_solution_users_FK_1 USING BTREE ON project_innovation_complementary_solution_functions (modified_by);
CREATE INDEX innovation_complementary_solution_phases_FK USING BTREE ON project_innovation_complementary_solution_functions (id_phase);
