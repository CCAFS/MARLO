[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [ 
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js",
  "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js"
   ] 
/]
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
              <div class="viewMoreSyntesis-block">
                [@tableFlagshipSynthesis tableName="meliaSummarytable" list=flagshipMeliaProgress columns=["summary"] /]
              </div>
            </div>
            [/#if]
            
            [#-- Table I-1: Status of Evaluations, Impact Assessments and other Learning excercises planned --]
            <div class="form-group margin-panel">
              <h4 class="subTitle headTitle">[@s.text name="${customLabel}.tableI.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
              [#if flagship]
                [@tableIMacro name="${customName}.plannedStudiesValue" list=(studiesList)![] /]
              [#else]
                <div class="viewMoreSyntesis-block" >
                  [@tableIMacro name="" list=(flagshipPlannedList)![] isPMU=PMU /]
                </div>
              [/#if]
            </div>
            
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
                    ${item[column]?replace('\n', '<br>')} 
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

[#macro tableIMacro name list=[]  isPMU=false ]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="" class="col-md-4">[@s.text name="${customLabel}.tableI.studies"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</th>
        <th>Type</th>
        <th id="">[@s.text name="${customLabel}.tableI.status" /]</th>
        <th id="" >[@s.text name="${customLabel}.tableI.comments" /]</th>
        [#if !isPMU]
          <th id="tb-include">[@s.text name="${customLabel}.tableI.include" /]</th>
        [/#if]
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        
        [#if isPMU]
          [#local element = item.projectExpectedStudy]
        [#else]
          [#local element = item]
        [/#if]
        [#local customName = "${name}" /]
        [#if (element.project.id??)!false]
          [#local URL][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${(element.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [#else]
          [#local URL][@s.url namespace="/studies" action="${(crpSession)!}/study"][@s.param name='expectedID']${(element.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [/#if]
        
        <tr>
          [#-- Studies/learning exercises in - (from POWB)--]
          <td class="">
            [#-- Title --]
            <a href="${URL}" target="_blank"> 
              [#if ((element.projectExpectedStudyInfo.title)?has_content)!false] ${element.projectExpectedStudyInfo.title}[#else]Untitled[/#if]
            </a>
            [#-- Project ID --]
            [#if (element.project.id??)!false] <br /><i style="opacity:0.5">(From Project P${(element.project.id)!})</i> [/#if]
            [#-- Flagships --]
            [#if isPMU]
              <div class="clearfix"></div>
              [#list item.liaisonInstitutions as liaisonInstitution]
                <span class="programTag" style="border-color:${(liaisonInstitution.crpProgram.color)!'#fff'}">${(liaisonInstitution.crpProgram.acronym)!}</span>
              [/#list]
            [/#if]
          </td>
          [#-- Type --]
          <td>
          [#if element.projectExpectedStudyInfo.studyType?has_content]
            ${element.projectExpectedStudyInfo.studyType.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Status --]
          <td class="text-center">
          [#if element.projectExpectedStudyInfo.statusName?has_content]
            ${element.projectExpectedStudyInfo.statusName}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Comments --]
          <td class="">
          [#if element.projectExpectedStudyInfo.topLevelComments?has_content]
            ${element.projectExpectedStudyInfo.topLevelComments?replace('\n', '<br>')}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Include in AR--]
          [#if !isPMU]
          <td class="text-center">
            [@customForm.checkmark id="expecteStudy-${(element.id)!''}" name="${name}" value="${(element.id)!''}" checked=((!reportSynthesis.reportSynthesisMelia.studiesIds?seq_contains(element.id))!true) editable=editable/]
          </td>
          [/#if]
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
        [@customForm.select name="${customName}.status" i18nkey="${customLabel}.evaluation.status" value="${(project.projectInfo.status)!}" listName="statuses"  required=true  className="" editable=isEditable/]
      </div>
    </div>
    
    [#-- By whom --]
    <div class="form-group">
      [@customForm.input name="${customName}.textWhom" i18nkey="${customLabel}.evaluation.whom" required=true className="" editable=isEditable /]
    </div>
    
    [#-- By when --]
    <div class="form-group">
      [@customForm.input name="${customName}.textWhen" i18nkey="${customLabel}.evaluation.when" required=true className="" editable=isEditable /]
    </div>
    
  </div>
[/#macro]