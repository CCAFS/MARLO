# update project_impacts FOREIGN
SET @tablename = 'project_impacts';


SET @constraintname = 'project_impacts_phases';


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
		) <= 0,
		CONCAT(
			"SELECT 'not was removed 1/7'"
		),
		CONCAT(
			"ALTER TABLE ",
			@tablename,
			" DROP FOREIGN KEY ",
			@constraintname
		)
	)
);

PREPARE alterIfNotExists
FROM
	@preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;

# update project_impacts COLUMN
SET @tablename = 'project_impacts';


SET @columnname = 'id_phase';


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
		) <= 0,
		"SELECT 'not was removed 2/7'",
		CONCAT(
			"ALTER TABLE ",
			@tablename,
			" DROP COLUMN ",
			@columnname
		)
	)
);

PREPARE alterIfNotExists
FROM
	@preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;

# update project_impacts COLUMN ADD
SET @tablename = 'project_impacts';


SET @columnname = 'year';


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
		"SELECT 'it already exists 3/7'",
		CONCAT(
			"ALTER TABLE ",
			@tablename,
			" ADD ",
			@columnname,
			" INT(11) NOT NULL;"
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
				`key` = 'crp_show_section_impact_covid19_ranges_years'
			AND global_unit_type_id = 1
		) > 0,
		CONCAT(
			"SELECT 'it already exists 4/7'"
		),
		"INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
	VALUES ( '1', 'crp_show_section_impact_covid19_ranges_years', 'Years ranges to show COVID-19 session. Format: [since]-[until]. Ex.: 2020-2021 or only 2020 means that from this year onwards.', '4', '2');"
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
						`key` = 'crp_show_section_impact_covid19_ranges_years'
					AND global_unit_type_id = 1
				)
		) > 0,
		CONCAT(
			"SELECT 'it already exists 5/7'"
		),
		CONCAT(
			"INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`)
SELECT ",
			@parameter_id,
			", '2020-2021', `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`
FROM `custom_parameters` WHERE parameter_id = 200;"
		)
	)
);

PREPARE alterIfNotExists
FROM
	@preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;

##AQUI
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
				`key` = 'crp_show_section_impact_covid19_ranges_years'
			AND global_unit_type_id = 3
		) > 0,
		CONCAT(
			"SELECT 'it already exists 6/7'"
		),
		"INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '3', 'crp_show_section_impact_covid19_ranges_years', 'Years ranges to show COVID-19 session. Format: [since]-[until]. Ex.: 2020-2021 or only 2020 means that from this year onwards.', '4', '2');"
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
				`key` = 'crp_show_section_impact_covid19_ranges_years'
			AND global_unit_type_id = 4
		) > 0,
		CONCAT(
			"SELECT 'it already exists 7/7'"
		),
		"INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '4', 'crp_show_section_impact_covid19_ranges_years', 'Years ranges to show COVID-19 session. Format: [since]-[until]. Ex.: 2020-2021 or only 2020 means that from this year onwards.', '4', '2');"
	)
);

PREPARE alterIfNotExists
FROM
	@preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;