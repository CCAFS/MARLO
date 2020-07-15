ALTER TABLE `bi_reports` ADD `has_rls_security` tinyint(1) DEFAULT 1;

INSERT INTO `bi_reports` (`id`, `report_name`, `report_id`, `dataset_id`, `embed_url`, `is_active`, `has_filters`, `global_unit_id`, `has_rls_security`) 
VALUES ('3', 'Embed Test', '6c0e0b91-8f6b-4e2e-927e-6cc8d88ac4ac', 'c4b7f2b5-83d0-4275-b0af-e067875d2176', 'https://app.powerbi.com/reportEmbed?reportId=6c0e0b91-8f6b-4e2e-927e-6cc8d88ac4ac&groupId=37376d13-3df2-4447-aaa5-49c047533b4f&config=eyJjbHVzdGVyVXJsIjoiaHR0cHM6Ly93YWJpLW5vcnRoLWV1cm9wZS1yZWRpcmVjdC5hbmFseXNpcy53aW5kb3dzLm5ldC8ifQ%3D%3D', '1', '1', '1', '0');