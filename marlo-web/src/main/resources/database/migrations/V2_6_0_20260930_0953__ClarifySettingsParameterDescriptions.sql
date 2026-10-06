-- Clarify the descriptions of the Settings parameters shown in the superadmin Parameters screen.
-- Several descriptions did not say what the parameter does, and crp_refresh promised a refresh of every connected
-- user while only the session of the next request is reloaded. The descriptions were the same for every global unit
-- type and the behaviour lives in shared code, so every type gets the new text.
--
-- Safe in every environment:
-- * no global unit, parameter or user id is assumed; rows are matched by key only;
-- * only DML, so Flyway applies it in a single transaction: all or nothing;
-- * re-running it rewrites the same text, and running it where a key does not exist changes 0 rows.

UPDATE parameters
SET description = 'Enable the Admin section for everyone in this global unit, admins included. When false, the section is not available.'
WHERE `key` = 'crp_admin_active';

UPDATE parameters
SET description = 'Enable the Impact Pathway section for everyone in this global unit, admins included. When false, the section is not available.'
WHERE `key` = 'crp_impPath_active';

UPDATE parameters
SET description = 'Id of the liaison institution (liaison_institutions.id) that represents the Program Management Unit. New PMU members are linked to it.'
WHERE `key` = 'crp_cu';

UPDATE parameters
SET description = 'Close this global unit for editing: every section becomes read-only and adding, submitting or unsubmitting is disabled. Takes effect immediately.'
WHERE `key` = 'crp_closed';

UPDATE parameters
SET description = 'Name of the file in the custom/ folder, without the .properties extension, that holds the texts specific to this global unit. Texts not defined there keep the default. Applied at login.'
WHERE `key` = 'crp_custom_file';

UPDATE parameters
SET description = 'Reload the parameters and phases into the session of the next user who opens a page of this global unit, then switch back to false. It does not refresh every connected user; they get the new values at their next login.'
WHERE `key` = 'crp_refresh';

UPDATE parameters
SET description = 'Id of the phase (phases.id) users land on when they sign in or no phase is selected. It must be a phase of this global unit.'
WHERE `key` = 'current_phase';

UPDATE parameters
SET description = 'Exact report_name of the bi_reports row shown in the project Feedback tab. It must match a report of this global unit. Applied at login; default_value is not used as a fallback.'
WHERE `key` = 'crp_cluster_bi_feedback_report_name';
