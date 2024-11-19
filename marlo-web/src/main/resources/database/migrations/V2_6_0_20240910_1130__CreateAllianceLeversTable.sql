CREATE TABLE alliance_levers (
  id bigint(20) auto_increment NOT NULL,
  name text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  description text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  CONSTRAINT alliance_levers_pk PRIMARY KEY (id),
  CONSTRAINT alliance_levers_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT alliance_levers_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;