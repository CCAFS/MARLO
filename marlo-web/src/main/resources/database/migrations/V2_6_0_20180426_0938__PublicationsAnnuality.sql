SET FOREIGN_KEY_CHECKS = 0;

-- deliverable_programs
CREATE TEMPORARY TABLE
IF  NOT EXISTS table_deliverable_programs AS ( SELECT * FROM deliverable_programs );
  
TRUNCATE TABLE deliverable_programs;
INSERT INTO deliverable_programs (
deliverable_id,
crp_program,
id_phase
)
SELECT
  t2.deliverable_id,
  t2.crp_program,
  ph.id 
FROM
  table_deliverable_programs t2
  LEFT JOIN deliverables d ON d.id = t2.deliverable_id
  LEFT JOIN deliverables_info di ON di.deliverable_id = d.id
  LEFT JOIN phases ph ON ph.id = di.id_phase;
  
ALTER TABLE `deliverable_programs` 
MODIFY COLUMN `id_phase` bigint(20) NOT NULL AFTER `crp_program`;

-- deliverable_leaders

CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_leaders AS (SELECT * FROM deliverable_leaders);

TRUNCATE TABLE deliverable_leaders;

INSERT INTO deliverable_leaders (
deliverable_id,
instituion_id,
id_phase
) SELECT 
t2.deliverable_id,
t2.instituion_id,
ph.id
FROM
  table_deliverable_leaders t2
LEFT JOIN deliverables d ON d.id = t2.deliverable_id
LEFT JOIN deliverables_info di ON di.deliverable_id = d.id
LEFT JOIN phases ph ON ph.id = di.id_phase;

ALTER TABLE `deliverable_leaders` 
MODIFY COLUMN `id_phase` bigint(20) NOT NULL AFTER `instituion_id`;