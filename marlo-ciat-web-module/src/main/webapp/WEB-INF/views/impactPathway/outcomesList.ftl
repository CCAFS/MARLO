[#ftl]
[#assign title = "Outcomes List" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${programID}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs","select2"] /]
[#assign customJS = ["${baseUrl}/js/global/usersManagement.js", "${baseUrl}/js/impactPathway/outcomeList.js", "${baseUrl}/js/global/fieldsValidation.js"] /]
[#assign customCSS = ["${baseUrl}/css/global/customDataTable.css","${baseUrl}/css/impactPathway/outcomeList.css"] /]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "outcomes" /]

[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"", "action":"topics"},
  {"label":"outcomesList", "nameSpace":"", "action":""}
]/]
[#assign leadersName = "leaders"/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#import "/WEB-INF/views/impactPathway/outcomeListTemplate.ftl" as outcomesList /]
[#--  Research Otcomes Help Text--] 
[@utils.helpInfos hlpInfo="researchOutcomesList.help" /]
[#--  marlo cluster of activities--]
<section class="marlo-content">
  <div class="container"> 
    [#if researchAreas?has_content]
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/views/impactPathway/messages-impactPathway.ftl" /]
        [#-- Impact pathway sub menu --]
        [#include "/WEB-INF/views/impactPathway/submenu-impactPathway.ftl" /]
        
          [#-- Program Title --]
          <div class="col-md-12">
            <h3 class="subTitle headTitle outcomeListTitle">${selectedProgram.name} - Outcomes</h3>
            <hr />
          </div><div class="clearfix"></div>
        
          [#-- Hidden Parameters --]
          <input type="hidden" name="programID" value="${programID}" />
        
        [#if researchTopics?has_content]
        
        <span id="programSelected" class="hidden">${selectedProgram.id}</span>
        
        
        <div class="simpleBox col-md-12">
            <label for="">Research Topic:<span class="red">*</span></label>
            <select name="researchTopics" id="researchTopics">
              <option value="-1" >View All</option>
              
                [#list researchTopics as researchTopic]
                  <option value="${researchTopic.id}"[#if (selectedResearchTopic.id)?has_content && (selectedResearchTopic.id== researchTopic.id)] selected="selected"[/#if]] >${researchTopic.researchTopic}</option>
                [/#list]
               
            </select>            
          </div>
          
        [@s.form action=actionName enctype="multipart/form-data" ]
        
          
          [#-- Outcomes Table --]
          [#if outcomes?has_content]
          <div style="">[@outcomesList.outcomesList outcomes=outcomes canValidate=true canEdit=editable namespace="/impactPathway" defaultAction="${(centerSession)!}/outcomes"/]</div>
          [#else]
            [#if selectedResearchTopic?has_content] 
            <div class="clearfix"></div>
            <div class="notOutcome">
            There are NO OUTCOMES added to "<b>${selectedResearchTopic.researchTopic}</b>" as of yet. [#if editable] If you want to add a new outcome, please click on the button below: [/#if]
            </div>
            [#else]
            <div class="clearfix"></div>
            <div class="notOutcome">
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
              <div class="addOutcome button-blue"><a  href="[@s.url namespace="/${currentSection}" action='${(centerSession)!}/addNewOutcome'] [@s.param name="programID"]${selectedProgram.id}[/@s.param] [@s.param name="topicID"]${selectedResearchTopic.id}[/@s.param][/@s.url]">
                <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addOutcome" /]
              </a></div>
              </div>
              [#else]
              <div class="text-right">
                In order to add a new outcome, please select a specific "Research Topic" from the drop-down list above.
              </div>
              [/#if]
            [#else]
              [#if selectedResearchTopic?has_content] 
              <div class="text-center">
              <div class="addOutcome button-blue"><a  href="[@s.url namespace="/${currentSection}" action='${(centerSession)!}/addNewOutcome'] [@s.param name="programID"]${selectedProgram.id}[/@s.param] [@s.param name="topicID"]${selectedResearchTopic.id}[/@s.param][/@s.url]">
                <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addOutcome" /]
              </a></div>
              </div>
              [#else]
              <div class="text-right">
                In order to add a new outcome, please select a specific "Research Topic" from the drop-down list above.
              </div>
              [/#if]
            [/#if]
         [/#if]          
        [/@s.form]
        [#else]
         <p class="text-center borderBox inf">Before completing this section, please add at least one Research Topic by <a href="[@s.url action='${centerSession}/researchTopics'][@s.param name="programID" value=programID /][@s.param name="edit" value="true"/][/@s.url]">clicking here</a></p> 
        [/#if]
      </div>
    </div>
    [#else]
     <p class="text-center borderBox inf">Program impacts are not available in for the selected research area</p>
    [/#if]
  </div>
</section>

[@customForm.confirmJustification action="deleteOutcome.do" namespace="/${currentSection}" title="Remove Outcome" /]

[#include "/WEB-INF/global/pages/footer.ftl" /]


