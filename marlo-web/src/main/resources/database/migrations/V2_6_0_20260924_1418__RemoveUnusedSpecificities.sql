-- A2-2526: remove six specificities (category 2) and one setting (category 3) that no longer have a
-- single reader in the application.
-- Verified at staging @ ff8b43d3c6 by resolving each key to its APConstants constant and its BaseAction
-- helper and searching all three forms across marlo-web, marlo-data and marlo-utils (Java, FTL, JS,
-- properties, XML). The Java side of the removal ships in the same commit:
--
--   crp_generic_project          never had a reader: efef7a5655 (2020-03-05) added the two constants and
--                                nothing else, and the generic-project routing was never built
--   crp_generic_project_value    same commit, same story; the stored 0 is not a project id
--   crp_budgetbycoas             the Budget by CoAs project section was deleted in 1e982f09a3 (2019-10-25),
--                                taking its DAO, manager, model and menu entry. The flag survived, read
--                                only by BaseAction.canEditBudgetByCoAs(), which nothing calls
--   melia_score_field_active     its only reader, the MELIA score block in studiesTemplates.ftl, was
--                                removed in 43e3f3f358 (2025-04-01) when the OICR score field replaced it
--   display_user_menu_new_style  retired deliberately: the user menu is now chrome for every Global Unit
--   crp_email_pl_crpAdmin_fl     no constant (commented out in both files) and no reader
--
-- And, from the Settings tab (category 3):
--
--   crp_timeline_week_parameter_visualization
--                                its only reader was TimelineVisualizationWeeksParameterAction, served at
--                                getTimelineWeeksParameter.do, which no JavaScript or template ever calls.
--                                The schedule card keeps its zoom in localStorage ('marlo.schedule.weeks',
--                                default 8 weeks), a decision recorded in the homepage-schedule spec on
--                                2026-08-19 precisely because this parameter's seeded default '423' is
--                                outside the 1..8 scale the feature accepts. GlobalUnitCreationManagerImpl
--                                worked around that default with a hardcoded "4"; that workaround, the
--                                action, its Struts mapping and both constants go with this migration.
--
-- Environment assumptions, since none of them is visible in the statements:
--
-- 1. NO hardcoded global_unit_id. Both deletes match on `key` and `category` = 2, so they apply to every
--    Global Unit and every Global Unit type in whichever database they run against. A database that never
--    seeded one of the keys simply deletes 0 rows, which MySQL reports as success. That is the intent here:
--    this migration must be a no-op where the rows are absent, not an error.
--
-- 2. Delete order is NOT cosmetic. custom_parameters.parameter_id has FOREIGN KEY custom_parameters_ibfk_1
--    onto parameters(id), so removing the catalog rows first fails with MySQL error 1452 and leaves the
--    migration half applied. Children first, then the catalog rows.
--
-- 3. This is destructive and there is no automatic rollback. On the AICCRA copy measured on 2026-09-24
--    (aiccradb2) it removes 18 parameters rows (6 keys x 3 Global Unit types) and 99 custom_parameters rows
--    spread over 33 Global Units -- so it is not an AICCRA-only change. Back the rows up before running it
--    in production:
--
--      SELECT cp.* FROM custom_parameters cp JOIN parameters p ON p.id = cp.parameter_id
--      WHERE p.`key` IN ('crp_generic_project', 'crp_generic_project_value', 'crp_budgetbycoas',
--        'melia_score_field_active', 'display_user_menu_new_style', 'crp_email_pl_crpAdmin_fl',
--        'crp_timeline_week_parameter_visualization');
--
--      SELECT * FROM parameters
--      WHERE `key` IN ('crp_generic_project', 'crp_generic_project_value', 'crp_budgetbycoas',
--        'melia_score_field_active', 'display_user_menu_new_style', 'crp_email_pl_crpAdmin_fl',
--        'crp_timeline_week_parameter_visualization');
--
--    To roll back, re-insert those rows: the original seeds are V2_6_0_20200413_1000__CLARISA_Generic_Project,
--    V2_6_0_20191018_1300__BudgetByCoAsCRP_Parameter, V2_6_0_20230412_1515__AddSpecifityForMELIAScore,
--    V2_6_0_20250724_1446__CreateSpecifiyForUserMenu, V2_6_0_20190307_1511__ParamEmailsCC3 and
--    V2_6_0_20240626_1150__AddTimelineZoom. Those files
--    are left untouched: they describe what happened when they ran, and rewriting applied history is what
--    makes a Flyway baseline untrustworthy -- the more so here, where MarloFlywayConfiguration calls
--    repair() on every startup, so an edited file would be quietly re-checksummed instead of failing loudly.

DELETE cp FROM custom_parameters cp
INNER JOIN parameters p ON p.id = cp.parameter_id
WHERE p.category = 2
  AND p.`key` IN ('crp_generic_project', 'crp_generic_project_value', 'crp_budgetbycoas',
    'melia_score_field_active', 'display_user_menu_new_style', 'crp_email_pl_crpAdmin_fl');

DELETE FROM parameters
WHERE category = 2
  AND `key` IN ('crp_generic_project', 'crp_generic_project_value', 'crp_budgetbycoas',
    'melia_score_field_active', 'display_user_menu_new_style', 'crp_email_pl_crpAdmin_fl');

-- The timeline setting is category 3, not 2, so it needs its own pair of statements rather than another
-- entry in the lists above. Same order and same reasoning: children first, matched by key and category.

DELETE cp FROM custom_parameters cp
INNER JOIN parameters p ON p.id = cp.parameter_id
WHERE p.category = 3
  AND p.`key` = 'crp_timeline_week_parameter_visualization';

DELETE FROM parameters
WHERE category = 3
  AND `key` = 'crp_timeline_week_parameter_visualization';
