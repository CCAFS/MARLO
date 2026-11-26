-- CHG-PARTROUTE-001-T01 (MIG-001): seeds the per-Global-Unit specificity partner_requests_to_clarisa.
--
-- It decides where partner requests made in a Global Unit go: true sends them to CLARISA, false (the default) keeps
-- them in MARLO for a superadmin to accept or reject.
--
-- Seeded for each Global Unit type 1, 2, 3 and 4 that exists in the database at migration time (type 2 because
-- migration-only databases hold "Center" as id 2). Exactly one parameters row per existing type. No custom_parameters
-- row is seeded, so deploying this migration changes no unit: switching a unit is an operational act on the Super
-- Admin Specificities tab.
--
-- Assumes no Global Unit, user or parameter id. Idempotent: parameters has no unique index on `key`, so the seed is
-- guarded by WHERE NOT EXISTS and a second run inserts 0 rows. DML only, so Flyway's transaction covers all of it.

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT gut.id, 'partner_requests_to_clarisa',
  'When on, partner requests made in this Global Unit are sent to CLARISA and are not stored in MARLO, and the Request Institutions menu entry is hidden. When off, requests are stored in MARLO for a superadmin to accept or reject. Country-office requests always stay in MARLO. The menu and the Partners page follow a change at the next login or unit switch.',
  1, 'false', 2
FROM global_unit_types gut
WHERE gut.id IN (1, 2, 3, 4)
AND NOT EXISTS (
  SELECT 1 FROM parameters existing_parameter
  WHERE existing_parameter.global_unit_type_id = gut.id
  AND existing_parameter.`key` = 'partner_requests_to_clarisa'
);
