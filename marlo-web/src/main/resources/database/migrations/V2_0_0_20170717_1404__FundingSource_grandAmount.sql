-- Adding a new field in funding sources for the grant amount
ALTER TABLE `funding_sources`
ADD COLUMN `grantAmount`  double(30,2) NULL AFTER `finance_code`;

 



