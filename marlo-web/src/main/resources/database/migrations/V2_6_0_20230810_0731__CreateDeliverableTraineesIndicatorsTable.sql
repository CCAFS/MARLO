CREATE TABLE deliverable_trainees_indicators (
  id bigint(20) auto_increment NOT NULL,
  `indicator` text NOT NULL,
  id_phase bigint(20) NOT NULL,
  CONSTRAINT deliverable_trainees_indicators_pk PRIMARY KEY (id),
  CONSTRAINT deliverable_trainees_indicators_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;