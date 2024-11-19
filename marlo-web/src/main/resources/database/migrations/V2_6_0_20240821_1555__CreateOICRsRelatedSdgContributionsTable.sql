CREATE TABLE project_expected_study_related_sdg_contributions (
  id bigint(20) auto_increment NOT NULL,
  lever_sdg_contribution_id bigint(20) NOT NULL,
  expected_study_id bigint(20) NOT NULL,
  id_phase bigint(20) NOT NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp NULL,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text NULL,
  CONSTRAINT project_expected_study_related_sdg_contributions_pk PRIMARY KEY (id),
  CONSTRAINT study_related_sdg_contributions_project_studies_FK FOREIGN KEY (expected_study_id) REFERENCES project_expected_studies(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_related_sdg_contributions_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_related_sdg_contributions_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_related_sdg_contributions_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT study_related_sdg_contributions_FK FOREIGN KEY (lever_sdg_contribution_id) REFERENCES related_alliance_lever_sdg_contributions(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;
