CREATE TABLE shfrm_sub_actions (
  id bigint(20) auto_increment NOT NULL,
  name varchar(100) NULL,
  description varchar(300) NULL,
  shfrm_priority_action_id bigint(20) NOT NULL,
  is_active tinyint(1) NULL,
  modified_by bigint(20) NULL,
  modification_justification text NULL,
  created_by bigint(20) NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  CONSTRAINT shfrm_sub_action_pk PRIMARY KEY (id),
  CONSTRAINT shfrm_sub_action_users_FK FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT shfrm_sub_actions_FK FOREIGN KEY (shfrm_priority_action_id) REFERENCES shfrm_priority_actions(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_czech_ci;