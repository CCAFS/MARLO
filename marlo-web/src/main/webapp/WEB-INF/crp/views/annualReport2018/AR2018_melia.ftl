[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "trumbowyg" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis" /]
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
          <div class="borderBox">
          
          [#-- Short narrative to introduce the table 9 --]
          [#if PMU]
            <div class="form-group">
                [@customForm.textArea name="${customName}.narrative" i18nkey="${customLabel}.narrative" className="" helpIcon=false required=false editable=editable allowTextEditor=true /]
            </div>
          [#else]
            <div class="textArea">
                <label for="">[@customForm.text name="${customLabel}.narrative" readText=true /]:</label>
                <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
            </div>
          [/#if]
          
          [#-- Table 9: MELIA --]
          [@meliaTable name="table9" list=[] /]
          
          [#-- Table 10: Update on actions taken in response to relevant evaluations --]
            [#if PMU]
              <div class="form-group">
                <h4 class="subTitle headTitle">[@s.text name="${customLabel}.table10.title" /]</h4>
                <div class="listEvaluations">
                  [#-- [#list (reportSynthesis.reportSynthesisMelia.evaluations)![] as item] --]
                  [#list (meliaUpdateList)![] as item]
                    [@relevantEvaluationMacro element=item name="${customName}.table10" index=item_index  isEditable=editable/]
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
    <h4 class="subTitle headTitle annualReport-table">[@s.text name="${customLabel}.${name}.title" /]</h4>
    [@customForm.helpLabel name="${customLabel}.${name}.help" showIcon=false editable=editable/]
    
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.studies" /] </th>
          <th class="text-center col-md-2"> [@s.text name="${customLabel}.${name}.status" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.type" /] </th>
          <th class="text-center col-md-4"> [@s.text name="${customLabel}.${name}.comments" /] </th>
          <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.includeAR" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
          <tr>
            <td>
              [#if (item.studies?has_content)!false]
                ${item.studies}
              [#else]
                <i style="opacity:0.5">Prefilled</i>
              [/#if]
            </td>
            <td class="text-center">
              [#if (item.status?has_content)!false]
                ${item.status}
              [#else]
                <i style="opacity:0.5">Prefilled</i>
              [/#if]
            </td>
            <td class="text-center">
              [#if (item.type?has_content)!false]
                ${item.type}
              [#else]
                <i style="opacity:0.5">Prefilled</i>
              [/#if]
            </td>
            <td>
              [#if (item.comments?has_content)!false]
                ${item.comments}
              [#else]
                <i style="opacity:0.5">Prefilled</i>
              [/#if]
            </td>
            <td class="text-center">
              [@customForm.checkmark id="" name="" checked=false editable=editable centered=true/] 
            </td>
          </tr>
          [/#list]
          [#else]
          <tr>
            <td class="text-center" colspan="6"><i>No entries added yet.</i></td>
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