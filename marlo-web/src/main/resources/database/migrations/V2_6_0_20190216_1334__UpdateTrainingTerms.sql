UPDATE `rep_ind_training_terms` SET `name` = 'Short-term' WHERE `id` = 1;
UPDATE `rep_ind_training_terms` SET `name` = 'Long-term' WHERE `id` = 2;
UPDATE `rep_ind_training_terms` SET `name` = 'N/A - Not applicable' WHERE `id` = 3;

ALTER TABLE `deliverable_participants` 
ADD COLUMN `rep_ind_training_term` bigint(20) NULL DEFAULT NULL AFTER `rep_ind_type_participant_id`,
ADD INDEX `idx_deliverable_participants_rep_ind_training_term`(`rep_ind_training_term`) USING BTREE,
ADD CONSTRAINT `deliverable_participants_ibfk_9` FOREIGN KEY (`rep_ind_training_term`) REFERENCES `rep_ind_training_terms` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;