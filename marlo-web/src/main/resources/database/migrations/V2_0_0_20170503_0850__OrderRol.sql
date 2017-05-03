ALTER TABLE `roles`
ADD COLUMN `order`  int NULL DEFAULT 0 AFTER `crp_id`;


UPDATE `roles` set `order`='1' WHERE (`acronym`='Admin');
UPDATE `roles` set `order`='2' WHERE (`acronym`='PMU');
UPDATE `roles` set `order`='3' WHERE (`acronym`='FM');
UPDATE `roles` set `order`='4' WHERE (`acronym`='DM');
UPDATE `roles` set `order`='5' WHERE (`acronym`='FPL');
UPDATE `roles` set `order`='6' WHERE (`acronym`='FPM');
UPDATE `roles` set `order`='7' WHERE (`acronym`='RPL');
UPDATE `roles` set `order`='8' WHERE (`acronym`='RPM');
UPDATE `roles` set `order`='9' WHERE (`acronym`='CP');
UPDATE `roles` set `order`='10' WHERE (`acronym`='CL');
UPDATE `roles` set `order`='11' WHERE (`acronym`='SL');
UPDATE `roles` set `order`='12' WHERE (`acronym`='ML');
UPDATE `roles` set `order`='13' WHERE (`acronym`='PL');
UPDATE `roles` set `order`='14' WHERE (`acronym`='PC');
UPDATE `roles` set `order`='15' WHERE (`acronym`='G');