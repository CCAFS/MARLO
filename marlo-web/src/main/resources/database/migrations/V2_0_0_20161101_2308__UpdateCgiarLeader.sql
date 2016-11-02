

UPDATE 
funding_sources fu  INNER JOIN project_partners pp on pp.project_id=fu.id and pp.is_active=1
INNER JOIN project_partner_persons ppp on ppp.project_partner_id=pp.id and ppp.contact_type='PL'and pp.is_active=1
set fu.institution_id=pp.institution_id
where fu.type in (2,3);