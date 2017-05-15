UPDATE `institutions` SET `name`='Department of Irrigation of Sri Lanka' WHERE (`id`='712');
UPDATE `institutions` SET `name`='Department of Irrigation of Nepal', acronym='' WHERE (`id`='333');
-- delete 1272 for 333
UPDATE institutions set headquarter=333 where  headquarter=1272;
UPDATE project_partners set institution_id=333  where institution_id=1272;
UPDATE project_leverage set  institution=333 where institution=1272;
update  funding_sources set donor=333 where donor=1272;
update liaison_institutions set institution_id=333 where institution_id=1272;
update funding_source_institutions set institution_id =333 where institution_id=1272;
update project_partner_persons set institution_id =333 where institution_id=1272;
DELETE FROM `institutions` WHERE (`id`='1272');

-- update names 
UPDATE `institutions` SET `name`='Ministry of Agriculture of Kyrgyzstan' WHERE (`id`='235');
UPDATE `institutions` SET `name`='Ministry of Agriculture of Ivory Coast' WHERE (`id`='460');
UPDATE `institutions` SET `name`='Ministry of Agriculture of Mozambique', `website_link`='http://www.masa.gov.mz/'  WHERE (`id`='821');
UPDATE `institutions` SET `name`='Ministry of Agriculture of India' WHERE (`id`='1275');
-- delete 1538 for 821  
UPDATE institutions set headquarter=821 where  headquarter=1538;
UPDATE project_partners set institution_id=821  where institution_id=1538;
UPDATE project_leverage set  institution=821 where institution=1538;
update  funding_sources set donor=333 where donor=1538;
update liaison_institutions set institution_id=821 where institution_id=1538;
update funding_source_institutions set institution_id =821 where institution_id=1538;
update project_partner_persons set institution_id =821 where institution_id=1538;
DELETE FROM `institutions` WHERE (`id`='1538');
-- update names

UPDATE `institutions` SET `name`='Ministry of Agriculture of Swaziland', acronym='' WHERE (`id`='1539');
UPDATE `institutions` SET `name`='Ministry of Agriculture of Ethiopia', acronym='' WHERE (`id`='290');
UPDATE `institutions` SET `name`='Ministry of Agriculture of India', acronym='' WHERE (`id`='664');
UPDATE `institutions` SET `name`='Ministry of Agriculture of Bangladesh', acronym='' WHERE (`id`='723');
UPDATE `institutions` SET `name`='Ministry of Agriculture and Cooperatives of Nepal' WHERE (`id`='26');
UPDATE `institutions` SET `name`='Ministry of Agriculture and Cooperatives of Zambia' WHERE (`id`='1540');
UPDATE `institutions` SET `name`='Ministry of Agriculture, Livestock and Fisheries of Kenya', acronym='' WHERE (`id`='596');
UPDATE `institutions` SET `name`='Ministry of Agriculture, Livestock and Fisheries of Tanzania', acronym='' WHERE (`id`='1545');
UPDATE `institutions` SET `name`='Ministry of Environment of Ivory Coast' WHERE (`id`='40');
UPDATE `institutions` SET `name`='Ministry of Environment of Brazil' WHERE (`id`='1548');
UPDATE `institutions` SET `name`='Ministry of Foreign Affairs of Finland' WHERE (`id`='1551');
UPDATE `institutions` SET `name`='Ministry of Foreign Affairs of Netherlands' WHERE (`id`='1552');
UPDATE `institutions` SET `name`='Ministry of Health of Uganda', acronym='' WHERE (`id`='566');
UPDATE `institutions` SET `name`='Ministry of Health of Vietnam', acronym='' WHERE (`id`='1179');
UPDATE `institutions` SET `name`='Ministry of Health of Mozambique' WHERE (`id`='1553');
UPDATE `institutions` SET `name`='Ministry of Health of Rwanda' WHERE (`id`='1554');
UPDATE `institutions` SET `name`='Ministry of Health of Swaziland' WHERE (`id`='1555');
UPDATE `institutions` SET `name`='Ministry of Health of Tanzania' WHERE (`id`='1556');

-- delete 1557 for 566  
UPDATE institutions set headquarter=566 where  headquarter=1557;
UPDATE project_partners set institution_id=566  where institution_id=1557;
UPDATE project_leverage set  institution=566 where institution=1557;
update  funding_sources set donor=566 where donor=1557;
update liaison_institutions set institution_id=566 where institution_id=1557;
update funding_source_institutions set institution_id =566 where institution_id=1557;
update project_partner_persons set institution_id =566 where institution_id=1557;
DELETE FROM `institutions` WHERE (`id`='1557');
-- update names
UPDATE `institutions` SET `name`='Ministry of Health of Zambia' WHERE (`id`='1558');
UPDATE `institutions` SET `name`='Ministry of Natural Resources and Environment of Vietnam'  , acronym='' WHERE (`id`='268');
UPDATE `institutions` SET `name`='Ministry of Natural Resources and Environment of Lao PDF', acronym=''  WHERE (`id`='1093');
UPDATE `institutions` SET `name`='National Agricultural Research Institute of Papua New Guinea', acronym=''  WHERE (`id`='16');
UPDATE `institutions` SET `name`='National Agricultural Research Institute of Benin', acronym=''  WHERE (`id`='1570');

-- delete 760  

DELETE FROM `institutions` WHERE (`id`='760');

update  funding_sources set donor=null where donor=1287;
DELETE FROM `institutions` WHERE (`id`='1287');


UPDATE `institutions` SET `name`='Servicio Nacional de Meteorologia e Hidrología de Bolivia', acronym=''  WHERE (`id`='850');
UPDATE `institutions` SET `name`='Servicio Nacional de Meteorologia e Hidrologia de Perú', acronym=''  WHERE (`id`='1172');

-- delete 1716 for 1293  
UPDATE institutions set headquarter=1293 where  headquarter=1716;
UPDATE project_partners set institution_id=1293  where institution_id=1716;
UPDATE project_leverage set  institution=1293 where institution=1716;
update  funding_sources set donor=1293 where donor=1716;
update liaison_institutions set institution_id=1293 where institution_id=1716;
update funding_source_institutions set institution_id =1293 where institution_id=1716;
update project_partner_persons set institution_id =1293 where institution_id=1716;
DELETE FROM `institutions` WHERE (`id`='1716');


-- delete 1282 for 1246  
UPDATE institutions set headquarter=1246 where  headquarter=1282;
UPDATE project_partners set institution_id=1246  where institution_id=1282;
UPDATE project_leverage set  institution=1246 where institution=1282;
update  funding_sources set donor=1246 where donor=1282;
update liaison_institutions set institution_id=1246 where institution_id=1282;
update funding_source_institutions set institution_id =1246 where institution_id=1282;
update project_partner_persons set institution_id =1246 where institution_id=1282;

UPDATE crp_ppa_partners  set institution_id= 1246 where institution_id=1282;
DELETE FROM `institutions` WHERE (`id`='1282');




