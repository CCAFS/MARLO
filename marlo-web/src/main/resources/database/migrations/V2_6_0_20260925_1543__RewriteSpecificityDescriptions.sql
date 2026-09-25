-- A2-2524: rewrite the description of every specificity so the Super Admin Specificities tab explains
-- what each switch actually does. The texts come from the catalog review carried out for A2-2448, where
-- each key was traced to its real readers -- FreeMarker templates, Struts actions, validators, interceptors,
-- the REST layer and the mailer -- and they follow one shape: what changes, on which screen or section,
-- then the catch (a required field, a permission, a report, an inverted meaning, or a second setting it
-- depends on).
--
-- What this replaces, and why it is a replacement rather than an edit:
--
-- 1. The description is stored once per Global Unit type, so the same switch could be explained three
--    different ways. Several keys had genuinely contradictory texts: crp_reports_description said "Hide
--    description" for CRP and "Description on Excel Reports" for Platform and Center; crp_ip_outcome_indicator
--    said "visible and required" for two types although no validator requires the field. Each statement below
--    matches on `key` alone, so all of that key's rows -- including the type-5 row that exists only for
--    crp_email_support_team -- end up with one text.
--
-- 2. Ten texts were factually wrong, not merely thin. crp_status_funding_sources claimed that activating it
--    disables Informally Confirmed when the code adds that status; crp_has_research_human pointed at the
--    Studies section although the field lives on the Funding Source form; tip_notification_email_active
--    described user creation in cluster partners although it fires when a non-CGIAR user is assigned as
--    Project Leader or Project Coordinator; crp_one_gender carried "Not used anymore" although the
--    deliverable macro still reads it. Those readings are corrected here.
--
-- 3. No description names a Global Unit. One text is stored for every unit, so a sentence about AICCRA is
--    wrong for the rest; where the code branches on isAiccra() the fact is tracked in the review page, not
--    in the catalog.
--
-- 4. No description quotes a stored key either. The screen relabels the `crp_` prefix as `system_` for
--    display, so a text pointing at "crp_show_section_impact_covid19" names something the administrator
--    cannot find in the list. Companion settings are referred to by what they are.
--
-- 5. 19 rows carried a screencast.com link, always on the CRP row and never on the other two. The links are
--    dropped: they stood in for an explanation that now exists in the text itself, and the service they
--    point at is no longer a dependable place to keep MARLO documentation. If any of them must survive,
--    that is a deliberate decision to take before this migration ships, not after.
--
-- Environment assumptions, since none of them is visible in the statements:
--
-- a. No hardcoded id of any kind: no row id, no global_unit_id, no global_unit_type_id. Every statement
--    matches on `key` and `category` = 2, so it applies to whichever rows that database happens to have.
--    A key that is absent updates 0 rows, which MySQL reports as success -- intended, because this must be
--    a no-op where a specificity was never seeded rather than an error.
--
-- b. Only `description` is written. `default_value`, `format` and every custom_parameters value are left
--    exactly as they are, so no Global Unit changes behaviour when this runs.
--
-- c. It is idempotent: running it twice writes the same text and reports 0 changed rows the second time.
--
-- c2. No text contains a semicolon. Flyway's parser tracks quoted strings and would handle one, but the
--    statement delimiter is the one character whose mishandling silently truncates a description mid
--    sentence, so the eight texts that wanted a semicolon were rephrased instead of relying on the parser.
--
-- d. There is no automatic way back: the previous text is overwritten in place and lives nowhere else.
--    Take a copy before running it in production, and keep it with the release notes:
--
--      SELECT id, global_unit_type_id, `key`, description
--      FROM parameters WHERE category = 2 ORDER BY `key`, global_unit_type_id;
--
--    Restoring one key is then a single UPDATE back to the saved text. The seed migrations that first
--    inserted these rows are NOT a rollback path: several descriptions were edited by hand afterwards, so
--    the files no longer say what the databases hold.
--
-- d2. It overwrites any description edited by hand in a database. That is the intent -- the catalog becomes
--    what this repository says it is -- but it means a local edit made outside Flyway is lost.
--
-- e. project_activity_creation_active is deliberately NOT touched. It was seeded on 2026-09-22 by
--    V2_6_0_20260922_1233__CreateProjectActivityCreationSpecificity with a description already written in
--    this shape, so rewriting it would only churn the row.
--
-- The six specificities removed by V2_6_0_20260924_1418__RemoveUnusedSpecificities are absent from the list
-- below; that migration carries an earlier timestamp, so it runs first and leaves nothing for these
-- statements to match.

