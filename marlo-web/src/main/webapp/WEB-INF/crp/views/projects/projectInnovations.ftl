[#macro innovationsTableMacro]
  <table class="table-innovations table-border-powb" id="table-innovations">
    <thead>
      <tr class="subHeader">
        <th id="tb-id" width="9%">[@s.text name="projectInnovations.table.id" /]</th>
        <th id="tb-title" width="43%">[@s.text name="projectInnovations.table.title" /]</th>
        <th id="tb-type" width="22%">[@s.text name="projectInnovations.table.type" /]</th>
        <th id="tb-stage" width="13%">[@s.text name="projectInnovations.table.stage" /]</th>
        <th id="tb-year" width="9%">[@s.text name="projectInnovations.table.year" /]</th>
        <th id="tb-remove" width="4%"></th>
      </tr>
    </thead>
    <tbody>
    [#if flagshipPlannedList?has_content]
      [#list flagshipPlannedList as flagshipPlanned]
        [#local tsURL][@s.url namespace="/projects" action="${(crpSession)!}/expectedStudies"][@s.param name='projectID']${(flagshipPlanned.projectExpectedStudy.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
          [#-- ID --]
          <td class="tb-id text-center">
            <a href="${tsURL}" target="_blank">${(flagshipPlanned.projectExpectedStudy.project.id)!}</a>
          </td>
          [#-- Title --]
          <td class="tb-title">
            [#if flagshipPlanned.projectExpectedStudy.topicStudy?has_content]
                <span>${(flagshipPlanned.projectExpectedStudy.topicStudy)!''}</span>
            [#else]
                <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
            [/#if]
          </td>
          [#-- Type --]
          <td>
          [#if flagshipPlanned.projectExpectedStudy.topicStudy?has_content]
            <span>${(flagshipPlanned.projectExpectedStudy.topicStudy)!''}</span>
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Stage --]
          <td class="text-center">
          [#if flagshipPlanned.projectExpectedStudy.scopeName?has_content]
            ${flagshipPlanned.projectExpectedStudy.scopeName!''}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Stage --]
          <td class="text-center">
          [#if flagshipPlanned.projectExpectedStudy.scopeName?has_content]
            ${flagshipPlanned.projectExpectedStudy.scopeName!''}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Remove --]
          <td class="remove" title="${(flagshipPlanned.projectExpectedStudy.comments)!''}">
            [#if canEdit && isProjectNew && action.deletePermission(project.id) && action.getActualPhase().editable ]
              <a id="remove-innovation" class="removeInnovation" href="#" title="">
                <img src="${baseUrl}/global/images/trash.png" title="[@s.text name="projectsList.deleteProject" /]" /> 
              </a>
            [#else]
              <img src="${baseUrl}/global/images/trash_disable.png" title="[@s.text name="projectsList.cantDeleteProject" /]" />
            [/#if]
          </td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="6">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]