ALTER TABLE `project_outcomes`
CHANGE COLUMN `order` `order_index`  double(20,0) NULL DEFAULT 0 AFTER `youth_component`;