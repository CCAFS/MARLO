START TRANSACTION;
ALTER TABLE `projects`
ADD COLUMN `preset_date`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP AFTER `create_date`;

update projects as a
inner join projects b on b.id = a.id
set a.preset_date = b.active_since;
COMMIT;