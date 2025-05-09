CREATE TABLE rep_ind_options (
  id bigint(20) auto_increment NOT NULL,
  title text NULL,
  description TEXT NULL,
  CONSTRAINT rep_ind_options_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
