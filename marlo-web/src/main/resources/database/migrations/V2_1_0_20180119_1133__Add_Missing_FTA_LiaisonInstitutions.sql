#Fixes bug 1241
INSERT INTO `liaison_institutions`(`institution_id`,`name`,`acronym`,`crp_id`,`is_active`)
VALUES
((SELECT i.`id` FROM `institutions` i WHERE i.`name` = 'World Agroforestry Centre' ),'World Agroforestry Centre',
(SELECT i.`acronym` FROM `institutions` i WHERE i.`name` = 'World Agroforestry Centre'), 11, 1);

INSERT INTO `liaison_institutions`(`institution_id`,`name`,`acronym`,`crp_id`,`is_active`)
VALUES
((SELECT i.`id` FROM `institutions` i WHERE i.`name` = 'Center for International Forestry Research' ),'Center for International Forestry Research',
(SELECT i.`acronym` FROM `institutions` i WHERE i.`name` = 'Center for International Forestry Research'), 11, 1);

INSERT INTO `liaison_institutions`(`institution_id`,`name`,`acronym`,`crp_id`,`is_active`)
VALUES
((SELECT i.`id` FROM `institutions` i WHERE i.`name` = 'Tropenbos International' ),'Tropenbos International',
(SELECT i.`acronym` FROM `institutions` i WHERE i.`name` = 'Tropenbos International'), 11, 1);

INSERT INTO `liaison_institutions`(`institution_id`,`name`,`acronym`,`crp_id`,`is_active`)
VALUES
((SELECT i.`id` FROM `institutions` i WHERE i.`name` = 'Centre International de Recherche Agricole et du Developppement' ),'Centre International de Recherche Agricole et du Developppement',
(SELECT i.`acronym` FROM `institutions` i WHERE i.`name` = 'Centre International de Recherche Agricole et du Developppement'), 11, 1);

