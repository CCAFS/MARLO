INSERT INTO feedback_roles_permissions (role_id,feedback_permission_id,description)
  VALUES (425,4,'Permission 4: FPL Role - Flagship Leaders');
INSERT INTO feedback_roles_permissions (role_id,feedback_permission_id,description)
  VALUES (432,4,'Permission 4: FMP Role - Flagship Manager');
INSERT INTO feedback_roles_permissions (role_id,feedback_permission_id,description)
  VALUES (424,4,'Permission 4: RPL Role - Regional Program Leaders');
INSERT INTO feedback_roles_permissions (role_id,feedback_permission_id,description)
  VALUES (433,4,'Permission 4: RPM Role - Regional Manager');
  
UPDATE feedback_roles_permissions
  SET description='Permission 2: PMU Role -  Theme Clusters',role_id=427
  WHERE id=5;
UPDATE feedback_roles_permissions
  SET description='Permission 2: PMU Role - Regional Clusters',role_id=427
  WHERE id=6;
UPDATE feedback_roles_permissions
  SET description='Permission 2: RPL Role - Country Clusters'
  WHERE id=7;
