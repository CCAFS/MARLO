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
      
        [#assign customName= "reportSynthesis.reportSynthesisCrpProgress" /]
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
            <h4 class="subTitle headTitle">[@s.text name="${customLabel}.table.title" /]</h4>
            
            <hr />
            [@tableFlagshipsMacro list=[{},{},{},{}] /]
          </div>
          [/#if]
          
          [#-- Table I-1: Status of Evaluations, Impact Assessments and other Learning excercises planned --]
          [#if flagship]
          <div class="form-group margin-panel">
            <div class="evidence-plannedStudies-header">
              <h4 class="subTitle headTitle">[@s.text name="${customLabel}.tableI.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            </div>
            <hr />
            [@tableIMacro list=[{},{},{},{}] /]
          </div>
          [/#if]
          
          [#-- (PMU Form) Table I-2: Update on actions taken in response to relevant evaluations ... --]
          [#if PMU]
            <div class="form-group">
              <h4 class="subTitle headTitle">[@s.text name="${customLabel}.evaluation.title" /]</h4>
              <div class="listEvaluations">
              
               [#-- REMOVE TEMPORAL LIST ASSIGN --]
               [#assign list=[{},{},{},{}] /]
               
               [#if list?has_content]
                [#list list as item]
                  [@relevantEvaluationMacro element=item name="list" index=item_index  isEditable=editable/]
                [/#list]
               [/#if]
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
</section>

[#--  Relevant Evaluation Form template --]
[@relevantEvaluationMacro element={} name="list" index=-1 template=true /]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro tableFlagshipsMacro list ]
  <div class="">
    <table class="annual-report-table table table-bordered">
      <thead>
        <tr class="subHeader">
          <th width="20%" > [@s.text name="${customLabel}.table.flagship" /] </th>
          <th width="80%" > [@s.text name="${customLabel}.table.summary" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            <tr>
              [#-- Flagship --]
              <td>
                [#if item.liaisonInstitution?has_content]
                <span class="programTag" style="border-color:${(item.liaisonInstitution.crpProgram.color)!'#fff'}">${item.liaisonInstitution.crpProgram.acronym!''}</span>
                [/#if]
              </td>
              [#-- Summary on any highlights of MELIA this year --]
              <td class="text-center">
              [#if item.summary?has_content] 
                ${item.summary!''} 
              [#else]
                <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
              [/#if]
              </td>
            </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="3"><i>[@s.text name="${customLabel}.table.void" /]</i></td>
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
          [#if editable]
            [@customForm.checkBoxFlat id="${(item.id)!''}" name="item.name" value="${(item.id)!''}" checked=((!powbSynthesis.powbEvidence.studiesIds?seq_contains(item.id))!true)/]
          [#else]
            [#-- If does no have permissions --]
            [#if powbSynthesis.powbEvidence.studiesIds?seq_contains(item.id)]<p class="checked"></p>[/#if]
          [/#if]
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
      [@customForm.input name="${customName}.name" i18nkey="${customLabel}.evaluation.name" required=true className="" editable=isEditable /]
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