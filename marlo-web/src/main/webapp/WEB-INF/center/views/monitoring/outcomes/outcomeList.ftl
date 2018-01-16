[#ftl]
[#assign title = "MARLO - ${centerSession} - outcomes monitoring" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2","datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/monitoring/outcomes/outcomesList.js" 
  ] 
/]
[#assign customCSS = [
  "${baseUrl}/global/css/customDataTable.css",
  "${baseUrlMedia}/css/impactPathway/outcomes.css"
  ] 
/]
[#assign currentSection = "outcomes" /]


[#assign breadCrumb = [
  {"label":"outcomesList", "nameSpace":"/monitoring", "action":"${(centerSession)!}/monitoringOutcomesList"}
]/]

[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]
[#import "/WEB-INF/center/views/impactPathway/outcomeListTemplate.ftl" as outcomesListMonitoring /]
[#-- Help text --]
<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />
    <p class="col-md-10"> [@s.text name="monitoring.outcomeList.help"][/@s.text] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
<span id="programSelected" class="hidden">${selectedProgram.id}</span>

<section class="container">
  <article class="row" id="mainInformation">
    <div class="col-md-12">
      [#include "/WEB-INF/center/views/monitoring/outcomes/submenu-outcomes.ftl" /]
      [#-- Outcomes List --]
      <h3 class="headTitle text-center">${selectedProgram.name} - Outcomes</h3>
      <div class="simpleBox col-md-12">
        <label for="">Research Topic:<span class="red">*</span></label>
        <select name="researchTopics" id="researchTopics">
          <option value="-1" >View All</option>
            [#list researchTopics as researchTopic]
              <option value="${researchTopic.id}"[#if (selectedResearchTopic.id)?has_content && (selectedResearchTopic.id== researchTopic.id)] selected="selected"[/#if]] >${researchTopic.researchTopic}</option>
            [/#list]
        </select>
      </div>
      <div class="loadingBlock"></div>
      <div style="display:none">[@outcomesListMonitoring.outcomesListMonitoring outcomes=outcomes canValidate=true canEdit=editable namespace="/monitoring" defaultAction="${(centerSession)!}/monitoringOutcome" /]</div>
      <div class="clearfix"></div>
    </div>
    
  </article>
</section>

[#-- Outcome Projects Popup --]
[#include "/WEB-INF/center/macros/outcomeProjectsPopup-center.ftl" /]

[#include "/WEB-INF/center/pages/footer.ftl"]
