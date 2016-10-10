START TRANSACTION;
UPDATE `permissions` SET `permission`='crp:{0}:project:{1}:deliverableList:canEdit' WHERE `id`='168';
UPDATE `permissions` SET `permission`='crp:{0}:project:{1}:deliverableList:*' WHERE `id`='106';
UPDATE `permissions` SET `permission`='crp:{0}:project:{1}:deliverableList:addDeliverable' WHERE `id`='169';
UPDATE `permissions` SET `permission`='crp:{0}:project:{1}:deliverableList:removeOldDeliverables' WHERE `id`='170';
COMMIT;