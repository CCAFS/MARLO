-- Follow-up to V2_6_0_20260925_1543__RewriteSpecificityDescriptions. That migration has already run, so its
-- texts are not edited in place: MarloFlywayConfiguration calls repair() on every startup, so an edited file
-- would be quietly re-checksummed and never run again, leaving the old texts in every database that already
-- applied it. This one corrects the descriptions a review of the result found wrong, stale or written for
-- developers rather than for the administrator reading the Specificities tab, and removes one specificity
-- that turned out to have no reader.
--
-- What is corrected:
--
-- 1. Wrong content.
--    - shfrm_contribution_active expanded SHFRM as "Sustainable Healthy Food and Resilience Mission". The
--      deliverable form calls it the Soil Health and Fertiliser Road Map.
--    - feedback_new_comment_field_active described "the newer comment input". The flag enables or hides the
--      box for writing a new comment (forms.ftl macro qaPopUpMultiple, feedbackAutoImplementation.js), and
--      when off the reviewer sees "it is not possible to add new comments".
--    - crp_enable_email_notification listed the deliverable status change notice as sent outside the switch.
--      Since e811956eb1, committed before the previous migration, SendMailS.send() and sendTemporalMethod()
--      drop every email of a Global Unit with the switch off, except one addressed only to the support team,
--      so that notice obeys it too.
--    - crp_email_cc_fl_fm_cl claimed to copy leaders on partner changes. It has no reader at all, so it is
--      removed instead of re-described (see the end of this file).
--
-- 2. Texts the previous migration itself made stale.
--    - crp_reports_description said "the two stored versions contradict each other", but that migration
--      unified them. The flag drives visible = [showDescription] in the .prpt layouts: on shows the
--      explanatory paragraph in the report header.
--    - crp_fs_w1w2_cofinancing pointed at "its stored note", which was the description being replaced.
--    - crp_multiple_coa referred the reader to the co-financing text instead of stating the rule.
--
-- 3. Developer vocabulary replaced by what the administrator sees: interceptors, validators, the mailer,
--    commented-out code, REST endpoints, LDAP bind, "catalog default", reports microservice, and notes on how
--    a key is spelled (the administrator reads the key on screen, never types it).
--
-- 4. "Inverted" removed from crp_cluster_leader and crp_project_budget_zero. Neither is inverted: on makes
--    the thing optional or allowed, which reads naturally. homepage_hide_section_map keeps the label because
--    on really means hidden there.
--
-- 5. The two longest texts (crp_enable_email_notification, project_website_year_value) and
--    highlight_comments_active are shortened, since the screen renders descriptions in small italics.
--
-- Environment assumptions for the UPDATEs: the same as the previous migration. No hardcoded id of any kind,
-- every statement matches on `key` and category = 2, a missing key updates 0 rows, only `description` is
-- written, no text contains a semicolon, and running them twice changes nothing the second time. The
-- previous text is overwritten in place. It remains recoverable from V2_6_0_20260925_1543, which holds it
-- verbatim. The removal at the end deletes rows and carries its own notes.

UPDATE parameters SET description = 'Signs CGIAR users in through Amazon Cognito instead of the CGIAR directory (LDAP). A change applies from the next login.'
WHERE category = 2 AND `key` = 'cognito_auth_active';

UPDATE parameters SET description = 'Shows the guide button in the page footer. It opens a short guide to the section on screen (home dashboard, project description, partners, locations, contributions and others) and appears only where the user can edit, plus on the home dashboard. The guide texts are built into MARLO and cannot be changed from the admin screens.'
WHERE category = 2 AND `key` = 'button_guide_active';

UPDATE parameters SET description = 'Lets flagship outcomes carry baseline indicators, shows them on project contributions and in Admin -> Program Management, and checks them when a project outcome is saved.'
WHERE category = 2 AND `key` = 'crp_baseline_indicators';

UPDATE parameters SET description = 'Makes the Cluster of Activity leader optional. When off, a leader is required.'
WHERE category = 2 AND `key` = 'crp_cluster_leader';

