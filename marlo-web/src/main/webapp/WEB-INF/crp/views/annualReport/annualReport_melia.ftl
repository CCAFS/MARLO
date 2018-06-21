[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"annualReport", "nameSpace":"annualReport", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}", "nameSpace":"annualReport", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="annualReport.${currentStage}.help" /]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
  [#else]
  
    [#-- Program (Flagships and PMU) --]
    [#include "/WEB-INF/crp/views/annualReport/submenu-annualReport.ftl" /]
    
    <div class="row">
      [#-- POWB Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/annualReport/menu-annualReport.ftl" /]
      </div> 
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/annualReport/messages-annualReport.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        
          [#assign customName= "reportSynthesis.reportSynthesisMelia" /]
          [#assign customLabel= "annualReport.${currentStage}" /]
          
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="annualReport.${currentStage}.title" /]</h3>
          <div class="borderBox">
          
            [#-- Please provide a summary on any highlights of MELIA this year --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.summary" i18nkey="${customLabel}.summary" help="${customLabel}.summary.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Flagships - Monitoring, Evaluation, Impact Assessment and Learning Synthesis --]
            [#if PMU]
            <div class="form-group margin-panel">
              [@tableFlagshipSynthesis tableName="meliaSummarytable" list=flagshipMeliaProgress columns=["summary"] /]
            </div>
            [/#if]
            
            [#-- Table I-1: Status of Evaluations, Impact Assessments and other Learning excercises planned --]
            [#if flagship]
            <div class="form-group margin-panel">
              <div class="evidence-plannedStudies-header">
                <h4 class="subTitle headTitle">[@s.text name="${customLabel}.tableI.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
              </div>
              <hr />
              [@tableIMacro list=(studiesList)![] /]
            </div>
            [/#if]
            
            [#-- (PMU Form) Table I-2: Update on actions taken in response to relevant evaluations ... --]
            [#if PMU]
              <div class="form-group">
                <h4 class="subTitle headTitle">[@s.text name="${customLabel}.evaluation.title" /]</h4>
                <div class="listEvaluations">
                  [#list (reportSynthesis.reportSynthesisMelia.evaluations)![] as item]
                    [@relevantEvaluationMacro element=item name="${customName}.evaluations" index=item_index  isEditable=editable/]
                  [/#list]
                </div>
                [#if canEdit && editable]
                <div class="text-right">
                  <div class="addEvaluation bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addEvaluation"/]</div>
                </div> 
                [/#if]
              </div>
            [/#if]
          
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport/buttons-annualReport.ftl" /]
        [/@s.form] 
      </div> 
    </div>
  [/#if]
</section>

[#--  Relevant Evaluation Form template --]
[@relevantEvaluationMacro element={} name="${customName}.evaluations" index=-1 template=true /]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro tableFlagshipSynthesis tableName="tableName" list=[] columns=[] ]
  <div class="form-group">
    <h4 class="simpleTitle">[@s.text name="${customLabel}.${tableName}.title" /]</h4>
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="col-md-1 text-center"> FP </th>
          [#list columns as column]<th> [@s.text name="${customLabel}.${tableName}.column${column_index}" /] </th>[/#list]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#local crpProgram = (item.reportSynthesis.liaisonInstitution.crpProgram)!{} ]
            <tr>
              <td>
                <span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}">${(crpProgram.acronym)!}</span>
              </td>
              [#list columns as column]
                <td>
                  [#if (item[column]?has_content)!false] 
                    ${item[column]} 
                  [#else]
                    <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                  [/#if]
                </td>
              [/#list]
            </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="3"><i>No flagships loaded...</i></td>
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro tableIMacro list ]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-projectId" width="11%">[@s.text name="${customLabel}.tableI.projectId" /]</th>
        <th id="tb-studies" width="35%">[@s.text name="${customLabel}.tableI.studies"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</th>
        <th id="tb-status" width="11%">[@s.text name="${customLabel}.tableI.status" /]</th>
        <th id="tb-comments" width="35%">[@s.text name="${customLabel}.tableI.comments" /]</th>
        <th id="tb-include" width="8%">[@s.text name="${customLabel}.tableI.include" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        [#local pURL][@s.url namespace="/projects" action="${(crpSession)!}/description"][@s.param name='projectID']${(item.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [#local wordCutterMaxPos=180]
        <tr>
          [#-- Project ID --]
          <td class="tb-projectId text-center">
            <a href="${pURL}" target="_blank">P${(item.project.id)!''}</a>
          </td>
          [#-- Studies/learning exercises in - (from POWB)--]
          <td class="text-center">
          [#if item.studies?has_content]
            ${item.studies}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Status --]
          <td class="text-center">
          [#if item.status?has_content]
            ${item.status}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Comments --]
          <td class="text-center">
          [#if item.comments?has_content]
            ${item.comments}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Include in AR--]
          <td class="text-center">
            [@customForm.checkmark id="${(item.id)!''}" name="item.name" value="${(item.id)!''}" checked=((!powbSynthesis.powbEvidence.studiesIds?seq_contains(item.id))!true) editable=editable/]
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

[#macro relevantEvaluationMacro element name index template=false isEditable=true]
  [#local customName = "${name}[${index}]" /]
  <div id="evaluation-${template?string('template', index)}" class="evaluation borderBox form-group" style="position:relative; display:${template?string('none','block')}">

    [#-- Index --]
    <div class="leftHead blue sm"><span class="index">${index+1}</span></div>
    [#-- Remove Button --]
    [#if isEditable]<div class="removeEvaluation removeElement sm" title="Remove"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
    <br />
    
    [#-- Name of the evaluation --]
    <div class="form-group">
      [@customForm.input name="${customName}.nameEvaluation" i18nkey="${customLabel}.evaluation.name" required=true className="" editable=isEditable /]
    </div>
    
    [#-- Recommendation --] 
    <div class="form-group"> 
      [@customForm.textArea name="${customName}.recommendation" i18nkey="${customLabel}.evaluation.recommendation" className="" required=true editable=isEditable /]
    </div>
    
    [#-- Management response --] 
    <div class="form-group"> 
      [@customForm.textArea name="${customName}.managementResponse" i18nkey="${customLabel}.evaluation.managementResponse" className="" required=true editable=isEditable /]
    </div>
    
    [#-- Status --]
    <div class="form-group row">
      <div class="col-md-5">
        [@customForm.select name="${customName}.status" i18nkey="${customLabel}.evaluation.status" value="${(project.projectInfo.status)!}" listName="statusList"  required=true  className="" editable=isEditable/]
      </div>
    </div>
    
    [#-- By whom --]
    <div class="form-group">
      [@customForm.input name="${customName}.whom" i18nkey="${customLabel}.evaluation.whom" required=true className="" editable=isEditable /]
    </div>
    
    [#-- By when --]
    <div class="form-group">
      [@customForm.input name="${customName}.when" i18nkey="${customLabel}.evaluation.when" required=true className="" editable=isEditable /]
    </div>
    
  </div>
[/#macro]