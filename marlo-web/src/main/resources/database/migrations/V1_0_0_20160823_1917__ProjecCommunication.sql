START TRANSACTION;

ALTER TABLE `project_communications`
ADD COLUMN `analysis_communication`  text NULL AFTER `year`;


 

COMMIT;