--  Auto-generated SQL script #202512161615
UPDATE report_configurations
	SET project_template_data='<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<title>AICCRA Cluster Summary</title>
<style>
  @page:first {
    background: url("https://marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com/cover-PDF.png") no-repeat center center;
    background-size: cover;
    margin: 0;
  }
  body { font-family: "Montserrat", "Arial", sans-serif; color: #1F1F1F; margin: 0; padding: 0; background-color: #f0f4f7; }
  .page { padding: 110px 60px 80px 60px; background-color: #f8f9fb; min-height: 100vh; box-sizing: border-box; }
  h1 { font-size: 24px; color: #0a4c70; margin-bottom: 0; letter-spacing: 0.04em; }
  h2 { font-size: 18px; margin-top: 5px; color: #4b4b4b; font-weight: 500; }
  .caption { font-size: 11px; color: #6a7a89; text-transform: uppercase; letter-spacing: 0.2em; margin-top: 12px; }
  .meta-table { width: 100%; border-collapse: collapse; margin-top: 25px; border-radius: 8px; overflow: hidden; box-shadow: 0 6px 12px rgba(10, 76, 112, 0.08); }
  .meta-table th { text-align: left; background: #0a4c70; color: white; padding: 12px; width: 28%; font-size: 10px; letter-spacing: 0.08em; text-transform: uppercase; }
  .meta-table td { padding: 12px; background: white; font-size: 12px; border-bottom: 1px solid #dfe6ee; color: #1F1F1F; }
  .meta-table tr:last-child td { border-bottom: none; }
  .badge { display: inline-block; padding: 5px 12px; border-radius: 999px; background: #e7f4ff; color: #0a4c70; font-weight: 600; margin-right: 10px; font-size: 10px; letter-spacing: 0.05em; }
  .tag { display: inline-block; padding: 2px 8px; border-radius: 999px; background: #f1f5f9; color: #0a4c70; font-size: 10px; margin-left: 6px; }
  .tag--muted { background: #e8edf3; color: #506b85; }
  .section { margin-top: 35px; }
  .section--page-break { page-break-before: always; }
  .section h3 { font-size: 14px; text-transform: uppercase; color: #0a4c70; letter-spacing: 0.12em; margin-bottom: 8px; }
  .section p { margin: 0; line-height: 1.6; color: #333333; font-size: 10px; }
  .grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; margin-top: 12px; }
  .grid--2 { grid-template-columns: repeat(2, minmax(220px, 1fr)); }
  .grid--3 { grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); }
  .grid-item { background: white; border-radius: 10px; padding: 16px; border: 1px solid #dfe6ee; min-height: auto; box-shadow: 0 4px 8px rgba(10, 76, 112, 0.05); }
  .grid-item span { display: block; font-size: 10px; letter-spacing: 0.1em; color: #6a7a89; margin-bottom: 8px; text-transform: uppercase; }
  .grid-item strong { font-size: 10px; color: #1F1F1F; word-break: break-word; }
  .grid-item p { font-size: 10px; white-space: pre-line; margin-top: 4px; }
  .card { background: white; border-radius: 12px; padding: 18px; border: 1px solid #dfe6ee; box-shadow: 0 4px 12px rgba(10, 76, 112, 0.05); margin-top: 12px; }
  .card p { white-space: pre-line; font-size: 10px; }
  .list-card { background: white; border-radius: 12px; padding: 20px; border: 1px solid #dfe6ee; box-shadow: 0 4px 12px rgba(10, 76, 112, 0.05); }
  .list-card h4 { margin: 0 0 12px 0; font-size: 9px; letter-spacing: 0.08em; color: #6a7a89; text-transform: uppercase; }
  .list-card ul { list-style: none; padding: 0; margin: 0; }
  .list-card li { font-size: 10px; padding: 8px 0; border-bottom: 1px solid #eef2f7; white-space: pre-line; }
  .list-card li:last-child { border-bottom: none; }
  .list-card small { display: block; color: #6a7a89; font-size: 9px; letter-spacing: 0.05em; }
  .partner-card { background: white; border-radius: 14px; padding: 20px; border: 1px solid #dfe6ee; box-shadow: 0 6px 16px rgba(10, 76, 112, 0.06); margin-bottom: 18px; }
  .partner-card__header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; gap: 12px; }
  .partner-card__title { font-size: 14px; color: #0a4c70; }
  .partner-card__label { font-size: 10px; letter-spacing: 0.08em; text-transform: uppercase; color: #6a7a89; margin-bottom: 4px; display: block; }
  .partner-card__section { margin-top: 10px; }
  .partner-card__section p { font-size: 10px; white-space: pre-line; }
  .partner-card__section ul { list-style: none; padding: 0; margin: 6px 0 0 0; }
  .partner-card__section li { padding: 6px 0; border-bottom: 1px solid #eef2f7; font-size: 10px; color: #1f1f1f; }
  .partner-card__section li:last-child { border-bottom: none; }
  .partner-card__section small { color: #6a7a89; font-size: 10px; display: inline-block; margin-left: 4px; }
  .partner-card__contacts { margin-top: 12px; border-top: 1px dashed #dfe6ee; padding-top: 12px; }
  .partner-card__contact { display: flex; flex-direction: column; margin-bottom: 10px; }
  .partner-card__contact strong { font-size: 10px; color: #1F1F1F; }
  .partner-card__contact small { color: #6a7a89; font-size: 10px; text-transform: uppercase; letter-spacing: 0.08em; }
  .partner-card__contact a, .partner-card__contact span { font-size: 11px; color: #0a4c70; text-decoration: none; word-break: break-all; }
  .partner-card__role { display: inline-block; margin-top: 6px; padding: 4px 12px; border-radius: 999px; border: 0.5px solid #dfe6ee; background: #f4f7fb; color: #0a4c70; font-size: 10px; letter-spacing: 0.05em; text-transform: capitalize; }
  .partner-card__role--muted { background: #f1f1f1; border-color: #e6eaef; color: #6a7a89; }
  .badge--soft { background: #f4f8fb; border: 1px solid #dfe6ee; }
  .contribution-card { background: white; border-radius: 16px; padding: 22px; border: 1px solid #dfe6ee; box-shadow: 0 6px 16px rgba(10, 76, 112, 0.06); margin-bottom: 22px; }
  .contribution-card__header { display: flex; justify-content: space-between; gap: 16px; flex-wrap: wrap; }
  .contribution-card__header h4 { margin: 6px 0 0 0; font-size: 13px; color: #0a4c70; }
  .contribution-card__indicator { font-size: 10px; color: #0a4c70; margin-top: 6px; letter-spacing: 0.04em; }
  .contribution-card__meta { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
  .contribution-card__grid .grid-item { min-height: auto; }
  .contribution-card__block { margin-top: 18px; }
  .contribution-card__block p { white-space: pre-line; font-size: 10px; }
  .contribution-card__block h4 { margin: 0 0 10px 0; font-size: 9px; letter-spacing: 0.08em; color: #6a7a89; text-transform: uppercase; }
  .contribution-list { list-style: none; margin: 0; padding: 0; }
  .contribution-list li { padding: 10px 0; border-bottom: 1px solid #eef2f7; }
  .contribution-list li:last-child { border-bottom: none; }
  .contribution-list li p { font-size: 10px; white-space: pre-line; margin: 2px 0; }
  .contribution-list strong { display: block; font-size: 10px; color: #0a4c70; margin-bottom: 4px; }
  .contribution-list a { font-size: 11px; color: #0a4c70; text-decoration: underline; word-break: break-all; }
  .contribution-note { font-size: 9px; color: #6a7a89; }
  #pageHeader, #pageFooter { position: fixed; left: 0; right: 0; height: 80px; padding: 15px 50px; background: white; box-sizing: border-box; }
  #pageHeader { top: 0; border-bottom: 1px solid rgba(10, 76, 112, 0.12); display: flex; align-items: center; justify-content: space-between; }
  #pageFooter { bottom: 0; border-top: 1px solid rgba(10, 76, 112, 0.12); font-size: 11px; color: #6a7a89; display: flex; align-items: center; justify-content: space-between; }
  .logo { height: 42px; }
  .disclaimer { font-size: 10px; color: #6a7a89; margin-top: 30px; line-height: 1.4; }
</style>
</head>
<body>
<div id="pageHeader">
  <img src="https://marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com/AICCRA-logo.png" class="logo" alt="AICCRA logo" />
  <span style="font-size: 12px; letter-spacing: 0.3em; color: #6a7a89;">{{projectAcronym}} CLUSTER SUMMARY</span>
</div>
<div class="page">
  <section>
    <h1>AICCRA Cluster Summary</h1>
    <h2>{{#safeEmpty}}{{projectTitle}}{{/safeEmpty}}</h2>
    <p class="caption">Report generated on {{#safeEmpty}}{{timeCreation}}{{/safeEmpty}}</p>
    <table class="meta-table">
      <tr>
        <th>Cluster identifier</th>
        <td>{{#safeEmpty}}{{projectID}}{{/safeEmpty}}</td>
      </tr>
      <tr>
        <th>Cluster name</th>
        <td>{{#safeEmpty}}{{projectTitle}}{{/safeEmpty}}</td>
      </tr>
      <tr>
        <th>Report year</th>
        <td>{{#safeEmpty}}{{year}}{{/safeEmpty}}</td>
      </tr>
      <tr>
        <th>Report cycle</th>
        <td>{{#safeEmpty}}{{cycle}}{{/safeEmpty}}</td>
      </tr>
    </table>
  </section>

  <section class="section">
    <h3>Key badges</h3>
    <div>
      <span class="badge">{{#safeEmpty}}{{projectAcronym}}{{/safeEmpty}} Cluster</span>
      <span class="badge">{{#safeEmpty}}{{cycle}}{{/safeEmpty}} Phase</span>
      <span class="badge">Year {{#safeEmpty}}{{year}}{{/safeEmpty}}</span>
    </div>
  </section>

  {{#projectDescription}}
  <section class="section section--page-break">
    <h3>Cluster Information</h3>
    <div class="grid grid--2">
      <div class="grid-item">
        <span>Start date</span>
        <strong>{{#safeEmpty}}{{startDate}}{{/safeEmpty}}</strong>
      </div>
      <div class="grid-item">
        <span>End date</span>
        <strong>{{#safeEmpty}}{{endDate}}{{/safeEmpty}}</strong>
      </div>
      <div class="grid-item">
        <span>Cluster type</span>
        <strong>{{#safeEmpty}}{{type}}{{/safeEmpty}}</strong>
      </div>
      <div class="grid-item">
        <span>Status</span>
        <strong>{{#safeEmpty}}{{status}}{{/safeEmpty}}</strong>
      </div>
      <div class="grid-item">
        <span>{{#safeEmpty}}{{liaisonLabel}}{{/safeEmpty}}</span>
        <strong>{{#safeEmpty}}{{liaisonInstitution}}{{/safeEmpty}}</strong>
      </div>
      <div class="grid-item">
        <span>{{#safeEmpty}}{{liaisonContactLabel}}{{/safeEmpty}}</span>
        <strong>{{#safeEmpty}}{{leader}}{{/safeEmpty}}</strong>
      </div>
      <div class="grid-item">
        <span>Lead organization</span>
        <strong>{{#safeEmpty}}{{leadOrganization}}{{/safeEmpty}}</strong>
      </div>
      <div class="grid-item">
        <span>Cycle</span>
        <strong>{{#safeEmpty}}{{cycle}}{{/safeEmpty}}</strong>
      </div>
    </div>
  </section>

  <section class="section">
    <h3>Cluster summary</h3>
    <div class="card">
      {{#summary}}
        <p>{{#safeEmpty}}{{.}}{{/safeEmpty}}</p>
      {{/summary}}
      {{^summary}}
        <p>No summary provided.</p>
      {{/summary}}
    </div>
  </section>

  <section class="section">
    <h3>Challenges &amp; solutions</h3>
    <div class="card">
      {{#challengesSolutions}}
        <p>{{#safeEmpty}}{{.}}{{/safeEmpty}}</p>
      {{/challengesSolutions}}
      {{^challengesSolutions}}
        <p>No challenges reported.</p>
      {{/challengesSolutions}}
    </div>
  </section>

  <section class="section">
    <h3>Lessons learned</h3>
    <div class="card">
      {{#lessonsLearned}}
        <p>{{#safeEmpty}}{{.}}{{/safeEmpty}}</p>
      {{/lessonsLearned}}
      {{^lessonsLearned}}
        <p>No lessons reported.</p>
      {{/lessonsLearned}}
    </div>
  </section>

  <section class="section">
    <h3>Components & regional programs</h3>
    <div class="grid grid--3">
      <div class="list-card">
        <h4>Components</h4>
        <ul>
          {{#flagships}}
          <li>
            <strong>
              {{#composedName}}{{#safeEmpty}}{{.}}{{/safeEmpty}}{{/composedName}}
              {{^composedName}}{{#name}}{{#safeEmpty}}{{.}}{{/safeEmpty}}{{/name}}{{/composedName}}
            </strong>
            {{#acronym}}<small>{{#safeEmpty}}{{.}}{{/safeEmpty}}</small>{{/acronym}}
          </li>
          {{/flagships}}
          {{^flagships}}
          <li>No components reported.</li>
          {{/flagships}}
        </ul>
      </div>
      {{#hasRegions}}
      <div class="list-card">
        <h4>Regional programs</h4>
        <ul>
          {{#regions}}
          <li>
            <strong>
              {{#composedName}}{{#safeEmpty}}{{.}}{{/safeEmpty}}{{/composedName}}
              {{^composedName}}{{#name}}{{#safeEmpty}}{{.}}{{/safeEmpty}}{{/name}}{{/composedName}}
            </strong>
            {{#acronym}}<small>{{#safeEmpty}}{{.}}{{/safeEmpty}}</small>{{/acronym}}
          </li>
          {{/regions}}
          {{^regions}}
          <li>No regional programs reported.</li>
          {{/regions}}
        </ul>
      </div>
      {{/hasRegions}}
      {{^hasRegions}}
      <div class="list-card">
        <h4>Regional programs</h4>
        <ul>
          <li>Not applicable for this cluster.</li>
        </ul>
      </div>
      {{/hasRegions}}

    </div>
  </section>
  {{/projectDescription}}

  <section class="section">
    <h3>Cross-cutting dimenssions</h3>
    <div class="card">
      {{#if crossCutting}}
        <p>{{{crossCutting}}}</p>
      {{else}}
        <p>No cross-cutting dimensions reported.</p>
      {{/if}}
    </div>
  </section>

  <br>
  <section class="section section--page-break">
    <h3>Cluster partners</h3>
    {{#projectPartners}}
    <div class="partner-card">
      <div class="partner-card__header">
        <div class="partner-card__title">{{#safeEmpty}}{{institutionName}}{{/safeEmpty}}</div>
      </div>
      <div class="partner-card__section">
        <span class="partner-card__label">Responsibilities</span>
        {{#responsibilities}}
          <p>{{#safeEmpty}}{{.}}{{/safeEmpty}}</p>
        {{/responsibilities}}
        {{^responsibilities}}
          <p>No responsibilities reported.</p>
        {{/responsibilities}}
      </div>
      <div class="partner-card__section">
        <span class="partner-card__label">Locations</span>
        <ul>
          {{#locations}}
          <li>
            <strong>{{#safeEmpty}}{{name}}{{/safeEmpty}}</strong>
            {{#headquarter}}<span class="tag tag--muted">HQ</span>{{/headquarter}}
          </li>
          {{/locations}}
          {{^locations}}
          <li>No partner locations reported.</li>
          {{/locations}}
        </ul>
      </div>
      <div class="partner-card__section partner-card__contacts">
        <span class="partner-card__label">Partner contacts</span>
        {{#persons}}
        <div class="partner-card__contact">
          <strong>{{#safeEmpty}}{{name}}{{/safeEmpty}}</strong>
          {{#role}}<span class="partner-card__role">{{#safeEmpty}}{{.}}{{/safeEmpty}}</span>{{/role}}
          {{^role}}<span class="partner-card__role partner-card__role--muted">&lt;Not provided&gt;</span>{{/role}}
          {{#division}}<span class="tag tag--muted">{{#safeEmpty}}{{division}}{{/safeEmpty}}</span>{{/division}}
          {{#email}}<span>{{#safeEmpty}}{{.}}{{/safeEmpty}}</span>{{/email}}
        </div>
        {{/persons}}
        {{^persons}}
        <p>No partner contacts reported.</p>
        {{/persons}}
      </div>
    </div>
    {{/projectPartners}}
    {{^projectPartners}}
      <p>No partner information reported.</p>
    {{/projectPartners}}
  </section>

  <br>
  {{#projectLocations}}
  <section class="section section--page-break">
    <h3>Cluster locations</h3>
    <div class="grid grid--2">
      <div class="grid-item">
        <span>Global dimension</span>
        {{#globalDimension}}
          <strong>Yes</strong>
        {{/globalDimension}}
        {{^globalDimension}}
          <strong>No</strong>
        {{/globalDimension}}
      </div>
      <div class="grid-item">
        <span>Regional dimension</span>
        {{#regionalDimension}}
          <strong>Yes</strong>
        {{/regionalDimension}}
        {{^regionalDimension}}
          <strong>No</strong>
        {{/regionalDimension}}
      </div>
    </div>
    {{#hasLocations}}
      <div class="grid grid--3">
        {{#locationGroups}}
        <div class="list-card">
          <h4>{{#safeEmpty}}{{typeName}}{{/safeEmpty}}</h4>
          <ul>
            {{#locations}}
            <li>
              <strong>{{#safeEmpty}}{{name}}{{/safeEmpty}}</strong>
              {{#code}}<small>{{#safeEmpty}}{{.}}{{/safeEmpty}}</small>{{/code}}
              <small>{{#safeEmpty}}{{latitude}}{{/safeEmpty}}, {{#safeEmpty}}{{longitude}}{{/safeEmpty}}</small>
            </li>
            {{/locations}}
          </ul>
        </div>
        {{/locationGroups}}
      </div>
    {{/hasLocations}}
    {{^hasLocations}}
      <p>No cluster locations reported.</p>
    {{/hasLocations}}
  </section>
  {{/projectLocations}}

  <br>
  <section class="section section--page-break">
    <h3>Contribution to performance indicators</h3>
    {{#performanceIndicatorContributions}}
    <div class="contribution-card">
      <div class="contribution-card__header">
        <div>
          <span class="caption">Performance indicator</span>
          <h4>{{#safeEmpty}}{{programOutcome}}{{/safeEmpty}}</h4>
          {{#indicator}}<p class="contribution-card__indicator">{{#safeEmpty}}{{indicator}}{{/safeEmpty}}</p>{{/indicator}}
        </div>
        <div class="contribution-card__meta">
          {{#if flagshipBadge}}<span class="badge badge--soft">{{#safeEmpty}}{{flagshipBadge}}{{/safeEmpty}}</span>{{/if}}
          {{#if programOutcomeYear}}<span class="tag tag--muted">Year {{#safeEmpty}}{{programOutcomeYear}}{{/safeEmpty}}</span>{{/if}}
        </div>
      </div>
      <div class="grid grid--2 contribution-card__grid">
        <div class="grid-item">
          <span>Target{{#if targetUnit}} ({{#safeEmpty}}{{targetUnit}}{{/safeEmpty}}){{/if}}</span>
          {{#if targetValue}}<strong>{{#safeEmpty}}{{targetValue}}{{/safeEmpty}}</strong>{{else}}<strong>&lt;Not provided&gt;</strong>{{/if}}
          {{#if targetNarrative}}<p>{{#safeEmpty}}{{targetNarrative}}{{/safeEmpty}}</p>{{/if}}
        </div>
        <div class="grid-item">
          <span>Achieved{{#if achievementUnit}} ({{#safeEmpty}}{{achievementUnit}}{{/safeEmpty}}){{/if}}</span>
          {{#if achievementValue}}<strong>{{#safeEmpty}}{{achievementValue}}{{/safeEmpty}}</strong>{{else}}<strong>&lt;Not provided&gt;</strong>{{/if}}
          {{#if achievementNarrative}}<p>{{#safeEmpty}}{{achievementNarrative}}{{/safeEmpty}}</p>{{/if}}
        </div>
      </div>
      {{#communications}}
      <div class="contribution-card__block">
        <h4>Communications &amp; engagement</h4>
        <p>{{#safeEmpty}}{{communications}}{{/safeEmpty}}</p>
      </div>
      {{/communications}}
      {{#lessonsLearned}}
      <div class="contribution-card__block">
        <h4>Lessons learned</h4>
        <p>{{#safeEmpty}}{{lessonsLearned}}{{/safeEmpty}}</p>
      </div>
      {{/lessonsLearned}}
      {{#if hasIndicatorResponses}}
      <div class="contribution-card__block">
        <h4>Additional questions</h4>
        <ul class="contribution-list">
          {{#indicatorResponses}}
          <li>
            {{#if question}}<strong>{{#safeEmpty}}{{question}}{{/safeEmpty}}</strong>{{/if}}
            {{#if narrative}}<p>{{#safeEmpty}}{{narrative}}{{/safeEmpty}}</p>{{/if}}
            {{#if achievedNarrative}}<p class="contribution-note">Achievement: {{#safeEmpty}}{{achievedNarrative}}{{/safeEmpty}}</p>{{/if}}
          </li>
          {{/indicatorResponses}}
        </ul>
      </div>
      {{/if}}
      {{#if hasMilestones}}
      <div class="contribution-card__block">
        <h4>Intermediate targets</h4>
        <ul class="contribution-list">
          {{#milestones}}
          <li>
            {{#if title}}<strong>{{#safeEmpty}}{{title}}{{/safeEmpty}}</strong>{{/if}}
            {{#if year}}<p class="contribution-note">Year {{#safeEmpty}}{{year}}{{/safeEmpty}}</p>{{/if}}
            {{#if narrative}}<p>{{#safeEmpty}}{{narrative}}{{/safeEmpty}}</p>{{/if}}
            <p class="contribution-note">
              Target: {{#if expectedValue}}{{#safeEmpty}}{{expectedValue}}{{/safeEmpty}}{{else}}&lt;Not provided&gt;{{/if}}
              {{#if achievedValue}} | Achieved: {{#safeEmpty}}{{achievedValue}}{{/safeEmpty}}{{/if}}
            </p>
          </li>
          {{/milestones}}
        </ul>
      </div>
      {{/if}}
      {{#hasNextUsers}}
      <div class="contribution-card__block">
        <h4>Key next users</h4>
        <ul class="contribution-list">
          {{#nextUsers}}
          <li>
            {{#name}}<strong>{{#safeEmpty}}{{name}}{{/safeEmpty}}</strong>{{/name}}
            {{#knowledge}}<p>Knowledge needs: {{#safeEmpty}}{{knowledge}}{{/safeEmpty}}</p>{{/knowledge}}
            {{#strategies}}<p>Strategies: {{#safeEmpty}}{{strategies}}{{/safeEmpty}}</p>{{/strategies}}
            {{#knowledgeReport}}<p class="contribution-note">Reporting: {{#safeEmpty}}{{knowledgeReport}}{{/safeEmpty}}</p>{{/knowledgeReport}}
            {{#strategiesReport}}<p class="contribution-note">Actions: {{#safeEmpty}}{{strategiesReport}}{{/safeEmpty}}</p>{{/strategiesReport}}
          </li>
          {{/nextUsers}}
        </ul>
      </div>
      {{/hasNextUsers}}
    </div>
    {{/performanceIndicatorContributions}}
    {{^performanceIndicatorContributions}}
      <p>No performance indicator contributions reported.</p>
    {{/performanceIndicatorContributions}}
  </section>

  <section class="section section--page-break">
    <h3>OICRs (Outcomes, Impact Case Reports)</h3>
    {{#each oicrs}}
    <div class="contribution-card">
      <div class="contribution-card__header">
        <div>
          <span class="caption">OICR #{{#safeEmpty}}{{id}}{{/safeEmpty}}</span>
          {{#if title}}<h4>{{#safeEmpty}}{{title}}{{/safeEmpty}}</h4>{{/if}}
          {{#if type}}<p class="contribution-card__indicator">{{#safeEmpty}}{{type}}{{/safeEmpty}}</p>{{/if}}
        </div>
        <div class="contribution-card__meta">
          {{#if year}}<span class="tag tag--muted">Year {{#safeEmpty}}{{year}}{{/safeEmpty}}</span>{{/if}}
          {{#if status}}<span class="badge badge--soft">{{#safeEmpty}}{{status}}{{/safeEmpty}}</span>{{/if}}
          {{#if tag}}<span class="badge badge--soft">{{#safeEmpty}}{{tag}}{{/safeEmpty}}</span>{{/if}}
        </div>
      </div>
      {{#if outcomeImpactStatement}}
      <div class="contribution-card__block">
        <h4>Outcome/Impact Statement</h4>
        <p>{{#safeEmpty}}{{outcomeImpactStatement}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if elaborationOutcomeImpactStatement}}
      <div class="contribution-card__block">
        <h4>Elaboration of Outcome/Impact Statement</h4>
        <p>{{#safeEmpty}}{{elaborationOutcomeImpactStatement}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if outcomeStory}}
      <div class="contribution-card__block">
        <h4>Outcome Story</h4>
        <p>{{#safeEmpty}}{{outcomeStory}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      <div class="grid grid--2 contribution-card__grid">
        {{#if geographicScopes}}
        <div class="grid-item">
          <span>Geographic Scope</span>
          <strong>{{#safeEmpty}}{{geographicScopes}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if regions}}
        <div class="grid-item">
          <span>Regions</span>
          <strong>{{#safeEmpty}}{{regions}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if countries}}
        <div class="grid-item">
          <span>Countries</span>
          <strong>{{#safeEmpty}}{{countries}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if cities}}
        <div class="grid-item">
          <span>Cities</span>
          <strong>{{#safeEmpty}}{{cities}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if flagships}}
        <div class="grid-item">
          <span>Flagships</span>
          <strong>{{#safeEmpty}}{{flagships}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if regionalPrograms}}
        <div class="grid-item">
          <span>Regional Programs</span>
          <strong>{{#safeEmpty}}{{regionalPrograms}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if crps}}
        <div class="grid-item">
          <span>CRPs/Platforms</span>
          <strong>{{#safeEmpty}}{{crps}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if subIdos}}
        <div class="grid-item">
          <span>Sub-IDOs</span>
          <strong>{{#safeEmpty}}{{subIdos}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if commissioningStudy}}
        <div class="grid-item">
          <span>Commissioning Study</span>
          <strong>{{#safeEmpty}}{{commissioningStudy}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if stageStudy}}
        <div class="grid-item">
          <span>Maturity of Change</span>
          <strong>{{#safeEmpty}}{{stageStudy}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if stageProcess}}
        <div class="grid-item">
          <span>Stage Process</span>
          <strong>{{#safeEmpty}}{{stageProcess}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if organizationType}}
        <div class="grid-item">
          <span>Organization Type</span>
          <strong>{{#safeEmpty}}{{organizationType}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if policyInvestimentType}}
        <div class="grid-item">
          <span>Policy Investment Type</span>
          <strong>{{#safeEmpty}}{{policyInvestimentType}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if policyAmount}}
        <div class="grid-item">
          <span>Policy Amount</span>
          <strong>{{#safeEmpty}}{{policyAmount}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if isContributionText}}
        <div class="grid-item">
          <span>Is Contribution</span>
          <strong>{{#safeEmpty}}{{isContributionText}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
      </div>
      {{#if topLevelComments}}
      <div class="contribution-card__block">
        <h4>Top Level Comments</h4>
        <p>{{#safeEmpty}}{{topLevelComments}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if scopeComments}}
      <div class="contribution-card__block">
        <h4>Scope Comments</h4>
        <p>{{#safeEmpty}}{{scopeComments}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if quantification}}
      <div class="contribution-card__block">
        <h4>Quantification</h4>
        <p>{{#safeEmpty}}{{quantification}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if hasQuantifications}}
      <div class="contribution-card__block">
        <h4>Quantifications</h4>
        <ul class="contribution-list">
          {{#each quantifications}}
          <li>
            {{#if type}}<strong>{{#safeEmpty}}{{type}}{{/safeEmpty}}</strong>{{/if}}
            {{#if number}}<p>Number: {{#safeEmpty}}{{number}}{{/safeEmpty}}{{#if unit}} {{#safeEmpty}}{{unit}}{{/safeEmpty}}{{/if}}</p>{{/if}}
            {{#if comments}}<p>{{#safeEmpty}}{{comments}}{{/safeEmpty}}</p>{{/if}}
          </li>
          {{/each}}
        </ul>
      </div>
      {{/if}}
      {{#if institutions}}
      <div class="contribution-card__block">
        <h4>External Partners</h4>
        <p>{{#safeEmpty}}{{institutions}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if centers}}
      <div class="contribution-card__block">
        <h4>Contributing Centers/Managing partners</h4>
        <p>{{#safeEmpty}}{{centers}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if policies}}
      <div class="contribution-card__block">
        <h4>Policies</h4>
        <p>{{#safeEmpty}}{{policies}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if innovations}}
      <div class="contribution-card__block">
        <h4>Innovations</h4>
        <p>{{#safeEmpty}}{{innovations}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if cgiarInnovation}}
      <div class="contribution-card__block">
        <h4>CGIAR Innovation</h4>
        <p>{{#safeEmpty}}{{cgiarInnovation}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if otherInnovationsNarrative}}
      <div class="contribution-card__block">
        <h4>Other Innovations Narrative</h4>
        <p>{{#safeEmpty}}{{otherInnovationsNarrative}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if projectOutcomes}}
      <div class="contribution-card__block">
        <h4>Project Outcomes</h4>
        <p>{{#safeEmpty}}{{projectOutcomes}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if crpOutcomes}}
      <div class="contribution-card__block">
        <h4>Link to Performance Indicators</h4>
        <p>{{#safeEmpty}}{{crpOutcomes}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if allianceLevers}}
      <div class="contribution-card__block">
        <h4>Alliance Levers</h4>
        <p>{{#safeEmpty}}{{allianceLevers}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if impactAreas}}
      <div class="contribution-card__block">
        <h4>Impact Areas</h4>
        <p>{{#safeEmpty}}{{impactAreas}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if globalTargets}}
      <div class="contribution-card__block">
        <h4>Global Targets</h4>
        <p>{{#safeEmpty}}{{globalTargets}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if genderRelevance}}
      <div class="contribution-card__block">
        <h4>Gender Relevance</h4>
        <p>{{#safeEmpty}}{{genderRelevance}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if youthRelevance}}
      <div class="contribution-card__block">
        <h4>Youth Relevance</h4>
        <p>{{#safeEmpty}}{{youthRelevance}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if capacityRelevance}}
      <div class="contribution-card__block">
        <h4>Capacity Development Relevance</h4>
        <p>{{#safeEmpty}}{{capacityRelevance}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if climateRelevance}}
      <div class="contribution-card__block">
        <h4>Climate Change Relevance</h4>
        <p>{{#safeEmpty}}{{climateRelevance}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if otherCrossCuttingSelection}}
      <div class="contribution-card__block">
        <h4>Other Cross-cutting Selection</h4>
        <p>{{#safeEmpty}}{{otherCrossCuttingSelection}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if otherCrossCuttingDimensions}}
      <div class="contribution-card__block">
        <h4>Other Cross-cutting Dimensions</h4>
        <p>{{#safeEmpty}}{{otherCrossCuttingDimensions}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if commentsRelevance}}
      <div class="contribution-card__block">
        <h4>Comments Relevance</h4>
        <p>{{#safeEmpty}}{{commentsRelevance}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if comunicationsMaterial}}
      <div class="contribution-card__block">
        <h4>Communications Material</h4>
        <p>{{#safeEmpty}}{{comunicationsMaterial}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if contacts}}
      <div class="contribution-card__block">
        <h4>Contacts</h4>
        <p>{{#safeEmpty}}{{contacts}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if links}}
      <div class="contribution-card__block">
        <h4>Links</h4>
        <ul class="contribution-list">
          {{#each links}}
          <li><a href="{{#safeEmpty}}{{this}}{{/safeEmpty}}" target="_blank">{{#safeEmpty}}{{this}}{{/safeEmpty}}</a></li>
          {{/each}}
        </ul>
      </div>
      {{/if}}
      {{#if hasReferences}}
      <div class="contribution-card__block">
        <h4>References</h4>
        <ul class="contribution-list">
          {{#each references}}
          <li>
            {{#if reference}}<strong>{{#safeEmpty}}{{reference}}{{/safeEmpty}}</strong>{{/if}}
            {{#if link}}<p><a href="{{#safeEmpty}}{{link}}{{/safeEmpty}}" target="_blank">{{#safeEmpty}}{{link}}{{/safeEmpty}}</a></p>{{/if}}
            {{#if externalAuthor}}<p class="contribution-note">External Author</p>{{/if}}
          </li>
          {{/each}}
        </ul>
      </div>
      {{/if}}
      {{#if referencesText}}
      <div class="contribution-card__block">
        <h4>References</h4>
        <p>{{#safeEmpty}}{{referencesText}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if hasPublications}}
      <div class="contribution-card__block">
        <h4>Publications</h4>
        <ul class="contribution-list">
          {{#each publications}}
          <li>
            {{#if name}}<strong>{{#safeEmpty}}{{name}}{{/safeEmpty}}</strong>{{/if}}
            {{#if position}}<p class="contribution-note">Position: {{#safeEmpty}}{{position}}{{/safeEmpty}}</p>{{/if}}
            {{#if affiliation}}<p>Affiliation: {{#safeEmpty}}{{affiliation}}{{/safeEmpty}}</p>{{/if}}
          </li>
          {{/each}}
        </ul>
      </div>
      {{/if}}
      {{#if meliaPublications}}
      <div class="contribution-card__block">
        <h4>MELIA Publications</h4>
        <p>{{#safeEmpty}}{{meliaPublications}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if hasCovidAnalysis}}
      <div class="contribution-card__block">
        <h4>COVID Analysis</h4>
        <p>This OICR includes COVID-19 analysis.</p>
      </div>
      {{/if}}
    </div>
    {{/each}}
    {{^oicrs}}
      <p>No OICRs reported.</p>
    {{/oicrs}}
  </section>

  <section class="section section--page-break">
    <h3>Deliverables</h3>
    {{#each deliverables}}
    <div class="contribution-card">
      <div class="contribution-card__header">
        <div>
          <span class="caption">Deliverable #{{#safeEmpty}}{{id}}{{/safeEmpty}}</span>
          {{#if title}}<h4>{{#safeEmpty}}{{title}}{{/safeEmpty}}</h4>{{/if}}
          {{#if type}}<p class="contribution-card__indicator">{{#safeEmpty}}{{type}}{{/safeEmpty}}</p>{{/if}}
          {{#if typeOther}}<p class="contribution-card__indicator">Other: {{#safeEmpty}}{{typeOther}}{{/safeEmpty}}</p>{{/if}}
        </div>
        <div class="contribution-card__meta">
          {{#if year}}<span class="tag tag--muted">Year {{#safeEmpty}}{{year}}{{/safeEmpty}}</span>{{/if}}
          {{#if newExpectedYear}}{{#if isExtended}}<span class="tag tag--muted">New Expected Year {{#safeEmpty}}{{newExpectedYear}}{{/safeEmpty}}</span>{{/if}}{{/if}}
          {{#if status}}<span class="badge badge--soft">Status: {{#safeEmpty}}{{status}}{{/safeEmpty}}</span>{{/if}}
          {{#if statusDescription}}<span class="badge badge--soft">{{#safeEmpty}}{{statusDescription}}{{/safeEmpty}}</span>{{/if}}
        </div>
      </div>
      {{#if description}}
      <div class="contribution-card__block">
        <h4>Description</h4>
        <p>{{#safeEmpty}}{{description}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      <div class="grid grid--2 contribution-card__grid">
        {{#if geographicScope}}
        <div class="grid-item">
          <span>Geographic Scope</span>
          <strong>{{#safeEmpty}}{{geographicScope}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if isLocationGlobal}}
        <div class="grid-item">
          <span>Is Location Global</span>
          <strong>{{#safeEmpty}}{{isLocationGlobal}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if regions}}
        <div class="grid-item">
          <span>Regions</span>
          <strong>{{#safeEmpty}}{{regions}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if countries}}
        <div class="grid-item">
          <span>Countries</span>
          <strong>{{#safeEmpty}}{{countries}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if crpProgramOutcome}}
        <div class="grid-item">
          <span>CRP Program Outcome</span>
          <strong>{{#safeEmpty}}{{crpProgramOutcome}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if crpClusterKeyOutput}}
        <div class="grid-item">
          <span>CRP Cluster Key Output</span>
          <strong>{{#safeEmpty}}{{crpClusterKeyOutput}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if adoptedLicense}}
        <div class="grid-item">
          <span>Adopted License</span>
          <strong>{{#safeEmpty}}{{adoptedLicense}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if duplicated}}
        <div class="grid-item">
          <span>Duplicated</span>
          <strong>{{#safeEmpty}}{{duplicated}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if remainingPending}}
        <div class="grid-item">
          <span>Remaining Pending</span>
          <strong>{{#safeEmpty}}{{remainingPending}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if contributingShfrm}}
        <div class="grid-item">
          <span>Contributing SHFRM</span>
          <strong>{{#safeEmpty}}{{contributingShfrm}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if meliaStudy}}
        <div class="grid-item">
          <span>MELIA Study</span>
          <strong>{{#safeEmpty}}{{meliaStudy}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
      </div>
      {{#if crps}}
      <div class="contribution-card__block">
        <h4>CRPs/Platforms</h4>
        <p>{{#safeEmpty}}{{crps}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if crpOutcomes}}
      <div class="contribution-card__block">
        <h4>Performance Indicators</h4>
        <p>{{#safeEmpty}}{{crpOutcomes}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if projectOutcomes}}
      <div class="contribution-card__block">
        <h4>Project Outcomes</h4>
        <p>{{#safeEmpty}}{{projectOutcomes}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if activities}}
      <div class="contribution-card__block">
        <h4>Activities</h4>
        <p>{{#safeEmpty}}{{activities}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if fundingSources}}
      <div class="contribution-card__block">
        <h4>Funding Sources</h4>
        <p>{{#safeEmpty}}{{fundingSources}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if genderLevels}}
      <div class="contribution-card__block">
        <h4>Gender Levels</h4>
        <p>{{#safeEmpty}}{{genderLevels}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if shfrmContributionNarrative}}
      <div class="contribution-card__block">
        <h4>SHFRM Contribution Narrative</h4>
        <p>{{#safeEmpty}}{{shfrmContributionNarrative}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if commissioningStudy}}
      <div class="contribution-card__block">
        <h4>Commissioning Study</h4>
        <p>{{#safeEmpty}}{{commissioningStudy}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if activityDescription}}
      <div class="contribution-card__block">
        <h4>Activity Description</h4>
        <p>{{#safeEmpty}}{{activityDescription}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if studyType}}
      <div class="contribution-card__block">
        <h4>Study Type</h4>
        <p>{{#safeEmpty}}{{studyType}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if disseminationUrl}}
      <div class="contribution-card__block">
        <h4>Dissemination URL</h4>
        <p><a href="{{#safeEmpty}}{{disseminationUrl}}{{/safeEmpty}}" target="_blank">{{#safeEmpty}}{{disseminationUrl}}{{/safeEmpty}}</a></p>
      </div>
      {{/if}}
      {{#if disseminationType}}
      <div class="contribution-card__block">
        <h4>Dissemination Type</h4>
        <p>{{#safeEmpty}}{{disseminationType}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if publicationJournal}}
      <div class="contribution-card__block">
        <h4>Publication Journal</h4>
        <p>{{#safeEmpty}}{{publicationJournal}}{{/safeEmpty}}</p>
        {{#if publicationVolume}}<p>Volume: {{#safeEmpty}}{{publicationVolume}}{{/safeEmpty}}</p>{{/if}}
        {{#if publicationIssue}}<p>Issue: {{#safeEmpty}}{{publicationIssue}}{{/safeEmpty}}</p>{{/if}}
        {{#if publicationPages}}<p>Pages: {{#safeEmpty}}{{publicationPages}}{{/safeEmpty}}</p>{{/if}}
        {{#if isiPublication}}<p>ISI Publication: {{#safeEmpty}}{{isiPublication}}{{/safeEmpty}}</p>{{/if}}
        {{#if coAuthor}}<p>Co-Author: {{#safeEmpty}}{{coAuthor}}{{/safeEmpty}}</p>{{/if}}
      </div>
      {{/if}}
      {{#if contacts}}
      <div class="contribution-card__block">
        <h4>Contacts</h4>
        <p>{{#safeEmpty}}{{contacts}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if fileAssurance}}
      <div class="contribution-card__block">
        <h4>File Assurance</h4>
        <p>{{#safeEmpty}}{{fileAssurance}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if fileDictionary}}
      <div class="contribution-card__block">
        <h4>File Dictionary</h4>
        <p>{{#safeEmpty}}{{fileDictionary}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if hasCrossCuttingMarkers}}
      <div class="contribution-card__block">
        <h4>Cross-cutting Markers</h4>
        <ul class="contribution-list">
          {{#each crossCuttingMarkers}}
          <li>
            {{#if name}}<strong>{{#safeEmpty}}{{name}}{{/safeEmpty}}</strong>{{/if}}
            {{#if focusLevel}}<p>Focus Level: {{#safeEmpty}}{{focusLevel}}{{/safeEmpty}}</p>{{/if}}
          </li>
          {{/each}}
        </ul>
      </div>
      {{/if}}
      {{#if hasMetadataElements}}
      <div class="contribution-card__block">
        <h4>Metadata Elements</h4>
        <ul class="contribution-list">
          {{#each metadataElements}}
          <li>
            {{#if name}}<strong>{{#safeEmpty}}{{name}}{{/safeEmpty}}</strong>{{/if}}
            {{#if value}}<p>{{#safeEmpty}}{{value}}{{/safeEmpty}}</p>{{/if}}
          </li>
          {{/each}}
        </ul>
      </div>
      {{/if}}
    </div>
    {{/each}}
    {{^deliverables}}
      <p>No deliverables reported.</p>
    {{/deliverables}}
  </section>

  <section class="section section--page-break">
    <h3>Innovations</h3>
    {{#each innovations}}
    <div class="contribution-card">
      <div class="contribution-card__header">
        <div>
          <span class="caption">Innovation #{{#safeEmpty}}{{id}}{{/safeEmpty}}</span>
          {{#if title}}<h4>{{#safeEmpty}}{{title}}{{/safeEmpty}}</h4>{{/if}}
          {{#if shortTitle}}<p class="contribution-card__indicator">{{#safeEmpty}}{{shortTitle}}{{/safeEmpty}}</p>{{/if}}
          {{#if innovationType}}<p class="contribution-card__indicator">{{#safeEmpty}}{{innovationType}}{{/safeEmpty}}</p>{{/if}}
          {{#if otherInnovationType}}<p class="contribution-card__indicator">Other: {{#safeEmpty}}{{otherInnovationType}}{{/safeEmpty}}</p>{{/if}}
        </div>
        <div class="contribution-card__meta">
          {{#if year}}<span class="tag tag--muted">Year {{#safeEmpty}}{{year}}{{/safeEmpty}}</span>{{/if}}
          {{#if innovationNumber}}<span class="tag tag--muted">Number {{#safeEmpty}}{{innovationNumber}}{{/safeEmpty}}</span>{{/if}}
          {{#if stageInnovation}}<span class="badge badge--soft">{{#safeEmpty}}{{stageInnovation}}{{/safeEmpty}}</span>{{/if}}
          {{#if phaseResearchPartnership}}<span class="badge badge--soft">{{#safeEmpty}}{{phaseResearchPartnership}}{{/safeEmpty}}</span>{{/if}}
        </div>
      </div>
      {{#if narrative}}
      <div class="contribution-card__block">
        <h4>Description</h4>
        <p>{{#safeEmpty}}{{narrative}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if adaptativeResearchNarrative}}
      <div class="contribution-card__block">
        <h4>Adaptative Research Narrative</h4>
        <p>{{#safeEmpty}}{{adaptativeResearchNarrative}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if beneficiariesNarrative}}
      <div class="contribution-card__block">
        <h4>Beneficiaries Narrative</h4>
        <p>{{#safeEmpty}}{{beneficiariesNarrative}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      <div class="grid grid--2 contribution-card__grid">
        {{#if geographicScopes}}
        <div class="grid-item">
          <span>Geographic Scope</span>
          <strong>{{#safeEmpty}}{{geographicScopes}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if regions}}
        <div class="grid-item">
          <span>Regions</span>
          <strong>{{#safeEmpty}}{{regions}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if countries}}
        <div class="grid-item">
          <span>Countries</span>
          <strong>{{#safeEmpty}}{{countries}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if degreeInnovation}}
        <div class="grid-item">
          <span>Degree Innovation</span>
          <strong>{{#safeEmpty}}{{degreeInnovation}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if innovationNature}}
        <div class="grid-item">
          <span>Innovation Nature</span>
          <strong>{{#safeEmpty}}{{innovationNature}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if leadOrganization}}
        <div class="grid-item">
          <span>Lead Organization</span>
          <strong>{{#safeEmpty}}{{leadOrganization}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if clearLead}}
        <div class="grid-item">
          <span>Clear Lead</span>
          <strong>{{#safeEmpty}}{{clearLead}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if innovationBundle}}
        <div class="grid-item">
          <span>Innovation Bundle</span>
          <strong>{{#safeEmpty}}{{innovationBundle}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if region}}
        <div class="grid-item">
          <span>Region</span>
          <strong>{{#safeEmpty}}{{region}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if hasCgiarContribution}}
        <div class="grid-item">
          <span>Has CGIAR Contribution</span>
          <strong>{{#safeEmpty}}{{hasCgiarContribution}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if intellectualPropertyInstitution}}
        <div class="grid-item">
          <span>Intellectual Property Institution</span>
          <strong>{{#safeEmpty}}{{intellectualPropertyInstitution}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
      </div>
      {{#if crps}}
      <div class="contribution-card__block">
        <h4>CRPs/Platforms</h4>
        <p>{{#safeEmpty}}{{crps}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if centers}}
      <div class="contribution-card__block">
        <h4>Contributing Centers</h4>
        <p>{{#safeEmpty}}{{centers}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if organizations}}
      <div class="contribution-card__block">
        <h4>Organizations</h4>
        <p>{{#safeEmpty}}{{organizations}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if hasContributingPartners}}
      <div class="contribution-card__block">
        <h4>Contributing External Partners</h4>
        <ul class="contribution-list">
          {{#each contributingPartners}}
          <li>
            {{#if name}}<strong>{{#safeEmpty}}{{name}}{{/safeEmpty}}</strong>{{/if}}
            {{#if role}}<p class="contribution-note">Role: {{#safeEmpty}}{{role}}{{/safeEmpty}}</p>{{/if}}
          </li>
          {{/each}}
        </ul>
      </div>
      {{/if}}
      {{#if hasAllianceOrganizations}}
      <div class="contribution-card__block">
        <h4>Alliance Organizations</h4>
        <ul class="contribution-list">
          {{#each allianceOrganizations}}
          <li>
            {{#if name}}<strong>{{#safeEmpty}}{{name}}{{/safeEmpty}}</strong>{{/if}}
            {{#if institutionType}}<p>Institution Type: {{#safeEmpty}}{{institutionType}}{{/safeEmpty}}</p>{{/if}}
            {{#if scalingPartner}}<p class="contribution-note">Scaling Partner: Yes</p>{{/if}}
            {{#if number}}<p class="contribution-note">Number: {{#safeEmpty}}{{number}}{{/safeEmpty}}</p>{{/if}}
          </li>
          {{/each}}
        </ul>
      </div>
      {{/if}}
      {{#if subIdos}}
      <div class="contribution-card__block">
        <h4>Sub-IDOs</h4>
        <p>{{#safeEmpty}}{{subIdos}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if projectOutcomes}}
      <div class="contribution-card__block">
        <h4>Project Outcomes</h4>
        <p>{{#safeEmpty}}{{projectOutcomes}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if crpOutcomes}}
      <div class="contribution-card__block">
        <h4>Performance Indicators</h4>
        <p>{{#safeEmpty}}{{crpOutcomes}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if allianceLevers}}
      <div class="contribution-card__block">
        <h4>Alliance Levers</h4>
        <p>{{#safeEmpty}}{{allianceLevers}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if hasLegalRestrictions}}
      <div class="contribution-card__block">
        <h4>Legal Restrictions</h4>
        <p>{{#safeEmpty}}{{hasLegalRestrictions}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if hasAssetPotential}}
      <div class="contribution-card__block">
        <h4>Asset Potential</h4>
        <p>{{#safeEmpty}}{{hasAssetPotential}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if hasFurtherDevelopment}}
      <div class="contribution-card__block">
        <h4>Further Development</h4>
        <p>{{#safeEmpty}}{{hasFurtherDevelopment}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if hasSdgs}}
      <div class="contribution-card__block">
        <h4>SDGs</h4>
        <div style="display: flex; flex-wrap: wrap; gap: 12px; align-items: center; margin-top: 8px;">
          {{#each sdgs}}
          <div style="display: flex; align-items: center; gap: 6px;">
            {{#if iconUrl}}<img src="{{iconUrl}}" alt="{{#if name}}{{name}}{{else}}SDG{{/if}}" style="width: 40px; height: 40px; object-fit: contain;" />{{/if}}
            {{#if name}}<span style="font-size: 10px; color: #1F1F1F;">{{#safeEmpty}}{{name}}{{/safeEmpty}}</span>{{/if}}
          </div>
          {{/each}}
        </div>
      </div>
      {{/if}}
      {{#if genderScore}}
      <div class="contribution-card__block">
        <h4>Gender Score</h4>
        <p>{{#safeEmpty}}{{genderScore}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if climateChangeScore}}
      <div class="contribution-card__block">
        <h4>Climate Change Score</h4>
        <p>{{#safeEmpty}}{{climateChangeScore}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if foodSecurityScore}}
      <div class="contribution-card__block">
        <h4>Food Security Score</h4>
        <p>{{#safeEmpty}}{{foodSecurityScore}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if environmentalScore}}
      <div class="contribution-card__block">
        <h4>Environmental Score</h4>
        <p>{{#safeEmpty}}{{environmentalScore}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if povertyScore}}
      <div class="contribution-card__block">
        <h4>Poverty Score</h4>
        <p>{{#safeEmpty}}{{povertyScore}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if impactAreas}}
      <div class="contribution-card__block">
        <h4>Impact Areas</h4>
        <p>{{#safeEmpty}}{{impactAreas}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if deliverables}}
      <div class="contribution-card__block">
        <h4>Deliverables</h4>
        <p>{{#safeEmpty}}{{deliverables}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if reasonNotCgiarContribution}}
      <div class="contribution-card__block">
        <h4>Reason Not CGIAR Contribution</h4>
        <p>{{#safeEmpty}}{{reasonNotCgiarContribution}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if genderFocusLevel}}
      <div class="contribution-card__block">
        <h4>Gender Focus Level</h4>
        <p>{{#safeEmpty}}{{genderFocusLevel}}{{/safeEmpty}}</p>
        {{#if genderExplanation}}<p>{{#safeEmpty}}{{genderExplanation}}{{/safeEmpty}}</p>{{/if}}
      </div>
      {{/if}}
      {{#if youthFocusLevel}}
      <div class="contribution-card__block">
        <h4>Youth Focus Level</h4>
        <p>{{#safeEmpty}}{{youthFocusLevel}}{{/safeEmpty}}</p>
        {{#if youthExplanation}}<p>{{#safeEmpty}}{{youthExplanation}}{{/safeEmpty}}</p>{{/if}}
      </div>
      {{/if}}
      {{#if readinessScale}}
      <div class="contribution-card__block">
        <h4>Scaling Readiness</h4>
        {{#if readinessScale}}<p><strong>Readiness Scale:</strong> {{#safeEmpty}}{{readinessScale}}{{/safeEmpty}}</p>{{/if}}
        {{#if cheaperAlternatives}}<p><strong>Cheaper Alternatives:</strong> {{#safeEmpty}}{{cheaperAlternatives}}{{/safeEmpty}}</p>{{/if}}
        {{#if simplerUse}}<p><strong>Simpler Use:</strong> {{#safeEmpty}}{{simplerUse}}{{/safeEmpty}}</p>{{/if}}
        {{#if performBetter}}<p><strong>Perform Better:</strong> {{#safeEmpty}}{{performBetter}}{{/safeEmpty}}</p>{{/if}}
        {{#if innovationDesirable}}<p><strong>Innovation Desirable:</strong> {{#safeEmpty}}{{innovationDesirable}}{{/safeEmpty}}</p>{{/if}}
        {{#if innovationCommercially}}<p><strong>Innovation Commercially:</strong> {{#safeEmpty}}{{innovationCommercially}}{{/safeEmpty}}</p>{{/if}}
        {{#if innovationSupported}}<p><strong>Innovation Supported:</strong> {{#safeEmpty}}{{innovationSupported}}{{/safeEmpty}}</p>{{/if}}
        {{#if evidenceUptake}}<p><strong>Evidence Uptake:</strong> {{#safeEmpty}}{{evidenceUptake}}{{/safeEmpty}}</p>{{/if}}
      </div>
      {{/if}}
      {{#if showReasonKnowledgePotential}}
      <div class="contribution-card__block">
        <h4>How could it be adopted and scaled in other contexts? What would need to be adapted?</h4>
        <p>{{#safeEmpty}}{{reasonKnowledgePotential}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if knowledgeMethodsAndToolsNarrative}}
      <div class="contribution-card__block">
        <h4>Are there methods and tools that were developed to support the outreach and the use of this reported innovation?</h4>
        <p>{{#safeEmpty}}{{knowledgeMethodsAndToolsNarrative}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if areUsersDetermined}}
        {{#if hasActors}}
        <div class="contribution-card__block">
          <h4>Actors</h4>
          <ul class="contribution-list">
            {{#each actors}}
            <li>
              {{#if type}}<strong>{{#safeEmpty}}{{type}}{{/safeEmpty}}</strong>{{/if}}
              {{#if total}}<p>Total: {{#safeEmpty}}{{total}}{{/safeEmpty}}</p>{{/if}}
              {{#if other}}<p>Other: {{#safeEmpty}}{{other}}{{/safeEmpty}}</p>{{/if}}
              {{#if womenYouthNumber}}<p>Women Youth: {{#safeEmpty}}{{womenYouthNumber}}{{/safeEmpty}}</p>{{/if}}
              {{#if womenNonYouthNumber}}<p>Women Non-Youth: {{#safeEmpty}}{{womenNonYouthNumber}}{{/safeEmpty}}</p>{{/if}}
              {{#if menYouthNumber}}<p>Men Youth: {{#safeEmpty}}{{menYouthNumber}}{{/safeEmpty}}</p>{{/if}}
              {{#if menNonYouthNumber}}<p>Men Non-Youth: {{#safeEmpty}}{{menNonYouthNumber}}{{/safeEmpty}}</p>{{/if}}
            </li>
            {{/each}}
          </ul>
        </div>
        {{/if}}
      {{/if}}
      {{#if hasReferences}}
      <div class="contribution-card__block">
        <h4>Evidences</h4>
        <ul class="contribution-list">
          {{#each references}}
          <li>
            {{#if reference}}
              <strong>{{#safeEmpty}}{{reference}}{{/safeEmpty}}</strong>
              {{#if category}}<p>Category: {{#safeEmpty}}{{category}}{{/safeEmpty}}</p>{{/if}}
              {{#if subCategory}}<p>Sub-Category: {{#safeEmpty}}{{subCategory}}{{/safeEmpty}}</p>{{/if}}
            {{else}}
              {{#if deliverableId}}
                <strong>D{{#safeEmpty}}{{deliverableId}}{{/safeEmpty}}
                {{#if deliverableTitle}}<p>{{#safeEmpty}}{{deliverableTitle}}{{/safeEmpty}}</p>{{/if}}</strong>
              {{/if}}
            {{/if}}
            {{#if crossCuttingDimensions}}
              <p class="contribution-note">Cross-cutting Dimensions: {{#each crossCuttingDimensions}}{{#safeEmpty}}{{this}}{{/safeEmpty}}{{#unless @last}}, {{/unless}}{{/each}}</p>
            {{/if}}
          </li>
          {{/each}}
        </ul>
      </div>
      {{/if}}
      {{#if evidenceLink}}
      <div class="contribution-card__block">
        <h4>Evidence Link</h4>
        <p><a href="{{#safeEmpty}}{{evidenceLink}}{{/safeEmpty}}" target="_blank">{{#safeEmpty}}{{evidenceLink}}{{/safeEmpty}}</a></p>
      </div>
      {{/if}}
      {{#if contacts}}
      <div class="contribution-card__block">
        <h4>Contacts</h4>
        <p>{{#safeEmpty}}{{contacts}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if hasMilestones}}
      <div class="contribution-card__block">
        <h4>Milestones</h4>
        <ul class="contribution-list">
          {{#each milestones}}
          <li>
            {{#if name}}<strong>{{#safeEmpty}}{{name}}{{/safeEmpty}}</strong>{{/if}}
            {{#if primary}}<p class="contribution-note">Primary milestone</p>{{/if}}
          </li>
          {{/each}}
        </ul>
      </div>
      {{/if}}
    </div>
    {{/each}}
    {{^innovations}}
      <p>No innovations reported.</p>
    {{/innovations}}
  </section>

  <section class="section section--page-break">
    <h3>Activities</h3>
    {{#each activities}}
    <div class="contribution-card">
      <div class="contribution-card__header">
        <div>
          <span class="caption">Activity #{{#safeEmpty}}{{id}}{{/safeEmpty}}</span>
          {{#if activityTitle}}<h4>{{#safeEmpty}}{{activityTitle}}{{/safeEmpty}}</h4>{{/if}}
        </div>
        <div class="contribution-card__meta" style="margin-top: 8px;">
          {{#if status}}<span class="badge badge--soft">{{#safeEmpty}}{{status}}{{/safeEmpty}}</span>{{/if}}
        </div>
      </div>
      {{#if description}}
      <div class="contribution-card__block">
        <h4>Description</h4>
        <p>{{#safeEmpty}}{{description}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      <div class="grid grid--2 contribution-card__grid">
        {{#if startDate}}
        <div class="grid-item">
          <span>Start Date</span>
          <strong>{{#safeEmpty}}{{startDate}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if endDate}}
        <div class="grid-item">
          <span>End Date</span>
          <strong>{{#safeEmpty}}{{endDate}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if leader}}
        <div class="grid-item">
          <span>Activity Leader</span>
          <strong>{{#safeEmpty}}{{leader}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
        {{#if institution}}
        <div class="grid-item">
          <span>Institution</span>
          <strong>{{#safeEmpty}}{{institution}}{{/safeEmpty}}</strong>
        </div>
        {{/if}}
      </div>
      {{#if activityProgress}}
      <div class="contribution-card__block">
        <h4>Activity Progress</h4>
        <p>{{#safeEmpty}}{{activityProgress}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
      {{#if hasDeliverables}}
      <div class="contribution-card__block">
        <h4>Associated Deliverables</h4>
        <p>{{#safeEmpty}}{{deliverables}}{{/safeEmpty}}</p>
      </div>
      {{/if}}
    </div>
    {{/each}}
    {{^activities}}
      <p>No activities reported.</p>
    {{/activities}}
  </section>

  <p class="disclaimer">This report was generated automatically from the MARLO AICCRA environment. The content depends on the data that was available in the system at the time of generation.</p>
</div>
<div id="pageFooter">
  <span>Generated on {{#safeEmpty}}{{timeCreation}}{{/safeEmpty}} CET</span>
  <span>MARLO AICCRA Reporting</span>
</div>
</body>
</html>'
	WHERE id=1;
