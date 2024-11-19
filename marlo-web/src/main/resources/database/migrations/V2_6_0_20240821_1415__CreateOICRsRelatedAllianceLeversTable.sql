CREATE TABLE project_expected_study_related_alliance_levers (
  id bigint(20) auto_increment NOT NULL,
  related_alliance_lever_id bigint(20) NOT NULL,
  expected_study_id bigint(20) NOT NULL,
  id_phase bigint(20) NOT NULL,
  is_active tinyint(1) NOT NULL,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text NULL,
  active_since timestamp NULL,
  CONSTRAINT study_related_alliance_levers_pk PRIMARY KEY (id),
  CONSTRAINT study_related_alliance_levers_project_expected_studies_FK FOREIGN KEY (expected_study_id) REFERENCES project_expected_studies(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_related_alliance_levers_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_related_alliance_levers_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_related_alliance_levers_related_alliance_levers_FK FOREIGN KEY (related_alliance_lever_id) REFERENCES related_alliance_levers(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_related_alliance_levers_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;