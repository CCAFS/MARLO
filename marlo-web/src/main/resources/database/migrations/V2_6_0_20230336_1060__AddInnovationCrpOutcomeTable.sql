CREATE TABLE project_innovation_crp_outcomes (
  id bigint(20) auto_increment NOT NULL,
  project_innovation_id bigint(20) NOT NULL,
  crp_outcome_id bigint(20) NOT NULL,
  id_phase bigint(20) NULL,
  CONSTRAINT `PRIMARY` PRIMARY KEY (id),
  CONSTRAINT project_innovation_crp_outcomes_FK FOREIGN KEY (crp_outcome_id) REFERENCES crp_program_outcomes(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_crp_outcomes_FK_1 FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT project_innovation_crp_outcomes_FK_2 FOREIGN KEY (project_innovation_id) REFERENCES project_innovations(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;