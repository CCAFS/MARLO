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

          [#-- Table 2B: Planned Evaluations/ Reviews, Impact Assessments and Learning Exercises --]
          <div class="form-group margin-panel">
            [#if PMU][@utilities.tag label="powb.docBadge" tooltip="powb.docBadge.tooltip"/][/#if]
            <h4 class="subTitle headTitle">[@s.text name="evidenceRelevant.table.title.2019" /]</h4>
            <hr />
            <label>[@s.text name="evidenceRelevant.tablePlannedStudies.includeLabel" /]:</label>
            [#-- Project planned studies (Table) --]
            [@tablePlannedStudiesMacro/]
          </div>
          
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
        <th id="tb-status">[@s.text name="evidenceRelevant.table.status" /]</th>
        <th id="tb-plannedStudy" class="text-left">[@s.text name="evidenceRelevant.table.plannedStudy" /]</th>
        <th id="tb-geographicScope" >[@s.text name="evidenceRelevant.tablePlannedStudies.geographicScope" /]</th>
        <th id="tb-commissioning" >[@s.text name="evidenceRelevant.table.commissioning" /]</th>
        <th id="tb-checkbox">[@s.text name="evidenceRelevant.tablePlannedStudies.include" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if popUpProjects?has_content]
      [#list popUpProjects as study]
        [#if study.project.id?has_content]
          [#local pURL][@s.url namespace="/projects" action="${(crpSession)!}/description"][@s.param name='projectID']${(study.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
          [#local tsURL][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${(study.id)!''}[/@s.param][@s.param name='projectID']${(study.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
          [#local wordCutterMaxPos=180]
        <tr>
          [#-- Status --]
          <td>
            [#if study.projectExpectedStudyInfo.status?has_content]
              <nobr>${(study.projectExpectedStudyInfo.statusName)!''}</nobr>
            [#else]
              <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
            [/#if]
          </td>
          [#-- Planned topic of study --]
          <td>
            [#-- Study title --]
            <a href="${tsURL}" target="_blank"> 
              [#if study.projectExpectedStudyInfo.title?has_content]
                  [#if ((study.projectExpectedStudyInfo.title)?has_content)!false] ${study.projectExpectedStudyInfo.title}[#else]Untitled[/#if]
              [#else]
                <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
              [/#if]
            </a>
            [#-- Project title --]
            [#if (study.project.id??)!false] <br /><i style="opacity:0.5">(From Project P${(study.project.id)!})</i> [/#if]
          </td>
          [#-- Geographic scope --]
          <td class="text-center">
          [#if study.projectExpectedStudyInfo.repIndGeographicScope?has_content]
            <nobr>${study.projectExpectedStudyInfo.repIndGeographicScope.name}</nobr>
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#--  Who is commissioning this study --]
          <td class="comments">
            [#if study.projectExpectedStudyInfo.commissioningStudy?has_content]
              ${(study.projectExpectedStudyInfo.commissioningStudy)!''}
            [#else]
              <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
            [/#if]
          </td>
          [#-- Include --]
          <td class="plannedStudiesCheckbox text-center">
            [@customForm.checkmark id="expecteStudy-${(study.id)!''}" name="powbSynthesis.powbEvidence.plannedStudiesValue" value="${(study.id)!''}" checked=((!powbSynthesis.powbEvidence.studiesIds?seq_contains(study.id))!true) editable=editable centered=true/]
          </td>
        </tr>
        [/#if]
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="7">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]