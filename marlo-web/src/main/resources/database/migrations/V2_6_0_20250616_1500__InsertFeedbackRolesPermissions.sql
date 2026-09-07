/*
 * AICCRA feedback permission matrix.
 *
 * Every row depends on two ids that only exist in a real AICCRA database: global unit 45, and the
 * AICCRA roles 420-433. The migrations seed global_units only up to 28 and roles only up to id 176
 * (V2_0_0_20161013_1142__RolParameters.sql), so on a database built from scratch the original
 * INSERT ... VALUES form failed against feedback_roles_permissions_global_units_FK and
 * feedback_roles_permissions_FK_1 (role_id) and aborted the whole Flyway run.
 *
 * The matrix is kept as a derived table joined against global_units and roles instead: when either
 * id is absent the join yields no rows and the migration is a no-op. feedback_permission_id (1-4)
 * and cluster_type_id (1-3) need no guard -- both catalogs are seeded by earlier migrations
 * (V2_6_0_20220915_1510, V2_6_0_20230525_1026, V2_6_0_20210601_1104).
 *
 * sort_order + ORDER BY are not cosmetic: without them the auto_increment ids depend on the join
 * plan the optimizer happens to pick, which grouped the rows by role_id. Ordering explicitly keeps
 * the ids in the same sequence the original INSERT ... VALUES form produced.
 */

INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
SELECT seed.role_id, seed.feedback_permission_id, seed.cluster_type_id, global_unit.id, seed.description
FROM (
  -- PMU (Program Management Unit) - Full access
            SELECT 1 AS sort_order, 427 AS role_id, 1 AS feedback_permission_id, CAST(NULL AS SIGNED) AS cluster_type_id, 'PMU - can_write_comments on all clusters' AS description
  UNION ALL SELECT  2, 427, 2, NULL, 'PMU - can_approve_comments on all clusters'
  UNION ALL SELECT  3, 427, 4, NULL, 'PMU - can_track_comments on all clusters'

  -- FPL (Flagship Leader)
  UNION ALL SELECT  4, 425, 1, 2,    'FPL - can_write_comments on regional clusters'
  UNION ALL SELECT  5, 425, 1, 3,    'FPL - can_write_comments on country clusters'
  UNION ALL SELECT  6, 425, 3, 1,    'FPL - can_react_comments on thematic clusters'
  UNION ALL SELECT  7, 425, 4, NULL, 'FPL - can_track_comments on all clusters'

  -- FPM (Flagship Manager)
  UNION ALL SELECT  8, 432, 1, 2,    'FPM - can_write_comments on regional clusters'
  UNION ALL SELECT  9, 432, 1, 3,    'FPM - can_write_comments on country clusters'
  UNION ALL SELECT 10, 432, 3, 1,    'FPM - can_react_comments on thematic clusters'
  UNION ALL SELECT 11, 432, 4, NULL, 'FPM - can_track_comments on all clusters'

  -- RPL (Regional Program Leader)
  UNION ALL SELECT 12, 424, 1, 3,    'RPL - can_write_comments on country clusters'
  UNION ALL SELECT 13, 424, 2, 3,    'RPL - can_approve_comments on country clusters'
  UNION ALL SELECT 14, 424, 3, 2,    'RPL - can_react_comments on regional clusters'
  UNION ALL SELECT 15, 424, 4, NULL, 'RPL - can_track_comments on all clusters'

  -- RPM (Regional Program Manager)
  UNION ALL SELECT 16, 433, 1, 3,    'RPM - can_write_comments on country clusters'
  UNION ALL SELECT 17, 433, 2, 3,    'RPM - can_approve_comments on country clusters'
  UNION ALL SELECT 18, 433, 3, 2,    'RPM - can_react_comments on regional clusters'
  UNION ALL SELECT 19, 433, 4, NULL, 'RPM - can_track_comments on all clusters'

  -- PL (Project Leader)
  UNION ALL SELECT 20, 420, 3, NULL, 'PL - can_react_comments on own cluster'

  -- PC (Project Coordinator)
  UNION ALL SELECT 21, 422, 3, NULL, 'PC - can_react_comments on own cluster'

  -- SuperAdmin
  UNION ALL SELECT 22, 430, 1, NULL, 'SuperAdmin - can_write_comments on all clusters'
  UNION ALL SELECT 23, 430, 2, NULL, 'SuperAdmin - can_approve_comments on all clusters'
  UNION ALL SELECT 24, 430, 3, NULL, 'SuperAdmin - can_react_comments on all clusters'
  UNION ALL SELECT 25, 430, 4, NULL, 'SuperAdmin - can_track_comments on all clusters'
) AS seed
INNER JOIN global_units global_unit ON global_unit.id = 45
INNER JOIN roles role_value ON role_value.id = seed.role_id
ORDER BY seed.sort_order;
