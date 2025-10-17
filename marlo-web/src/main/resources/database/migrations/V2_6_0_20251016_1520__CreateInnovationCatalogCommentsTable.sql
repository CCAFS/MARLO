CREATE TABLE innovation_catalog_comments (
	id bigint(20) auto_increment NOT NULL,
	comment TEXT NULL,
	is_active tinyint(1) DEFAULT 1 NOT NULL,
	active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NOT NULL,
	modification_justification text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
	user_name text NULL,
	user_lastname text NULL,
	user_email text NULL,
	CONSTRAINT innovation_catalog_comments_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;