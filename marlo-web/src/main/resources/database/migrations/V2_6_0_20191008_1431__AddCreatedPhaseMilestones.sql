ALTER TABLE `crp_milestones`
ADD COLUMN `phase_created`  bigint(20) NULL AFTER `milestone_status`;

ALTER TABLE `crp_milestones` ADD FOREIGN KEY (`phase_created`) REFERENCES `phases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;



