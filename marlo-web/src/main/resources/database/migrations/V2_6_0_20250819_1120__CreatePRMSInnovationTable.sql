CREATE TABLE prms_innovations (
	id bigint(20) auto_increment NOT NULL,
	prms_result_id bigint(20) NULL,
	title TEXT NULL,
	description text NULL,
	type_id bigint(20) NULL,
	type_name text NULL,
	`year` INT NULL,
	pdf_link TEXT NULL,
	readiness_level_id bigint(20) NULL,
	readiness_level_name TEXT NULL,
	CONSTRAINT prms_innovations_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;