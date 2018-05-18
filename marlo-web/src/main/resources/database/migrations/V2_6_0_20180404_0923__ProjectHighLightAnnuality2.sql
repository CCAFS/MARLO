SET FOREIGN_KEY_CHECKS = 0;

CREATE TEMPORARY TABLE
IF NOT EXISTS tableproject_highlights_types AS (SELECT
  ty.id_type,
  ty.project_highlights_id,
  pha.id_phase
FROM
  project_highlights ph
INNER JOIN project_highlights_types ty ON ty.project_highlights_id = ph.id
INNER JOIN project_phases pha ON pha.project_id = ph.project_id);


CREATE TEMPORARY TABLE
IF NOT EXISTS tableproject_highlights_country AS (
SELECT
  ty.id_country,
  ty.project_highlights_id,
  pha.id_phase
FROM
  project_highlights ph
INNER JOIN project_highlights_country ty ON ty.project_highlights_id = ph.id
INNER JOIN project_phases pha ON pha.project_id = ph.project_id
);



TRUNCATE TABLE project_highlights_types;
TRUNCATE TABLE project_highlights_country;

ALTER TABLE `project_highlights_types`
ADD COLUMN `id_phase`  bigint(20) NULL ;


ALTER TABLE `project_highlights_country`
ADD COLUMN `id_phase`  bigint(20) NULL ;


INSERT INTO project_highlights_types (
 
id_type,project_highlights_id, id_phase
) SELECT  * from tableproject_highlights_types;


INSERT INTO project_highlights_country (
 
id_country,project_highlights_id, id_phase
) SELECT  * from tableproject_highlights_country;

