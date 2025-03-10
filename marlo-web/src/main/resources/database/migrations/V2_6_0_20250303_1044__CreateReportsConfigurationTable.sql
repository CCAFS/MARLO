CREATE TABLE report_configurations (
  id bigint(20) auto_increment NOT NULL,
  name text NULL,
  description text NULL,
  value text NULL,
  CONSTRAINT reports_configuration_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;