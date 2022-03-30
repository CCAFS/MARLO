[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "trumbowyg", "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js?20211103a",
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js?20220328a" 
] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20190621"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#import "/WEB-INF/crp/views/annualReport2018/macros-AR2018.ftl" as macrosAR /]
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
          <span id="actualCrpID" style="display: none;">${(action.getCurrentCrp().id)!-1}</span>
          <span id="actualPhase" style="display: none;">${(action.isSelectedPhaseAR2021())?c}</span>
          <span id="isSubmitted" style="display: none;">${submission?c}</span>
          [#assign actualPhaseAR2021 = action.isSelectedPhaseAR2021()!false]
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
                    [#-- Word Document Tag --]
                    [#if PMU]
                    
                     [#--  Table for show the summary information of flagships -> use the list reportSynthesisMeliaList and the field summary   --]
                      <div class="form-group">
                         [@macrosAR.tableFPSynthesis tableName="${customLabel}.meliaTable" list=reportSynthesisMeliaList columns=["summary"] showTitle=false allInOne=false /]
                      </div>
                      <br>
                    
                    [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                    [#else]
                    [@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/]
                    [/#if]
                    [@customForm.textArea name="${customName}.summary" i18nkey="${customLabel}.narrative" className="" helpIcon=false required=true editable=editable allowTextEditor=true /]
                  </div>
                  
                  [#-- Table 10: MELIA --]
                  <div class="form-group">
                    [#-- Word Document Tag --]
                    [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
                    
                    [#-- Button --]
                    <button type="button" class="btn btn-default btn-xs pull-right" data-toggle="modal" data-target="#modal-policies">
                       <span class="glyphicon glyphicon-fullscreen"></span> See Full table 10
                    </button>
                    [#-- Missing fields in FPs --] [#--
                    [#if listOfFlagships?has_content]
                      </br>
                      <div class="missingFieldFp">
                        <div><span class="glyphicon glyphicon-exclamation-sign mffp-icon" title="Incomplete"></span> Missing fields in
                        [#list listOfFlagships as fp]
                         ${fp}[#if fp?index !=(listOfFlagships?size-1) ],[/#if]
                        [/#list]
                        </div>
                       </div>
                    [/#if]--]
                    <h4 class="subTitle headTitle annualReport-table">[@s.text name="${customLabel}.table10.title" /]</h4>
                    [@customForm.helpLabel name="${customLabel}.table10.help" showIcon=false editable=editable/]
                    [#-- Modal --]
                    <div class="modal fade" id="modal-policies" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                      <div class="modal-dialog modal-lg" role="document">
                        <div class="modal-content">
                          <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title" id="myModalLabel">[@s.text name="${customLabel}.table10.title" /]</h4>
                          </div>
                          <div class="modal-body">
                            [#-- Full table --]
                            <div class="">
                              [@meliaTable name="${customName}.plannedStudies" list=(studiesList)![] expandedTable=true/]
                            </div>
                          </div>
                          <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                          </div>
                        </div>
                      </div>
                    </div>
                    
                    [#-- Table --]
                    <div class="">
                      [@meliaTable name="${customName}.plannedStudies" list=(studiesList)![] /]
                    </div>
                    
                  </div>
                </div>
                
                <div id="tab-table11" role="tabpanel" class="tab-pane fade">
                  [#-- Table 11: Update on actions taken in response to relevant evaluations --]
                  [#if PMU]
                    <div class="form-group">
                      [#-- Word Document Tag --]
                      [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                      
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

[@relevantEvaluationMacro element={} name="${customName}.evaluations" index=-1  template=true/]
[@evaluationActionMacro element={} name="${customName}.evaluations[-1].meliaEvaluationActions" index=-1  template=true/]



[#include "/WEB-INF/global/pages/footer.ftl"]


[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro meliaTable name list=[] expandedTable=false]

  <div class="form-group">    
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="text-center"> [@s.text name="${customLabel}.table10.studies" /] </th>
          <th class="text-center col-md-2"> [@s.text name="${customLabel}.table10.status" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.table10.type" /] </th>
          <th class="text-center col-md-4"> [@s.text name="${customLabel}.table10.comments" /] </th>
          [#if expandedTable]
          <th class="text-center col-md-4"> [@s.text name="${customLabel}.table10.publicationsLinks" /] </th>
          [/#if]
          [#if !expandedTable]
            <th class="col-md-1 text-center"> <small>[@s.text name="${customLabel}.table11.missingFields" /]</small>  </th>
            [#if PMU]
              <th class="col-md-1 text-center"> <small>[@s.text name="${customLabel}.table10.includeAR" /]</small>
              <button type="button" class="selectAllCheckMelias" id="selectAllMelias" style="color: #1da5ce; font-style: italic; font-weight: 500; background-color: #F9F9F9; border-bottom: none; outline: none">Select All</button>
              </th>
              [#if actualPhaseAR2021 && submission]
                <th class="col-md-1 text-center">[@s.text name="${customLabel}.table10.QA" /]</th>
              [/#if]
            [/#if]
          [/#if]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#local isFromProject = (item.project??)!false]
            [#if isFromProject]
            [#local url][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(item.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
            [#else]
              [#local url][@s.url namespace="/studies" action="${(crpSession)!}/study"][@s.param name='expectedID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [/#if]
            [#-- Is new --]
            [#local isNew = (action.isEvidenceNew(item.id)) /]
            <tr>
              <td>
                [#if isNew] <span class="label label-info">[@s.text name="global.new" /]</span> [/#if] 
                [@utils.tableText value=(item.composedName)!"" /]
                [#if item.project??]<br /> <small>(From Project P${item.project.id})</small> [/#if]
                
                [#if PMU]
                <br />
                <div class="form-group">
                  [#list (item.selectedFlahsgips)![] as liason]
                    <span class="programTag" style="border-color:${(liason.crpProgram.color)!'#fff'}" title="${(liason.composedName)!}">${(liason.acronym)!}</span>
                  [/#list]
                </div>
                [/#if]
                <a href="${url}" target="_blank" class="pull-right"><span class="glyphicon glyphicon-new-window"></span></a>
              </td>
              <td class="text-center">
                [@utils.tableText value=(item.projectExpectedStudyInfo.status.name)!"" /]
              </td>
              <td class="text-center">
                [@utils.tableText value=(item.projectExpectedStudyInfo.studyType.name)!"" /]
              </td>
              <td class="urlify">
                [@utils.tableText value=(item.projectExpectedStudyInfo.topLevelComments)!"" /]
              </td>
              [#if expandedTable]
              <td class="urlify">
                [@utils.tableText value=(item.projectExpectedStudyInfo.MELIAPublications)!"" /]
              </td>
              [/#if]
              [#if !expandedTable]
                [#-- Complete Status--]
                <td class="text-center">
                  [#assign isStudyComplete = action.isStudyComplete(item.id, actualPhase.id) /]
                  [#if isStudyComplete]
                    <span class="glyphicon glyphicon-ok-sign mf-icon check" title="Complete"></span> 
                  [#else]
                    <span class="glyphicon glyphicon-exclamation-sign mf-icon" title="Incomplete"></span>
                  [/#if]   
                </td>
                [#if PMU]         
                  [#local isChecked = ((!reportSynthesis.reportSynthesisMelia.studiesIds?seq_contains(item.id))!true) /]
                  <td class="text-center">
                    [@customForm.checkmark id="melia-${(item.id)!}" name="reportSynthesis.reportSynthesisMelia.plannedStudiesValue" value="${(item.id)!''}" checked=isChecked editable=editable centered=true/]
                    <div id="isCheckedAR-${item.id}" style="display: none">${isChecked?string('1','0')}</div>
                  </td>
                  [#if actualPhaseAR2021 && submission]
                    <td id="QAStatusIcon-${item.id}" class="text-center">
                      [#if isChecked]
                        <i style="font-weight: normal;opacity:0.8;">[@s.text name="annualReport2018.policies.table2.pendingForReview"/]</i>
                      [#else]
                        <i style="font-weight: normal;opacity:0.8;">[@s.text name="annualReport2018.policies.table2.notInluded"/]</i>
                      [/#if]
                    </td>
                  [/#if]
                [/#if]
              [/#if]
            </tr>
          [/#list]
        [#else]
           <tr>
            <td class="text-center" colspan="5"><i>No entries added yet.</i></td>
          </tr>
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
      [@customForm.input name="${customName}.nameEvaluation" i18nkey="${customLabel}.table11.name" help="${customLabel}.table11.name.help" helpIcon=false required=true className="" editable=isEditable /]
    </div>
    [#-- Recommendation --] 
    <div class="form-group"> 
      [@customForm.input name="${customName}.recommendation" i18nkey="${customLabel}.table11.recommendation" help="${customLabel}.table11.recommendation.help" helpIcon=false className="" required=true editable=isEditable /]
    </div>
    [#-- Management response --] 
    <div class="form-group">
      [@customForm.textArea name="${customName}.managementResponse" i18nkey="${customLabel}.table11.textOfRecommendation" help="${customLabel}.table11.textOfRecommendation.help" helpIcon=false className="" required=true editable=isEditable allowTextEditor=true /]
    </div>
    [#-- Status --]
    <div class="form-group row">
      <div class="col-md-5">
        [@customForm.select name="${customName}.status" i18nkey="${customLabel}.table11.status" listName="statuses"  required=true  className="" editable=isEditable/]
      </div>
    </div>
    
    [#-- Concrete actions --]
    <div class="evaluationActions">
      <h5 class="sectionSubTitle"> [@s.text name="${customLabel}.table11.actions" /] </h5>
      <div class="list-block">
        [#if (element.meliaEvaluationActions?has_content)!false]
          [#list (element.meliaEvaluationActions)![] as evalAction]
            [@evaluationActionMacro element=evalAction name="${customName}.meliaEvaluationActions" index=evalAction_index  isEditable=editable/]
          [/#list]
        [#else]
          [@evaluationActionMacro element={} name="${customName}.meliaEvaluationActions" index=0  isEditable=editable/]
        [/#if]
      </div>
      
      [#if editable]
        <div class="text-right">
          <div class="addEvaluationAction button-blue text-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addAction"/]</div>
        </div>
      [/#if]
    </div>
    [#-- Link to evidence --]
    <div class="form-group">
       [@customForm.textArea name="${customName}.evidences" i18nkey="${customLabel}.table11.evidences" required=true className="evidenceList" helpIcon=false className="" editable=isEditable allowTextEditor=true /]
    </div>

      
    [#-- Comments --] 
    <div class="form-group">
      [@customForm.textArea name="${customName}.comments" i18nkey="${customLabel}.table11.comments" help="${customLabel}.table11.comments.help" helpIcon=false className="" required=false editable=isEditable allowTextEditor=true /]
    </div>
    
  </div>
[/#macro]

[#macro evaluationActionMacro element name index template=false isEditable=true]
  [#local customName = "${name}[${index}]" /]
  <div id="evaluationAction-${template?string('template', index)}" class="evaluationAction simpleBox form-group" style="position:relative; display:${template?string('none','block')}">
    [#-- Remove Button --]
    [#if isEditable]<div class="removeEvaluationAction removeElement sm" title="Remove"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
    [#-- Action title --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.actions" i18nkey="${customLabel}.table11.action" className="" required=true editable=isEditable allowTextEditor=true /]
    </div>
    <div class="form-group row">
      <div class="col-md-6">
        [#-- By whom --]
        [@customForm.input name="${customName}.textWhom" i18nkey="${customLabel}.table11.whom" help="${customLabel}.table11.whom.help" helpIcon=false required=true className="" editable=isEditable /]
      </div>
      <div class="col-md-6">
        [#-- By when --]
        [@customForm.input name="${customName}.textWhen" i18nkey="${customLabel}.table11.when" help="${customLabel}.table11.when.help" helpIcon=false required=true className="" editable=isEditable /]
      </div>
    </div>
  </div>
[/#macro]