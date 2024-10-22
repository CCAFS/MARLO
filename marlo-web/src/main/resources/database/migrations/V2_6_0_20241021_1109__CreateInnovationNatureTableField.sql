CREATE TABLE rep_ind_innovation_natures (
  id bigint(20) auto_increment NOT NULL,
  name text NULL,
  definition text NULL,
  is_active tinyint(1) NULL,
  CONSTRAINT rep_ind_innovation_natures_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;