ALTER table rep_ind_regions MODIFY un_m49_code VARCHAR(20);
ALTER TABLE rep_ind_regions
ADD UNIQUE INDEX `un_m49_code_rep_ind_regions`(`un_m49_code`) USING BTREE;

ALTER table global_units MODIFY smo_code VARCHAR(20);
ALTER TABLE global_units 
ADD UNIQUE INDEX `smo_code_global_unique`(`smo_code`) USING BTREE;

ALTER table srf_slos MODIFY smo_code VARCHAR(20);
ALTER TABLE srf_slos 
ADD UNIQUE INDEX `smo_code_srf_slos_unique`(`smo_code`) USING BTREE;

ALTER table srf_idos MODIFY smo_code VARCHAR(20);
ALTER TABLE srf_idos 
ADD UNIQUE INDEX `smo_code_srf_idos_unique`(`smo_code`) USING BTREE;

ALTER table srf_sub_idos MODIFY smo_code VARCHAR(20);
ALTER TABLE srf_sub_idos 
ADD UNIQUE INDEX `smo_code_srf_sub_idos_unique`(`smo_code`) USING BTREE;

ALTER table srf_cross_cutting_issues MODIFY smo_code VARCHAR(20);
ALTER TABLE srf_cross_cutting_issues 
ADD UNIQUE INDEX `srf_cross_cutting_issues_unique`(`smo_code`) USING BTREE;

ALTER table srf_slo_indicator_targets MODIFY targets_indicator VARCHAR(20);
ALTER TABLE targets_indicator 
ADD UNIQUE INDEX `srf_cross_cutting_issues_unique`(`targets_indicator`) USING BTREE;

