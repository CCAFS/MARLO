CREATE TABLE organization_roles (
  id bigint(20) auto_increment NOT NULL,
  name text NULL,
  is_active tinyint(1) NULL,
  CONSTRAINT organization_roles_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;