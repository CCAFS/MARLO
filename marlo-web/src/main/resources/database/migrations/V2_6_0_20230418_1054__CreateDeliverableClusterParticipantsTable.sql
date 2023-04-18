CREATE TABLE deliverable_cluster_participants (
  id bigint(20) auto_increment NOT NULL,
  deliverable_id bigint(20) NOT NULL,
  id_phase bigint(20) NOT NULL,
  participants double NULL,
  females double NULL,
  african double NULL,
  youth double NULL,
  project_id bigint(20) NULL,
  CONSTRAINT deliverable_cluster_participants_pk PRIMARY KEY (id),
  CONSTRAINT deliverable_cluster_participants_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT deliverable_cluster_participants_FK_1 FOREIGN KEY (deliverable_id) REFERENCES deliverables(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT deliverable_cluster_participants_FK_2 FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;