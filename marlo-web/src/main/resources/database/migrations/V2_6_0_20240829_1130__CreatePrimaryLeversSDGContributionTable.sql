CREATE TABLE primary_levers_related_sdg_contributions (
  id bigint(20) auto_increment NOT NULL,
  primary_lever_id bigint(20) NOT NULL,
  related_sdg_contribution_id bigint(20) NOT NULL,
  id_phase bigint(20) NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  CONSTRAINT primary_sdg_contributions_pk PRIMARY KEY (id),
  CONSTRAINT primary_sdg_contributions_primary_levers_FK FOREIGN KEY (primary_lever_id) REFERENCES primary_alliance_levers(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT primary_sdg_contributions_related_lever_sdg_contributions_FK FOREIGN KEY (related_sdg_contribution_id) REFERENCES related_alliance_lever_sdg_contributions(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT primary_sdg_contributions_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT primary_sdg_contributions_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT primary_sdg_contributions_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;
