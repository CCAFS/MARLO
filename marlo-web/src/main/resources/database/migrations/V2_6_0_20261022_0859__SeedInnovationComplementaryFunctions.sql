-- A2-2613 (rule 5.2): the Function of a complementary innovation (Bundle composition) is required, but an
-- environment whose project_innovation_functions is empty offers no option, so no complementary innovation can be
-- saved. V2_6_0_20250728_1019 and V2_6_0_20250915_1100 seeded these rows; an environment that was baselined after
-- them, or emptied, never got them. This seeds the same options as the AICCRA II instance, in the same order.
--
-- Safe in every environment: a row is inserted only when no function with the same title exists, so environments
-- that already have the catalog are left as they are. created_by is required: it takes the first user of the
-- environment instead of assuming user 1 (nothing is inserted on a database without users). No id, phase or global
-- unit is assumed.

INSERT INTO project_innovation_functions (title, is_active, created_by, order_index)
SELECT seed.title, 1, (SELECT MIN(u.id) FROM users u), seed.order_index
FROM (
  SELECT 'This is a core innovation' AS title, 1 AS order_index
  UNION ALL SELECT 'Improves beneficiary / user awareness of the core innovation', 2
  UNION ALL SELECT 'Improves beneficiary/ user capacity and knowhow to appropriately use the core innovation', 3
  UNION ALL SELECT 'Improves availability and beneficiary/ user access to core innovation', 4
  UNION ALL SELECT 'Improves stakeholder coordination and partnerships', 5
  UNION ALL SELECT 'Improves beneficiary/ user access to finance/ affordability of the core innovation', 6
  UNION ALL SELECT 'Improves compatibility of core innovation with existing farming/ market/ business systems and models', 7
  UNION ALL SELECT 'Improves beneficiary / user confidence / trust in core innovation', 8
  UNION ALL SELECT 'Improves legal conditions and governance to scale the core innovation (new or improved by-laws, policies, regulations and mechanisms)', 9
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM project_innovation_functions f WHERE f.title = seed.title)
  AND EXISTS (SELECT 1 FROM users);
