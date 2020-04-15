ALTER TABLE `section_statuses`
ADD COLUMN `synthesis_flagships`  text NULL AFTER `project_lp6_contribution_id`, ALGORITHM=INPLACE, LOCK=NONE;