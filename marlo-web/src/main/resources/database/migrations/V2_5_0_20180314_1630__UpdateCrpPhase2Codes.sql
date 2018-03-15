/*CRP FISH*/
UPDATE global_units set smo_code= '11' where id = 27;
UPDATE global_units set institution_id = 99 where id = 27;

/*CRP FTA*/
UPDATE global_units set smo_code= '12' where id = 11;
UPDATE global_units set institution_id = 115 where id = 11;

/*CRP LIVESTOCK*/
UPDATE global_units set smo_code= '13' where id = 7;
UPDATE global_units set institution_id = 66 where id = 7;

/*CRP MAIZE*/
UPDATE global_units set smo_code= '14' where id = 22;
UPDATE global_units set institution_id = 50 where id = 22;

/*CRP RICE*/
UPDATE global_units set smo_code= '15' where id = 16;
UPDATE global_units set institution_id = 5 where id = 16;

/*CRP RTB*/
UPDATE global_units set smo_code= '16' where id = 17;
UPDATE global_units set institution_id = 67 where id = 17;

/*CRP WHEAT*/
UPDATE global_units set smo_code= '17' where id = 21;
UPDATE global_units set institution_id = 50 where id = 21;

/*CRP GLDC*/
INSERT INTO `global_units` VALUES ('28', '1', '18', 'Grain Legumes and Dryland Cereals Agri-food Systems', 'GLDC', 1273,'1', '3', '3', '2018-02-28 07:36:22', ' ', '0', '0');

/*CRP A4NH*/
UPDATE global_units set smo_code= '21' where id = 5;
UPDATE global_units set institution_id = 89 where id = 5;

/*CRP CCAFS*/
UPDATE global_units set smo_code= '22' where id = 1;
UPDATE global_units set institution_id = 46 where id = 1;

/*CRP PIM*/
UPDATE global_units set smo_code= '23' where id = 3;
UPDATE global_units set institution_id = 89 where id = 3;

/*CRP WLE*/
UPDATE global_units set smo_code= '24' where id = 4;
UPDATE global_units set institution_id = 172 where id = 4;

/*PLATFORM EiB*/
UPDATE global_units set smo_code= '31' where id = 25;
UPDATE global_units set institution_id = 50 where id = 25;

/*PLATFORM BIG DATA*/
UPDATE global_units set smo_code= '32' where id = 24;
UPDATE global_units set institution_id = 46 where id = 24;

/*PLATFORM GENEBANK*/
UPDATE global_units set smo_code= '33' where id = 26;
UPDATE global_units set institution_id = 71 where id = 26;

/*CENTER CIAT*/
UPDATE global_units set institution_id = 46 where id = 23;