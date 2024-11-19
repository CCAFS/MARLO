CREATE TABLE related_alliance_lever_sdg_contributions (
  id bigint(20) auto_increment NOT NULL,
  name text NULL,
  description text NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text NULL,
  related_alliance_lever_id bigint(20) NULL,
  CONSTRAINT related_alliance_lever_sdg_contribution_pk PRIMARY KEY (id),
  CONSTRAINT related_alliance_lever_sdg_contribution_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT related_alliance_lever_sdg_contribution_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT related_alliance_levers_FK FOREIGN KEY (related_alliance_lever_id) REFERENCES related_alliance_levers(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;