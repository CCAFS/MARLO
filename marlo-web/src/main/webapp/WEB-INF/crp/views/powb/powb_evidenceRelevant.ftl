[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${powbSynthesisID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = ["${baseUrlMedia}/js/powb/powb_evidenceRelevant.js"] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "evidenceRelevant" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"evidenceRelevant", "nameSpace":"powb", "action":"${crpSession}/evidenceRelevant"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="evidenceRelevant.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/powb/submenu-powb.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/powb/menu-powb.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/powb/messages-powb.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="evidenceRelevant.title" /]</h3>
        <div class="borderBox">
          [#-- Provide a short narrative for any outcome --]
          [#if PMU]
          <div class="form-group margin-panel">
            [#-- Change display=true for display=PMU to show just for PMU --]
            [@customForm.textArea name="powbSynthesis.powbEvidence.narrative" i18nkey="evidenceRelevant.narrative" help="evidenceRelevant.help" display=true required=true className="limitWords-100" paramText="${(actualPhase.year)!}" editable=editable /]
          </div>
          [/#if]
          
          [#-- Table B: Flagships planned Studies for Relevant Outcomes and Impacts --]
          [#if PMU]
          <div class="form-group margin-panel">
            <h4 class="subTitle headTitle">[@s.text name="evidenceRelevant.table.title" /]</h4>
            <hr />
            [@tableBMacro /]
          </div>
          [/#if]
          
          [#-- Planned Studies for Relevant Outcomes and Impacts --]
          [#if Flagship]
          <div class="form-group margin-panel">
            <div class="evidence-plannedStudies-header row">
              <h4 class="subTitle headTitle col-md-9">[@s.text name="evidenceRelevant.plannedStudies" /]</h4>
            </div>
            [#-- Project planned studies (Table) --]
            [@tablePlannedStudiesMacro/]
          </div>
          [/#if]
          
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>

[#include "/WEB-INF/crp/pages/footer.ftl"]

[#macro tablePlannedStudiesMacro ]
  <table class="table-plannedStudies table-border-powb" id="table-plannedStudies">
    <thead>
      <tr class="subHeader">
        <th id="tb-plannedTopic" width="27%">[@s.text name="evidenceRelevant.tablePlannedStudies.plannedTopic" /]</th>
        <th id="tb-projectId" width="11%">[@s.text name="evidenceRelevant.tablePlannedStudies.projectId" /]</th>
        <th id="tb-geographicScope" width="15%">[@s.text name="evidenceRelevant.tablePlannedStudies.geographicScope" /]</th>
        <th id="tb-relevant" width="28%">[@s.text name="evidenceRelevant.tablePlannedStudies.relevant" /]</th>
        <th id="tb-comments" width="19%">[@s.text name="evidenceRelevant.tablePlannedStudies.comments" /]</th>
        <th id="tb-checkbox" width="0%">Checkbox</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#-- <div class="loading clustersBlock" style="display:none"></div> --]
    [#if popUpProjects?has_content]
      [#list popUpProjects as popUp]
        <tr>
          [#-- Planned topic of study --]
          <td>
            ${(popUp.topicStudy)!''}
          </td>
          <td class="tb-projectId text-center">
            P${(popUp.project.id)!''}
          </td>
          [#-- Geographic scope --]
          <td class="text-center">
            ${popUp.scopeName}
          </td>
          [#-- Relevant to Sub-IDO, or SRF target if appropiate --]
          <td class="relevantSubIDO">
            <ul>  
              <li title="${(popUp.srfSubIdo.description)!''}">[@utilities.wordCutter string="${(popUp.srfSubIdo.description)!''}" maxPos=50 /]</li>
              [#if popUp.srfSloIndicator?has_content]
              <li title="${(popUp.srfSloIndicator.title)!''}">[@utilities.wordCutter string="${(popUp.srfSloIndicator.title)!''}" maxPos=50 /]</li>
              [/#if]
            </ul>
          </td>
          [#-- Comments --]
          <td class="comments" title="${(popUp.comments)!''}"> 
            [@utilities.wordCutter string="${(popUp.comments)!''}" maxPos=100 /]
          </td>
          [#-- Checkbox --]
          <td class="plannedStudiesCheckbox text-center">
            [@customForm.checkBoxFlat id="${(popUp.project.id)!''}" name="${(popUp.topicStudy)!''}" value="${(popUp.project.id)!''}" checked=false /]
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

[#macro tableBMacro]
  <table class="table-plannedStudies table-border-powb" id="table-plannedStudies">
    <thead>
      <tr class="subHeader">
        <th id="tb-fp" width="11%">[@s.text name="evidenceRelevant.table.fp" /]</th>
        <th id="tb-plannedTopic" width="27%">[@s.text name="evidenceRelevant.table.plannedTopic" /]</th>
        <th id="tb-geographicScope" width="15%">[@s.text name="evidenceRelevant.table.geographicScope" /]</th>
        <th id="tb-relevant" width="28%">[@s.text name="evidenceRelevant.table.relevant" /]</th>
        <th id="tb-comments" width="19%">[@s.text name="evidenceRelevant.table.comments" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if flagshipPlannedList?has_content]
      [#list flagshipPlannedList as flagshipPlanned]
        <tr>
          [#-- FP --]
          <td class="tb-fp text-center">
            <span class="programTag" style="border-color:${(flagshipPlanned.powbEvidence.powbSynthesis.liaisonInstitution.crpProgram.color)!'#fff'}">
              ${flagshipPlanned.powbEvidence.powbSynthesis.liaisonInstitution.crpProgram.acronym}
            </span>
          </td>
          [#-- Planned topic of study --]
          <td>
            ${(flagshipPlanned.plannedTopic)!''}
          </td>
          [#-- Geographic scope --]
          <td class="text-center">
            ${flagshipPlanned.scopeName}
          </td>
          [#-- Relevant to Sub-IDO, or SRF target if appropiate --]
          <td class="relevantSubIDO">
            <ul>
              [#if flagshipPlanned.srfSubIdo?has_content && flagshipPlanned.srfSloIndicator?has_content][#assign maxPosition=50][#else][#assign maxPosition=100][/#if]
              <li title="${(flagshipPlanned.srfSubIdo.description)!''}">[@utilities.wordCutter string="${(flagshipPlanned.srfSubIdo.description)!''}" maxPos=maxPosition /]</li>
              [#if flagshipPlanned.srfSloIndicator?has_content]
              <li title="${(flagshipPlanned.srfSloIndicator.title)!''}">[@utilities.wordCutter string="${(flagshipPlanned.srfSloIndicator.title)!''}" maxPos=maxPosition /]</li>
              [/#if]
            </ul>
          </td>
          [#-- Comments --]
          <td class="comments" title="${(flagshipPlanned.comments)!''}"> 
            [@utilities.wordCutter string="${(flagshipPlanned.comments)!''}" maxPos=100 /]
          </td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="5">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]