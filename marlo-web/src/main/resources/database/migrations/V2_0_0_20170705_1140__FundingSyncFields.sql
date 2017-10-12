ALTER TABLE `funding_sources`
ADD COLUMN `sync`  tinyint(1) NULL AFTER `w1w2`,
ADD COLUMN `extended_date`  date NULL AFTER `sync`,
ADD COLUMN `syn_date`  date NULL AFTER `extended_date`;

