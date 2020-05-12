## create table impacts covid-19
CREATE TABLE `project_impacts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `answer` text,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
   PRIMARY KEY (`id`),
   CONSTRAINT `project_impacts_projects` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `project_impacts_phases` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `project_impacts_users1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `project_impacts_users2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

## create section status for covid-19
ALTER TABLE `section_statuses` ADD `project_impact_id` bigint(20) DEFAULT NULL;
ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_impacts` FOREIGN KEY (`project_impact_id`) REFERENCES `project_impacts`(`id`);

## create parameters specificity 
INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '1', 'crp_show_section_impact_covid19', 'Show section impact of COVID-19', '1', '2');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`)
SELECT @@identity, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`
FROM `custom_parameters` WHERE parameter_id = 217;

INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '3', 'crp_show_section_impact_covid19', 'Show section impact of COVID-19', '1', '2');

INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '4', 'crp_show_section_impact_covid19', 'Show section impact of COVID-19', '1', '2');
## create table impacts covid-19
CREATE TABLE
IF NOT EXISTS `project_impacts` (
	`id` BIGINT (20) NOT NULL AUTO_INCREMENT,
	`project_id` BIGINT (20) DEFAULT NULL,
	`id_phase` BIGINT (20) DEFAULT NULL,
	`answer` text,
	`is_active` TINYINT (1) NOT NULL DEFAULT '1',
	`active_since` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`created_by` BIGINT (20) DEFAULT NULL,
	`modified_by` BIGINT (20) DEFAULT NULL,
	`modification_justification` text,
	PRIMARY KEY (`id`),
	CONSTRAINT `project_impacts_projects` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT `project_impacts_phases` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT `project_impacts_users1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT `project_impacts_users2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

## create section status for covid-19
## column
SET @tablename = 'section_statuses';


SET @columnname = 'project_impact_id';


SET @preparedStatement = (
	SELECT

	IF (
		(
			SELECT
				COUNT(*)
			FROM
				INFORMATION_SCHEMA. COLUMNS
			WHERE
				table_name = @tablename
			AND column_name = @columnname
		) > 0,
		"SELECT 'it already exists 1/6'",
		CONCAT(
			"ALTER TABLE ",
			@tablename,
			" ADD ",
			@columnname,
			" BIGINT(20) DEFAULT NULL;"
		)
	)
);

PREPARE alterIfNotExists
FROM
	@preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;

## constraint
SET @tablename = 'section_statuses';


SET @constraintname = 'section_statuses_impacts';


SET @preparedStatement = (
	SELECT

	IF (
		(
			SELECT
				COUNT(*)
			FROM
				INFORMATION_SCHEMA.TABLE_CONSTRAINTS
			WHERE
				TABLE_NAME = @tablename
			AND CONSTRAINT_NAME = @constraintname
		) > 0,
		CONCAT(
			"SELECT 'it already exists 2/6'"
		),
		CONCAT(
			"ALTER TABLE ",
			@tablename,
			" ADD CONSTRAINT ",
			@constraintname,
			" FOREIGN KEY (project_impact_id) REFERENCES project_impacts(id);"
		)
	)
);

PREPARE alterIfNotExists
FROM
	@preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;

## create parameters specificity 
SET @preparedStatement = (
	SELECT

	IF (
		(
			SELECT
				COUNT(*)
			FROM
				parameters
			WHERE
				`key` = 'crp_show_section_impact_covid19'
			AND global_unit_type_id = 1
		) > 0,
		CONCAT(
			"SELECT 'it already exists 3/6'"
		),
		"INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '1', 'crp_show_section_impact_covid19', 'Show section impact of COVID-19', '1', '2');"
	)
);

PREPARE alterIfNotExists
FROM
	@preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;

## create parameters specificity - custom_parameters
SET @parameter_id = @@identity;


SET @preparedStatement = (
	SELECT

	IF (
		(
			SELECT
				COUNT(*)
			FROM
				`custom_parameters`
			WHERE
				parameter_id IN (
					SELECT
						id
					FROM
						parameters
					WHERE
						`key` = 'crp_show_section_impact_covid19'
					AND global_unit_type_id = 1
				)
		) > 0,
		CONCAT(
			"SELECT 'it already exists 4/6'"
		),
		CONCAT(
			"INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`)
SELECT ",
			@parameter_id,
			", `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`
FROM `custom_parameters` WHERE parameter_id = 200;
"
		)
	)
);

PREPARE alterIfNotExists
FROM
	@preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;

## create parameters specificity - other global units 3
SET @preparedStatement = (
	SELECT

	IF (
		(
			SELECT
				COUNT(*)
			FROM
				parameters
			WHERE
				`key` = 'crp_show_section_impact_covid19'
			AND global_unit_type_id = 3
		) > 0,
		CONCAT(
			"SELECT 'it already exists 5/6'"
		),
		"INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '3', 'crp_show_section_impact_covid19', 'Show section impact of COVID-19', '1', '2');"
	)
);

PREPARE alterIfNotExists
FROM
	@preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;

## create parameters specificity - other global units 4
SET @preparedStatement = (
	SELECT

	IF (
		(
			SELECT
				COUNT(*)
			FROM
				parameters
			WHERE
				`key` = 'crp_show_section_impact_covid19'
			AND global_unit_type_id = 4
		) > 0,
		CONCAT(
			"SELECT 'it already exists 6/6'"
		),
		"INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '4', 'crp_show_section_impact_covid19', 'Show section impact of COVID-19', '1', '2');"
	)
);

PREPARE alterIfNotExists
FROM
	@preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;