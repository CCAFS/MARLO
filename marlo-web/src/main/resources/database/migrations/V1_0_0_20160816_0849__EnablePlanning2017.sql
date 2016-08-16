START TRANSACTION;
UPDATE `crp_parameters` SET `value`='1' WHERE (`key`='crp_planning_active');
UPDATE `crp_parameters` SET `value`='0' WHERE (`key`='crp_reporting_active');
UPDATE `crp_parameters` SET `value`='2017' WHERE (`key`='crp_planning_year');
commit;