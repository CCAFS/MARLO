[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${powbSynthesisID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = ["${baseUrlMedia}/js/powb/2019/powb_plannedStudies.js"] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "plannedStudies" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb2019", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"evidenceRelevant", "nameSpace":"powb2019", "action":"${crpSession}/evidenceRelevant"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="evidenceRelevant.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/powb2019/submenu-powb2019.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/powb2019/menu-powb2019.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/powb2019/messages-powb2019.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="evidenceRelevant.title.2019" /]</h3>
        <div class="borderBox">
          [#-- Provide a short narrative for any outcome --]
          [#if PMU]
          <div class="form-group margin-panel">
            [@customForm.textArea name="powbSynthesis.powbEvidence.narrative" i18nkey="evidenceRelevant.narrative" help="evidenceRelevant.help" helpIcon=false display=true required=true className="" labelClass="" paramText="${(actualPhase.year)!}" editable=editable powbInclude=true /]
          </div>
          [/#if]
          
          [#-- Table 2B: Planned Evaluations/ Reviews, Impact Assessments and Learning Exercises --]
          [#if PMU]
          <div class="form-group margin-panel">
            <h4 class="subTitle headTitle powb-table">[@s.text name="evidenceRelevant.table.title.2019" /]
              <span class="powb-doc badge label-powb-table pull-right" title="[@s.text name="powb.includedField.title" /]">
                [@s.text name="powb.includedField" /]<span class="glyphicon glyphicon-save-file"></span>
              </span>
            </h4>
            
            <hr />
            [@tableBMacro /]
          </div>
          [/#if]
          
          [#-- Planned Studies for Relevant Outcomes and Impacts --]
          [#if flagship]
          <div class="form-group margin-panel">
            <div class="evidence-plannedStudies-header">
              <h4 class="subTitle headTitle">[@s.text name="evidenceRelevant.plannedStudies" /]</h4>
            </div>
            [#-- Project planned studies (Table) --]
            <hr />
            <label>[@s.text name="evidenceRelevant.tablePlannedStudies.includeLabel" /]:</label>
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

[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro tablePlannedStudiesMacro ]
  <table class="table-plannedStudies table-border-powb" id="table-plannedStudies">
    <thead>
      <tr class="subHeader">
        <th id="tb-projectId" width="0%">[@s.text name="evidenceRelevant.tablePlannedStudies.projectId" /]</th>
        <th id="tb-status" width="10%">[@s.text name="evidenceRelevant.table.status" /]</th>
        <th id="tb-plannedStudy" width="20%">[@s.text name="evidenceRelevant.table.plannedStudy" /]</th>
        <th id="tb-geographicScope" width="11%">[@s.text name="evidenceRelevant.tablePlannedStudies.geographicScope" /]</th>
        <th id="tb-relevant" width="24%">[@s.text name="evidenceRelevant.tablePlannedStudies.relevant" /]</th>
        <th id="tb-commissioning" width="34%">[@s.text name="evidenceRelevant.table.commissioning" /]</th>
        <th id="tb-checkbox" width="0%">[@s.text name="evidenceRelevant.tablePlannedStudies.include" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if popUpProjects?has_content]
      [#list popUpProjects as popUp]
        [#if popUp.project.id?has_content]
            [#local pURL][@s.url namespace="/projects" action="${(crpSession)!}/description"][@s.param name='projectID']${(popUp.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [#local tsURL][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${(popUp.id)!''}[/@s.param][@s.param name='projectID']${(popUp.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [#local wordCutterMaxPos=180]
        <tr>
          [#-- Project ID --]
          <td class="tb-projectId text-center">
            <a href="${pURL}" target="_blank">P${(popUp.project.id)!''}</a>
          </td>
           [#-- Status --]
          <td>
          [#if popUp.projectExpectedStudyInfo.status?has_content]
            [#if popUp.projectExpectedStudyInfo.statusName?length gt wordCutterMaxPos]
              <div title="${(popUp.projectExpectedStudyInfo.statusName)!''}">
            [/#if]
              <a href="${tsURL}" target="_blank">[@utilities.wordCutter string="${(popUp.projectExpectedStudyInfo.statusName)!''}" maxPos=wordCutterMaxPos /]</a>
            [#if popUp.projectExpectedStudyInfo.statusName?length gt wordCutterMaxPos]
              </div>
            [/#if]
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Planned topic of study --]
          <td>
          [#if popUp.projectExpectedStudyInfo.title?has_content]
            [#if popUp.projectExpectedStudyInfo.title?length gt wordCutterMaxPos]
              <div title="${(popUp.projectExpectedStudyInfo.title)!''}">
            [/#if]
              <a href="${tsURL}" target="_blank">[@utilities.wordCutter string="${(popUp.projectExpectedStudyInfo.title)!''}" maxPos=wordCutterMaxPos /]</a>
            [#if popUp.projectExpectedStudyInfo.title?length gt wordCutterMaxPos]
              </div>
            [/#if]
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Geographic scope --]
          <td class="text-center">
          [#if popUp.projectExpectedStudyInfo.repIndGeographicScope?has_content]
            ${popUp.projectExpectedStudyInfo.repIndGeographicScope.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Relevant to Sub-IDO, or SRF target if appropiate --]
          <td class="relevantSubIDO">
          [#if popUp.srfSubIdo?has_content]
            [#if popUp.srfSubIdo?has_content && popUp.srfSloIndicator?has_content][#assign maxPosition=50][#else][#assign maxPosition=100][/#if]
            <ul>  
              <li [#if popUp.srfSubIdo.description?length gt 50]title="${(popUp.srfSubIdo.description)!''}"[/#if]>
                [@utilities.wordCutter string="${(popUp.srfSubIdo.description)!''}" maxPos=maxPosition /]
              </li>
              [#if popUp.srfSloIndicator?has_content]
              <li [#if popUp.srfSloIndicator.title?length gt 50]title="${(popUp.srfSloIndicator.title)!''}"[/#if]>
                [@utilities.wordCutter string="${(popUp.srfSloIndicator.title)!''}" maxPos=maxPosition /]
              </li>
              [/#if]
            </ul>
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#--  Who is commissioning this study --]
          <td class="comments">
          [#if popUp.projectExpectedStudyInfo.commissioningStudy?has_content]
            [#if popUp.projectExpectedStudyInfo.commissioningStudy?length gt wordCutterMaxPos]
              <div title="${(popUp.projectExpectedStudyInfo.commissioningStudy)!''}">
            [/#if]
              [@utilities.wordCutter string="${(popUp.projectExpectedStudyInfo.commissioningStudy)!''}" maxPos=wordCutterMaxPos /]
            [#if popUp.projectExpectedStudyInfo.commissioningStudy?length gt wordCutterMaxPos]
              </div>
            [/#if]
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Include --]
          <td class="plannedStudiesCheckbox text-center">
          [#if editable]
            [@customForm.checkBoxFlat id="${(popUp.id)!''}" name="powbSynthesis.powbEvidence.plannedStudiesValue" value="${(popUp.id)!''}" checked=((!powbSynthesis.powbEvidence.studiesIds?seq_contains(popUp.id))!true)/]
          [#else]
            [#-- If does no have permissions --]
            [#if powbSynthesis.powbEvidence.studiesIds?seq_contains(popUp.id)]<p class="checked"></p>[/#if]
          [/#if]
          </td>
        </tr>
        [/#if]
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
        <th id="tb-fp" width="0%">[@s.text name="evidenceRelevant.table.fp" /]</th>
        <th id="tb-pID" width="0%">[@s.text name="evidenceRelevant.table.projectId" /]</th>
        <th id="tb-status" width="10%">[@s.text name="evidenceRelevant.table.status" /]</th>
        <th id="tb-plannedStudy" width="22%">[@s.text name="evidenceRelevant.table.plannedStudy" /]</th>
        <th id="tb-geographicScope" width="12%">[@s.text name="evidenceRelevant.table.geographicScope" /]</th>
        <th id="tb-relevant" width="28%">[@s.text name="evidenceRelevant.table.relevant" /]</th>
        <th id="tb-commissioning" width="16%">[@s.text name="evidenceRelevant.table.commissioning" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if flagshipPlannedList?has_content]
      [#list flagshipPlannedList as flagshipPlanned]
        [#local tsURL][@s.url namespace="/projects" action="${(crpSession)!}/expectedStudies"][@s.param name='projectID']${(flagshipPlanned.projectExpectedStudy.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [#local wordCutterMaxPos=180]
        <tr>
          [#-- FP --]
          <td class="tb-fp text-center">
            [#if flagshipPlanned.liaisonInstitutions?has_content]
              [#list flagshipPlanned.liaisonInstitutions as institution]
                <span class="programTag" style="border-color:${(institution.crpProgram.color)!'#000000'}">${(institution.crpProgram.acronym)!institution.acronym}</span>
              [/#list]
            [#elseif flagshipPlanned.projectExpectedStudy.project.projectInfo.administrative]
              <span class="programTag" style="border-color:#444">PMU</span>
            [/#if]
          </td>
          [#-- Project ID --]
          <td class="tb-projectId text-center">
            <a href="${tsURL}" target="_blank">P${(flagshipPlanned.projectExpectedStudy.project.id)!}</a>
          </td>    
          [#-- Status --]
          <td>
          [#if flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.status?has_content]
            [#if flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.statusName?length gt wordCutterMaxPos]
              <div title="${(flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.statusName)!''}">
            [/#if]
              <a href="${tsURL}" target="_blank">[@utilities.wordCutter string="${(flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.statusName)!''}" maxPos=wordCutterMaxPos /]</a>
            [#if flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.statusName?length gt wordCutterMaxPos]
              </div>
            [/#if]
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>      
          [#-- Planned topic of study --]
          [#if flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.title?has_content]
            <a title="${(flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.title)!''}" href="${tsURL}" target="_blank">[@utilities.wordCutter string="${(flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.title)!''}" maxPos=wordCutterMaxPos /]</a>
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Geographic scope --]
          <td class="text-center">
          [#if flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.repIndGeographicScope?has_content]
            ${flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.repIndGeographicScope.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Relevant to Sub-IDO, or SRF target if appropiate --]
          <td class="relevantSubIDO">
          [#if flagshipPlanned.projectExpectedStudy.srfSubIdo?has_content || flagshipPlanned.projectExpectedStudy.srfSloIndicator?has_content]
            <ul>
              [#if flagshipPlanned.projectExpectedStudy.srfSubIdo?has_content && flagshipPlanned.projectExpectedStudy.srfSloIndicator?has_content][#assign maxPosition=50][#else][#assign maxPosition=100][/#if]
              <li [#if flagshipPlanned.projectExpectedStudy.srfSubIdo.description?length gt maxPosition]title="${(flagshipPlanned.projectExpectedStudy.srfSubIdo.description)!''}"[/#if]>
                [@utilities.wordCutter string="${(flagshipPlanned.projectExpectedStudy.srfSubIdo.description)!''}" maxPos=maxPosition /]
              </li>
              [#if flagshipPlanned.projectExpectedStudy.srfSloIndicator?has_content]
              <li [#if flagshipPlanned.projectExpectedStudy.srfSloIndicator.title?length gt maxPosition]title="${(flagshipPlanned.projectExpectedStudy.srfSloIndicator.title)!''}"[/#if]>
                [@utilities.wordCutter string="${(flagshipPlanned.projectExpectedStudy.srfSloIndicator.title)!''}" maxPos=maxPosition /]
              </li>
              [/#if]
            </ul>
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Who is commissioning this study --]
          <td class="comments" title="${(flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.commissioningStudy)!''}">
          [#if flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.commissioningStudy?has_content]
            [#if flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.commissioningStudy?length gt wordCutterMaxPos]
              <div title="${(flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.commissioningStudy)!''}">
            [/#if]
              [@utilities.wordCutter string="${(flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.commissioningStudy)!''}" maxPos=wordCutterMaxPos /]
            [#if flagshipPlanned.projectExpectedStudy.projectExpectedStudyInfo.commissioningStudy?length gt wordCutterMaxPos]
              </div>
            [/#if]
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
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