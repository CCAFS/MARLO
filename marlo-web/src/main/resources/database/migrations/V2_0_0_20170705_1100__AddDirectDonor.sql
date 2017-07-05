ALTER TABLE `funding_sources`
ADD COLUMN `direct_donor`  bigint(20) NULL DEFAULT NULL AFTER `donor`;
