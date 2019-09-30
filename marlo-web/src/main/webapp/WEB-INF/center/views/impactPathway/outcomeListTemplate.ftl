[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

[#macro outcomesList outcomes={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction=""]
  <table class="outcomeList" id="outcomes">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="programImpact.outcomeList.idTable" /]</th>
        <th id="outcomeTitles" >[@s.text name="programImpact.outcomeList.statement" /]</th>
        <th id="outcomeTargetYear">[@s.text name="programImpact.outcomeList.targetYear" /]</th>
        <th id="outcomeRF">[@s.text name="programImpact.outcomeList.requiredFields" /]</th>
        <th id="outcomeDelete">[@s.text name="programImpact.outcomeList.delete" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if outcomes?has_content]
      [#list outcomes as outcome]
        <tr>
          [#-- ID --]
          <td class="outcomeId">
            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='outcomeID']${outcome.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">OC${outcome.id}</a>
          </td>
          [#-- Outcome statement --]
          <td class="left"> 
            [#if outcome.description?has_content]
              <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='outcomeID']${outcome.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" title="${outcome.description}">
              [#if outcome.description?length < 120] ${outcome.description}</a> [#else] [@utilities.wordCutter string=outcome.description maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='outcomeID']${outcome.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url] ">
                [@s.text name="programImpact.outcomeList.title.none" /]
              </a>
            [/#if]
          </td>
          [#-- Outcome Year --]
          <td class="text-center">
            [#if outcome.targetYear == -1 ]none[#else]${(outcome.targetYear)!'none'}[/#if]
          </td>
          [#-- Outcome required fields --]
          <td class="text-center">
            [#if action.getCenterOutcomeStatus(outcome.id, actionNameSimple)??]
              [#if !((action.getCenterOutcomeStatus(outcome.id, actionNameSimple)).missingFields)?has_content]
                <span class="icon-20 icon-check" title="Complete"></span>
              [#else]
                <span class="icon-20 icon-uncheck" title=""></span> 
              [/#if]
            [#else]
              <span class="icon-20 icon-uncheck" title=""></span>
            [/#if]
          </td>
          [#-- Delete Outcome--]
          <td class="text-center">
            [#--  ${action.outcomeCanBeDeleted(outcome.id)?string} --]
            [#if canEdit && action.outcomeCanBeDeleted(outcome.id)!false]
              <a id="removeOutcome-${outcome.id}" class="removeOutcome" href="#" title="">
                <img src="${baseUrlCdn}/global/images/trash.png" title="[@s.text name="programImpact.outcomeList.removeOutcome" /]" /> 
              </a>
            [#else]
              <img src="${baseUrlCdn}/global/images/trash_disable.png" title="[@s.text name="programImpact.outcomeList.cannotDelete" /]" />
            [/#if]
          </td>          
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]

[#macro outcomesListMonitoring outcomes={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction=""]
  <table class="outcomeListMonitoring" id="outcomesListM">
    <thead>
      <tr class="subHeader">
        <th id="idsM">[@s.text name="programImpact.outcomeList.idTable" /]</th>
        <th id="outcomeTitlesM" >[@s.text name="programImpact.outcomeList.statement" /]</th>
        <th id="outcomeTargetYearM">[@s.text name="programImpact.outcomeList.targetYear" /]</th>
        <th id="statusM">[@s.text name="outcomeList.status" /]</th>
        <th id="projectsLink">Contributions</th>
      </tr>
    </thead>
    <tbody>
    [#if outcomes?has_content]
      [#list outcomes as outcome]
        <tr>
        [#-- ID --]
        <td class="outcomeId">
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='outcomeID']${outcome.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">OC${outcome.id}</a>
        </td>
          [#-- outcome statement --]
          <td class="left"> 
            [#if outcome.description?has_content]
                <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='outcomeID']${outcome.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" title="${outcome.description}">
                [#if outcome.description?length < 120] ${outcome.description}</a> [#else] [@utilities.wordCutter string=outcome.description maxPos=120 /]...</a> [/#if]
            [#else]
                <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='outcomeID']${outcome.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url] ">
                  [@s.text name="programImpact.outcomeList.title.none" /]
                </a>
            [/#if]
          </td>
          [#-- outcome Year --]
          <td class="text-center">
          [#if outcome.targetYear== -1 ]
          none
          [#else]
          ${(outcome.targetYear)!'none'}
          [/#if]
          [#-- Status --]
          <td class="text-center">
            [#if action.isCompleteOutcome(outcome.id)]
              <span class="icon-20 icon-check" title=""></span> 
            [#else]
              <span class="icon-20 icon-uncheck" title=""></span> 
            [/#if] 
          </td>
          <td class="text-center">
            <button type="button" class="btn btn-default btn-xs disabled outcomeProjects-${outcome.id}" data-toggle="modal" data-target="#outcomeProjectsModal">
              <span class="glyphicon glyphicon-pushpin"></span> Projects
            </button> 
          </td>
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]