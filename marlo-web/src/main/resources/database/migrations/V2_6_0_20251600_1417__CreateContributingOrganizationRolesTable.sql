CREATE TABLE project_innovation_contributing_organization_roles (
  id bigint(20) auto_increment NOT NULL,
  innovation_contributing_organization_id bigint(20) NULL,
  organization_role bigint(20) NULL,
  CONSTRAINT innovation_contributing_org_roles_pk PRIMARY KEY (id),
  CONSTRAINT innovation_contributing_org_roles_org_roles_FK FOREIGN KEY (innovation_contributing_organization_id) REFERENCES organization_roles(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT innovation_contributing_org_roles_contributing_org_FK FOREIGN KEY (innovation_contributing_organization_id) REFERENCES project_innovation_contributing_organizations(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;