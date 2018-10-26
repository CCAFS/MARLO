ALTER TABLE `rep_ind_regions`
ADD COLUMN `sub_region`  bigint(20) NULL AFTER `name`;

ALTER TABLE `rep_ind_regions` ADD FOREIGN KEY (`sub_region`) REFERENCES `rep_ind_regions` (`id`);