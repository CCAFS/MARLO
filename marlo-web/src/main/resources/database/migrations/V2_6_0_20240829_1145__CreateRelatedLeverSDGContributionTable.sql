CREATE TABLE related_levers_sdg_contributions (
  id bigint(20) auto_increment NOT NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  id_phase bigint(20) NULL,
  related_alliance_lever_id bigint(20) NOT NULL,
  sdg_contribution_id bigint(20) NOT NULL,
  CONSTRAINT related_levers_sdg_contributions_pk PRIMARY KEY (id),
  CONSTRAINT related_levers_sdg_sdg_contributions_fk FOREIGN KEY (sdg_contribution_id) REFERENCES sdg_contributions(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT related_levers_sdg_related_alliance_levers_fk FOREIGN KEY (related_alliance_lever_id) REFERENCES related_alliance_levers(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT related_levers_sdg_users_fk FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT related_levers_sdg_users_fk_1 FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;