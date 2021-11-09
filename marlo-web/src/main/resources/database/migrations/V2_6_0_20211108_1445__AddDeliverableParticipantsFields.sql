ALTER TABLE `deliverable_participants`
ADD COLUMN `african`  double NULL AFTER `dont_know_female`,
ADD COLUMN `estimate_african`  tinyint(1) NULL AFTER `african`,
ADD COLUMN `african_percentage`  double NULL AFTER `estimate_african`,
ADD COLUMN `youth`  double NULL AFTER `african_percentage`,
ADD COLUMN `estimate_youth`  tinyint(1) NULL AFTER `youth`,
ADD COLUMN `youth_percentage`  double NULL AFTER `estimate_youth`,
ADD COLUMN `focus`  text NULL AFTER `youth_percentage`,
ADD COLUMN `likely_outcomes`  text NULL AFTER `focus`;