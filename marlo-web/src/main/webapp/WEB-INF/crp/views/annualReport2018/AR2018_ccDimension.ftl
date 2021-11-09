[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "trumbowyg", "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [ 
  "https://www.gstatic.com/charts/loader.js",
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js?20210422A",
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js?20211109B" 
] /]
[#assign customCSS = [
  "${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20210823a",
  "${baseUrlCdn}/global/css/global.css?20211109a"
] /]

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
      [#-- Menu --]
      <div class="col-md-3">[#include "/WEB-INF/crp/views/annualReport2018/menu-AR2018.ftl" /]</div>
      [#-- Content --]
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
                <li role="presentation" class="active"><a index="0" href="#tab-gender" aria-controls="info" role="tab" data-toggle="tab">1.3.1 Gender</a></li>
                <li role="presentation" class=""><a index="1" href="#tab-youth" aria-controls="info" role="tab" data-toggle="tab">1.3.2 Youth</a></li>
                <li role="presentation" class=""><a index="2" href="#tab-capdev" aria-controls="info" role="tab" data-toggle="tab">1.3.3 Capacity Development</small> </a></li>
                <li role="presentation" class=""><a index="3" href="#tab-climateChange" aria-controls="info" role="tab" data-toggle="tab">1.3.4 Climate Change</a></li>
              </ul>
              [#-- Content --] 
              <div class="tab-content ">
                <div id="tab-gender" role="tabpanel" class="tab-pane fade in active">
                  [#-- 1.3.1 Gender --]
                  [@customForm.helpLabel name="${customLabel}.gender.help" showIcon=false editable=editable /]
                  
                  [#-- List any important CRP research findings --]
                  <div class="form-group">
                    [#-- Word Document Tag --]
                    [#if PMU]
                      [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                    [#else]
                      [@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/]
                    [/#if]
                    [@customForm.textArea name="${customName}.genderResearchFindings" i18nkey="${customLabel}.gender.researchFindings" help="${customLabel}.gender.researchFindings.help" className="limitWords-${calculateLimitWords(450)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
                  </div>
                  
                  [#-- What have you learned?  What are you doing differently? --]
                  <div class="form-group">
                    [#-- Word Document Tag --]
                    <br>
                    [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                    [#else][@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/]
                    [/#if]
                    [@customForm.textArea name="${customName}.genderLearned" i18nkey="${customLabel}.gender.learned" help="${customLabel}.gender.learned.help" className="limitWords-${calculateLimitWords(200)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
                  </div>
                  
                  [#-- Problems arisen in relation to gender issues --]
                  <div class="form-group">
                    [#-- Word Document Tag --]
                    <br>
                    [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                    [#else][@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/]
                    [/#if]
                    [@customForm.textArea name="${customName}.genderProblemsArisen" i18nkey="${customLabel}.gender.problemsArisen" help="${customLabel}.gender.problemsArisen.help" className="limitWords-${calculateLimitWords(100)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
                  </div>
                  
                  [#if PMU]
                    [#-- Flagships - Gender Synthesis --]
                    <div class="form-group">
                      [@macrosAR.tableFPSynthesis tableName="${customLabel}.gender.flagshipSynthesis" list=flagshipCCDimensions columns=["genderResearchFindings", "genderLearned", "genderProblemsArisen"] allInOne=true /]
                    </div>
                  [/#if]
                </div>
                
                <div id="tab-youth" role="tabpanel" class="tab-pane fade ">
                  [#-- 1.3.2 Youth --]
                  [#-- CRPs contribution to youth --]
                  <div class="form-group">
                    [#-- Word Document Tag --]
                    [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                    [#else][@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/]
                    [/#if]
                    [@customForm.textArea name="${customName}.youthContribution" i18nkey="${customLabel}.youth.youthContribution" help="${customLabel}.youth.youthContribution.help" className="limitWords-${calculateLimitWords(600)}" helpIcon=false required=false editable=editable allowTextEditor=true /]
                  </div>
                  
                  [@customForm.helpLabel name="${customLabel}.youth.help" showIcon=false editable=editable /]
                 
                   [#-- Youth - Research findings --]
                  <div class="form-group">
                    [#-- Word Document Tag --]
                    [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                    [#else][@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/]
                    [/#if]
                    [@customForm.textArea name="${customName}.youthResearchFindings" i18nkey="${customLabel}.youth.researchFindings" help="${customLabel}.youth.researchFindings.help" className="limitWords-${calculateLimitWords(450)}" helpIcon=false required=!isPlatform editable=editable allowTextEditor=true /]
                  </div>
                  
                  [#-- Youth - What have you learned --]
                  <div class="form-group">
                    [#-- Word Document Tag --]
                    <br>
                    [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                    [#else][@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/]
                    [/#if]
                    [@customForm.textArea name="${customName}.youthLearned" i18nkey="${customLabel}.youth.learned" help="${customLabel}.youth.learned.help" className="limitWords-${calculateLimitWords(200)}" helpIcon=false required=!isPlatform editable=editable allowTextEditor=true /]
                  </div>
                  
                  [#-- Youth - Problems arisen --]
                  <div class="form-group">
                    [#-- Word Document Tag --]
                    <br>
                    [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                    [#else][@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/]
                    [/#if]
                    [@customForm.textArea name="${customName}.youthProblemsArisen" i18nkey="${customLabel}.youth.problemsArisen" help="" className="limitWords-${calculateLimitWords(100)}" helpIcon=false required=!isPlatform editable=editable allowTextEditor=true /]
                  </div>
                  
                  
                  [#if PMU]
                    [#-- Flagships - Youth Synthesis --]
                    <div class="form-group">
                      [@macrosAR.tableFPSynthesis tableName="${customLabel}.youth.flagshipSynthesis" list=flagshipCCDimensions columns=["youthContribution", "youthResearchFindings", "youthLearned", "youthProblemsArisen"]  allInOne=true  /]
                    </div>
                  [/#if]
                </div>
                <div id="tab-capdev" role="tabpanel" class="tab-pane fade">
                  [#-- 1.3.3 Capacity Development --]
                  [#-- CRPs contribution to CapDev --]
                  <div class="form-group">
                    [#-- Word Document Tag --]
                    [#if PMU]
                        [#assign guideSheetURL = "https://docs.google.com/document/d/1VSV90ioOcofH2N2DhmUdHV7flZctkIVI/edit" /]
                        <small class="pull-left"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> Capacity development  -  Guideline </a> </small>
                      [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                    [#else]
                      [#assign guideSheetURL = "https://docs.google.com/document/d/1VSV90ioOcofH2N2DhmUdHV7flZctkIVI/edit" /]
                      <small class="pull-left"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> Capacity development  -  Guideline </a> </small>
                      [@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/]
                    [/#if]
                    [@customForm.textArea name="${customName}.capDevKeyAchievements" i18nkey="${customLabel}.capDev.keyAchievements" help="${customLabel}.capDev.keyAchievements.help" className="limitWords-${calculateLimitWords(300)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
                  
                    [#if PMU]
                      [#-- Flagships - CapDev Synthesis --]
                      <div class="form-group">
                        [@macrosAR.tableFPSynthesis tableName="${customLabel}.capDev.flagshipSynthesis" list=flagshipCCDimensions columns=["capDevKeyAchievements"] showHeader=false showTitle=false /]
                      </div>
                    [/#if]
                  </div>
                  
                  [#if PMU]
                    [#if actualPhaseAR2021 && submission]
                      [#assign qaIncluded = (!(reportSynthesis.reportSynthesisCrossCuttingDimension.isQAIncluded))!false]
                      <span id="isCheckedAR" style="display: none;">${(qaIncluded)?c}</span>
                      <button type="button" class="${qaIncluded?then('removeARButton', 'includeARButton')}" id="qaStatus-button">${qaIncluded?then('Remove from AR', 'Include in AR')}</button>
                      <input type="hidden" name="${customName}.isQAIncluded" id="qaStatus-value" class="onoffswitch-radio"  value="${(!qaIncluded)?c}" />
                      [#if qaIncluded]
                        <div class="containerTitleElements">
                          <div class="containerTitleStatusMessage">
                            <div id="containerQAStatus" class="pendingForReview-mode text-center animated flipInX">
                              <p>
                                [@s.text name="annualReport2018.policies.table2.pendingForReview"][/@s.text]
                              </p>
                            </div> 
                          </div>
                        </div>
                      [/#if]
                    [/#if]
                    [#-- Table 7: Participants in CapDev Activities  --]
                    <div class="form-group">
                      [#-- Word Document Tag --]
                      [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
                    
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
                              <td> [@s.text name="${customLabel}.table7.shortTerm" /] [@s.text name="*" /]</td>
                              <td> [@customForm.input name="${customName}.traineesShortTermFemale" i18nkey="" showTitle=false className="numericInput" required=true editable=editable /] </td>
                              <td> [@customForm.input name="${customName}.traineesShortTermMale" i18nkey="" showTitle=false className="numericInput" required=true editable=editable /] </td>
                            </tr>
                            <tr>
                              <td> [@s.text name="${customLabel}.table7.longTerm" /] [@s.text name="*" /]</td>
                              <td> [@customForm.input name="${customName}.traineesLongTermFemale" i18nkey="" showTitle=false className="numericInput" required=true editable=editable /] </td>
                              <td> [@customForm.input name="${customName}.traineesLongTermMale" i18nkey="" showTitle=false className="numericInput" required=true editable=editable /] </td>
                            </tr>
                            <tr>
                              <td> [@s.text name="${customLabel}.table7.phd" /] [@s.text name="*" /]
                                   <br/><small>([@s.text name="${customLabel}.table7.phd.help" /])</small>
                              </td>
                              <td> [@customForm.input name="${customName}.phdFemale" i18nkey="" showTitle=false className="numericInput" required=true editable=editable /] </td>
                              <td> [@customForm.input name="${customName}.phdMale" i18nkey="" showTitle=false className="numericInput" required=true editable=editable /] </td>
                            </tr>
                          </tbody>
                        </table>
                        <br>
                        <table>
                          <tr>
                             <td> [@s.text name="${customLabel}.table7.evidenceLink" /]
                              </td>
                              <td> [@customForm.input name="${customName}.evidenceLink" i18nkey="" showTitle=false required=false editable=editable /] </td>
                          </tr>
                        </table>
                      </div>
                    </div>
                    
                    <br />
                    <hr />
                    <br />
                    
                    [#-- CapDevCharts--]
                    <div class="form-group row">
                      <div class="col-md-4">
                        <div id="" class="simpleBox numberBox">
                            <label for="">[@s.text name="${customLabel}.totalParticipants" /]</label><br />
                            <span class="totalParticipantsNumber">${(totalParticipants?number?string(",##0"))!0}</span>
                         </div>
                         <div id="" class="simpleBox numberBox">
                            <label for="">Participants in [@s.text name="${customLabel}.totalParticipantFormalTraining" /]</label><br />
                            <span class="totalParticipantFormalTrainingNumber">${(totalParticipantFormalTraining?number?string(",##0"))!0}</span>
                         </div>
                      </div>
                      <div class="col-md-8">
                        [#-- Trainees in Short-Term --]
                        <div id="chart12" class="chartBox simpleBox">
                          [#assign chartData = [
                            {"name":"Male",   "value": "${(totalParticipantFormalTrainingShortMale)!0}"},
                            {"name":"Female", "value": "${(totalParticipantFormalTrainingShortFemale)!0}"}
                          ] /] 
                          <ul class="chartData" style="display:none">
                            <li>
                              <span>[@s.text name="${customLabel}" /]</span>
                              <span>[@s.text name="Short-Term" /]</span>
                              <span class="json">{"role":"annotation"}</span>
                            </li>
                            [#if (((totalParticipantFormalTrainingShortMale)!0) + ((totalParticipantFormalTrainingShortFemale)!0)) > 0 ]
                              [#list chartData as data]
                                <li>
                                  <span>${data.name}</span>
                                  <span class="number">${data.value}</span>
                                  <span>${data.value}</span>
                                </li>
                              [/#list]
                            [/#if]
                          </ul>
                        </div>
                        <br />
                        [#-- Trainees in Long-Term --]
                        <div id="chart13" class="chartBox simpleBox">
                          [#assign chartData = [
                            {"name":"Male",   "value": "${(totalParticipantFormalTrainingLongMale)!0}",   "valuePhD": "${(totalParticipantFormalTrainingPhdMale)!0}"}
                            {"name":"Female", "value": "${(totalParticipantFormalTrainingLongFemale)!0}",   "valuePhD": "${(totalParticipantFormalTrainingPhdFemale)!0}"}
                          ] /] 
                          <ul class="chartData" style="display:none">
                            <li>
                              <span>[@s.text name="${customLabel}.chart13" /]</span>
                              <span>[@s.text name="Long-Term" /]</span>
                              <span class="json">{"role":"annotation"}</span>
                              <span>[@s.text name="PhD" /]</span>
                              <span class="json">{"role":"annotation"}</span>
                            </li>
                            [#if (((totalParticipantFormalTrainingLongMale)!0) + ((totalParticipantFormalTrainingLongFemale)!0)) > 0 ]
                              [#list chartData as data]
                                <li><span>${data.name}</span>
                                <span class="number">${data.value}</span>
                                <span>${data.value}</span>
                                <span class="number">${data.valuePhD}</span>
                                <span>${data.valuePhD}</span></li>
                              [/#list]
                            [/#if]
                          </ul>
                         </div>
                      </div>
                    </div>
                    
                    
                    [#-- Deliverables Participants & Trainees --]
                    <div class="form-group">
                      <h4 class="simpleTitle headTitle annualReport-table">[@s.text name="${customLabel}.deliverableParticipants.title" /]</h4>
                      <div class="viewMoreSyntesis-block">
                        [@tableParticipantsTrainingsMacro list=(deliverableParticipants)![] /]
                      </div>
                    </div> 
                  [/#if]
                </div>
                <div id="tab-climateChange" role="tabpanel" class="tab-pane fade">
                  [#-- 1.3.4 Climate change --]
                  
                  [#-- CRPs contribution to Climate Change --]
                  <div class="form-group">
                    [#-- Word Document Tag --]
                    [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                    [#else][@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/]
                    [/#if]
                    [@customForm.textArea name="${customName}.climateChangeKeyAchievements" i18nkey="${customLabel}.climateChange.keyAchievements" help="${customLabel}.climateChange.keyAchievements.help" className="limitWords-${calculateLimitWords(300)}" helpIcon=false required=false editable=editable allowTextEditor=true /]
                  </div>
                  
                  [#if PMU]
                    [#-- Flagships - Climate Change Synthesis --]
                    <div class="form-group">
                      [@macrosAR.tableFPSynthesis tableName="${customLabel}.climateChange.flagshipSynthesis" list=flagshipCCDimensions columns=["climateChangeKeyAchievements"] /]
                    </div>
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
[#include "/WEB-INF/global/pages/footer.ftl"]



[#macro tableParticipantsTrainingsMacro list]
  <table id="tableParticipantsTrainingsMacro" class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-id">[@s.text name="${customLabel}.activitiesEventsTable.activityEvent" /]</th>
        <th id="tb-title">[@s.text name="${customLabel}.activitiesEventsTable.type" /]</th>        
        <th id="tb-organization-type">[@s.text name="${customLabel}.activitiesEventsTable.typeParticipants" /]</th>
        <th id="tb-type">[@s.text name="${customLabel}.activitiesEventsTable.numberMales" /]</th>
        <th id="tb-type">[@s.text name="${customLabel}.activitiesEventsTable.numberFemales" /]</th>
        <th id="tb-type">[@s.text name="${customLabel}.activitiesEventsTable.totalParticipants" /]</th>
        <th id="tb-training-period">[@s.text name="${customLabel}.activitiesEventsTable.trainingPeriod" /]</th>
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
            [@utils.tableText value=(item.eventActivityName)!"" /] 
            [#-- Deliverable ID --]
            <br /><i style="opacity:0.5"><small>(From D${(item.deliverable.id)!''})</small></i>
            <a href="${URL}" target="_blank" class="pull-right"> <span class="glyphicon glyphicon-new-window"></span> </a>
          </td>
          [#-- Activity Type --]
          <td class="">
            <small>[@utils.tableText value=(item.repIndTypeActivity.name)!"" /]</small>
          </td>          
          [#-- Type of participants --]
          <td class="text-center">
            [@utils.tableText value=(item.repIndTypeParticipant.name)!"" /]
          </td>          
          [#assign knowFemale = (item.dontKnowFemale)!false]
          [#assign hasFemale = (item.females?has_content)!false]
          [#-- Total Participants --]
          <td class="text-center">
            [#if knowFemale && !hasFemale ]
              <i><small>Not specified</small></i>
              [#else]
              ${(item.males?number?string(",##0"))!0}
            [/#if]
          </td>
          [#-- Number of females --]
          <td class="text-center">
          [#if knowFemale && !hasFemale ]
            <i><small>Not specified</small></i>
            [#else]
            ${(item.females?number?string(",##0"))!0}
          [/#if]
          </td>
          [#-- Total Participants --]
          <td class="text-center">
            ${(item.participants?number?string(",##0"))!0}
          </td>
          [#-- Training period of time --]
          <td class="text-center">
            [@utils.tableText value=(item.repIndTrainingTerm.name)!"" /]
          </td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="5">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]
