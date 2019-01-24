DELETE FROM role_permissions WHERE id in (
SELECT * FROM (
SELECT
rp.id
FROM
role_permissions AS rp
INNER JOIN permissions ON rp.permission_id = permissions.id
WHERE
permissions.permission LIKE '%api%'
) as D
)