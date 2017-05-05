INSERT INTO `phases` (`id`,`description`, `year`, `crp_id`) VALUES (1,'Planning', '2017', '1');
INSERT INTO `phases` (`id`,`description`, `year`, `crp_id`) VALUES (2,'Planning', '2017', '5');
INSERT INTO `phases` (`id`,`description`, `year`, `crp_id`) VALUES (3,'Reporting', '2016', '1');
INSERT INTO `project_phases`(id_phase,project_id) select 1,id
from projects
where is_active=1
and crp_id=1
and `status`=2;
INSERT INTO `project_phases`(id_phase,project_id) select 2,id
from projects
where is_active=1
and crp_id=5
and `status`=2;

INSERT INTO `project_phases`(id_phase,project_id) select 3,id
from projects
where is_active=1
and crp_id=1
and `reporting`=1;