ALTER TABLE `funding_sources`
ADD COLUMN `status`  int NULL AFTER `center_type`;

update funding_sources set status=2 ;
UPDATE `budget_types` SET `name`='W3/Budget' WHERE (`id`='2');