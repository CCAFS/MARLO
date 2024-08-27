CREATE TABLE quantification_types (
  id bigint(20) auto_increment NOT NULL,
  name text NULL,
  description text NULL,
  CONSTRAINT quantification_types_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;