UPDATE parameters SET description = 'Shows the AI tab in the main menu, where each AI tool the Global Unit has configured appears as a card. It controls the menu entry only: the cards come from the AI tool configuration, so the tab opens on an empty page when no tools are set up.'
WHERE category = 2 AND `key` = 'ai_section_active';

UPDATE parameters SET description = 'Shows the floating guide button in the page footer. The screen that would configure it is commented out in the code, so the button currently has no admin surface.'
WHERE category = 2 AND `key` = 'button_guide_active';

UPDATE parameters SET description = 'Signs CGIAR users in through Amazon Cognito instead of the LDAP bind. It is resolved per Global Unit before a session exists, so a change here takes effect on the next login without any refresh.'
WHERE category = 2 AND `key` = 'cognito_auth_active';

UPDATE parameters SET description = 'Renders the contribution to performance indicators as milestones grouped by year instead of one flat list. Layout only - both layouts save under the same rules.'
WHERE category = 2 AND `key` = 'contribution_performance_indicators_show_multiple_milestones_per_year_active';

UPDATE parameters SET description = 'Enables the Activities group inside a project and the activity blocks of the reporting PDFs.'
WHERE category = 2 AND `key` = 'crp_activities_module';

UPDATE parameters SET description = 'Lets flagship outcomes carry baseline indicators and shows them on project contributions and in Admin -> Program Management, and adds their rule to the project outcome validator.'
WHERE category = 2 AND `key` = 'crp_baseline_indicators';

UPDATE parameters SET description = 'Shows the Business Intelligence tab in the main menu. Menu visibility only - the BI dashboards themselves are configured separately.'
WHERE category = 2 AND `key` = 'crp_bi_module_active';

UPDATE parameters SET description = 'Asks for the gender share of each budget line, by partner and by Cluster of Activity, and prints it in the budget reports. The field is shown only to the project''s editing leader.'
WHERE category = 2 AND `key` = 'crp_budget_gender';

UPDATE parameters SET description = 'Makes the Cluster of Activity leader optional. Inverted: when off, the form and the validator both require a leader.'
WHERE category = 2 AND `key` = 'crp_cluster_leader';

UPDATE parameters SET description = 'Gives the Contact Point role edit rights on a project and all of its sections (deliverables, outcomes, innovations, studies, policies, highlights). Off leaves the Contact Point with read-only access.'
WHERE category = 2 AND `key` = 'crp_contact_point_edit_project';

UPDATE parameters SET description = 'Makes the COVID-19 impact section mandatory for a project to count as complete. Independent of the switch that displays that section, so a Global Unit can require a section it never shows.'
WHERE category = 2 AND `key` = 'crp_covid_required';

UPDATE parameters SET description = 'Switches the gender scale to None / Some / Significant. When off the five research levels apply (collection of sex-disaggregated data, analysis, diagnostics, development of innovations, monitoring and impact assessment). It re-labels values already stored, so flipping it changes how past data reads.'
WHERE category = 2 AND `key` = 'crp_custom_gender';

UPDATE parameters SET description = 'Enables the intellectual-asset block (patent, plant variety, licence) on deliverables and publications, its roll-up in the Annual Report cross-cutting dimensions, and what the super-admin bulk replication copies.'
WHERE category = 2 AND `key` = 'crp_deliverable_intellectual_asset';

UPDATE parameters SET description = 'Asks for the IFPRI division on funding sources and on project partners, requires it on save, and prints it in the funding-source reports.'
WHERE category = 2 AND `key` = 'crp_division_fs';

