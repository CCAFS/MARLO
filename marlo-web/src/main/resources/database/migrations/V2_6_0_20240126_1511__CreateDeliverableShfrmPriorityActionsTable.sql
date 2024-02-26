CREATE TABLE deliverable_shfrm_priority_action (
  id bigint(20) auto_increment NOT NULL,
  deliverable_id bigint(20) NOT NULL,
  shfrm_priority_action_id bigint(20) NOT NULL,
  is_active tinyint(1) NULL,
  id_phase bigint(20) NOT NULL,
  modified_by bigint(20) NULL,
  modification_justification text NULL,
  created_by bigint(20) NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  CONSTRAINT deliverable_shfrm_priority_action_pk PRIMARY KEY (id),
  CONSTRAINT deliverable_shfrm_priority_action_deliverables_FK FOREIGN KEY (deliverable_id) REFERENCES deliverables(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT deliverable_shfrm_priority_action_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT deliverable_shfrm_priority_action_shfrm_priority_actions_FK FOREIGN KEY (shfrm_priority_action_id) REFERENCES shfrm_priority_actions(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT deliverable_shfmr_priority_action_users_FK FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT

)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;