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
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#import "/WEB-INF/crp/views/annualReport2018/macros-AR2018.ftl" as macrosAR /]
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
               [@customForm.textArea name="${customName}.genderResearchFindings" i18nkey="${customLabel}.gender.researchFindings" help="${customLabel}.gender.researchFindings.help" className="limitWords-${calculateLimitWords(450)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#-- What have you learned?  What are you doing differently? --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.genderLearned" i18nkey="${customLabel}.gender.learned" help="${customLabel}.gender.learned.help" className="limitWords-${calculateLimitWords(200)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#-- Problems arisen in relation to gender issues --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.genderProblemsArisen" i18nkey="${customLabel}.gender.problemsArisen" help="${customLabel}.gender.problemsArisen.help" className="limitWords-${calculateLimitWords(100)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#if PMU]
              [#-- Flagships - Gender Synthesis --]
              <div class="form-group">
                [@macrosAR.tableFPSynthesis tableName="${customLabel}.gender.flagshipSynthesis" list=flagshipCCDimensions columns=["genderResearchFindings", "genderLearned", "genderProblemsArisen"] /]
              </div>
            [/#if]
            
            [#-- 1.3.2 Youth --]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.youth.title" /]</h5>
            
            [#-- CRPs contribution to youth --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.youthContribution" i18nkey="${customLabel}.youth.youthContribution" help="${customLabel}.youth.youthContribution.help" className="limitWords-${calculateLimitWords(600)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            <div class="form-group">
              <i class="helpLabel">[@s.text name="${customLabel}.youth.help" /]</i>
            </div>
            
            [#-- Youth - Research findings --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.youthResearchFindings" i18nkey="${customLabel}.youth.researchFindings" help="${customLabel}.youth.researchFindings.help" className="limitWords-${calculateLimitWords(450)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#-- Youth - What have you learned --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.youthLearned" i18nkey="${customLabel}.youth.learned" help="${customLabel}.youth.learned.help" className="limitWords-${calculateLimitWords(200)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#-- Youth - Problems arisen --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.youthProblemsArisen" i18nkey="${customLabel}.youth.problemsArisen" help="" className="limitWords-${calculateLimitWords(100)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#if PMU]
              [#-- Flagships - Youth Synthesis --]
              <div class="form-group">
                [@macrosAR.tableFPSynthesis tableName="${customLabel}.youth.flagshipSynthesis" list=flagshipCCDimensions columns=["youthContribution", "youthResearchFindings", "youthLearned", "youthProblemsArisen"] /]
              </div>
            [/#if]
            
            [#-- 1.3.3 Capacity Development --]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.capDev.title" /]</h5>
            
            [#-- CRPs contribution to CapDev --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.capDevKeyAchievements" i18nkey="${customLabel}.capDev.keyAchievements" help="${customLabel}.capDev.keyAchievements.help" className="limitWords-${calculateLimitWords(300)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#if PMU]
              [#-- CapDevCharts--]
              <div class="form-group">
              </div>
              
              [#-- Table 7: Participants in CapDev Activities  --]
              <div class="form-group">
                <h4 class="simpleTitle headTitle annualReport-table">[@s.text name="${customLabel}.table7.title" /]</h4>
                [@customForm.helpLabel name="${customLabel}.table7.help" showIcon=false editable=editable/]
                
                <div class="">
                  <table>
                    <thead>
                      <tr>
                        <th>[@s.text name="${customLabel}.table7.numberTrainnees" /]</th>
                        <th>[@s.text name="${customLabel}.table7.female" /]</th>
                        <th>[@s.text name="${customLabel}.table7.male" /]</th>
                      </tr>
                    </thead>
                    <tbody>
                    	<tr>
                    		<td> [@s.text name="${customLabel}.table7.shortTerm" /]</td>
                    		<td> [@customForm.input name="${customName}.traineesShortTermFemale" i18nkey="" showTitle=false className="numericInput" required=true editable=editable /] </td>
                    		<td> [@customForm.input name="${customName}.traineesShortTermMale" i18nkey="" showTitle=false className="numericInput" required=true editable=editable /] </td>
                    	</tr>
                    	<tr>
                        <td> [@s.text name="${customLabel}.table7.longTerm" /]</td>
                        <td> [@customForm.input name="${customName}.traineesLongTermFemale" i18nkey="" showTitle=false className="numericInput" required=true editable=editable /] </td>
                        <td> [@customForm.input name="${customName}.traineesLongTermMale" i18nkey="" showTitle=false className="numericInput" required=true editable=editable /] </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
              
              [#-- Deliverables Participants & Trainees --]
              <div class="form-group">
                <h4 class="simpleTitle headTitle annualReport-table">[@s.text name="${customLabel}.deliverableParticipants.title" /]</h4>
                
                <div class="">
                  [@tableParticipantsTrainingsMacro list=(deliverableParticipants)![] /]
                </div>
              </div>
              
            [/#if]
            
            [#if PMU]
              [#-- Flagships - CapDev Synthesis --]
              <div class="form-group">
                [@macrosAR.tableFPSynthesis tableName="${customLabel}.capDev.flagshipSynthesis" list=flagshipCCDimensions columns=["capDevKeyAchievements"] /]
              </div>
            [/#if]
            
            [#-- 1.3.4 Climate change --]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.climateChange.title" /]</h5>
            
            [#-- CRPs contribution to Climate Change --]
            <div class="form-group">
               [@customForm.textArea name="${customName}.climateChangeKeyAchievements" i18nkey="${customLabel}.climateChange.keyAchievements" help="${customLabel}.climateChange.keyAchievements.help" className="limitWords-${calculateLimitWords(300)}" helpIcon=false required=false editable=editable allowTextEditor=true /]
            </div>
            
            
            [#if PMU]
              [#-- Flagships - Climate Change Synthesis --]
              <div class="form-group">
                [@macrosAR.tableFPSynthesis tableName="${customLabel}.climateChange.flagshipSynthesis" list=flagshipCCDimensions columns=["climateChangeKeyAchievements"] /]
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



[#macro tableParticipantsTrainingsMacro list]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-id">[@s.text name="${customLabel}.activitiesEventsTable.activityEvent" /]</th>
        <th id="tb-title">[@s.text name="${customLabel}.activitiesEventsTable.type" /]</th>
        <th id="tb-type">[@s.text name="${customLabel}.activitiesEventsTable.totalParticipants" /]</th>
        <th id="tb-type">[@s.text name="${customLabel}.activitiesEventsTable.numberFemales" /]</th>
        <th id="tb-organization-type">[@s.text name="${customLabel}.activitiesEventsTable.typeParticipants" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        [#local URL][@s.url namespace="/projects" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${(item.deliverable.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
          [#-- Title of Innovation --]
          <td class="">
            <a href="${URL}" target="_blank">
              [#if item.eventActivityName?has_content]
                ${item.eventActivityName}
              [#else]
                <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
              [/#if]
              [#-- Deliverable ID --]
              <br /><i style="opacity:0.5"><small>(From D${(item.deliverable.id)!''})</small></i>
            </a>
          </td>
          [#-- Activity Type --]
          <td class="">
          [#if item.repIndTypeActivity?has_content]
            ${item.repIndTypeActivity.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Total Participants --]
          <td class="text-center">
            ${(item.participants?number?string(",##0"))!0}
          </td>
          [#-- Number of females --]
          <td class="text-center">
            ${(item.females?number?string(",##0"))!0}
          </td>
          [#-- Type of participants --]
          <td class="text-center">
          [#if item.repIndTypeParticipant?has_content]
            ${item.repIndTypeParticipant.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="6">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]
