ALTER TABLE `project_outcomes`
ADD COLUMN `gender_dimenssion`  text NULL AFTER `achieved_unit`,
ADD COLUMN `youth_component`  text NULL AFTER `gender_dimenssion`;

