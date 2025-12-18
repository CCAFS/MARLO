--  Auto-generated SQL script #202512181000
-- Add page-break CSS class and apply it to specific cluster sections
UPDATE report_configurations
SET project_template_data = REPLACE(
  REPLACE(
    REPLACE(
      REPLACE(
        REPLACE(
          project_template_data,
          '  .section { margin-top: 35px; }',
          '  .section { margin-top: 35px; }
  .section--page-break { page-break-before: always; }'
        ),
        '  {{#projectDescription}}
  <section class="section">
    <h3>Cluster Information</h3>',
        '  {{#projectDescription}}
  <section class="section section--page-break">
    <h3>Cluster Information</h3>'
      ),
      '  <br>
  <section class="section">
    <h3>Cluster partners</h3>',
      '  <br>
  <section class="section section--page-break">
    <h3>Cluster partners</h3>'
    ),
    '  {{#projectLocations}}
  <section class="section">
    <h3>Cluster locations</h3>',
    '  {{#projectLocations}}
  <section class="section section--page-break">
    <h3>Cluster locations</h3>'
  ),
  '  <br>
  <section class="section">
    <h3>Contribution to performance indicators</h3>',
  '  <br>
  <section class="section section--page-break">
    <h3>Contribution to performance indicators</h3>'
)
WHERE id = 1;

