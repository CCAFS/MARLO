START TRANSACTION;
ALTER TABLE `project_budgets`
MODIFY COLUMN `budget_type`  bigint(20) NULL AFTER `amount`;

ALTER TABLE `project_budgets` DROP FOREIGN KEY `project_budgets_ibfk_1`;

ALTER TABLE `project_budgets` ADD CONSTRAINT `project_budgets_ibfk_1` FOREIGN KEY (`budget_type`) REFERENCES `budget_types` (`id`);

COMMIT;