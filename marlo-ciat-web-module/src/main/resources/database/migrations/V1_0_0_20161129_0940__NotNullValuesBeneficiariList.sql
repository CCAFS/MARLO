ALTER TABLE `research_impact_beneficiaries`
MODIFY COLUMN `beneficiary_id`  int(11) NULL AFTER `impact_id`,
MODIFY COLUMN `research_region_id`  int(11) NULL AFTER `beneficiary_id`;