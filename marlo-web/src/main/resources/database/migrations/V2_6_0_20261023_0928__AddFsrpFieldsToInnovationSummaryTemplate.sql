-- A2-2613 (rules 4.1.7, 4.2.4): the innovation summary PDF shows the AICCRA-FSRP IV fields.
--
-- The summary is rendered by the PDF microservice from the Handlebars template stored in
-- report_configurations.innovation_template_data. That template is loaded by hand in each environment (its image
-- URLs differ per environment), so it is not rewritten here: each block is inserted before an anchor the template
-- carries once. Where the anchor is missing nothing changes, and a second run is skipped by the NOT LIKE guard.
--
-- ProjectInnovationSummaryAction only sends valueChains, coDevelopers and institutionalLevel for a global unit whose
-- specificity shows them in the form, so the blocks stay hidden for every other unit.

-- Value chain(s), in the General Information section, before the Impact Area contribution title
UPDATE report_configurations
SET innovation_template_data = REPLACE(innovation_template_data,
  '<h2 class="title">Impact Area contribution </h2>',
  CONCAT('{{#if valueChains}}<h2 class="title">Value chain(s)</h2><span class="content"><ul>',
    '{{#each (parseJSON valueChains)}}<li>{{this}}</li>{{/each}}</ul></span><br>{{/if}}',
    '<h2 class="title">Impact Area contribution </h2>'))
WHERE innovation_template_data LIKE '%<h2 class="title">Impact Area contribution </h2>%'
  AND innovation_template_data NOT LIKE '%{{#if valueChains}}%';

-- Co-developing African institution(s) and Institutional level, in the Bundle composition section, before the
-- complementary innovations
UPDATE report_configurations
SET innovation_template_data = REPLACE(innovation_template_data,
  '{{#if complementarySolutions}}',
  CONCAT('{{#ifOr coDevelopers institutionalLevel}}<span class="section">',
    '<h2 class="title">Co-developing African institution(s)</h2><span class="content">',
    '{{#if coDevelopers}}<ul>{{#each (parseJSON coDevelopers)}}<li>{{this}}</li>{{/each}}</ul>',
    '{{else}}Not provided{{/if}}',
    '{{#if institutionalLevel}}<span class="field"><span class="name">Institutional level: </span>',
    '{{institutionalLevel}}</span>{{/if}}</span></span>{{/ifOr}}',
    '{{#if complementarySolutions}}'))
WHERE innovation_template_data LIKE '%{{#if complementarySolutions}}%'
  AND innovation_template_data NOT LIKE '%{{#ifOr coDevelopers institutionalLevel}}%';
