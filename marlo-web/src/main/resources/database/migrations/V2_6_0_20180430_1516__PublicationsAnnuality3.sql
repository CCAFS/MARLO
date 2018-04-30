SET FOREIGN_KEY_CHECKS = 0;

-- deliverable_users

INSERT INTO deliverable_users (
deliverable_id,
id_phase,
first_name,
last_name,
element_id
)
SELECT
  du.deliverable_id,
  di.id_phase,
  du.first_name,
  du.last_name,
  du.element_id 
FROM
  deliverable_users AS du
  INNER JOIN deliverables AS d ON du.deliverable_id = d.id 
  AND d.is_publication = 1
  INNER JOIN deliverables_info AS di ON di.deliverable_id = d.id
  INNER JOIN phases AS p ON di.id_phase = p.id;
  
DELETE du.* FROM deliverable_users du
INNER JOIN deliverables AS d ON du.deliverable_id = d.id 
  AND d.is_publication = 1
WHERE du.id_phase IS NULL;
