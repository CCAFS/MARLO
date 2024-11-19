CREATE TABLE primary_alliance_levers (
  id bigint(20) auto_increment NOT NULL,
  name text NULL,
  description text NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text NULL,
  CONSTRAINT primary_alliance_lever_created_primary PRIMARY KEY (id),
  CONSTRAINT primary_alliance_lever_created_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT primary_alliance_lever_modified_FK FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;