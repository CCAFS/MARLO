

update 
project_partner_persons ppp
INNER JOIN project_branches pb on ppp.project_partner_id=pb.project_partner_id

set ppp.institution_id=pb.institution_id
where ppp.is_active=1 and ppp.institution_id is null;

UPDATE  project_partner_persons ppp
INNER JOIN project_partners pb on ppp.project_partner_id=pb.id
set ppp.institution_id=pb.institution_id
where ppp.is_active=1 and ppp.institution_id is null;

drop table project_branches;