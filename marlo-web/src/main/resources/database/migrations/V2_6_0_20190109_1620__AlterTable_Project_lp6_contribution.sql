ALTER TABLE `project_lp6_contribution`
ADD COLUMN `narrative`  text(100) NULL AFTER `contribution`,
ADD COLUMN `is_working_across_flagships`  tinyint(1) NULL AFTER `narrative`,
ADD COLUMN `working_across_flagships`  text(100) NULL AFTER `is_working_across_flagships`,
ADD COLUMN `is_undertaking_efforts_leading`  tinyint(1) NULL AFTER `working_across_flagships`,
ADD COLUMN `undertaking_efforts_leading`  text(100) NULL AFTER `is_undertaking_efforts_leading`,
ADD COLUMN `is_providing_partways`  tinyint(1) NULL AFTER `undertaking_efforts_leading`,
ADD COLUMN `top_three_partnerships`  text(100) NULL AFTER `is_providing_partways`,
ADD COLUMN `is_undertaking_efforts_csa`  tinyint(1) NULL AFTER `top_three_partnerships`,
ADD COLUMN `undertaking_efforts_csa`  text(100) NULL AFTER `is_undertaking_efforts_csa`,
ADD COLUMN `is_initiative_related`  tinyint(1) NULL AFTER `undertaking_efforts_csa`,
ADD COLUMN `initiative_related`  text(100) NULL AFTER `is_initiative_related`;