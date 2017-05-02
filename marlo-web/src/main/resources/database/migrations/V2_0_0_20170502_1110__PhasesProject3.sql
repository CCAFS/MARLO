INSERT INTO `phases` (`id`,`description`, `year`, `crp_id`) VALUES (4,'Planning', '2017', '3');

INSERT INTO `project_phases`(id_phase,project_id) select 4,id
from projects
where is_active=1
and crp_id=3
and `status`=2;

INSERT INTO `phases` (`id`,`description`, `year`, `crp_id`) VALUES (5,'Planning', '2017', '4');

INSERT INTO `project_phases`(id_phase,project_id) select 5,id
from projects
where is_active=1
and crp_id=4
and `status`=2;


INSERT INTO `phases` (`id`,`description`, `year`, `crp_id`) VALUES (6,'Planning', '2017', '7');

INSERT INTO `project_phases`(id_phase,project_id) select 6,id
from projects
where is_active=1
and crp_id=7
and `status`=2;

INSERT INTO `phases` (`id`,`description`, `year`, `crp_id`) VALUES (7,'Planning', '2017', '21');

INSERT INTO `project_phases`(id_phase,project_id) select 7,id
from projects
where is_active=1
and crp_id=21
and `status`=2;


INSERT INTO `phases` (`id`,`description`, `year`, `crp_id`) VALUES (8,'Planning', '2017', '22');

INSERT INTO `project_phases`(id_phase,project_id) select 8,id
from projects
where is_active=1
and crp_id=22
and `status`=2;
