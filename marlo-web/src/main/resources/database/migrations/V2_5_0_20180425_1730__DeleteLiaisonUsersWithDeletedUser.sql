DELETE from liaison_users  where id in 

(select * from(SELECT lu.id FROM liaison_users lu
LEFT OUTER JOIN users u ON lu.user_id = u.id
WHERE u.id IS NULL) as t)

;