UPDATE parameters SET description = 'Copies flagship leaders, flagship managers and cluster leaders on project partner changes. The mailer forces this on for two of its own paths and the submission readers are commented out, so the stored value is advisory rather than decisive.'
WHERE category = 2 AND `key` = 'crp_email_cc_fl_fm_cl';

UPDATE parameters SET description = 'Asks for the principal investigator (donor) email on a funding source, requires it on save, and prints it in the funding-source exports - so turning it on publishes a personal email address into those reports.'
WHERE category = 2 AND `key` = 'crp_email_funding_source';

UPDATE parameters SET description = 'Redirects every outgoing email to the support team and ignores the normal recipients. It is the safety switch for a test or staging Global Unit, and left on in production it silences users'' notifications.'
WHERE category = 2 AND `key` = 'crp_email_support_team';

UPDATE parameters SET description = 'Enables the budget execution block, where reported spend is captured against the planned budget. It also feeds the section-status check, so it affects whether a project reads as complete.'
WHERE category = 2 AND `key` = 'crp_enable_budget_execution';

UPDATE parameters SET description = 'Master switch for the notifications MARLO sends about work in the system: project and impact pathway submissions and unsubmits, partner, country office and target unit requests, changes made in the admin management screens, and the feedback comment and reaction emails. It is not absolute - the deliverable status change notice and the system error reports are sent outside it. A Global Unit with no value saved has notifications on.'
WHERE category = 2 AND `key` = 'crp_enable_email_notification';

UPDATE parameters SET description = 'Separates the W1/W2 budget that co-finances W3/Bilateral money: it adds the extra budget type and the Co-Financing tag on funding-source lists, project budgets and the budget reports. Its stored note requires multiple Clusters of Activity to be off, but that is a human convention - nothing in the code enforces it.'
WHERE category = 2 AND `key` = 'crp_fs_w1w2_cofinancing';

UPDATE parameters SET description = 'Declares that this Global Unit uses Contact Points: Admin -> PPA Partners exposes them and a funding source can be attributed to one.'
WHERE category = 2 AND `key` = 'crp_has_contact_point';

UPDATE parameters SET description = 'Enables the Dissemination & Metadata and Quality Check blocks on deliverables and publications during reporting and upkeep, and their required fields. Note the key itself is misspelled (disemination) and must be used exactly as stored.'
WHERE category = 2 AND `key` = 'crp_has_disemination';

UPDATE parameters SET description = 'Declares that this Global Unit runs Regional Programs: it enables the Regional Mapping admin screen, the regional dimension on projects and publications, the regional blocks of POWB collaboration and the region columns of the summary reports. It is also saved from Admin -> Program Management, not only from this screen.'
WHERE category = 2 AND `key` = 'crp_has_regions';

UPDATE parameters SET description = 'Funding sources: asks whether the funded research involves human subjects and, when the answer is yes, requires the supporting file before the funding source can be saved. It is not part of the Studies / OICR section.'
WHERE category = 2 AND `key` = 'crp_has_research_human';

UPDATE parameters SET description = 'Gives management projects their own deliverable type list instead of the standard one. The same list is applied in the innovation deliverable picker.'
WHERE category = 2 AND `key` = 'crp_has_specific_management_deliverable_type';

UPDATE parameters SET description = 'Shows the outcome indicator field in the Impact Pathway outcomes, in the pathway graphs and in the outcome reports. Visible only - no validator requires the field.'
WHERE category = 2 AND `key` = 'crp_ip_outcome_indicator';

UPDATE parameters SET description = 'Shows the lessons-learned module in project partners and in the contribution to flagship outcomes, and includes it in the section-completeness check.'
WHERE category = 2 AND `key` = 'crp_lessons_active';

UPDATE parameters SET description = 'Enables the Leverages section of a project during reporting and its Board summary report.'
WHERE category = 2 AND `key` = 'crp_leverages_module';

UPDATE parameters SET description = 'Enables the CSV upload of activity locations in the project Locations section during reporting, and validates the uploaded rows.'
WHERE category = 2 AND `key` = 'crp_location_csv_activities';

