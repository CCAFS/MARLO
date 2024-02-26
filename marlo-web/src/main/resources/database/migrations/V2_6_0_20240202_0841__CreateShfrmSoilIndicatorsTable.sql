CREATE TABLE soil_indicators (
  id bigint(20) auto_increment NOT NULL,
  indicator_name varchar(100) NOT NULL,
  indicador_id bigint(20) NULL,
  id_phase bigint(20) NOT NULL,
  CONSTRAINT soil_indicators_pk PRIMARY KEY (id),
  CONSTRAINT soil_indicators_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;