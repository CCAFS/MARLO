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
            [#if flagship]
              [#-- Progress by flagships --]
              <div class="form-group">
                  [@customForm.textArea name="${customName}.progressByFlagships" i18nkey="${customLabel}.progressByFlagships" help="${customLabel}.progressByFlagships.help" className="limitWords-200" helpIcon=false required=true editable=editable allowTextEditor=true /]
              </div>
              [#-- Detailed annex --]
              <div class="form-group">
                  [@customForm.textArea name="${customName}.detailedAnnex" i18nkey="${customLabel}.detailedAnnex" className="limitWords-800" helpIcon=false required=false editable=editable allowTextEditor=true /]
              </div>
            [#else]
              [#-- Overall CRP progress --]
              <div class="form-group">
                  [@customForm.textArea name="${customName}.overallProgress" i18nkey="${customLabel}.overallProgress" help="${customLabel}.overallProgress.help" className="limitWords-250" helpIcon=false required=true editable=editable allowTextEditor=true /]
              </div>
              
              [#-- Flagship Synthesis --]
              <div class="form-group">
                  [@tableFlagshipSynthesis tableName="tableflagshipSynthesis" list=flagships columns=["progressByFlagships", "detailedAnnex"] /]
              </div>
            [/#if]
            
            
            [#-- 1.2.3 Variance from Planned Program for this year --]
            <div class="form-group">
              <h4 class="headTitle">[@s.text name="${customLabel}.variance" /]</h4>
              <i class="helpLabel">[@s.text name="${customLabel}.variance.help" /]</i>
            </div>
            
            [#-- Expandend research areas --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.expandedResearchAreas" i18nkey="${customLabel}.expandedResearchAreas" help="${customLabel}.expandedResearchAreas.help" className="limitWords-${calculateLimitWords(200)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#-- Dropped research lines --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.droppedResearchLines" i18nkey="${customLabel}.droppedResearchLines" help="${customLabel}.droppedResearchLines.help" className="limitWords-${calculateLimitWords(200)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#-- Changed direction --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.changedDirection" i18nkey="${customLabel}.changedDirection" help="${customLabel}.changedDirection.help" className="limitWords-${calculateLimitWords(200)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#if PMU]
            [#-- Flagships - Synthesis (Variance from Planned Program) --]
            <div class="form-group">
              [@tableFlagshipSynthesis tableName="tableFlagshipVariance" list=flagships columns=["expandedResearchAreas", "droppedResearchLines", "changedDirection"] /]
            </div>
            [/#if]
            
            [#-- Altmetric Score --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.altmetricScore" i18nkey="${customLabel}.altmetricScore" help="${customLabel}.altmetricScore.help" className="limitWords-400" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#if PMU]
            [#-- Flagships - Synthesis (Altmetric Score) --]
            <div class="form-group">
              [@tableFlagshipSynthesis tableName="tableFlagshipAltmetric" list=flagships columns=["altmetricScore"] /]
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

[#macro tableFlagshipSynthesis tableName="tableName" list=[] columns=[] ]
  <div class="form-group">
    <h4 class="simpleTitle">[@s.text name="${customLabel}.${tableName}.title" /]</h4>
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="col-md-1 text-center"> FP </th>
          [#list columns as column]<th class="text-center"> [@s.text name="${customLabel}.${tableName}.column${column_index}" /] </th>[/#list]
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
                    <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
                  [/#if]
                </td>
              [/#list]
            </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]