UPDATE parameters SET description = 'Enables the Contribution to LP6 section of a project during reporting, its deliverable link and the LP6 report.'
WHERE category = 2 AND `key` = 'crp_lp6_active';

UPDATE parameters SET description = 'Requires a contact person on every managing partner - in project partners and in the partner pickers of deliverables, innovations and studies.'
WHERE category = 2 AND `key` = 'crp_managing_partners_contact_persons';

UPDATE parameters SET description = 'Lets a project select more than one Cluster of Activity in its description. The pairing documented on the W1/W2 co-financing switch - that this must be off when co-financing is on - is a human convention that nothing in the code enforces.'
WHERE category = 2 AND `key` = 'crp_multiple_coa';

UPDATE parameters SET description = 'Asks for Next Users in the project contribution to outcomes section and validates the answer on save.'
WHERE category = 2 AND `key` = 'crp_next_users';

UPDATE parameters SET description = 'Makes the Responsibilities field mandatory for partners that are not managing partners.'
WHERE category = 2 AND `key` = 'crp_nonPPAPartner_resp_required';

UPDATE parameters SET description = 'Restricts a deliverable to a single gender level, where off allows several. Still read on every deliverable form.'
WHERE category = 2 AND `key` = 'crp_one_gender';

UPDATE parameters SET description = 'Lets a project set its global and regional dimensions by hand and add locations beyond those inherited from its funding sources. When off, the save recomputes both dimensions from the country list and overwrites whatever was entered.'
WHERE category = 2 AND `key` = 'crp_other_locations';

UPDATE parameters SET description = 'Makes partner country offices mandatory in the project partners section. The section heading switches to its ''required'' wording at the same time.'
WHERE category = 2 AND `key` = 'crp_partners_office';

UPDATE parameters SET description = 'Keeps write access for users with the PMU role after the cycle is closed - on projects, deliverables, outcomes, innovations, studies, policies, highlights and funding sources. Eleven edit interceptors read it, so it changes permissions, not what is displayed.'
WHERE category = 2 AND `key` = 'crp_pmu_closed';

UPDATE parameters SET description = 'Shows the ''4. CCAFS Specific'' tab in the POWB 2019 menu. POWB 2019 is a legacy cycle, so no current cycle reads it.'
WHERE category = 2 AND `key` = 'crp_powb_program_change';

UPDATE parameters SET description = 'Widens the Flagship / liaison institution list in the project description to every active liaison institution - including managing partners (PPAs) and institutions not tied to a Global Unit. Off offers only the flagship-level entries.'
WHERE category = 2 AND `key` = 'crp_ppa_enable_project_description';

UPDATE parameters SET description = 'Allows a project to carry a zero budget. Inverted: when off, the budget validator rejects a zero total.'
WHERE category = 2 AND `key` = 'crp_project_budget_zero';

UPDATE parameters SET description = 'Publishes this Global Unit''s projects through the public project-page REST endpoints, readable by anyone without logging in - treat it as a publication decision. What it publishes is the planning phase named by the public website year on this same screen.'
WHERE category = 2 AND `key` = 'crp_project_page';

UPDATE parameters SET description = 'Passes a flag into the Excel and Pentaho summary reports (projects, deliverables, partners, institutions) that controls the description block in their header. The two stored versions contradict each other, and the actual branch lives inside the .prpt report definitions rather than in the application code.'
WHERE category = 2 AND `key` = 'crp_reports_description';

UPDATE parameters SET description = 'Adds the communications field to a project outcome during reporting and requires it before the section counts as complete.'
WHERE category = 2 AND `key` = 'crp_show_project_outcome_communications';

UPDATE parameters SET description = 'Enables the COVID-19 impact section of a project, but only for the years set in the COVID-19 year range field on this same screen - with that range empty the section stays hidden.'
WHERE category = 2 AND `key` = 'crp_show_section_impact_covid19';

