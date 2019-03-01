[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "trumbowyg" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis.reportSynthesisFundingUseSummary" /]
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
            
            [#-- Interesting points on the use of W1/2 --]
            <div class="form-group">
              [#if PMU]
                [@customForm.textArea name="${customName}.interestingPoints" i18nkey="${customLabel}.interestingPoints" help="${customLabel}.interestingPoints.help" helpIcon=false required=true editable=editable allowTextEditor=true /]
              [#else]
                <div class="textArea">
                  <label for="">[@customForm.text name="${customLabel}.interestingPoints" readText=true /]</label>:
                  <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
                </div>
              [/#if]
            </div>
            
            [#assign fundingList = [
                { 
                  "title": "Title",
                  "maturity": "Maturity Level",
                  "status": "Status"
                },
                { 
                  "title": "Title 1",
                  "maturity": "Maturity Level 1",
                  "status": "Status 1"
                }
            ] /]
            
            [#if PMU]
            [#-- Table 11 - Examples of W1/2 Use --]
            <div class="form-group">
              <h4 class="simpleTitle headTitle annualReport-table">[@s.text name="${customLabel}.table11.title" /]</h4>
                [@customForm.helpLabel name="${customLabel}.table11.help" showIcon=false editable=editable/]
                [#if reportSynthesis.reportSynthesisFundingUseSummary.expenditureAreas?has_content]
                    [#list reportSynthesis.reportSynthesisFundingUseSummary.expenditureAreas as item]
                      [@fundingExamples element=item name="${customName}.fundingUse.table11" index=item_index template=false isEditable=editable/]
                    [/#list]
                [/#if]
                [#if canEdit && editable]
                  <div class="text-right">
                    <div class="addKeyPartnership bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="annualReport2018.fundingUse.addExpenditure"/]</div>
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
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro fundingExamples name element index isEditable template=false] 

[#local customName = "${name}[${index}]" /]
  <div id="flagshipCollaboration-${template?string('template', index)}" class="flagshipCollaboration borderBox form-group" style="position:relative; display:${template?string('none','block')}">

    [#-- Index --]
    <div class="leftHead blue sm"><span class="index">${index+1}</span></div>
    [#-- Remove Button --]
    [#if isEditable]<div class="removeProgramCollaboration removeElement sm" title="Remove"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
    <br />
    
    <div class="form-group">
      [#-- Name of examples of W1/2 Expenditure --]
        [@customForm.textArea name="${customName}.table11.examples" i18nkey="${customLabel}.table11.examples" help="${customLabel}.table11.examples.help" helpIcon=false className="limitWords-50" required=true editable=editable allowTextEditor=true /]
    </div>

    <div class="form-group row">
      [#-- Broad area --] 
        <div class="col-md-7">
          [@customForm.select name="${customName}.table11.broadArea" label="" keyFieldName="id" displayFieldName="acronym" i18nkey="${customLabel}.table11.broadArea" listName="globalUnitList"  required=true  className="" editable=isEditable/]
        </div>
    </div>    
    
    <div class="form-group row">
      [#-- Other --]
        <div class="col-md-7">
          [@customForm.input name="${customName}.table11.otherArea" i18nkey="${customLabel}.table11.otherArea"required=true editable=editable /]
        </div>
    </div>
    
  </div>

[/#macro]