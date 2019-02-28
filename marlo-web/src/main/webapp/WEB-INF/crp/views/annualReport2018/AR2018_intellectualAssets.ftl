[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "trumbowyg" ] /]
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

[#assign customName= "reportSynthesis.reportSynthesisIntellectualAsset" /]
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
            [#-- Section description --]
            <i class="helpLabel">[@s.text name="${customLabel}.help" /]</i>
            
            [#if PMU]
              [#-- Strategically managed assets --]
              <div class="form-group">
                [@customForm.textArea name="${customName}.managed" i18nkey="${customLabel}.managed" help="${customLabel}.managed.help" className="" helpIcon=false required=true editable=editable allowTextEditor=true /]
              </div>
              [#-- Published patents --]
              <div class="form-group">
                [@customForm.textArea name="${customName}.patents" i18nkey="${customLabel}.patents" help="${customLabel}.patents.help" className="" helpIcon=false required=true editable=editable allowTextEditor=true /]
              </div>
              [#-- Critical issues --]
              <div class="form-group">
                [@customForm.textArea name="${customName}.criticalIssues" i18nkey="${customLabel}.criticalIssues" help="${customLabel}.criticalIssues.help" className="" helpIcon=false required=true editable=editable allowTextEditor=true /]
              </div>
              [#else]
              <div class="textArea">
                <label for="">[@customForm.text name="${customLabel}.managed" readText=true /]:</label>
                <p>[#if (managedPMUText?has_content)!false]${managedPMUText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
              </div>
              <div class="textArea">
                <label for="">[@customForm.text name="${customLabel}.patents" readText=true /]:</label>
                <p>[#if (patentsPMUText?has_content)!false]${patentsPMUText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
              </div>
              <div class="textArea">
                <label for="">[@customForm.text name="${customLabel}.criticalIssues" readText=true /]:</label>
                <p>[#if (patentsPMUText?has_content)!false]${patentsPMUText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
              </div>
            [/#if]
            
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#if PMU]
            [#include "/WEB-INF/crp/views/annualReport2018/buttons-AR2018.ftl" /]
          [/#if]
        [/@s.form] 
      </div> 
    </div>
  [/#if] 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]