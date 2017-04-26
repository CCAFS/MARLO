ALTER TABLE `budget_types`
ADD COLUMN `description`  text NULL AFTER `name`;

UPDATE `budget_types` SET `description`='Funds allocated by the CGIAR Fund Council to a CRP' WHERE (`id`='1');
UPDATE `budget_types` SET `description`='Funds allocated by Fund Donors to a Center' WHERE (`id`='2');
UPDATE `budget_types` SET `description`='Funds allocated by donors to a Center outside of the CGIAR Fund' WHERE (`id`='3');
UPDATE `budget_types` SET `description`='Funds that belong to the center and not to other parties like donors. Mostly coming from reserves generation.' WHERE (`id`='4');
UPDATE `budget_types` SET `description`='W1/W2 funds allocated by a CRP to cover overhead costs or other costs not covered through a W3/Bilateral grant.' WHERE (`id`='5');
