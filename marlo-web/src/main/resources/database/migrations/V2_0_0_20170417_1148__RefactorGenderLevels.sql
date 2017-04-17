ALTER TABLE `gender_types`
ADD COLUMN `complete_description`  text NULL AFTER `description`;


UPDATE `gender_types` SET `complete_description`='Focus on women or girls only\r\nFocus on women or girls only' WHERE (`id`='6');
UPDATE `gender_types` SET `complete_description`='Some gender dimension, but not the main focus' WHERE (`id`='7');
UPDATE `gender_types` SET `complete_description`='Gender dimension is the main focus' WHERE (`id`='8');
