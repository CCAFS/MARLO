#script migration bi-reports
CREATE TABLE
IF NOT EXISTS `bi_reports` (
	id BIGINT (20) NOT NULL AUTO_INCREMENT,
	report_name VARCHAR (500) NULL,
	report_id VARCHAR (500) NULL,
	dataset_id VARCHAR (500) NULL,
	embed_url VARCHAR (1000) NULL,
	is_active TINYINT (1) NULL,
	has_filters TINYINT (1) NULL,
	global_unit_id BIGINT (20) NULL,
	PRIMARY KEY (id),
	CONSTRAINT `bi_reports_ibfk_1` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`)
);

CREATE TABLE
IF NOT EXISTS `bi_parameters` (
	id BIGINT (20) NOT NULL AUTO_INCREMENT,
	parameter_name VARCHAR (500),
	parameter_value VARCHAR (500),
	PRIMARY KEY (id)
);

INSERT INTO  bi_parameters (parameter_name,parameter_value) VALUES('api_token_url','https://api.powerbi.com/v1.0/myorg/GenerateToken');
INSERT INTO  bi_parameters (parameter_name,parameter_value) VALUES('azure_api_url','https://login.microsoftonline.com/{tenantID}/oauth2/token');
INSERT INTO  bi_parameters (parameter_name,parameter_value) VALUES('tenant_id','6afa0e00-fa14-40b7-8a2e-22a7f8c357d5');
INSERT INTO  bi_parameters (parameter_name,parameter_value) VALUES('secret','T69q-Krzgbu.YypNmQWDMJh=Jl?m7m6J');
INSERT INTO  bi_parameters (parameter_name,parameter_value) VALUES('app_id','a30f2154-8314-4d82-8131-97c1cdfaf6fe');
INSERT INTO  bi_parameters (parameter_name,parameter_value) VALUES('resource_url','https://analysis.windows.net/powerbi/api');

INSERT INTO `bi_reports` (`id`, `report_name`, `report_id`, `dataset_id`, `embed_url`, `is_active`, `has_filters`, `global_unit_id`) 
VALUES (1, 'Project Submission Status', '50e6f7be-fef1-43cd-9983-4008f47f4a4d', '9bd72c88-3162-4a6b-a4cc-8422e61e9eeb', 'https://app.powerbi.com/reportEmbed?reportId=50e6f7be-fef1-43cd-9983-4008f47f4a4d&groupId=37376d13-3df2-4447-aaa5-49c047533b4f&config=eyJjbHVzdGVyVXJsIjoiaHR0cHM6Ly93YWJpLW5vcnRoLWV1cm9wZS1yZWRpcmVjdC5hbmFseXNpcy53aW5kb3dzLm5ldC8ifQ%3D%3D', '1', '1', '1');
INSERT INTO `bi_reports` (`id`, `report_name`, `report_id`, `dataset_id`, `embed_url`, `is_active`, `has_filters`, `global_unit_id`) 
VALUES (2, 'Deliverables', '27470a39-12f9-4c09-b3af-8639d1b8ceb4', '90154583-185d-4e68-8dcf-aa36626d013a', 'https://app.powerbi.com/reportEmbed?reportId=27470a39-12f9-4c09-b3af-8639d1b8ceb4&groupId=37376d13-3df2-4447-aaa5-49c047533b4f&config=eyJjbHVzdGVyVXJsIjoiaHR0cHM6Ly93YWJpLW5vcnRoLWV1cm9wZS1yZWRpcmVjdC5hbmFseXNpcy53aW5kb3dzLm5ldC8ifQ%3D%3D', '1', '1', '1');