ALTER TABLE `report_synthesis_cross_cutting_dimensions`
ADD COLUMN `phd_female`  double NULL AFTER `trainees_phd_male`,
ADD COLUMN `phd_male`  double NULL AFTER `phd_female`;