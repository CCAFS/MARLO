CREATE TABLE project_innovation_bundles (
	id bigint(20) auto_increment NOT NULL,
	project_innovation_id bigint(20) NOT NULL,
	selected_innovation_id bigint(20) NOT NULL,
	id_phase bigint(20) NULL,
	is_active tinyint(1) DEFAULT 1 NOT NULL,
	active_since timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
	created_by bigint NOT NULL,
	modified_by bigint NULL,
	modification_justification text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
	CONSTRAINT bundle_innovation_pk PRIMARY KEY (id),
	CONSTRAINT bundle_innovation_project_innovations_FK FOREIGN KEY (project_innovation_id) REFERENCES project_innovations(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT bundle_innovation_project_innovations_FK_1 FOREIGN KEY (selected_innovation_id) REFERENCES project_innovations(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT bundle_innovation_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT bundle_innovation_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT bundle_innovation_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;