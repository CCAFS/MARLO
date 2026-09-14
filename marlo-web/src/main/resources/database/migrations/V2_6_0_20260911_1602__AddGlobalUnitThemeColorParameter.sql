-- Prototype -- per Global Unit brand colour for the redesigned chrome (top bar, header, main menu,
-- phase selector, footer). Free text (format 4) under Settings (category 3), edited by a superadmin
-- in Marlo Parameters rather than shipped as a deploy artifact, which is why no custom_parameters
-- rows are seeded here: with no row the session carries no value and the chrome keeps the
-- --marlo-brand default already declared in global/css/marlo-redesign.css.
-- default_value is that same default, so the admin input shows #0277a2 as its placeholder.
-- Global Unit types 1 (CRP), 3 (Platform) and 4 (Center) only, per the AGENTS.md specificity
-- template. Types 2 ('Old Center') and 5 carry no active Global Unit and are deliberately absent.

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '1', 'crp_theme_color', 'Brand colour for this Global Unit chrome, as a hex value such as #0277a2. Empty means the MARLO default', '4', '#0277a2', '3');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '3', 'crp_theme_color', 'Brand colour for this Global Unit chrome, as a hex value such as #0277a2. Empty means the MARLO default', '4', '#0277a2', '3');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '4', 'crp_theme_color', 'Brand colour for this Global Unit chrome, as a hex value such as #0277a2. Empty means the MARLO default', '4', '#0277a2', '3');
