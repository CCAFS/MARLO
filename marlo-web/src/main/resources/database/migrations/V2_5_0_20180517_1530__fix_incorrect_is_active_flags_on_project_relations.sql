## Update activities

UPDATE activities a
INNER JOIN projects p ON a.project_id = p.id
SET a.is_active = 0
WHERE a.is_active = 1 AND p.is_active = 0;

### Update center projects

UPDATE center_projects cp
INNER JOIN projects p ON cp.id = p.id
SET cp.is_active = 0
WHERE cp.is_active = 1 AND p.is_active = 0;

#### Update deliverables

UPDATE deliverables d
INNER JOIN projects p ON d.project_id = p.id
SET d.is_active = 0
WHERE d.is_active = 1 AND p.is_active = 0;

#### Update Expected_study_projects
-- in this instance Expected_study_projects does not have is_active 
--UPDATE expected_study_projects esp
--INNER JOIN projects p ON esp.project_id = p.id
--SET esp.is_active = 0
--WHERE esp.is_active = 1 AND p.is_active = 0;

#### Update Global unit projects

UPDATE global_unit_projects gup
INNER JOIN projects p ON gup.project_id = p.id
SET gup.is_active = 0
WHERE gup.is_active = 1 AND p.is_active = 0;

#### Update ip_project_contribution_overviews

UPDATE ip_project_contribution_overviews ipco
INNER JOIN projects p ON ipco.project_id = p.id
SET ipco.is_active = 0
WHERE ipco.is_active = 1 AND p.is_active = 0;

### Update ip_project_contributions

UPDATE ip_project_contributions ipc
INNER JOIN projects p ON ipc.project_id = p.id
SET ipc.is_active = 0
WHERE ipc.is_active = 1 AND p.is_active = 0;

### Update ip_project_contributions

UPDATE ip_project_indicators ipi
INNER JOIN projects p ON ipi.project_id = p.id
SET ipi.is_active = 0
WHERE ipi.is_active = 1 AND p.is_active = 0;

### Update other_contributions

UPDATE other_contributions oc
INNER JOIN projects p ON oc.project_id = p.id
SET oc.is_active = 0
WHERE oc.is_active = 1 AND p.is_active = 0;

####  Update project budgets

UPDATE project_budgets pb
INNER JOIN projects p ON pb.project_id = p.id
SET pb.is_active = 0
WHERE pb.is_active = 1 AND p.is_active = 0;

####  Update project_budgets_cluser_actvities

UPDATE project_budgets_cluser_actvities pbca
INNER JOIN projects p ON pbca.project_id = p.id
SET pbca.is_active = 0
WHERE pbca.is_active = 1 AND p.is_active = 0;

####  Update project_budgets_flagships

UPDATE project_budgets_flagships pbf
INNER JOIN projects p ON pbf.project_id = p.id
SET pbf.is_active = 0
WHERE pbf.is_active = 1 AND p.is_active = 0;

####  Update project_cluster_activities

UPDATE project_cluster_activities pca
INNER JOIN projects p ON pca.project_id = p.id
SET pca.is_active = 0
WHERE pca.is_active = 1 AND p.is_active = 0;

####  Update project_component_lessons

UPDATE project_component_lessons pcl
INNER JOIN projects p ON pcl.project_id = p.id
SET pcl.is_active = 0
WHERE pcl.is_active = 1 AND p.is_active = 0;

####  Update project_crp_contributions

UPDATE project_crp_contributions pcc
INNER JOIN projects p ON pcc.project_id = p.id
SET pcc.is_active = 0
WHERE pcc.is_active = 1 AND p.is_active = 0;

####  Update project_expected_studies

UPDATE project_expected_studies pes
INNER JOIN projects p ON pes.project_id = p.id
SET pes.is_active = 0
WHERE pes.is_active = 1 AND p.is_active = 0;

####  Update project_focuses

UPDATE project_focuses pf
INNER JOIN projects p ON pf.project_id = p.id
SET pf.is_active = 0
WHERE pf.is_active = 1 AND p.is_active = 0;

####  Update project_focuses_prev

UPDATE project_focuses_prev pfp
INNER JOIN projects p ON pfp.project_id = p.id
SET pfp.is_active = 0
WHERE pfp.is_active = 1 AND p.is_active = 0;

####  Update project_further_contributions

UPDATE project_further_contributions pfc
INNER JOIN projects p ON pfc.project_id = p.id
SET pfc.is_active = 0
WHERE pfc.is_active = 1 AND p.is_active = 0;

####  Update project_highlights

UPDATE project_highlights ph
INNER JOIN projects p ON ph.project_id = p.id
SET ph.is_active = 0
WHERE ph.is_active = 1 AND p.is_active = 0;

###  Update project_leverage

UPDATE project_leverage pl
INNER JOIN projects p ON pl.project_id = p.id
SET pl.is_active = 0
WHERE pl.is_active = 1 AND p.is_active = 0;

###  Update project_locations

UPDATE project_locations ploc
INNER JOIN projects p ON ploc.project_id = p.id
SET ploc.is_active = 0
WHERE ploc.is_active = 1 AND p.is_active = 0;

###  Update project_other_contributions

UPDATE project_other_contributions poc
INNER JOIN projects p ON poc.project_id = p.id
SET poc.is_active = 0
WHERE poc.is_active = 1 AND p.is_active = 0;

###  Update project_outcomes

UPDATE project_outcomes poms
INNER JOIN projects p ON poms.project_id = p.id
SET poms.is_active = 0
WHERE poms.is_active = 1 AND p.is_active = 0;

###  Update project_outcomes_pandr

UPDATE project_outcomes_pandr popd
INNER JOIN projects p ON popd.project_id = p.id
SET popd.is_active = 0
WHERE popd.is_active = 1 AND p.is_active = 0;

###  Update project_partners

UPDATE project_partners pp
INNER JOIN projects p ON pp.project_id = p.id
SET pp.is_active = 0
WHERE pp.is_active = 1 AND p.is_active = 0;

###  Update project_scopes

UPDATE project_scopes ps
INNER JOIN projects p ON ps.project_id = p.id
SET ps.is_active = 0
WHERE ps.is_active = 1 AND p.is_active = 0;
