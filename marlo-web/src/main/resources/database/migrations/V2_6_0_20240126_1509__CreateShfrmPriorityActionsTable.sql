CREATE TABLE shfrm_priority_actions (
  id bigint(20) auto_increment NOT NULL,
  name varchar(100) NULL,
  is_active tinyint(1) NULL,
  description varchar(300) NULL,
  modified_by bigint(20) NULL,
  modification_justification text NULL,
  created_by bigint(20) NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  CONSTRAINT shfrm_priority_action_pk PRIMARY KEY (id),
  CONSTRAINT shfrm_priority_actions_users_FK FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;