ALTER TABLE `deliverables`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `crp_id`;

ALTER TABLE `deliverables` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

update   deliverables d
INNER JOIN projects p ON p.id = d.project_id
INNER JOIN crps cp ON cp.id = p.crp_id
INNER JOIN phases ph ON cp.id = ph.crp_id
set id_phase=ph.id
WHERE
  d.project_id IS NOT NULL
AND d.is_active = 1
AND ph.`year` = '2017'
AND ph.description = 'Planning';


update
  deliverables d
INNER JOIN crps cp ON cp.id = d.crp_id
INNER JOIN phases ph ON cp.id = ph.crp_id
set id_phase=ph.id
WHERE
  d.project_id IS  NULL
AND d.is_active = 1
AND ph.`year` = '2016'
AND ph.description = 'Reporting';


update 
  deliverables d
INNER JOIN projects p ON p.id = d.project_id
INNER JOIN crps cp ON cp.id = p.crp_id
INNER JOIN phases ph ON cp.id = ph.crp_id
set id_phase=ph.id
WHERE
  d.project_id IS NOT NULL
AND d.is_active = 1
AND ph.`year` = '2016'
AND ph.description = 'Reporting'
and (select COUNT('x') from deliverables_info   info where d.id=info.deliverable_id and info.id_phase in (1,3))>0
;


