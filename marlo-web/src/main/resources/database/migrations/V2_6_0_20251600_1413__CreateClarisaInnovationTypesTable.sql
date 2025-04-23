CREATE TABLE clarisa_innovation_types (
  id bigint(20) auto_increment NOT NULL,
  code bigint(20) NULL,
  name text NULL,
  definition text NULL,
  CONSTRAINT clarisaInnovationTypes_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE rep_ind_innovation_types DROP COLUMN clarisa_name;
ALTER TABLE rep_ind_innovation_types DROP COLUMN clarisa_description;