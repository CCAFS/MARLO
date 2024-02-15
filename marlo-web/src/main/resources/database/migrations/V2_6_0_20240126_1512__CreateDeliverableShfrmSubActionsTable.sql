CREATE TABLE deliverable_shfmr_sub_action (
  id bigint(20) auto_increment NOT NULL,
  deliverable_shfrm_priority_action_id bigint(20) NOT NULL,
  shfrm_sub_action_id bigint(20) NULL,
  is_active tinyint(1) NULL,
  id_phase bigint(20) NOT NULL,
    modified_by bigint(20) NULL,
  modification_justification text NULL,
  created_by bigint(20) NULL,
  active_since timestamp DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP NULL,
  CONSTRAINT deliverable_shfmr_sub_action_pk PRIMARY KEY (id),
  CONSTRAINT deliverable_shfmr_sub_action_phases_FK FOREIGN KEY (id_phase) REFERENCES phases(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT deliverable_shfmr_sub_action_users_FK FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT deliverable_shfmr_sub_action_shfrm_sub_actions_FK FOREIGN KEY (shfrm_sub_action_id) REFERENCES shfrm_sub_actions(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT deliverable_shfmr_sub_action_deliverable_FK FOREIGN KEY (deliverable_shfrm_priority_action_id) REFERENCES deliverable_shfrm_priority_action(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)

ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;