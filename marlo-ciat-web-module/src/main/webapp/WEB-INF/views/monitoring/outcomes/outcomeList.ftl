[#ftl]
[#assign title = "MiLE outcomes monitoring" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2","datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrl}/js/monitoring/outcomes/outcomesList.js" ] /]
[#assign customCSS = ["${baseUrl}/css/global/customDataTable.css","${baseUrl}/css/impactPathway/outcomes.css"] /]
[#assign currentSection = "outcomes" /]


[#assign breadCrumb = [
  {"label":"outcomesList", "nameSpace":"/monitoring", "action":"${(centerSession)!}/monitoringOutcomesList"}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#import "/WEB-INF/views/impactPathway/outcomeListTemplate.ftl" as outcomesListMonitoring /]
[#-- Help text --]
<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.png" />
    <p class="col-md-10"> [@s.text name="monitoring.outcomeList.help"][/@s.text] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
<span id="programSelected" class="hidden">${selectedProgram.id}</span>

<section class="container">
  <article class="row" id="mainInformation">
    <div class="col-md-12">
      [#include "/WEB-INF/views/monitoring/outcomes/submenu-outcomes.ftl" /]
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
[#include "/WEB-INF/global/macros/outcomeProjectsPopup.ftl" /]

[#include "/WEB-INF/global/pages/footer.ftl"]
