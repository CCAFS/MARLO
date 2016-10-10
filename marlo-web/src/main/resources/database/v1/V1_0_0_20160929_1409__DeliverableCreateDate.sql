START TRANSACTION;
ALTER TABLE `deliverables`
ADD COLUMN `create_date`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP AFTER `key_output_id`;

update deliverables as a
inner join deliverables b on b.id = a.id
set a.create_date = b.active_since;
COMMIT;