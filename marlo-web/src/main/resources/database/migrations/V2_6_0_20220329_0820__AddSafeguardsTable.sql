CREATE TABLE safeguards (
  id bigint(20) auto_increment NOT NULL,
  file_id bigint(20) NULL,
  project_id bigint(20) NULL,
  id_phase bigint(20) NULL,
  is_active tinyint(1) NOT NULL,
  CONSTRAINT safeguards_pk PRIMARY KEY (id),
  CONSTRAINT safeguards_FK FOREIGN KEY (project_id) REFERENCES aiccradb.projects(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT safeguards_FK_1 FOREIGN KEY (id_phase) REFERENCES aiccradb.phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;
CREATE UNIQUE INDEX safeguards_id_IDX USING BTREE ON aiccradb.safeguards (id);
CREATE INDEX safeguards_project_id_IDX USING BTREE ON aiccradb.safeguards (project_id);
CREATE INDEX safeguards_id_phase_IDX USING BTREE ON aiccradb.safeguards (id_phase);