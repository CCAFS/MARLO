CREATE TABLE prms_innovation_aiccra (
	id bigint(20) auto_increment NOT NULL,
	prms_innovation_id bigint(20) NOT NULL,
	project_innovation_id bigint(20) NOT NULL,
	is_active tinyint(1) DEFAULT 1 NOT NULL,
	active_since timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
	created_by bigint NOT NULL,
	modified_by bigint NULL,
	modification_justification text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
	CONSTRAINT prms_innovation_aiccra_pk PRIMARY KEY (id),
	CONSTRAINT prms_innovation_aiccra_prms_innovations_FK FOREIGN KEY (prms_innovation_id) REFERENCES prms_innovations(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT prms_innovation_aiccra_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT prms_innovation_aiccra_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;