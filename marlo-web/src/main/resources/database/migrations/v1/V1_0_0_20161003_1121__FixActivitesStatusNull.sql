START TRANSACTION;
update activities set activityStatus = 2
where activityStatus is NULL;
COMMIT;