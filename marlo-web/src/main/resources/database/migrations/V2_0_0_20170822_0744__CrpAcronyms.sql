UPDATE crps
SET acronym = UPPER(acronym)
WHERE
  is_marlo = 1
AND is_active = 1
AND acronym IS NOT NULL;

UPDATE `crps` SET `acronym`='Livestock' WHERE (`id`='7');
UPDATE `crps` SET `acronym`='Wheat' WHERE (`id`='21');
UPDATE `crps` SET `acronym`='Maize' WHERE (`id`='22');