UPDATE parameters SET description = 'Free text, not a switch: the years the COVID-19 section applies to, written as since-until (2020-2021) or as a single start year (2020 = from that year onwards). Empty or unreadable values keep the section hidden even when the COVID-19 section itself is enabled.'
WHERE category = 2 AND `key` = 'crp_show_section_impact_covid19_ranges_years';

UPDATE parameters SET description = 'Offers the full list of funding-source agreement statuses, including Pipeline and Informally Confirmed. When off, those two are hidden and the rest remain.'
WHERE category = 2 AND `key` = 'crp_status_funding_sources';

UPDATE parameters SET description = 'Enables the Project Highlights section of a project during reporting and its Board summary report.'
WHERE category = 2 AND `key` = 'crp_view_highlights';

UPDATE parameters SET description = 'Shows deliverables already completed in an earlier phase and blocks editing them in the current phase. The edit interceptor enforces it, so it is a permission rule and not only a display one.'
WHERE category = 2 AND `key` = 'deliverable_completed_in_previous_phases_active';

UPDATE parameters SET description = 'Enables the MELIA block (monitoring, evaluation, learning and impact assessment) on a deliverable and validates it on save.'
WHERE category = 2 AND `key` = 'deliverable_melia_module_active';

UPDATE parameters SET description = 'Lets one capacity-development deliverable report its trainees for several clusters (indicator 2.3) instead of duplicating the deliverable per cluster. It changes what the participant figures mean, and deleting such a deliverable goes through an extra check.'
WHERE category = 2 AND `key` = 'deliverable_shared_clusters_trainees_active';

UPDATE parameters SET description = 'Enables duplicating a deliverable and marks the copies in the deliverable lists.'
WHERE category = 2 AND `key` = 'duplicated_deliverables_functionality_active';

UPDATE parameters SET description = 'Master switch for the reviewer feedback layer: the Feedback section of a project, the comment widget on every commentable field and the comment counts in the lists. Its administration lives in Feedback Management and Feedback Roles & Permissions.'
WHERE category = 2 AND `key` = 'feedback_active';

UPDATE parameters SET description = 'Adds the ''clarification needed'' state to the feedback comment widget.'
WHERE category = 2 AND `key` = 'feedback_clarification_needed_active';

UPDATE parameters SET description = 'Lets a reviewer save a comment as a draft before publishing it. It is independent of the feedback layer''s master switch - nothing prevents drafts being enabled while the layer itself is off.'
WHERE category = 2 AND `key` = 'feedback_draft_active';

UPDATE parameters SET description = 'Switches the feedback comment widget to the newer comment input.'
WHERE category = 2 AND `key` = 'feedback_new_comment_field_active';

UPDATE parameters SET description = 'Generates the OICR report with Pentaho instead of the reports microservice. The key has an uppercase segment and must be written exactly as OICRs.'
WHERE category = 2 AND `key` = 'generate_pentaho_OICRs_report_active';

UPDATE parameters SET description = 'Generates the innovations report with Pentaho instead of the reports microservice.'
WHERE category = 2 AND `key` = 'generate_pentaho_innovations_report_active';

UPDATE parameters SET description = 'Enables the lookup that fills a deliverable''s metadata from its handle or link through the external Web of Science service. With it off no outbound call is made and no metadata is returned.'
WHERE category = 2 AND `key` = 'handle_wos_service_active';

UPDATE parameters SET description = 'Adds the tracking icon to each feedback comment, which lets the comment''s own author subscribe to an email notification sent when someone reacts to it. It appears only on your own comments, only for a role holding the ''can track comments'' feedback permission, and only while the feedback layer is on. Despite the key name, nothing is highlighted.'
WHERE category = 2 AND `key` = 'highlight_comments_active';

UPDATE parameters SET description = 'Hides the banner and map block on the home dashboard. Inverted: on = hidden. The block also needs a homepage banner configured to appear at all.'
WHERE category = 2 AND `key` = 'homepage_hide_section_map';

UPDATE parameters SET description = 'Shows the phase timeline block on the home dashboard. Its content is maintained on the Timeline Management screen.'
WHERE category = 2 AND `key` = 'homepage_timeline_active';

