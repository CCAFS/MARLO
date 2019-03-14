[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "trumbowyg", "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js",
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js" 
] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis.reportSynthesisMelia" /]
[#assign customLabel= "annualReport2018.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="${customLabel}.help" /]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
  [#else]
    [#-- Program (Flagships and PMU) --]
    [#include "/WEB-INF/crp/views/annualReport2018/submenu-AR2018.ftl" /]
    
    <div class="row">
      [#-- POWB Menu --]
      <div class="col-md-3">[#include "/WEB-INF/crp/views/annualReport2018/menu-AR2018.ftl" /]</div>
      [#-- POWB Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/annualReport2018/messages-AR2018.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="">
          
            <div class="bootstrapTabs">
              [#-- Tabs --] 
              <ul class="nav nav-tabs" role="tablist"> 
                <li role="presentation" class="active"><a index="0" href="#tab-table10" aria-controls="info" role="tab" data-toggle="tab"> Table 10: MELIA </a></li>
                <li role="presentation" class=""><a index="1" href="#tab-table11" aria-controls="info" role="tab" data-toggle="tab"> Table 11: Update on Actions Taken </a></li>
              </ul>
              
              [#-- Content --] 
              <div class="tab-content ">
                <div id="tab-table10" role="tabpanel" class="tab-pane fade in active">
                  [#-- Short narrative to introduce the table 9 --]
                  <div class="form-group">
                    [@customForm.textArea name="${customName}.summary" i18nkey="${customLabel}.narrative" className="" helpIcon=false required=false editable=editable allowTextEditor=true /]
                  </div>
                  
                  [#-- Table 10: MELIA --]
                  <div class="form-group">
                    [@meliaTable name="${customName}.plannedStudies" list=(studiesList)![] /]
                  </div>
                </div>
                
                <div id="tab-table11" role="tabpanel" class="tab-pane fade">
                  [#-- Table 11: Update on actions taken in response to relevant evaluations --]
                  [#if PMU]
                    <div class="form-group">
                      <h4 class="subTitle headTitle">[@s.text name="${customLabel}.table11.title" /]</h4>
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
                  [#else]
                    <p class="text-center">Only for PMU</p>
                  [/#if]
                </div>
              </div>
            </div>
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport2018/buttons-AR2018.ftl" /]
        [/@s.form] 
      </div> 
    </div>
  [/#if] 
</section>

[@relevantEvaluationMacro element={} name="${customName}.table10" index=-1  template=true/]

[#include "/WEB-INF/global/pages/footer.ftl"]


[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro meliaTable name list=[]]

  <div class="form-group">
    <h4 class="subTitle headTitle annualReport-table">[@s.text name="${customLabel}.table10.title" /]</h4>
    [@customForm.helpLabel name="${customLabel}.help" showIcon=false editable=editable/]
    
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="text-center"> [@s.text name="${customLabel}.table10.studies" /] </th>
          <th class="text-center col-md-2"> [@s.text name="${customLabel}.table10.status" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.table10.type" /] </th>
          <th class="text-center col-md-4"> [@s.text name="${customLabel}.table10.comments" /] </th>
          <th class="col-md-1 text-center"> [@s.text name="${customLabel}.table10.includeAR" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#local url][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(item.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
            <tr>
              <td>
                [@utils.tableText value=(item.composedName)!"" /]
                [#if item.project??]<br /> <small>(From Project P${item.project.id})</small> [/#if]
                <a href="${url}" target="_blank" class="pull-right"><span class="glyphicon glyphicon-new-window"></span></a>
              </td>
              <td class="text-center">
                [@utils.tableText value=(item.projectExpectedStudyInfo.status.name)!"" /]
              </td>
              <td class="text-center">
                [@utils.tableText value=(item.projectExpectedStudyInfo.studyType.name)!"" /]
              </td>
              <td>
                [@utils.tableText value=(item.projectExpectedStudyInfo.topLevelComments)!"" /]
              </td>
              <td class="text-center">
                [@customForm.checkmark id="" name="" checked=false editable=editable centered=true/] 
              </td>
            </tr>
          [/#list]
        [#else]
           
        [/#if]
        <tr>
        </tr>
      </tbody>
    </table>
    
  </div>

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
      [@customForm.input name="${customName}.nameEvaluation" i18nkey="${customLabel}.table10.name" help="${customLabel}.table10.name.help" helpIcon=false required=true className="" editable=isEditable /]
    </div>
    
    [#-- Recommendation --] 
    <div class="form-group"> 
      [@customForm.input name="${customName}.recommendation" i18nkey="${customLabel}.table10.recommendation" help="${customLabel}.table10.recommendation.help" helpIcon=false className="" required=true editable=isEditable /]
    </div>
    
    [#-- Management response --] 
    <div class="form-group">
      [@customForm.textArea name="${customName}.managementResponse" i18nkey="${customLabel}.table10.textOfRecommendation" help="${customLabel}.table10.textOfRecommendation.help" helpIcon=false className="" required=true editable=isEditable allowTextEditor=true /]
    </div>
    
    [#-- Status --]
    <div class="form-group row">
      <div class="col-md-5">
        [@customForm.select name="${customName}.status" i18nkey="${customLabel}.table10.status" listName="statuses"  required=true  className="" editable=isEditable/]
      </div>
    </div>
    
    [#-- Concrete actions --] 
    <div class="form-group">
      [@customForm.textArea name="${customName}.actions" i18nkey="${customLabel}.table10.actions" help="${customLabel}.table10.actions.help" helpIcon=false className="" required=true editable=isEditable allowTextEditor=true /]
    </div>
    
    <div class="form-group row">
      <div class="col-md-6">
        [#-- By whom --]
        [@customForm.input name="${customName}.textWhom" i18nkey="${customLabel}.table10.whom" help="${customLabel}.table10.whom.help" helpIcon=false required=true className="" editable=isEditable /]
      </div>
      <div class="col-md-6">
        [#-- By when --]
        [@customForm.input name="${customName}.textWhen" i18nkey="${customLabel}.table10.when" help="${customLabel}.table10.when.help" helpIcon=false required=true className="" editable=isEditable /]
      </div>
    </div>
    
    
    <div class="form-group">
        [@customForm.textArea name="${customName}.comments" i18nkey="${customLabel}.table10.comments" help="${customLabel}.table10.comments.help" helpIcon=false className="" required=true editable=isEditable allowTextEditor=true /]
    </div>
    
  </div>
[/#macro]