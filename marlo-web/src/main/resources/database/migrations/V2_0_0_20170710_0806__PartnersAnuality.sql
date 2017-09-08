
SET FOREIGN_KEY_CHECKS = 0;



CREATE TEMPORARY TABLE
IF NOT EXISTS tablepartners AS (SELECT * FROM project_partners);



CREATE TEMPORARY TABLE
IF NOT EXISTS table_temp_persons AS (
SELECT
  ppp.*,pp.institution_id,pp.project_id
FROM
  project_partners pp
INNER JOIN project_partner_persons ppp ON pp.id = ppp.project_partner_id)
;

CREATE TEMPORARY TABLE
IF NOT EXISTS table_temp_locations AS (
SELECT
  ppp.*,pp.institution_id,pp.project_id
FROM
  project_partners pp
INNER JOIN project_partner_locations ppp ON pp.id = ppp.project_partner_id)
;


CREATE TEMPORARY TABLE
IF NOT EXISTS table_deliverable_partnerships AS (select dp.*,ppp.project_partner_id,ppp.user_id from deliverable_partnerships dp inner join project_partner_persons ppp on dp.partner_person_id=ppp.id);

TRUNCATE TABLE deliverable_partnerships;


ALTER TABLE `deliverable_partnerships`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `deliverable_id`;

ALTER TABLE `deliverable_partnerships` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

TRUNCATE TABLE project_partners;
TRUNCATE TABLE project_partner_persons;
TRUNCATE TABLE project_partner_locations;


ALTER TABLE `project_partners`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `project_partners` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO project_partners (
 
 project_id,
institution_id,
is_active,
active_since,
created_by,
modified_by,
modification_justification,
responsibilities,


  id_phase
) SELECT 

t2.project_id,
t2.institution_id,
t2.is_active,
t2.active_since,
t2.created_by,
t2.modified_by,
t2.modification_justification,
t2.responsibilities,

  ph.id
FROM
  tablepartners t2
left JOIN project_phases pp ON pp.project_id = t2.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;


insert into project_partner_locations (project_partner_id,
institution_loc_id,
is_active,
active_since,
created_by,
modified_by,
modification_justification
)
select distinct pp.id,
temp.institution_loc_id,
temp.is_active,
temp.active_since,
temp.created_by,
temp.modified_by,
temp.modification_justification
from table_temp_locations temp 
INNER JOIN project_partners pp on pp.project_id=temp.project_id
and pp.institution_id =temp.institution_id;


insert into project_partner_persons (project_partner_id,
user_id,
contact_type,
is_active,
active_since,
created_by,
modified_by,
modification_justification
)
select distinct pp.id,
temp.user_id,
temp.contact_type,
temp.is_active,
temp.active_since,
temp.created_by,
temp.modified_by,
temp.modification_justification
from table_temp_persons temp 
INNER JOIN project_partners pp on pp.project_id=temp.project_id
and pp.institution_id =temp.institution_id
; 



-- deliverable_partnerships



INSERT INTO deliverable_partnerships (
deliverable_id,
partner_person_id,
partner_type,
is_active,
active_since,
created_by,
modified_by,
modification_justification,
division_id,

id_phase
) SELECT 

t2.deliverable_id,
ppp.id,
t2.partner_type,
t2.is_active,
t2.active_since,
t2.created_by,
t2.modified_by,
t2.modification_justification,
t2.division_id,
ph.id
FROM
table_deliverable_partnerships t2
inner join  project_partner_persons ppp on 
ppp.project_partner_id=t2.project_partner_id and ppp.user_id=t2.user_id
left join deliverables d on d.id=t2.deliverable_id
left JOIN project_phases pp ON pp.project_id = d.project_id
left JOIN phases ph ON ph.id = pp.id_phase
inner join project_partners ppa on  ppa.id=t2.project_partner_id and ppa.id_phase=ph.id
;