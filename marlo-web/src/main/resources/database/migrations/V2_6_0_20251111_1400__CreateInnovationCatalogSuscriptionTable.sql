CREATE TABLE innovation_catalog_suscriptions (
	id bigint(20) auto_increment NOT NULL,
	email TEXT NULL,
	is_active TINYINT(1) NULL,
	active_since TIMESTAMP NULL,
	modification_justification TEXT NULL,
	CONSTRAINT catalog_suscriptions_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;