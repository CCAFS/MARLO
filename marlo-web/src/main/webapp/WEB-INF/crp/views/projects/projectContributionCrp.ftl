[#ftl]
[#assign title = "Cluster Contribution to Indicators" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectOutcomeID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "trumbowyg", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/projects/projectContributionCrp.js?20220707B", 
  "${baseUrlCdn}/global/js/fieldsValidation.js?20220613",
  "${baseUrlCdn}/crp/js/feedback/feedbackAutoImplementation.js?20220905",
  "https://www.gstatic.com/charts/loader.js",
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_ccDimensions.js?20220613" 
  ] 
/] 
[#assign customCSS = [ 
  "${baseUrlMedia}/css/projects/projectContributionCrp.css?20220613",
  "${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20220613"
  ] 
/]
[#assign currentSection = "projects" /]
[#assign currentStage = "contributionsCrpList" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectContributionsCrpList", "nameSpace":"/projects", "action":"${crpSession}/contributionsCrpList" , "param": "projectID=${project.id}"},
  {"label":"projectContributionCrp", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/relationsPopupMacro.ftl" as popUps /]

[#assign startYear = (project.projectInfo.startDate?string.yyyy)?number /]
[#assign endYear = (project.projectInfo.endDate?string.yyyy)?number /]


[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]

[#--  [@customForm.qaPopUp /]  --]

[#if action.hasSpecificities('feedback_active') ]
  [#list feedbackComments as feedback]
    [@customForm.qaPopUpMultiple fields=feedback.qaComments name=feedback.fieldDescription index=feedback_index canLeaveComments=(action.canLeaveComments()!false)/]
  [/#list]
  <div id="qaTemplate" style="display: none">
    [@customForm.qaPopUpMultiple canLeaveComments=(action.canLeaveComments()!false) template=true/]
  </div>
[/#if]

<input type="hidden" id="sectionNameToFeedback" value="projectContributionCrp" />

<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projectOutcomes.ftl" /]
        
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          [#--  Feedback Status --]
          [#if action.hasSpecificities('feedback_active') ]
            <div class="form-group col-md-12 legendContent">
              <div class="colors">
                <div class="col-md-12 form-group "><b>Feedback status:</b></div>
                <div class="color col-md-4"><img src="${baseUrlCdn}/global/images/comment.png" class="qaCommentStatus feedbackStatus">[@s.text name="feedbackStatus.blue" /]</div>
                <div class="color col-md-4"><img src="${baseUrlCdn}/global/images/comment_yellow.png" class="qaCommentStatus feedbackStatus">[@s.text name="feedbackStatus.yellow" /]</div>
                <div class="color col-md-4"><img src="${baseUrlCdn}/global/images/comment_green.png" class="qaCommentStatus feedbackStatus">[@s.text name="feedbackStatus.green" /]</div>
              </div>
            </div>
          [/#if]

          [#-- Back --]
          <small class="pull-right">
            <a href="[@s.url action='${crpSession}/contributionsCrpList'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the clusters contributions
            </a>
          </small>
          
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="projectOutcome.projectContribution" /] </h3>

          <span id="parentID" style="display: none;">${projectOutcomeID!}</span>
          <span id="phaseID" style="display: none;">${phaseID!}</span>
          <span id="userID" style="display: none;">${currentUser.id!}</span>
          <span id="projectID" style="display: none;">${projectID!}</span>
          <span id="userCanManageFeedback" style="display: none;">${(action.canManageFeedback(projectID)?c)!}</span>
          <span id="userCanLeaveComments" style="display: none;">${(action.canLeaveComments()?c)!}</span>
          <span id="isFeedbackActive" style="display: none;">${(action.hasSpecificities('feedback_active')?c)!}</span>

          [#-- Outcomen name --]
          [#assign showOutcomeValue = projectOutcome.crpProgramOutcome.srfTargetUnit??  && projectOutcome.crpProgramOutcome.srfTargetUnit.id?? && (projectOutcome.crpProgramOutcome.srfTargetUnit.id != -1) /]

          <div class="grayBox">
            <div class="col-md-12">
              <strong>${(projectOutcome.crpProgramOutcome.crpProgram.acronym)!} - Performance Indicator ${(projectOutcome.crpProgramOutcome.year)!}</strong>: ${projectOutcome.crpProgramOutcome.description}
            </div>
            <div class="clearfix"></div>
            [#if showOutcomeValue]
            <div class="form-group">
            [#if !action.isAiccra()]
              <div class="col-md-4"><strong>Target Value:</strong> ${projectOutcome.crpProgramOutcome.value} </div>
            [#else]
              <div class="col-md-4"><strong>AICCRA Target Value:</strong> ${projectOutcome.crpProgramOutcome.value} </div>
            [/#if]
            <div class="col-md-6"><strong>Target Unit:</strong> ${projectOutcome.crpProgramOutcome.srfTargetUnit.name}</div>
            </div>
            [/#if]
            <div class="clearfix"></div>
          </div>
          <br />
          
          [#-- Project Targets --]
          [#assign showExpectedTarget = true /]
          [#assign showAchievedTarget = (reportingActive && (endYear == currentCycleYear)) /]
          
          
          <div class="borderBox">
            [#-- Project Outcome expected target (AT THE BEGINNING) --]
            [#if showExpectedTarget]
            <h5 class="sectionSubTitle">[@s.text name="projectOutcome.contributionToThisOutcome" /]</h5>
            <div class="form-group">
              <div class="row form-group" style="display:${showOutcomeValue?string('block', 'none')}">
                <div class="col-md-5 input-container">
                [#if !action.isAiccra()]
                  [#if editable]
                    [@customForm.input name="projectOutcome.expectedValue" type="text"  placeholder="" className="targetValue" required=true  editable=!reportingActive/]
                  [#else]
                    <label for="">[@s.text name="projectOutcome.expectedValue" /]:</label>
                    <div class="input"><p class="text">${(projectOutcome.expectedValue)!'No expected value indicated'}</p></div>
                  [/#if]
                [#else]
                  [#if editable]
                    [@customForm.input name="projectOutcome.expectedValue" type="text"  placeholder="" className="targetValue" required=true  editable=!reportingActive && editOutcomeExpectedValue/]
                  [#else]
                    <label for="">[@s.text name="projectOutcome.expectedValue" /]:</label>
                    <div class="input"><p class="text">${(projectOutcome.expectedValue)!'No expected value indicated'}</p></div>
                  [/#if]
                [/#if]
                </div>
                [#if !action.isAiccra()]
                <div class="col-md-7">
                  <div class="select">
                    <label for="">[@s.text name="projectOutcome.expectedUnit" /]:</label>
                    <div class="selectList">   
                        [#if projectOutcome.crpProgramOutcome.srfTargetUnit?has_content]
                        <input type="hidden" name="projectOutcome.expectedUnit.id" value="${(projectOutcome.crpProgramOutcome.srfTargetUnit.id)!}" class="">
                        <p class="text">${(projectOutcome.crpProgramOutcome.srfTargetUnit.name)!}</p>
                        [/#if]
                    </div> 
                  </div>
                </div>
                [/#if]
              </div>
              <div class="form-group ">
                [@customForm.textArea name="projectOutcome.narrativeTarget" required=true className="limitWords-100" editable=editable && (!reportingActive || (!(projectOutcome.narrativeTarget?has_content)!false))/]
              </div>
              
            </div> 
            [/#if]
            
            [#-- Project Outcome achieved target (AT THE END) --]
            [#if showAchievedTarget]
            <h5 class="sectionSubTitle">Achieved Target</h5>
            <div class="form-group">
              <div class="row form-group" style="display:${showOutcomeValue?string('block', 'none')}">
                <div class="col-md-5">
                  [#if editable]
                    [@customForm.input name="projectOutcome.achievedValue" type="text"  placeholder="" className="targetValue ${reportingActive?string('fieldFocus','')}" required=true /]
                  [#else]
                    <label for="">[@s.text name="projectOutcome.achievedValue" /]:</label>
                    <div class="input"><p>${(projectOutcome.achievedValue)!'No achieved value indicated'}</p></div>
                  [/#if]
                </div>
                <div class="col-md-7">
                  <div class="select">
                    <label for="">[@s.text name="projectOutcome.achievedUnit" /]:</label>
                    <div class="selectList">   
                        <input type="hidden" name="projectOutcome.achievedUnit.id" value="${(projectOutcome.crpProgramOutcome.srfTargetUnit.id)!}" class="">
                        <p>${(projectOutcome.crpProgramOutcome.srfTargetUnit.name)!'Prefilled if available'}</p>
                    </div> 
                  </div>
                </div>
              </div>
              <div class="form-group">
                [@customForm.textArea name="projectOutcome.narrativeAchieved" required=true className="limitWords-100 ${reportingActive?string('fieldFocus','')}" editable=editable /]
              </div>
            </div>
            [/#if]
                        
            [#-- Baseline Indicators --]
            [#if !action.isAiccra()]
              [#if action.hasSpecificities('crp_baseline_indicators') && ((projectOutcome.crpProgramOutcome.crpProgram.baseLine)!false) && ((projectOutcome.crpProgramOutcome.indicators?has_content)!false)]
                <h5 class="sectionSubTitle">Progress to Targets</h5>
                <div class="form-group">
                  <div class="" id="baseline">
                    <div class="form-group text-right">
                      [#if (projectOutcome.crpProgramOutcome.file.fileName??)!false]
                        <a href="${action.getBaseLineFileURL((projectOutcome.crpProgramOutcome.id?string)!-1)}${ (projectOutcome.crpProgramOutcome.file.fileName)!}" target="_blank" class="downloadBaseline"><img src="${baseUrlCdn}/global/images/pdf.png" width="20px" alt="Download document" />&nbsp &nbsp Download Indicator Guidance &nbsp &nbsp &nbsp &nbsp</a>
                        [#-- (projectOutcome.crpProgramOutcome.file.fileName)! --] 
                      [#else]
                        <p class="note"><i>[@s.text name="projectOutcome.askForBaselineInstructions" /]</i></p>
                      [/#if]
                    </div>
                    [#-- Indicators --]
                    [#list projectOutcome.crpProgramOutcome.indicators as  indicator   ]
                    [#if action.isAiccra()]
                      [@baselineAiccraIndicatorMacro element=indicator name="projectOutcome.indicators" index=indicator_index AREditable=true/]
                    [#else]
                      [@baselineIndicatorMacro element=indicator name="projectOutcome.indicators" index=indicator_index  /]
                    [/#if]
                    [/#list]
                  </div>
                </div>
              [/#if]
             [/#if]
            
          </div>
                    
          [#-- Project Milestones and Communications contributions per year--]
          <h4 class="headTitle"> [@s.text name="projectOutcome.contributionToMilestones" /]</h4>
          
          [#-- List milestones  --]
          <div class="milestonesYearBlock borderBox" listname="milestonesProject">
            <div class="milestonesYearList">
              [#if milestonesProject?has_content]
                [#list milestonesProject as milestone]
                  [@milestoneMacro element=milestone name="projectOutcome.milestones" index=milestone_index /]
                [/#list]
              [#else]
                <p class="emptyMessage text-center">There is not a Intermediate Target added.</p>
              [/#if]
            </div>
            [#-- Select a milestone  --]
            [#if false][#-- Change false to editable to enable the component --]
            
            <div class="milestonesYearSelect"> 
              <div class="pull-left"> <span class="glyphicon glyphicon-plus"></span>  &nbsp</div>
              <span class="milestonesSelectedIds" style="display:none">[#if milestonesProject?has_content][#list milestonesProject as e]${(e.id)!}[#if e_has_next],[/#if][/#list][/#if]</span>
              [@customForm.select name="" label="" disabled=!canEdit i18nkey="projectContributionCrp.selectMilestone${reportingActive?string('.reporting', '')}"  listName="" keyFieldName="id" displayFieldName="title" className="" value="" /]
            </div>
            [/#if]
          </div>        
          
          [#-- Communications --]
          [#if reportingActive && action.hasSpecificities('crp_show_project_outcome_communications') ]  
          <div class="">
            <h4 class="headTitle">Communications </h4>
            <div class="borderBox" id="communicationsYear-${currentCycleYear}">
              [#assign comunication = action.loadProjectCommunication(currentCycleYear) /]
              [#assign comunicationIndex = action.getIndexCommunication(currentCycleYear) /]
              <input type="hidden" name="projectOutcome.communications.id" value=${(projectOutcome.communications.id)!"-1"} />
              <input type="hidden" name="projectOutcome.communications[${comunicationIndex}].year" value="${currentCycleYear}"/>
              <div class="communicationsBlock form-group">
                <div class="form-group">
                  [@customForm.textArea name="projectOutcome.communications[${comunicationIndex}].communication" i18nkey="projectOutcome.communicationEngagement" required=isYearRequired(currentCycleYear) className="limitWords-100 fieldFocus" editable=editable /]
                </div>
              </div>
            </div>
          </div>
          [/#if]          
           
          <br>       
          [#if reportingActive]
            <div class="deliverableTabs"> 
              <ul class="nav nav-tabs" role="tablist"> 
                 <li role="presentation" class=""><a index="1" href="#deliverable-mainInformation" aria-controls="info" role="tab" data-toggle="tab">Progress ${currentCycleYear}</a></li>                       
                 <li role="presentation" class="active"><a index="2" href="#deliverable-disseminationMetadata" aria-controls="metadata" role="tab" data-toggle="tab">Reporting ${currentCycleYear}</a></li>                            
              </ul>
              
              <div class="tab-content ">          
                [#-- Progress tab --]  
                  <div id="deliverable-mainInformation" role="tabpanel" class="tab-pane fade">
                    [#if action.isAiccra()  && projectOutcomeLastPhase?has_content && projectOutcomeLastPhase.crpProgramOutcome?has_content && projectOutcomeLastPhase.crpProgramOutcome.indicators?has_content && projectOutcomeLastPhase.crpProgramOutcome.indicators?size != 0]
                      [#-- 
                      && projectOutcomeLastPhase.crpProgramOutcome?has_content && projectOutcomeLastPhase.crpProgramOutcome.indicators?has_content
                      --]
                      <h4 class="headTitle">Progress to Targets</h4>
                      <div class="nextUsersBlock borderBox">
                        <div class="nextUsersList">
                          [#-- Baseline Indicators --]
                          [#if action.hasSpecificities('crp_baseline_indicators') && ((projectOutcomeLastPhase.crpProgramOutcome.crpProgram.baseLine)!false) && ((projectOutcomeLastPhase.crpProgramOutcome.indicators?has_content)!false)]
                            <h5 class="sectionSubTitle">Progress to Key Performance Indicator</h5>
                            <div class="form-group">
                              <div class="" id="baseline">
                                <div class="form-group text-right">
                                  [#if (projectOutcomeLastPhase.crpProgramOutcome.file.fileName??)!false]
                                    <a href="${action.getBaseLineFileURL((projectOutcomeLastPhase.crpProgramOutcome.id?string)!-1)}&filename=${ (projectOutcomeLastPhase.crpProgramOutcome.file.fileName)!}" target="_blank" class="downloadBaseline"><img src="${baseUrlCdn}/global/images/pdf.png" width="30px" alt="Download document" />&nbsp &nbsp Download Indicator Guidance &nbsp &nbsp &nbsp &nbsp</a> 
                                  [#else]
                                    <p class="note"><i>[@s.text name="projectOutcome.askForBaselineInstructions" /]</i></p>
                                  [/#if]
                                </div>
                                [#-- Indicators --]
                                [#list projectOutcomeLastPhase.crpProgramOutcome.indicators as  indicator   ]
                                  [#if action.isAiccra()]
                                    [@baselineAiccraPrevIndicatorMacro element=indicator name="projectOutcomeLastPhase.indicators" index=indicator_index  AREditable=false/]
                                  [#else]
                                    [@baselineIndicatorMacro element=indicator name="projectOutcome.indicators" index=indicator_index  /]
                                  [/#if]
                                [/#list]
                              </div>
                            </div>
                          [/#if]
                        </div>           
                      </div>
                    [/#if]
                  </div>  
                  [#-- Reporting tab --]
                  
                    <div id="deliverable-disseminationMetadata" role="tabpanel" class="tab-pane fade in active">
                      [#if action.isAiccra() && projectOutcome.crpProgramOutcome.indicators?size != 0]
                        <h4 class="headTitle">Progress to Targets</h4>
                        <div class="nextUsersBlock borderBox">
                          <div class="nextUsersList">
                            [#-- Baseline Indicators --]
                            [#if action.hasSpecificities('crp_baseline_indicators') && ((projectOutcome.crpProgramOutcome.crpProgram.baseLine)!false) && ((projectOutcome.crpProgramOutcome.indicators?has_content)!false)]
                              <h5 class="sectionSubTitle">Progress to Key Performance Indicator</h5>
                              <div class="form-group">
                                <div class="" id="baseline">
                                  <div class="form-group text-right">
                                    [#if (projectOutcome.crpProgramOutcome.file.fileName??)!false]
                                      <a href="${action.getBaseLineFileURL((projectOutcome.crpProgramOutcome.id?string)!-1)}&filename=${ (projectOutcome.crpProgramOutcome.file.fileName)!}" target="_blank" class="downloadBaseline"><img src="${baseUrlCdn}/global/images/pdf.png" width="30px" alt="Download document" />&nbsp &nbsp Download Indicator Guidance &nbsp &nbsp &nbsp &nbsp</a> 
                                    [#else]
                                      <p class="note"><i>[@s.text name="projectOutcome.askForBaselineInstructions" /]</i></p>
                                    [/#if]
                                  </div>
                                  [#-- Indicators --]
                                  [#list projectOutcome.crpProgramOutcome.indicators as  indicator   ]
                                    [#if action.isAiccra()]
                                      [@baselineAiccraIndicatorMacro element=indicator name="projectOutcome.indicators" index=indicator_index AREditable=true/]
                                    [#else]
                                      [@baselineIndicatorMacro element=indicator name="projectOutcome.indicators" index=indicator_index  /]
                                    [/#if]
                                  [/#list]
                                </div>
                              </div>
                            [/#if]
                          </div>           
                        </div>
                      [#else]
                        <h5 class="headTitle">No Progress to Target indicators added</h5>
                      [/#if]
                    </div>
                  
                        
              </div>   
            </div>
          [#else]     
            [#if action.isAiccra() && projectOutcome.crpProgramOutcome.indicators?size != 0]
                <h4 class="headTitle">Progress to Targets</h4>
                <div class="nextUsersBlock borderBox">
                      <div class="nextUsersList">
                        [#-- Baseline Indicators --]
                        [#if action.hasSpecificities('crp_baseline_indicators') && ((projectOutcome.crpProgramOutcome.crpProgram.baseLine)!false) && ((projectOutcome.crpProgramOutcome.indicators?has_content)!false)]
                          <h5 class="sectionSubTitle">Progress to Key Performance Indicator</h5>
                          <div class="form-group">
                            <div class="" id="baseline">
                              <div class="form-group text-left">
                                [#if (projectOutcome.crpProgramOutcome.file.fileName??)!false]
                                  <p><b>Baseline Instructions:</b></p>
                                  <a href="${action.getBaseLineFileURL((projectOutcome.crpProgramOutcome.id?string)!-1)}&filename=${ (projectOutcome.crpProgramOutcome.file.fileName)!}" target="_blank" class="downloadBaseline"><img src="${baseUrlCdn}/global/images/pdf.png" width="30px" alt="Download document" />&nbsp &nbsp Download Indicator Guidance &nbsp &nbsp &nbsp &nbsp</a>
                                  [#-- ${ (projectOutcome.crpProgramOutcome.file.fileName)!} --]
                                  <p><br></p> 
                                [#else]
                                  <p class="note"><i>[@s.text name="projectOutcome.askForBaselineInstructions" /]</i></p>
                                [/#if]
                              </div>
                              [#-- Indicators --]
                              [#list projectOutcome.crpProgramOutcome.indicators as  indicator   ]
                                [#if action.isAiccra()]
                                  [@baselineAiccraIndicatorMacro element=indicator name="projectOutcome.indicators" index=indicator_index  AREditable=true/]
                                [#else]
                                  [@baselineIndicatorMacro element=indicator name="projectOutcome.indicators" index=indicator_index  /]
                                [/#if]
                              [/#list]
                            </div>
                          </div>
                        [/#if]
                      </div>           
                </div>
            [/#if]
          [/#if]        
          
          [#-- capdev --]
          [#if totalParticipants?number > 0]
          <h4 class="headTitle"> Capacity Development</h4>
          <div class="borderBox">
                   [#-- CapDevCharts--]
                    <div class="form-group row">
                      <div class="col-md-4">
                        <div id="" class="simpleBox numberBox">
                            <label for="">Total of Participants</label><br />
                            <span>${(totalParticipants?number?string(",##0"))!0}</span>
                         </div>
                        <div id="" class="simpleBox numberBox">
                            <label for="">Total of Females</label><br />
                            <span>${(totalFemales?number?string(",##0"))!0}</span>
                         </div>
                        <div id="" class="simpleBox numberBox">
                            <label for="">Total of Africans</label><br />
                            <span>${(totalAfricans?number?string(",##0"))!0}</span>
                         </div>
                         [#--  
                         <div id="" class="simpleBox numberBox">
                            <label for="">Participants in [@s.text name="totalParticipantFormalTraining" /]</label><br />
                            <span>${(totalParticipantFormalTraining?number?string(",##0"))!0}</span>
                         </div>
                         --]
                      </div>
                      <div class="col-md-8">
                        [#-- Trainees in Short-Term --]
                        [#if (((totalParticipantFormalTrainingShortMale)!0) + ((totalParticipantFormalTrainingShortFemale)!0)) > 0 ]
                        <div id="chart12" class="chartBox simpleBox">
                          [#assign chartData = [
                            {"name":"Male",   "value": "${(totalParticipantFormalTrainingShortMale)!0}"},
                            {"name":"Female", "value": "${(totalParticipantFormalTrainingShortFemale)!0}"}
                          ] /] 
                          <ul class="chartData" style="display:none">
                            <li>
                              <span>[@s.text name="{customLabel}" /]</span>
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
                        [/#if]
                        <br />
                        [#-- Trainees in Long-Term --]
                        [#if (((totalParticipantFormalTrainingLongMale)!0) + ((totalParticipantFormalTrainingLongFemale)!0)) > 0 ]
                        <div id="chart13" class="chartBox simpleBox">
                          [#assign chartData = [
                            {"name":"Male",   "value": "${(totalParticipantFormalTrainingLongMale)!0}",   "valuePhD": "${(totalParticipantFormalTrainingPhdMale)!0}"}
                            {"name":"Female", "value": "${(totalParticipantFormalTrainingLongFemale)!0}",   "valuePhD": "${(totalParticipantFormalTrainingPhdFemale)!0}"}
                          ] /] 
                          <ul class="chartData" style="display:none">
                            <li>
                              <span>[@s.text name="chart13" /]</span>
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
                         [/#if]
                      </div>
                    </div>
                    
                    [#-- Deliverables Participants & Trainees --]
                    <div class="form-group">
                      <h4 class="simpleTitle headTitle annualReport-table">[@s.text name="Deliverables Participants & Trainees" /]</h4>
                      <div class="viewMoreSyntesis-block">
                      
                      <div id="Layer1" style="width:100%; min-height:200px height:auto; overflow: auto;"><br>
                        [@tableParticipantsTrainingsMacro list=(deliverableParticipants)![] /]
                      </div>
                      
                     </div>
                    </div> 
          </div>  
          [/#if]
          
          [#-- Next Users --]
          [#-- For A4NH CRP, nextusers aren't required --]
          [#if action.hasSpecificities('crp_next_users')]
          <h4 class="headTitle">(Next) Users </h4>
          <div class="nextUsersBlock borderBox">
            <div class="nextUsersList">
              [#if projectOutcome.nextUsers?has_content]
                [#list projectOutcome.nextUsers as nextUser]
                  [@nextUserMacro element=nextUser name="projectOutcome.nextUsers" index=nextUser_index /]
                [/#list]
              [#else]
                [@nextUserMacro element={} name="projectOutcome.nextUsers" index=0 /]
              [/#if]
            </div>
            [#if editable && !reportingActive]
              <div class="addNextUser bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addNextUser"/]</div>
            [/#if]
          </div>
          [/#if]
                    
          [#-- Lessons and progress --]
          [#if !action.isProjectNew(project.id) && action.isReportingActive()]
          <div id="lessons" class="borderBox">
            [#-- Lessons learnt from last planning/reporting cycle --]
            [#if (projectOutcome.projectComponentLessonPreview.lessons?has_content)!false]
            <div class="fullBlock">
              <label>[@customForm.text name="projectOutcome.previousLessons.${reportingActive?string('reporting','planning')}" param="${reportingActive?string(reportingYear,planningYear-1)}" /]:</label>
              <div class="textArea"><p>${projectOutcome.projectComponentLessonPreview.lessons}</p></div>
            </div>
            [/#if]
            [#-- Planning/Reporting lessons --]
            <div class="fullBlock ">
              <input type="hidden" name="projectOutcome.projectComponentLesson.id" value=${(projectOutcome.projectComponentLesson.id)!"-1"} />
              <input type="hidden" name="projectOutcome.projectComponentLesson.year" value=${reportingActive?string(reportingYear,planningYear)} />
              <input type="hidden" name="projectOutcome.projectComponentLesson.componentName" value="${actionName}">
              [@customForm.textArea name="projectOutcome.projectComponentLesson.lessons" i18nkey="projectOutcome.lessons.${reportingActive?string('reporting','planning')}" help="projectOutcome.lessons.help" helpIcon=false className=" ${reportingActive?string('fieldFocus','')}" required=true editable=editable /]
            </div>
          </div>
          [/#if]
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projectOutcomes.ftl" /]

        [/@s.form] 
      </div>
    </div>  
</section>
[/#if]

[#-- Milestone Template --]
[@milestoneMacro element={} name="projectOutcome.milestones" index="-1" isTemplate=true /]

[#-- Next user Template --]
[@nextUserMacro element={} name="projectOutcome.nextUsers" index="-1" isTemplate=true /]



  
[#include "/WEB-INF/global/pages/footer.ftl"]


[#macro milestoneMacro element name index isTemplate=false]
  <div id="milestoneYear-${isTemplate?string('template', index)}" class="milestoneYear simpleBox" style="display:${isTemplate?string('none','block')}">
    [#-- Milestone content --]
    [#if isTemplate]
      [#local year = -1 /]
      [#local projectMilestone = {} /]
      [#local projectMilestoneIndex = -1 /]
    [#else]
      [#local year = element.year /]
      [#local projectMilestone = action.getMilestone(element.id, year) /]
      [#local projectMilestoneIndex = action.getIndexMilestone(element.id, year) /]
    [/#if]
    
    [#-- Getting the milestone year --]
    [#local hasExtendedYear = (element.extendedYear?has_content) && (element.extendedYear != -1)]
    [#local milestoneYear =  (element.year)!currentCycleYear ]
    [#if hasExtendedYear]
      [#local milestoneYear =  element.extendedYear ]
    [/#if]
    
    [#local isNewAtReporting =  reportingActive && (!(projectMilestone.narrativeTarget?has_content))!true]

    [#-- Remove Button --]
    [#-- 
    [#if editable && (!reportingActive || isNewAtReporting) && (milestoneYear gte currentCycleYear)!true]<div class="removeElement removeIcon removeProjectMilestone" title="Remove"></div>[/#if]
    --]
    <div class="leftHead sm">
      <span class="index">${index+1}</span>
      <span class="elementId">[@s.text name="projectOutcomeMilestone.projectMilestoneTarget" /] ${(element.year)!}</span>
    </div>

    [#local showMilestoneValue = element.srfTargetUnit??  && element.srfTargetUnit.id?? && (element.srfTargetUnit.id != -1) /]
    [#local prefilled]<p style="opacity:0.6">[@s.text name="form.values.fieldEmpty" /]</p>[/#local]
    
    [#-- Milestone Title --]
    <div class="form-group grayBox">
      [#if showMilestoneValue]
        <div class="form-group pull-right">
          <strong>AICCRA Target to ${(element.year)!}:</strong> ${(element.value)!}
        </div>
      [/#if]
      [#-- Milestone Year --]
      <div class="row">
        <div class="col-md-6">
          <strong>Cluster contribution to this indicator in <span class="crpMilestoneYear">${(element.year)!} [#if hasExtendedYear] Extended to ${(element.extendedYear)!}[/#if]  </span> </strong> 
        </div>
      </div>
      
 
      [#--  Title --]
      <div class="form-group">
        <span class="title">${(element.title)!}</span>
      </div>
      [#--  Means of verification
      <div class="form-group">
        <strong>[@s.text name="outcome.milestone.powbMilestoneVerification" /]</strong>
        <br /> [#if (element.powbMilestoneVerification?has_content)!false]${element.powbMilestoneVerification}[#else]${prefilled}[/#if]
      </div>
       --]
      [#-- DAC Markers --]
      [#if !action.isAiccra()]
        <div class="form-group row">
          <div class="col-md-3"><strong>Gender</strong> <br /> ${(element.genderFocusLevel.powbName)!prefilled} </div>
          <div class="col-md-3"><strong>Youth</strong> <br /> ${(element.youthFocusLevel.powbName)!prefilled}</div>
          <div class="col-md-3"><strong>CapDev</strong> <br /> ${(element.capdevFocusLevel.powbName)!prefilled}</div>
          <div class="col-md-3"><strong>Climate Change</strong> <br /> ${(element.climateFocusLevel.powbName)!prefilled}</div>
        </div>
      [/#if]
    </div>
    
    <div role="tabpanel" class="tab-pane [#if milestoneYear == currentCycleYear]active[/#if]" id="milestoneYear${index}-${milestoneYear}">
      [#local customName = "${name}[${projectMilestoneIndex}]" /]
      <div class="outcomeMilestoneYear">
        [#-- Hidden inputs --]
        <input type="hidden" name="${customName}.id" value="${(projectMilestone.id)!}" />
        <input type="hidden" name="${customName}.year" class="crpMilestoneYearInput" value="${(year)!}" class="year" />
        <input type="hidden" name="${customName}.crpMilestone.id" value="${(element.id)!}" class="crpMilestoneId" />
        
        <div class="row form-group milestoneTargetValue" style="display:${showMilestoneValue?string('block', 'none')}">
          <div class="col-md-4 input-container" style="padding-top:3px">
            [@customForm.input name="${customName}.settedValue" i18nkey="projectOutcomeMilestone.settedValue" type="text"  placeholder="" className="targetValue" required=false editable=action.canAccessSuperAdmin() && isYearRequired(milestoneYear) /]
          </div>
          
          [#-- All phases --]
            <div class="col-md-3 input-container">
              [@customForm.input name="${customName}.expectedValue" i18nkey="projectOutcomeMilestone.finalExpectedValue" type="text"  placeholder="" className="targetValue" required=isYearRequired(milestoneYear) && POWB editable=(editable || isTemplate) && !reportingActive && POWB && (milestoneYear gte currentCycleYear)!true /]
            </div>
         
          
          [#-- Upkeep (progress) active --]
          [#if reportingActive || upKeepActive]
            <div class="col-md-3 input-container">
              [@customForm.input name="${customName}.progressValue" i18nkey="projectOutcomeMilestone.progressValue" type="text"  placeholder="" className="targetValue" required=isYearRequired(milestoneYear) && upKeepActive editable=(editable || isTemplate) && upKeepActive && (milestoneYear gte currentCycleYear)!true /]
            </div>
          [/#if]
      
          [#--  
          <div class="col-md-4">
            <div class="select">
              <label for="">[@s.text name="projectOutcomeMilestone.expectedUnit" /]:</label>
              <div class="selectList">   
                  <input type="hidden" class="crpMilestoneTargetUnitInput" name="${customName}.expectedUnit.id" value="${(element.srfTargetUnit.id)!}" class="">
                  <p class="crpMilestoneTargetUnit">${(element.srfTargetUnit.name)!}</p>
              </div> 
            </div>
          </div>
          --]
          [#-- REPORTING BLOCK --]
          [#if reportingActive]
            [#if !isYearRequired(milestoneYear) && action.isAiccra()]
              <div class="col-md-3 input-container">
                [@customForm.input name="${customName}.achievedValue" i18nkey="projectOutcomeMilestone.achievedValue" type="text"  placeholder="" className=" ${reportingActive?string('fieldFocus','')}" required=isYearRequired(milestoneYear) editable=(editable || isTemplate) && isYearRequired(milestoneYear) /]
              </div>
            [#else]
             [#if isYearRequired(milestoneYear)]
              <div class="col-md-3 input-container">
                [@customForm.input name="${customName}.achievedValue" i18nkey="projectOutcomeMilestone.achievedValue" type="text"  placeholder="" className=" ${reportingActive?string('fieldFocus','')}" required=isYearRequired(milestoneYear) editable=(editable || isTemplate) && isYearRequired(milestoneYear) && reportingActive/]
              </div>
             [/#if]
            [/#if]
          [/#if]
        </div>
        
        <div class="form-group text-area-container">
          [@customForm.textArea name="${customName}.narrativeTarget" i18nkey="projectOutcomeMilestone.expectedNarrative2021" required=isYearRequired(milestoneYear) && !upKeepActive className="limitWords-100" editable=(editable || isTemplate) && !reportingActive && !upKeepActive && (milestoneYear gte currentCycleYear)!true help="projectOutcomeMilestone.expectedNarrative2021.helpText" helpIcon=false/]
              [#if isYearRequired(milestoneYear)]
                <div class="text-left">
                  [@popUps.relationsMacro element=projectOutcome labelText=true /]
                  [@popUps.relationsMacro element=projectOutcome tag="expectedOutcomes" labelText=true /]
                  [@popUps.relationsMacro element=projectOutcome tag="innovationOutcomes" labelText=true /]
                </div>
              [/#if]
        </div>
        [#if !POWB]
          <div class="form-group text-area-container">
            [@customForm.textArea name="${customName}.narrativeProgress" i18nkey="projectOutcomeMilestone.progressNarrative" required=isYearRequired(milestoneYear) && upKeepActive className="limitWords-150 ${(reportingActive)?string('fieldFocus','')}" editable=(editable || isTemplate) && upKeepActive &&( milestoneYear gte currentCycleYear)!true /]
          </div>
        [/#if]
        [#-- REPORTING BLOCK --]
        [#if reportingActive]
        <div class="form-group ">
          [@customForm.textArea name="${customName}.narrativeAchieved" i18nkey="projectOutcomeMilestone.achievedNarrative" required=isYearRequired(milestoneYear) && reportingActive className="limitWords-150 ${(reportingActive)?string('fieldFocus','')}" editable=(editable || isTemplate) &&( milestoneYear gte currentCycleYear)!true && reportingActive /]
        </div>
        [/#if]
        [#-- PROGRESS BLOCK 
        [#if upKeepActive]
          <div class="form-group ">
            [@customForm.textArea name="${customName}.progressNarrative" i18nkey="projectOutcomeMilestone.narrativeProgress" required=isYearRequired(milestoneYear) className="limitWords-150 ${(reportingActive)?string('fieldFocus','')}" editable=(editable || isTemplate) &&( milestoneYear gte currentCycleYear)!true /]
          </div>
        [/#if]
        --]
      </div>
    </div>
       
  </div>
[/#macro]

[#macro nextUserMacro element name index isTemplate=false]
  <div id="nextUser-${isTemplate?string('template', index)}" class="nextUser simpleBox" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]" /]
    [#-- Remove Button --]
    [#if editable && !reportingActive]<div class="removeIcon removeNextUser" title="Remove"></div>[/#if]
    <div class="leftHead sm">
      <span class="index">${index+1}</span>
      <span class="elementId">[@s.text name="projectOutcomeNextUser.projectNextUser" /]</span>
    </div>
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
     
    <div class="form-group">
      [#-- Title --]
      <div class="form-group input-container">
        [@customForm.input name="${customName}.nextUser" i18nkey="projectOutcomeNextUser.title" help="projectOutcomeNextUser.title.help" required=true className="limitWords-20" editable=editable && (!reportingActive || (!element.nextUser?has_content)!true) /]
      </div>
      [#-- Knowledge, attitude, skills and practice changes expected in this next user --]
      <div class="form-group ">
        [@customForm.textArea name="${customName}.knowledge" i18nkey="projectOutcomeNextUser.knowledge" help="projectOutcomeNextUser.knowledge.help" required=true className="limitWords-100" editable=editable && (!reportingActive || (!element.knowledge?has_content)!true) /]
      </div>
      [#-- Strategies will be used to encourage and enable this next user to utilize deliverables and adopt changes --]
      <div class="form-group ">
        [@customForm.textArea name="${customName}.strategies" i18nkey="projectOutcomeNextUser.strategies" help="projectOutcomeNextUser.strategies.help" required=true className="limitWords-100" editable=editable && (!reportingActive || (!element.strategies?has_content)!true) /]
      </div>
     
    </div>
    
    [#-- Reporting --]
    [#if reportingActive]
      <br /> 
      <div class="" id="nextUserYear-${currentCycleYear}">
        <div class="form-group ">
          [@customForm.textArea name="${customName}.knowledgeReport" i18nkey="projectOutcomeNextUser.reportOnProgress" help="" required=true className="limitWords-200 ${reportingActive?string('fieldFocus','')}" editable=editable /]
        </div>
        <div class="form-group ">
          [@customForm.textArea name="${customName}.strategiesReport" i18nkey="projectOutcomeNextUser.strategiesEncourage" help="" required=true className="limitWords-100 ${reportingActive?string('fieldFocus','')}" editable=editable /]
        </div> 
        <div class="clearfix"></div>
      </div> 
    [/#if]
  </div>
[/#macro]

[#macro baselineIndicatorMacro element name index isTemplate=false]
  <div id="baselineIndicator-${isTemplate?string('template', index)}" class="baselineIndicator simpleBox" style="display:${isTemplate?string('none','block')}">
    [#local indexIndicator = action.getIndexIndicator(element.id)! /]
    [#local projectOutcomeIndicator  = action.getIndicator(element.id)! /]
    [#local customName = "${name}[${indexIndicator}]" /]
    <div class="leftHead gray sm">
      <span class="index">${index+1}</span>
    </div>
    <div class="form-group grayBox">
      <strong>${element.indicator}</strong>
    </div>
    <input type="hidden" name="${customName}.id" value="${(projectOutcomeIndicator.id)!}" >
    <input type="hidden" name="${customName}.crpProgramOutcomeIndicator.id" value="${(projectOutcomeIndicator.crpProgramOutcomeIndicator.id)!}" >
    <div class="form-group row">
      <div class="col-md-3">
        [@customForm.input name="${customName}.value" i18nkey="projectOutcomeBaseline.expectedValue" className="targetValue" placeholder="Numeric Value" value="${(projectOutcomeIndicator.value)!}" required=true editable=editable && !reportingActive /]
      </div>
      <div class="col-md-3">
        [#if reportingActive]
          [@customForm.input name="${customName}.valueReporting" i18nkey="projectOutcomeBaseline.achievedValue" className="targetValue" placeholder="Numeric Value" required=true editable=editable /]
        [/#if]
      </div>
      <div class="col-md-3"></div>
    </div>
    <div class="form-group">
      [@customForm.textArea name="${customName}.narrative" i18nkey="projectOutcomeBaseline.expectedNarrative" value="${(projectOutcomeIndicator.narrative)!}" required=true className="limitWords-100" editable=editable && !reportingActive /]
    </div>
    [#if reportingActive]
      <div class="form-group">
        [@customForm.textArea name="${customName}.achievedNarrative" i18nkey="projectOutcomeBaseline.achievedNarrative" required=true className="limitWords-100" editable=editable /]
      </div>
    [/#if]
  </div>
[/#macro]

[#macro baselineAiccraIndicatorMacro element name index isTemplate=false AREditable=true]
  <div id="baselineIndicator-${isTemplate?string('template', index)}" class="baselineIndicator simpleBox" style="display:${isTemplate?string('none','block')}">
    [#local indexIndicator = action.getIndexIndicator(element.id) /]
    [#local projectOutcomeIndicator  = action.getIndicator(element.id) /]
    [#-- [#local customName = "${name}[${indexIndicator}]" /] --]
    [#local customName = "${name}[${index}]" /]
    <div class="leftHead gray sm">
      <span class="index">${index+1}</span>
    </div>
    <div class="form-group grayBox">
      <strong>${element.indicator}</strong>
    </div>
    <input type="hidden" name="${customName}.id" value="${(projectOutcomeIndicator.id)!}" >
    <input type="hidden" name="${customName}.crpProgramOutcomeIndicator.id" value="${(projectOutcomeIndicator.crpProgramOutcomeIndicator.id)!}" >
    
    [#-- [#if index==0]
      <div class="form-group row">
        <div class="col-md-3">
          [@customForm.input name="${customName}.value" i18nkey="projectOutcomeBaseline.expectedValue" className="targetValue" placeholder="Numeric Value" value="${(projectOutcomeIndicator.value)!}" required=true editable=editable && !reportingActive /]
        </div>
        <div class="col-md-3">
          [#if reportingActive]
            [@customForm.input name="${customName}.valueReporting" i18nkey="projectOutcomeBaseline.achievedValue" className="targetValue" placeholder="Numeric Value" required=true editable=editable /]
          [/#if]
        </div>
        <div class="col-md-3"></div>
      </div>
    [/#if]
    --]
    
      <div class="form-group ">
        [@customForm.textArea name="${customName}.narrative" i18nkey="projectOutcomeBaseline.expectedNarrative" value="${(projectOutcomeIndicator.narrative)!}" required=true className="limitWords-100" editable=editable && AREditable/]
        [#-- && !reportingActive  --]
      </div>
      [#--  
      [#if reportingActive]
        <div class="form-group">
          [@customForm.textArea name="${customName}.achievedNarrative" i18nkey="projectOutcomeBaseline.achievedNarrative" required=true className="limitWords-100" editable=editable /]
        </div>
      [/#if]
      --]
  </div>
[/#macro]

[#macro baselineAiccraPrevIndicatorMacro element name index isTemplate=false AREditable=true]
  <div id="baselineIndicator-${isTemplate?string('template', index)}" class="baselineIndicator simpleBox" style="display:${isTemplate?string('none','block')}">
    [#local indexIndicator = action.getPrevIndexIndicator(element.id) /]
    [#local projectOutcomePrevIndicator  = action.getPrevIndicator(element.id) /]
    [#local customName = "${name}[${indexIndicator}]" /]
    <div class="leftHead gray sm">
      <span class="index">${index+1}</span>
    </div>
    <div class="form-group grayBox">
      <strong>${element.indicator}</strong>
    </div>
    <input type="hidden" name="${customName}.id" value="${(projectOutcomePrevIndicator.id)!}" >
    <input type="hidden" name="${customName}.crpProgramOutcomeIndicator.id" value="${(projectOutcomePrevIndicator.crpProgramOutcomeIndicator.id)!}" >
        
      <div class="form-group ">
        [@customForm.textArea name="${customName}.narrative" i18nkey="projectOutcomeBaseline.expectedNarrative" value="${(projectOutcomePrevIndicator.narrative)!}" required=true className="limitWords-100" editable=editable && AREditable/]
        [#-- && !reportingActive  --]
      </div>

  </div>
[/#macro]

[#macro tableParticipantsTrainingsMacro list]
  <table id="tableParticipantsTrainingsMacro" class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-id">[@s.text name="Activity Event" /]</th>
        <th id="tb-title">[@s.text name="Activity Type" /]</th>        
        <th id="tb-organization-type">[@s.text name="Participants Type" /]</th>
        <th id="tb-type">[@s.text name="Males" /]</th>
        <th id="tb-type">[@s.text name="Females" /]</th>
        <th id="tb-type">[@s.text name="Africans" /]</th>
        <th id="tb-type">[@s.text name="Youth" /]</th>
        <th id="tb-type">[@s.text name="total Participants" /]</th>
        <th id="tb-training-period">[@s.text name="Training Period" /]</th>
        [#--  
        <th id="tb-training-period">[@s.text name="Event Focus" /]</th>
        <th id="tb-training-period">[@s.text name="Likely Outcomes" /]</th>
        --]
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        [#local URL][@s.url namespace="/clusters" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${(item.deliverable.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
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
          [#assign isEstimateTotalParticipants = (item.estimateParticipants?has_content)!false]
          [#assign isEstimateFemales = (item.estimateFemales?has_content)!false]
          [#assign isEstimateAfricans = (item.estimateAfrican?has_content)!false]
          [#assign isEstimateYouth = (item.estimateYouth?has_content)!false]
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
            [#if isEstimateFemales ]
              <i><small> (Estimated value)</small></i>
            [/#if]
          [/#if]
          </td>
          [#-- Number of african --]
          <td class="text-center">
            ${(item.african?number?string(",##0"))!0}
            [#--<p><i><small>(${(item.africanPercentage?number?string(",##0"))!0}% )</small></i><p>--]
            [#if isEstimateAfricans ]
              <i><small> (Estimated value)</small></i>
            [/#if]
          </td>
          [#-- Number of youth --]
          <td class="text-center">
            ${(item.youth?number?string(",##0"))!0}
            [#--<p><i><small>(${(item.youthPercentage?number?string(",##0"))!0}% )</small></i></p>--]
            [#if isEstimateYouth ]
              <i><small> (Estimated value)</small></i>
            [/#if]
          </td>
          [#-- Total Participants --]
          <td class="text-center">
            ${(item.participants?number?string(",##0"))!0}
            [#if isEstimateTotalParticipants ]
              <i><small> (Estimated value)</small></i>
            [/#if]
          </td>
          [#-- Training period of time --]
          <td class="text-center">
            [@utils.tableText value=(item.repIndTrainingTerm.name)!"" /]
          </td>
          [#-- Training period of time --]
          [#--  
          <td class="text-center">
            [@utils.tableText value=(item.focus)!"" /]
          </td>
          --]
          [#-- Training period of time --]
          [#--  
          <td class="text-center">
            [@utils.tableText value=(item.likelyOutcomes)!"" /]
            --]
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

[#-- Get if the year is required--]
[#function isYearRequired year]
  [#if project.projectInfo.endDate??]
    [#assign endDate = (project.projectInfo.endDate?string.yyyy)?number]
    [#if reportingActive]
      [#return  (year == currentCycleYear)  && (endDate gte year) ]
    [#else]
      [#return  (year == currentCycleYear) && (endDate gte year) ]
    [/#if]
  [#else]
    [#return false]
  [/#if]
[/#function]