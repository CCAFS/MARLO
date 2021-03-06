[#ftl]
[#assign title = "Outcomes List" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${crpProgramID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs","select2"] /]
[#assign customJS = [
  "${baseUrlCdn}/global/js/usersManagement.js", 
  "${baseUrlCdn}/global/js/fieldsValidation.js",
  "${baseUrlMedia}/js/impactPathway/centerOutcomeList.js?20181203"
  ] 
/]
[#assign customCSS = [
  "${baseUrlCdn}/global/css/customDataTable.css"
  ]
/]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "outcomes" /]

[#assign breadCrumb = [
  {"label":"centerImpactPathway", "nameSpace":"", "action":"topics"},
  {"label":"outcomesList", "nameSpace":"", "action":""}
]/]
[#assign leadersName = "leaders"/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#import "/WEB-INF/center/views/impactPathway/outcomeListTemplate.ftl" as outcomesList /]

[#--  Research Otcomes Help Text--] 
[@utils.helpInfos hlpInfo="researchOutcomesList.help" /]
[#--  marlo cluster of activities--]
<section class="marlo-content">
  <div class="container"> 
    [#if researchAreas?has_content]
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/center/views/impactPathway/messages-impactPathway.ftl" /]
        [#-- Impact pathway sub menu --]
        [#include "/WEB-INF/center/views/impactPathway/submenu-impactPathway.ftl" /]
        
        [#-- Program Title --]
        <h3 class="subTitle headTitle outcomeListTitle">${selectedProgram.name} - Outcomes</h3>
        <hr />
    
        [#-- Hidden Parameters --]
        <input type="hidden" name="programID" value="${crpProgramID}" />
        
        [#if researchTopics?has_content]
        
        <span id="programSelected" class="hidden">${selectedProgram.id}</span>
        
        
        <div class="simpleBox form-group">
          <label for="">Research Topic:<span class="red">*</span></label>
          <select name="researchTopics" id="researchTopics" class="form-control ">
            <option value="-1" >View All</option>
            [#list researchTopics as researchTopic]
              <option value="${researchTopic.id}"[#if (selectedResearchTopic.id)?has_content && (selectedResearchTopic.id== researchTopic.id)] selected="selected"[/#if]] >${researchTopic.researchTopic}</option>
            [/#list]
          </select>
        </div>
        <br />
          
        [@s.form action=actionName enctype="multipart/form-data" ]
        
          
          [#-- Outcomes Table --]
          [#if outcomes?has_content]
          <div style="">[@outcomesList.outcomesList outcomes=outcomes canValidate=true canEdit=editable namespace="/impactPathway" defaultAction="${(centerSession)!}/centerOutcome"/]</div>
          [#else]
            [#if selectedResearchTopic?has_content] 
            <div class="clearfix"></div>
            <div class="notOutcome grayBox text-center">
              There are NO OUTCOMES added to "<b>${selectedResearchTopic.researchTopic}</b>" as of yet. [#if editable] If you want to add a new outcome, please click on the button below: [/#if]
            </div>
            [#else]
            <div class="clearfix"></div>
            <div class="notOutcome grayBox text-center">
              There are NO OUTCOMES added to "<b>${selectedProgram.name}</b>" as of yet.
            </div>
            [/#if]
          [/#if]
          <br>
          [#-- Add Outcome button --]
          [#if editable]            
            [#if outcomes?has_content]
              [#if selectedResearchTopic?has_content] 
              <div class="text-right">
              <div class="addOutcome button-blue"><a  href="[@s.url namespace="/${currentSection}" action='${(centerSession)!}/addNewCenterOutcome'] [@s.param name="crpProgramID"]${selectedProgram.id}[/@s.param] [@s.param name="topicID"]${selectedResearchTopic.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addOutcome" /]
              </a></div>
              </div>
              [#else]
              <div class="text-right">
                [@s.text name="programImpact.outcomeList.allTopics" /]
              </div>
              [/#if]
            [#else]
              [#if selectedResearchTopic?has_content] 
                <div class="text-center">
                <div class="addOutcome button-blue"><a  href="[@s.url namespace="/${currentSection}" action='${(centerSession)!}/addNewCenterOutcome'] [@s.param name="crpProgramID"]${selectedProgram.id}[/@s.param] [@s.param name="topicID"]${selectedResearchTopic.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                  <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addOutcome" /]
                </a></div>
                </div>
              [#else]
                <div class="note text-right">
                 [@s.text name="programImpact.outcomeList.allTopics" /]
                </div>
              [/#if]
            [/#if]
         [/#if]          
        [/@s.form]
        [#else]
         <p class="text-center borderBox inf">Before completing this section, please add at least one Research Topic by <a href="[@s.url action='${centerSession}/researchTopics'][@s.param name="crpProgramID" value=programID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">clicking here</a></p> 
        [/#if]
      </div>
    </div>
    [#else]
     <p class="text-center borderBox inf">Program impacts are not available in for the selected research area</p>
    [/#if]
  </div>
</section>

[@customForm.confirmJustificationOutcome action="deleteCenterOutcome.do" namespace="/${currentSection}" title="Remove Outcome" /]

[#include "/WEB-INF/global/pages/footer.ftl" /]


