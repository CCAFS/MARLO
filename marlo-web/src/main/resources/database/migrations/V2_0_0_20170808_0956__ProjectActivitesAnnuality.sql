
SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE `activities`
ADD COLUMN `composed_id`  varchar(20)  NULL AFTER `modification_justification`;

update  activities ml 
set ml.composed_id=CONCAT(ml.project_id,'-',ml.id) ;

CREATE TEMPORARY TABLE
IF NOT EXISTS table_activites_project AS (SELECT * FROM activities);

TRUNCATE TABLE activities;



ALTER TABLE `activities`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `activities` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO activities (
 
project_id,
title,
description,
startDate,
endDate,
leader_id,
activityStatus,
activityProgress,
created_by,
is_active,
active_since,
modified_by,
modification_justification,
composed_id,
id_phase
) SELECT 

t2.project_id,
t2.title,
t2.description,
t2.startDate,
t2.endDate,
t2.leader_id,
t2.activityStatus,
t2.activityProgress,
t2.created_by,
t2.is_active,
t2.active_since,
t2.modified_by,
t2.modification_justification,
t2.composed_id,
ph.id
FROM
  table_activites_project t2
left JOIN project_phases pp ON pp.project_id = t2.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;
