CREATE TABLE feedback_roles_permissions (
  id bigint(20) auto_increment NOT NULL,
  role_id bigint(20) NOT NULL,
  feedback_permission_id bigint(20) NOT NULL,
  cluster_type_id bigint(20) NULL,
  description text NULL,
  CONSTRAINT feedback_roles_permissions_pk PRIMARY KEY (id),
  CONSTRAINT feedback_roles_permissions_FK FOREIGN KEY (feedback_permission_id) REFERENCES feedback_permissions(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT feedback_roles_permissions_FK_1 FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT feedback_roles_permissions_FK_2 FOREIGN KEY (cluster_type_id) REFERENCES cluster_types(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci