START TRANSACTION;
Update deliverables 
set status = NULL
where status=0;
COMMIT;