UPDATE parameters SET description = 'Shows the cross-cutting marker fields on outcomes and on project contributions, and adds a milestone rule to the outcome validator. Its catalog default is on, unlike almost every other flag here.'
WHERE category = 2 AND `key` = 'impact_pathway_cross_cutting_markets_active';

UPDATE parameters SET description = 'Adds the field that links an innovation to another innovation already reported in PRMS. Its catalog default is on, so a new Global Unit gets the field unless it is switched off.'
WHERE category = 2 AND `key` = 'innovation_link_to_other_reported_in_prms_active';

UPDATE parameters SET description = 'Enables the Innovations section inside a project and its block on the home dashboard.'
WHERE category = 2 AND `key` = 'innovation_section_active';

UPDATE parameters SET description = 'Opens the journal-article indicator pop-up when a project outcome is loaded (indicator 1.2).'
WHERE category = 2 AND `key` = 'journal_articles_indicator_popup_active';

UPDATE parameters SET description = 'Sends a notification when a deliverable''s status changes. It only ever fires where shared trainees across clusters is also enabled, so on its own it does nothing.'
WHERE category = 2 AND `key` = 'notify_deliverable_status_change';

UPDATE parameters SET description = 'Shows the OICR score field, and only to PMU members and super administrators.'
WHERE category = 2 AND `key` = 'oicr_score_field_active';

UPDATE parameters SET description = 'Lets a super administrator set the OICR tag by hand instead of leaving it assigned automatically. Enforced on save as well as in the form.'
WHERE category = 2 AND `key` = 'oicr_tag_field_manual_manage_active';

UPDATE parameters SET description = 'Switches project outcomes and the contributions list to the portfolio model and adds the Portfolio Management screen to the Global Unit''s admin menu. It also changes what the outcome validator requires, so it affects saving, not only display.'
WHERE category = 2 AND `key` = 'portfolio_feature_active';

UPDATE parameters SET description = 'Adds the previous project ID field to the project description, for projects carried over from an earlier system.'
WHERE category = 2 AND `key` = 'previous_project_id_field_active';

UPDATE parameters SET description = 'The year whose planning phase the public project page shows. MARLO looks up the Planning phase of that year and publishes its project information, flagships and regions. When no planning phase matches the year, the page answers with nothing at all, even while publishing is switched on. Enter a year that has a planning phase - 0 matches none.'
WHERE category = 2 AND `key` = 'project_website_year_value';

UPDATE parameters SET description = 'Enables the SHFRM (Sustainable Healthy Food and Resilience Mission) contribution block on a deliverable and validates it on save.'
WHERE category = 2 AND `key` = 'shfrm_contribution_active';

UPDATE parameters SET description = 'Restores the superseded tabbed layout of the contribution to performance indicators list. It is kept as an escape hatch and the tabbed layout stays deprecated.'
WHERE category = 2 AND `key` = 'show_contribution_performance_indicator_deprecated_tab_active';

UPDATE parameters SET description = 'Adds the gender predefined keywords option to the Search Terms board summary.'
WHERE category = 2 AND `key` = 'show_gender_keywords_summaries';

UPDATE parameters SET description = 'Adds the AI-generated narrative tab to the Board summaries. Independent of the AI section in the main menu: the reports tab and the section are two separate switches.'
WHERE category = 2 AND `key` = 'summary_ai_report_tab_active';

UPDATE parameters SET description = 'Sends the TIP notification when a non-CGIAR user is assigned as Project Leader or Project Coordinator in project partners. It uses the PL and PC role ids configured on the Roles tab, so a wrong id there stops it silently.'
WHERE category = 2 AND `key` = 'tip_notification_email_active';

UPDATE parameters SET description = 'Shows the TIP tab in the main menu (flagged beta) and the TIP Management entry in System Admin.'
WHERE category = 2 AND `key` = 'tip_section_active';

UPDATE parameters SET description = 'Adds the security token to the generated TIP dashboard URL instead of handing out a plain link.'
WHERE category = 2 AND `key` = 'tip_security_active';
