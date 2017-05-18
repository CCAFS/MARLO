UPDATE parameters p
INNER JOIN custom_parameters cp ON cp.parameter_id = p.id
SET cp.`value` = 'false'
WHERE
  p.format = '1'
AND cp.`value` = '0';

UPDATE parameters p
INNER JOIN custom_parameters cp ON cp.parameter_id = p.id
SET cp.`value` = 'true'
WHERE
  p.format = '1'
AND cp.`value` = '1';