UPDATE parameters SET description = 'Enables the intellectual-asset block (patent, plant variety, licence) on deliverables and publications and its summary in the Annual Report cross-cutting dimensions. The super administrator''s bulk replication also copies it.'
WHERE category = 2 AND `key` = 'crp_deliverable_intellectual_asset';

UPDATE parameters SET description = 'Asks for the Principal Investigator email on a funding source, requires it on save, and prints it in the funding-source exports. Turning it on publishes a personal email address in those reports.'
WHERE category = 2 AND `key` = 'crp_email_funding_source';

UPDATE parameters SET description = 'Enables the budget execution block, where reported spend is captured against the planned budget. It counts toward whether a project''s sections read as complete.'
WHERE category = 2 AND `key` = 'crp_enable_budget_execution';

UPDATE parameters SET description = 'Master switch for the emails MARLO sends about work in the system: project and impact pathway submissions and unsubmits, partner, country office and target unit requests, changes in the admin management screens, deliverable status changes, and feedback comment and reaction emails. Only the system error reports, which go to the support team alone, are sent regardless. With no value saved, emails stay on.'
WHERE category = 2 AND `key` = 'crp_enable_email_notification';

UPDATE parameters SET description = 'Separates the W1/W2 budget that co-finances W3/Bilateral money: it adds the extra budget type and the Co-Financing tag on funding-source lists, project budgets and the budget reports. Keep multiple Clusters of Activity per project switched off while this is on. MARLO does not enforce it.'
WHERE category = 2 AND `key` = 'crp_fs_w1w2_cofinancing';

UPDATE parameters SET description = 'Enables the Dissemination & Metadata and Quality Check blocks on deliverables and publications during reporting and upkeep, with their required fields.'
WHERE category = 2 AND `key` = 'crp_has_disemination';

UPDATE parameters SET description = 'Shows the outcome indicator field in the Impact Pathway outcomes, in the pathway graphs and in the outcome reports. The field is optional - saving never requires it.'
WHERE category = 2 AND `key` = 'crp_ip_outcome_indicator';

UPDATE parameters SET description = 'Lets a project select more than one Cluster of Activity in its description. Keep it off while W1/W2 co-financing is on. MARLO does not enforce it.'
WHERE category = 2 AND `key` = 'crp_multiple_coa';

UPDATE parameters SET description = 'Restricts a deliverable to a single gender level. When off, several levels can be selected.'
WHERE category = 2 AND `key` = 'crp_one_gender';

UPDATE parameters SET description = 'Keeps write access for users with the PMU role after the cycle is closed - on projects, deliverables, outcomes, innovations, studies, policies, highlights and funding sources. It changes who can edit, not what is displayed.'
WHERE category = 2 AND `key` = 'crp_pmu_closed';

UPDATE parameters SET description = 'Allows a project to carry a zero budget. When off, a zero total for the current year flags the budget amounts as missing and the budget section stays incomplete.'
WHERE category = 2 AND `key` = 'crp_project_budget_zero';

UPDATE parameters SET description = 'Publishes this Global Unit''s projects on the public project page, readable by anyone without logging in - treat it as a publication decision. The page shows the planning phase of the public website year set on this same screen.'
WHERE category = 2 AND `key` = 'crp_project_page';

UPDATE parameters SET description = 'Adds the paragraph that explains what the report contains to the header of the summary reports (projects, deliverables, partners, institutions and LP6 contribution). When off, the reports start directly with the data.'
WHERE category = 2 AND `key` = 'crp_reports_description';

UPDATE parameters SET description = 'Locks deliverables marked Complete in the previous phase, so they open read-only in the current phase. Global Unit administrators and super administrators can still edit them. When off, a completed deliverable stays editable.'
WHERE category = 2 AND `key` = 'deliverable_completed_in_previous_phases_active';

UPDATE parameters SET description = 'Shows the box for writing new feedback comments. When off, the box is hidden and reviewers see a notice that new comments cannot be added in this phase, while existing comments stay visible. Use it to close commenting at the end of a review round.'
WHERE category = 2 AND `key` = 'feedback_new_comment_field_active';

UPDATE parameters SET description = 'Produces the OICR PDF with MARLO''s built-in report engine (Pentaho) instead of the external reports service, which formats the document differently. It applies to signed-in users only: a report opened from a public link always comes from the reports service.'
WHERE category = 2 AND `key` = 'generate_pentaho_OICRs_report_active';

