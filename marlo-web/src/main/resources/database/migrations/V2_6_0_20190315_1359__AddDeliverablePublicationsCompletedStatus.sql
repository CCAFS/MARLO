/* Add status Complete to all publications */

UPDATE deliverables_info di
INNER JOIN deliverables d ON d.id = di.deliverable_id
SET di.`status` = 3
WHERE di.`status` IS NULL AND (d.project_id IS NULL OR d.is_publication);