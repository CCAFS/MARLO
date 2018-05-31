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
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.genderTitle" /]</h5>
            [#-- Describe any important CRP research findings, capacity development or outcomes in 2017 related to Gender issues. --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.describeGenderIssues" i18nkey="${customLabel}.describeGenderIssues" help="${customLabel}.describeGenderIssues.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Please briefly highlight any lessons and implications for your future work on Gender. --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.lessonsGender" i18nkey="${customLabel}.lessonsGender" help="${customLabel}.lessonsGender.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Gender Synthesis Table--]
            [#if PMU]
            <div class="form-group">
              {TABLE HERE}
            </div>
            [/#if]
            
            [#-- 1.3.2 Youth --]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.youthTitle" /]</h5>
            [#-- Describe any important CRP research findings, capacity development or outcomes in 2017 related to Youth issues. --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.describeYouthIssues" i18nkey="${customLabel}.describeYouthIssues" help="${customLabel}.describeYouthIssues.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Please briefly highlight any lessons and implications for your future work on Youth. --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.lessonsYouth" i18nkey="${customLabel}.lessonsYouth" help="${customLabel}.lessonsYouth.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Youth Synthesis Table--]
            [#if PMU]
            <div class="form-group">
              {TABLE HERE}
            </div>
            [/#if]
            
            [#-- 1.3.3 Other Aspects of Equity / “Leaving No-one Behind ” --]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.otherAspectsTitle" /]</h5>
            [#-- Add information on other aspects of equity and your CRP’s contribution to “leaving no-one behind” --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.infoOtherAspects" i18nkey="${customLabel}.infoOtherAspects" help="${customLabel}.infoOtherAspects.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- 1.3.4 Capacity Development --]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.capDevTitle" /]</h5>
            [#-- Please summarize key achievements and learning points in Capacity Development this year--]
            <div class="form-group">
              [@customForm.textArea name="${customName}.infoCapDev" i18nkey="${customLabel}.infoCapDev" help="${customLabel}.infoCapDev.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Other/Capdev Table--]
            [#if PMU]
            <div class="form-group">
              {TABLE HERE}
            </div>
            [/#if]
            
            [#-- Table C: Cross-cutting Aspect of Outputs (ONLY READ)--]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.tableCTitle" /]</h5>
            <div class="form-group">
              {TABLE HERE}
            </div>
            
            [#-- 1.3.5 Open Data --]
            [#if PMU]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.openDataTitle" /]</h5>
            [#-- Please provide a brief summary on CRP progress, challenges, and lessons with implementing the open data commitment. --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.openData" i18nkey="${customLabel}.openData" help="${customLabel}.openData.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            [/#if]
            
            [#-- Table D-2: List of CRP Innovations in 2017 (From indicator #C1 in Table D-1)  --]
            [#if flagship]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.tableD2Title" /]</h5>
            <div class="form-group">
              {TABLE HERE}
            </div>
            [/#if]
            
            [#-- 1.3.6 Intellectual Assets --]
            [#if PMU]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.intellectualAssetsTitle" /]</h5>
            [#-- Please provide a brief summary under the three following headings --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.intellectualAssets" i18nkey="${customLabel}.intellectualAssets" help="${customLabel}.intellectualAssets.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            [/#if]
            
            [#-- Table E: Intellectual Assets  --]
            [#if flagship]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.tableETitle" /]</h5>
            <div class="form-group">
              {TABLE HERE}
            </div>
            [/#if]
            
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

