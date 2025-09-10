CREATE TABLE user_ideas (
	id bigint(20) auto_increment NOT NULL,
	question TEXT NULL,
	answer TEXT NULL,
	is_active tinyint(1) DEFAULT 1 NOT NULL,
	active_since timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
	created_by bigint NOT NULL,
	modified_by bigint NULL,
	modification_justification text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
	CONSTRAINT user_ideas_pk PRIMARY KEY (id),
	CONSTRAINT user_ideas_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT user_ideas_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;