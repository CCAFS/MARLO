[#ftl]
[#assign title = "Outputs List" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${(crpProgramID)!}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs","select2"] /]
[#assign customJS = [
  "${baseUrl}/global/js/usersManagement.js",
  "${baseUrlMedia}/js/impactPathway/centerOutputList.js", 
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = [
  "${baseUrl}/global/css/customDataTable.css",
  "${baseUrlMedia}/css/impactPathway/outputList.css"
  ] 
/]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "outputs" /]

[#assign breadCrumb = [
  {"label":"centerImpactPathway", "nameSpace":"", "action":"topics"},
  {"label":"outputsList", "nameSpace":"", "action":""}
]/]
[#assign leadersName = "leaders"/]
[#assign editable = false /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#import "/WEB-INF/center/views/impactPathway/outputListTemplate.ftl" as outputsList /]
[#--  Research Outputs Help Text--] 
[@utils.helpInfos hlpInfo="researchOutputsList.help" /]
[#--  marlo cluster of activities--]
<section class="marlo-content">
  <div class="container"> 
    [#if researchAreas?has_content]
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Impact pathway sub menu --]
        [#include "/WEB-INF/center/views/impactPathway/submenu-impactPathway.ftl" /]
        [#-- Section Messages --]
        [#include "/WEB-INF/center/views/impactPathway/messages-impactPathway.ftl" /]
        
        [#-- Program Title --]
        <h3 class="subTitle headTitle outcomeListTitle">${selectedProgram.name} - Outputs</h3>
        <hr />

          
        [#-- Hidden Parameters --]
        <input type="hidden" name="programID" value="${crpProgramID}" />
        
        <span id="programSelected" class="hidden">${selectedProgram.id}</span>
          [@s.form action=actionName enctype="multipart/form-data" ]
            [#-- Output Table --]
            [#if outputs?has_content]
            <div style="">[@outputsList.outputsList outputs=outputs canValidate=true canEdit=canEdit namespace="/impactPathway" defaultAction="${(centerSession)!}/centerOutput"/]</div>
            [#else]
              [#if selectedResearchTopic?has_content]
              <div class="clearfix"></div>
              <div class="notOutcome">
                There are NO OUTPUTS added to "<b>${selectedResearchOutcome.description}</b>" as of yet. [#if canEdit] If you want to add a new outcome, please click on the button below: [/#if]
              </div>
              [#else]
              <div class="clearfix"></div>
              <div class="notOutcome">
                There are NO OUTPUTS added to "<b>${selectedProgram.name}</b>" as of yet.
              </div>
              [/#if]
            [/#if]
            <br>
            [#-- Add Outcome button --]
            [#if canEdit]
              <div class="text-right">
                <div class="addOutcome button-blue"><a  href="[@s.url namespace="/${currentSection}" action='${(centerSession)!}/addNewOutput'] [@s.param name="crpProgramID"]${selectedProgram.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                  <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addOutput" /]
                </a></div>
              </div>
            [/#if]
          [/@s.form]

      </div>
    </div>
    [#else]
     <p class="text-center borderBox inf">Program impacts are not available in for the selected research area</p>
    [/#if]
  </div>
</section>

[@customForm.confirmJustificationOut action="deleteOutput.do" namespace="/${currentSection}" title="Remove Output" /]

[#include "/WEB-INF/global/pages/footer.ftl" /]


