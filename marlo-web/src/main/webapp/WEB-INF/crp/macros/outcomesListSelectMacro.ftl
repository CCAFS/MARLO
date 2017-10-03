[#ftl]
[#if outcomes??]
<div style="display:none">
  [#list outcomes as outcome]
  <span class="outcomeID-${outcome.id}">
    <p>
      <strong>${outcome.crpProgram.acronym} Outcome:</strong> ${outcome.description}
      [#if action.hasSpecificities('crp_ip_outcome_indicator')]
      <i class="indicatorText"><br /><strong>Indicator: </strong>${(outcome.indicator)!'No Indicator'}</i>
      [/#if]
    </p>
  </span>
  [/#list]
</div>
[/#if]