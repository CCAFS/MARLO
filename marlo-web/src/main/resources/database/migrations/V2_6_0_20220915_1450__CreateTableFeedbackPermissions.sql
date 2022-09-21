CREATE TABLE feedback_permissions (
  id bigint(20) auto_increment NOT NULL,
  name text NULL,
  description text NULL,
  CONSTRAINT feedback_permissions_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;