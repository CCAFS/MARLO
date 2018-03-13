ALTER TABLE `powb_synthesis_crp_staffing_category`
ADD COLUMN `female_no_cgiar`  double(20,0) NULL AFTER `powb_crp_staffing_category_id`,
ADD COLUMN `male_no_cgiar`  double NULL AFTER `female`;

