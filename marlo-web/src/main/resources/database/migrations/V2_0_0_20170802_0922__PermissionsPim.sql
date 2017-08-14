delete from  role_permissions where permission_id=429 and role_id=31;
delete 
  
FROM
  role_permissions
WHERE
  permission_id IN (
    69,
    70,
    73,
    74,
    75,
    76,
    77,
    78,
    79,
    80,
    81,
    82,
    83,
    84,
    430,
    431,
    432
  )
AND role_id IN (
  28,
  29,
  30,
  31,
  48,
  52,
  53,
  62,
  69,
  76
);

