CREATE TABLE innovation_catalog_reports (
	id bigint(20) auto_increment NOT NULL,
	is_active tinyint(1) DEFAULT 1 NOT NULL,
	active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NOT NULL,
	modification_justification text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
	innovation_id bigint(20) NULL,
	user_name text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
	user_lastname text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
	user_email text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
	interest_narrative TEXT NULL,
	CONSTRAINT innovation_catalog_report_pk PRIMARY KEY (id),
	CONSTRAINT innovation_catalog_report_project_innovations_FK FOREIGN KEY (id) REFERENCES project_innovations(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;
