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

[#assign customName= "reportSynthesis.reportSynthesisCrossCuttingDimension" /]
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
          
            [#-- 1.3.1 Gender --]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.gender.title" /]</h5>
            <i class="helpLabel">[@s.text name="${customLabel}.gender.help" /]</i>
            
            [#-- List any important CRP research findings --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.genderResearchFindings" i18nkey="${customLabel}.gender.researchFindings" help="${customLabel}.gender.researchFindings.help" className="limitWords-450" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#-- What have you learned?  What are you doing differently? --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.genderLearned" i18nkey="${customLabel}.gender.learned" help="${customLabel}.gender.learned.help" className="limitWords-200" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#-- Problems arisen in relation to gender issues --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.genderProblemsArisen" i18nkey="${customLabel}.gender.problemsArisen" help="${customLabel}.gender.problemsArisen.help" className="limitWords-100" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#if PMU]
              [#-- Flagships - Gender Synthesis --]
              <div class="form-group">
                [@tableFlagshipSynthesis tableName="gender.flagshipSynthesis" list=flagshipCCDimensions columns=["A", "B", "C"] /]
              </div>
            [/#if]
            
            [#-- 1.3.2 Youth --]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.youth.title" /]</h5>
            
            [#-- CRPs contribution to youth --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.youthContribution" i18nkey="${customLabel}.youth.youthContribution" help="${customLabel}.youth.youthContribution.help" className="limitWords-600" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            <div class="form-group">
              <i class="helpLabel">[@s.text name="${customLabel}.youth.help" /]</i>
            </div>
            
            [#-- Youth - Research findings --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.youthResearchFindings" i18nkey="${customLabel}.youth.researchFindings" help="${customLabel}.youth.researchFindings.help" className="limitWords-450" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#-- Youth - What have you learned --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.youthLearned" i18nkey="${customLabel}.youth.learned" help="${customLabel}.youth.learned.help" className="limitWords-200" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#-- Youth - Problems arisen --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.youthProblemsArisen" i18nkey="${customLabel}.youth.problemsArisen" help="" className="limitWords-100" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#if PMU]
              [#-- Flagships - Youth Synthesis --]
              <div class="form-group">
                [@tableFlagshipSynthesis tableName="youth.flagshipSynthesis" list=flagshipCCDimensions columns=["A", "B", "C"] /]
              </div>
            [/#if]
            
            [#-- 1.3.3 Capacity Development --]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.capDev.title" /]</h5>
            
            [#-- CRPs contribution to CapDev --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.capDevKeyAchievements" i18nkey="${customLabel}.capDev.keyAchievements" help="${customLabel}.capDev.keyAchievements.help" className="limitWords-300" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            
            [#if PMU]
              [#-- Flagships - CapDev Synthesis --]
              <div class="form-group">
                [@tableFlagshipSynthesis tableName="capDev.flagshipSynthesis" list=flagshipCCDimensions columns=["summary"] /]
              </div>
            [/#if]
            
            [#-- 1.3.4 Climate change --]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.climateChange.title" /]</h5>
            
            [#-- CRPs contribution to Climate Change --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.climateChangeKeyAchievements" i18nkey="${customLabel}.climateChange.keyAchievements" help="${customLabel}.climateChange.keyAchievements.help" className="limitWords-300" helpIcon=false required=false editable=editable allowTextEditor=true /]
            </div>
            
            
            [#if PMU]
              [#-- Flagships - Climate Change Synthesis --]
              <div class="form-group">
                [@tableFlagshipSynthesis tableName="climateChange.flagshipSynthesis" list=flagshipCCDimensions columns=["summary"] /]
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