CREATE TABLE intellectual_property_rights_institutions (
  id bigint(20) auto_increment NOT NULL,
  institution_id bigint(20) NULL,
  is_active tinyint(1) NOT NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  created_by bigint(20) NULL,
  modified_by bigint(20) NULL,
  modification_justification text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  CONSTRAINT intellectual_property_rights_inst_pk PRIMARY KEY (id),
  CONSTRAINT intellectual_property_rights_inst_i_FK FOREIGN KEY (institution_id) REFERENCES institutions(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT intellectual_property_rights_inst_users_FK FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT intellectual_property_rights_insts_users_FK_1 FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;