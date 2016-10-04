START TRANSACTION;
ALTER TABLE `projects_bilateral_cofinancing`
ADD COLUMN `crp_id`  bigint(20) NULL AFTER `budget`;

ALTER TABLE `projects_bilateral_cofinancing` ADD CONSTRAINT `projects_bilateral_cofinancing_ibfk_10` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`);
COMMIT;