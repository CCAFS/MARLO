[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ ] /]
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

[#assign customName= "reportSynthesis.reportSynthesisDimesions" /]
[#assign customLabel= "annualReport.${currentStage}" /]

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
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
           
            [#-- 1.3.1 Gender --]
            <h5 class="sectionSubTitle">1.3.1 Gender</h5>
            [#-- Describe any important CRP research findings, capacity development or outcomes in 2017 related to Gender issues. --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.describeGenderIssues" i18nkey="${customLabel}.describeGenderIssues" help="${customLabel}.describeGenderIssues.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Please briefly highlight any lessons and implications for your future work on Gender. --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.lessonsGender" i18nkey="${customLabel}.lessonsGender" help="${customLabel}.lessonsGender.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Gender Synthesis Table--]
            <div class="form-group">
              {TABLE HERE}
            </div>
            
            [#-- 1.3.2 Youth --]
            <h5 class="sectionSubTitle">1.3.2 Youth </h5>
            [#-- Describe any important CRP research findings, capacity development or outcomes in 2017 related to Youth issues. --]
            <div class="form-group">
            
            </div>
            
            [#-- Please briefly highlight any lessons and implications for your future work on Youth. --]
            <div class="form-group">
            
            </div>
            
            [#-- Youth Synthesis Table--]
            <div class="form-group">
              {TABLE HERE}
            </div>
            
            [#-- 1.3.3 Other Aspects of Equity / “Leaving No-one Behind ” --]
            <h5 class="sectionSubTitle">1.3.3 Other Aspects of Equity / “Leaving No-one Behind ”</h5>
            [#-- Add information on other aspects of equity and your CRP’s contribution to “leaving no-one behind” --]
            <div class="form-group">
              
            </div>
            
            [#-- 1.3.4 Capacity Development --]
            <h5 class="sectionSubTitle">1.3.4 Capacity Development</h5>
            [#-- Please summarize key achievements and learning points in Capacity Development this year--]
            <div class="form-group">
            
            </div>
            
            [#-- Other/Capdev Table--]
            <div class="form-group">
              {TABLE HERE}
            </div>
          
          
          </div>
          
          [#-- Section Buttons & hidden inputs--]
          [#if PMU]
            [#include "/WEB-INF/crp/views/annualReport/buttons-annualReport.ftl" /]
          [/#if]
        [/@s.form] 
      </div> 
    </div>
  [/#if]
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

