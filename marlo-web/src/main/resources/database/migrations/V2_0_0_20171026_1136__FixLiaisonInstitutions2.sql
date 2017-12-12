/* Update projects to match with added liaison_institutions for WLE */
UPDATE projects p
INNER JOIN liaison_institutions li ON li.id=p.liaison_institution_id
INNER JOIN liaison_institutions li2 ON li2.institution_id = li.institution_id
AND li2.crp_id = (SELECT  id FROM crps c WHERE c.is_active AND c.acronym = "WLE")
SET p.liaison_institution_id = li2.id
WHERE
  li2.institution_id IS NOT NULL
AND p.crp_id = (SELECT  id FROM crps c WHERE c.is_active AND c.acronym = "WLE");