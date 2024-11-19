CREATE TABLE global_targets (
  id bigint(20) auto_increment NOT NULL,
  name text NULL,
  description text NULL,
  st_impact_area_id bigint(20) NULL,
  CONSTRAINT global_targets_pk PRIMARY KEY (id),
  CONSTRAINT global_targets_st_impact_areas_FK FOREIGN KEY (st_impact_area_id) REFERENCES st_impact_areas(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;