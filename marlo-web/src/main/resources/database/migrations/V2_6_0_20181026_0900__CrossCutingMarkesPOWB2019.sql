ALTER TABLE `rep_ind_gender_youth_focus_levels`
ADD COLUMN `powb_name`  text NULL AFTER `name`;

UPDATE rep_ind_gender_youth_focus_levels SET powb_name='0 - Not targeted' where id = 1;
UPDATE rep_ind_gender_youth_focus_levels SET powb_name='1 - Significant;' where id = 2;
UPDATE rep_ind_gender_youth_focus_levels SET powb_name='2 - Principal' where id = 3;
UPDATE rep_ind_gender_youth_focus_levels SET powb_name='N/A - Not applicable' where id = 4;