UPDATE parameters SET description = 'Produces the innovation PDF with MARLO''s built-in report engine (Pentaho) instead of the external reports service, which formats the document differently. It applies to signed-in users only: a report opened from a public link always comes from the reports service.'
WHERE category = 2 AND `key` = 'generate_pentaho_innovations_report_active';

UPDATE parameters SET description = 'Adds a tracking icon to feedback comments, so a comment''s author can get an email when someone reacts to it. It appears on the user''s own comments only, needs the ''can track comments'' feedback permission, and needs the feedback layer switched on.'
WHERE category = 2 AND `key` = 'highlight_comments_active';

UPDATE parameters SET description = 'Shows the cross-cutting marker fields on outcomes and on project contributions, and checks the related milestone rule when an outcome is saved. On by default for a new Global Unit, unlike almost every other switch here.'
WHERE category = 2 AND `key` = 'impact_pathway_cross_cutting_markets_active';

UPDATE parameters SET description = 'Adds the field that links an innovation to another innovation already reported in PRMS. On by default, so a new Global Unit gets the field unless it is switched off.'
WHERE category = 2 AND `key` = 'innovation_link_to_other_reported_in_prms_active';

UPDATE parameters SET description = 'Switches project outcomes and the contributions list to the portfolio model and adds the Portfolio Management screen to the Global Unit''s admin menu. It also changes which fields an outcome needs before it can be saved as complete.'
WHERE category = 2 AND `key` = 'portfolio_feature_active';

UPDATE parameters SET description = 'The year whose Planning phase the public project page shows. If that year has no Planning phase, the page shows nothing, even with publishing switched on. 0 matches no phase.'
WHERE category = 2 AND `key` = 'project_website_year_value';

UPDATE parameters SET description = 'Enables the SHFRM (Soil Health and Fertiliser Road Map) contribution block on a deliverable and validates it on save.'
WHERE category = 2 AND `key` = 'shfrm_contribution_active';

-- A2-2526: remove crp_email_cc_fl_fm_cl, a specificity the inventory behind
-- V2_6_0_20260924_1418__RemoveUnusedSpecificities missed. It never controlled whether flagship leaders were
-- copied (they always were), only whether cluster leaders were added to the CC of the project submit and
-- unsubmit emails. 84e904cfe8 (2023-08-09) simplified those emails to CC only the submitting user and the
-- Project Leader and left the old CC logic, with its two readers, commented out in ProjectSubmissionAction and
-- UnsubmitProjectAction. Its last caller was utils/SendEmails.main(), a one-off data-load script for Global
-- Units 21 and 22 that forced the value to true in its own session instead of reading it, so the stored value
-- has not changed anything since 2023. The Java side of the removal (both constants and the SendEmails block)
-- ships in the same commit.
--
-- Same assumptions and order as V2_6_0_20260924_1418: no hardcoded id, match on `key` and category = 2, a
-- missing key deletes 0 rows, and custom_parameters goes first because custom_parameters_ibfk_1 references
-- parameters(id). On aiccradb2 (2026-09-28) it removes 3 parameters rows and 20 custom_parameters rows over 20
-- Global Units, 13 of them set to true. None of that changes behaviour, since nothing reads the value. There is
-- no automatic rollback. Back the rows up before running it in production:
--
--   SELECT cp.* FROM custom_parameters cp JOIN parameters p ON p.id = cp.parameter_id
--   WHERE p.`key` = 'crp_email_cc_fl_fm_cl';
--
--   SELECT * FROM parameters WHERE `key` = 'crp_email_cc_fl_fm_cl';
--
-- The original seed is V2_0_0_20170620_1000__ParamEmailsCC, left untouched.

DELETE cp FROM custom_parameters cp
INNER JOIN parameters p ON p.id = cp.parameter_id
WHERE p.category = 2
  AND p.`key` = 'crp_email_cc_fl_fm_cl';

DELETE FROM parameters
WHERE category = 2
  AND `key` = 'crp_email_cc_fl_fm_cl';
