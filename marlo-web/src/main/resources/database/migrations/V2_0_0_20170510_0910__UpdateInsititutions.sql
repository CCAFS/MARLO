UPDATE `institutions` SET `website_link`='http://www.entwicklung.at/en/' WHERE (`id`='1288');
DELETE FROM `institutions` WHERE (`id`='1356');



UPDATE institutions set headquarter=1398 where  headquarter=291;

UPDATE project_partners set institution_id=1398  where institution_id=291;
UPDATE project_leverage set  institution=1398 where institution=291;

update  funding_sources set donor=1398 where donor=291;

update liaison_institutions set institution_id=1398 where institution_id=291;
update funding_source_institutions set institution_id =1398 where institution_id=291;
update project_partner_persons set institution_id =1398 where institution_id=291;


DELETE FROM `institutions` WHERE (`id`='291');

DELETE FROM `institutions` WHERE (`id`='291');


DELETE FROM `institutions` WHERE (`id`='1451');