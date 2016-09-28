START TRANSACTION;
update projects as a
inner join projects b on b.id = a.id
set a.create_date = b.active_since;
COMMIT;