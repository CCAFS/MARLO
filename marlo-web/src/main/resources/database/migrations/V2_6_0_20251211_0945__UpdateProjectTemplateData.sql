UPDATE report_configurations
SET project_template_data = '<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<title>AICCRA Project Summary</title>
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
  .meta-table td { padding: 12px; background: white; font-size: 13px; border-bottom: 1px solid #dfe6ee; color: #1F1F1F; }
  .meta-table tr:last-child td { border-bottom: none; }
  .badge { display: inline-block; padding: 5px 12px; border-radius: 999px; background: #e7f4ff; color: #0a4c70; font-weight: 600; margin-right: 10px; font-size: 11px; letter-spacing: 0.05em; }
  .tag { display: inline-block; padding: 2px 8px; border-radius: 999px; background: #f1f5f9; color: #0a4c70; font-size: 10px; margin-left: 6px; }
  .tag--muted { background: #e8edf3; color: #506b85; }
  .section { margin-top: 35px; }
  .section h3 { font-size: 14px; text-transform: uppercase; color: #0a4c70; letter-spacing: 0.12em; margin-bottom: 8px; }
  .section p { margin: 0; line-height: 1.6; color: #333333; font-size: 12px; }
  .grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; margin-top: 12px; }
  .grid--2 { grid-template-columns: repeat(2, minmax(220px, 1fr)); }
  .grid--3 { grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); }
  .grid-item { background: white; border-radius: 10px; padding: 16px; border: 1px solid #dfe6ee; min-height: 90px; box-shadow: 0 4px 8px rgba(10, 76, 112, 0.05); }
  .grid-item span { display: block; font-size: 10px; letter-spacing: 0.1em; color: #6a7a89; margin-bottom: 8px; text-transform: uppercase; }
  .grid-item strong { font-size: 13px; color: #1F1F1F; word-break: break-word; }
  .card { background: white; border-radius: 12px; padding: 18px; border: 1px solid #dfe6ee; box-shadow: 0 4px 12px rgba(10, 76, 112, 0.05); margin-top: 12px; }
  .card p { white-space: pre-line; }
  .list-card { background: white; border-radius: 12px; padding: 20px; border: 1px solid #dfe6ee; box-shadow: 0 4px 12px rgba(10, 76, 112, 0.05); }
  .list-card h4 { margin: 0 0 12px 0; font-size: 12px; letter-spacing: 0.08em; color: #6a7a89; text-transform: uppercase; }
  .list-card ul { list-style: none; padding: 0; margin: 0; }
  .list-card li { font-size: 12px; padding: 8px 0; border-bottom: 1px solid #eef2f7; white-space: pre-line; }
  .list-card li:last-child { border-bottom: none; }
  .list-card small { display: block; color: #6a7a89; font-size: 11px; letter-spacing: 0.05em; }
  .partner-card { background: white; border-radius: 14px; padding: 20px; border: 1px solid #dfe6ee; box-shadow: 0 6px 16px rgba(10, 76, 112, 0.06); margin-bottom: 18px; }
  .partner-card__header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; gap: 12px; }
  .partner-card__title { font-size: 15px; color: #0a4c70; }
  .partner-card__label { font-size: 10px; letter-spacing: 0.08em; text-transform: uppercase; color: #6a7a89; margin-bottom: 4px; display: block; }
  .partner-card__section { margin-top: 10px; }
  .partner-card__section ul { list-style: none; padding: 0; margin: 6px 0 0 0; }
  .partner-card__section li { padding: 6px 0; border-bottom: 1px solid #eef2f7; font-size: 12px; color: #1f1f1f; }
  .partner-card__section li:last-child { border-bottom: none; }
  .partner-card__section small { color: #6a7a89; font-size: 10px; display: inline-block; margin-left: 4px; }
  .partner-card__contacts { margin-top: 12px; border-top: 1px dashed #dfe6ee; padding-top: 12px; }
  .partner-card__contact { display: flex; flex-direction: column; margin-bottom: 10px; }
  .partner-card__contact strong { font-size: 13px; color: #1F1F1F; }
  .partner-card__contact small { color: #6a7a89; font-size: 10px; text-transform: uppercase; letter-spacing: 0.08em; }
  .partner-card__contact a, .partner-card__contact span { font-size: 11px; color: #0a4c70; text-decoration: none; word-break: break-all; }
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
  <span style="font-size: 12px; letter-spacing: 0.3em; color: #6a7a89;">PROJECT SUMMARY</span>
</div>
<div class="page">
  <section>
    <h1>AICCRA Project Summary</h1>
    <h2>{{#safeEmpty}}{{projectTitle}}{{/safeEmpty}}</h2>
    <p class="caption">Report generated on {{#safeEmpty}}{{timeCreation}}{{/safeEmpty}}</p>
    <table class="meta-table">
      <tr>
        <th>Project identifier</th>
        <td>{{#safeEmpty}}{{projectID}}{{/safeEmpty}}</td>
      </tr>
      <tr>
        <th>Phase identifier</th>
        <td>{{#safeEmpty}}{{phaseID}}{{/safeEmpty}}</td>
      </tr>
      <tr>
        <th>Reporting year</th>
        <td>{{#safeEmpty}}{{year}}{{/safeEmpty}}</td>
      </tr>
      <tr>
        <th>Reporting cycle</th>
        <td>{{#safeEmpty}}{{cycle}}{{/safeEmpty}}</td>
      </tr>
      <tr>
        <th>Logged center</th>
        <td>{{#safeEmpty}}{{loggedCenter}}{{/safeEmpty}}</td>
      </tr>
    </table>
  </section>

  <section class="section">
    <h3>Key badges</h3>
    <div>
      <span class="badge">Project {{#safeEmpty}}{{projectID}}{{/safeEmpty}}</span>
      <span class="badge">Phase {{#safeEmpty}}{{phaseID}}{{/safeEmpty}}</span>
      <span class="badge">Year {{#safeEmpty}}{{year}}{{/safeEmpty}}</span>
    </div>
  </section>

  <section class="section">
    <h3>Reporting context</h3>
    <div class="grid">
      <div class="grid-item">
        <span>Project title</span>
        <strong>{{#safeEmpty}}{{projectTitle}}{{/safeEmpty}}</strong>
      </div>
      <div class="grid-item">
        <span>Cycle</span>
        <strong>{{#safeEmpty}}{{cycle}}{{/safeEmpty}}</strong>
      </div>
      <div class="grid-item">
        <span>Logged center</span>
        <strong>{{#safeEmpty}}{{loggedCenter}}{{/safeEmpty}}</strong>
      </div>
    </div>
    <div class="grid">
      <div class="grid-item">
        <span>Phase identifier</span>
        <strong>{{#safeEmpty}}{{phaseID}}{{/safeEmpty}}</strong>
      </div>
      <div class="grid-item">
        <span>Year</span>
        <strong>{{#safeEmpty}}{{year}}{{/safeEmpty}}</strong>
      </div>
      <div class="grid-item">
        <span>Generated on</span>
        <strong>{{#safeEmpty}}{{timeCreation}}{{/safeEmpty}}</strong>
      </div>
    </div>
    <div class="grid grid--2">
      <div class="grid-item">
        <span>Flagship focus</span>
        {{#flagshipsSummary}}
          <strong>{{#safeEmpty}}{{.}}{{/safeEmpty}}</strong>
        {{/flagshipsSummary}}
        {{^flagshipsSummary}}
          <strong>No flagships reported</strong>
        {{/flagshipsSummary}}
      </div>
      <div class="grid-item">
        <span>Regional focus</span>
        {{#hasRegions}}
          {{#regionsSummary}}
            <strong>{{#safeEmpty}}{{.}}{{/safeEmpty}}</strong>
          {{/regionsSummary}}
          {{^regionsSummary}}
            <strong>No regions reported</strong>
          {{/regionsSummary}}
        {{/hasRegions}}
        {{^hasRegions}}
          <strong>Not applicable</strong>
        {{/hasRegions}}
      </div>
      <div class="grid-item">
        <span>Cluster activities</span>
        {{#clusterActivitiesSummary}}
          <strong>{{#safeEmpty}}{{.}}{{/safeEmpty}}</strong>
        {{/clusterActivitiesSummary}}
        {{^clusterActivitiesSummary}}
          <strong>No cluster activities reported</strong>
        {{/clusterActivitiesSummary}}
      </div>
    </div>
  </section>

  {{#projectDescription}}
  <section class="section">
    <h3>Project timeline & contacts</h3>
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
        <span>Project type</span>
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
    <h3>Project summary</h3>
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
    <h3>Cross-cutting focus</h3>
    <div class="card">
      {{#crossCutting}}
        <p>{{{crossCutting}}}</p>
      {{/crossCutting}}
      {{^crossCutting}}
        <p>No cross-cutting focus reported.</p>
      {{/crossCutting}}
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
    <h3>Flagships, regions & clusters</h3>
    <div class="grid grid--3">
      <div class="list-card">
        <h4>Flagships</h4>
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
          <li>No flagships reported.</li>
          {{/flagships}}
        </ul>
      </div>
      {{#hasRegions}}
      <div class="list-card">
        <h4>Regions</h4>
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
          <li>No regions reported.</li>
          {{/regions}}
        </ul>
      </div>
      {{/hasRegions}}
      {{^hasRegions}}
      <div class="list-card">
        <h4>Regions</h4>
        <ul>
          <li>Not applicable for this project.</li>
        </ul>
      </div>
      {{/hasRegions}}
      <div class="list-card">
        <h4>Cluster activities</h4>
        <ul>
          {{#clusterActivities}}
          <li>
            <strong>
              {{#name}}{{#safeEmpty}}{{.}}{{/safeEmpty}}{{/name}}
              {{^name}}{{#identifier}}{{#safeEmpty}}{{.}}{{/safeEmpty}}{{/identifier}}{{/name}}
            </strong>
            {{#identifier}}<small>{{#safeEmpty}}{{.}}{{/safeEmpty}}</small>{{/identifier}}
          </li>
          {{/clusterActivities}}
          {{^clusterActivities}}
          <li>No cluster activities reported.</li>
          {{/clusterActivities}}
        </ul>
      </div>
    </div>
  </section>
  {{/projectDescription}}

  <section class="section">
    <h3>Project partners</h3>
    {{#projectPartners}}
    <div class="partner-card">
      <div class="partner-card__header">
        <div class="partner-card__title">{{#safeEmpty}}{{institutionName}}{{/safeEmpty}}</div>
        {{#institutionAcronym}}<span class="tag">{{#safeEmpty}}{{institutionAcronym}}{{/safeEmpty}}</span>{{/institutionAcronym}}
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
            {{#country}}<small>{{#safeEmpty}}{{country}}{{/safeEmpty}}{{#isoCode}} ({{#safeEmpty}}{{isoCode}}{{/safeEmpty}}){{/isoCode}}</small>{{/country}}
            {{#city}}<small>{{#safeEmpty}}{{city}}{{/safeEmpty}}</small>{{/city}}
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
          {{#role}}<small>{{#safeEmpty}}{{.}}{{/safeEmpty}}</small>{{/role}}
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

  <section class="section">
    <h3>Next steps</h3>
    <p>This layout mirrors the style used across the innovation and study summaries. As additional project level data points become available they can be appended as new rows or cards without altering the base structure.</p>
  </section>

  <p class="disclaimer">This report was generated automatically from the MARLO AICCRA environment. The content depends on the data that was available in the system at the time of generation.</p>
</div>
<div id="pageFooter">
  <span>Generated on {{#safeEmpty}}{{timeCreation}}{{/safeEmpty}} CET</span>
  <span>AICCRA MARLO Reporting</span>
</div>
</body>
</html>'
WHERE id = 1;
