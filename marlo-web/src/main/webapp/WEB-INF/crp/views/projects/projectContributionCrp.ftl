[#ftl]
[#assign title = "Project Outcome Contribution to CRP" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectOutcomeID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/projects/projectContributionCrp.js", 
  "${baseUrlCdn}/global/js/autoSave.js", 
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ] 
/] 
[#assign customCSS = [ 
  "${baseUrlMedia}/css/projects/projectContributionCrp.css"
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
          
          [#-- Back --]
          <small class="pull-right">
            <a href="[@s.url action='${crpSession}/contributionsCrpList'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the project contributions
            </a>
          </small>
          
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="projectOutcome.projectContribution" /] </h3>
          [#-- Outcomen name --]
          [#assign showOutcomeValue = projectOutcome.crpProgramOutcome.srfTargetUnit??  && projectOutcome.crpProgramOutcome.srfTargetUnit.id?? && (projectOutcome.crpProgramOutcome.srfTargetUnit.id != -1) /]

          <div class="grayBox">
            <div class="col-md-12">
              <strong>${(projectOutcome.crpProgramOutcome.crpProgram.acronym)!} - Outcome ${(projectOutcome.crpProgramOutcome.year)!}</strong>: ${projectOutcome.crpProgramOutcome.description}
            </div>
            <div class="clearfix"></div>
            [#if showOutcomeValue]
            <div class="form-group">
              <div class="col-md-4"><strong>Target Value:</strong> ${projectOutcome.crpProgramOutcome.value} </div>
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
                <div class="col-md-5">
                  [#if editable]
                    [@customForm.input name="projectOutcome.expectedValue" type="text"  placeholder="" className="targetValue" required=true  editable=!reportingActive/]
                  [#else]
                    <label for="">[@s.text name="projectOutcome.expectedValue" /]:</label>
                    <div class="input"><p>${(projectOutcome.expectedValue)!'Prefilled if available'}</p></div>
                  [/#if]
                </div>
                <div class="col-md-7">
                  <div class="select">
                    <label for="">[@s.text name="projectOutcome.expectedUnit" /]:</label>
                    <div class="selectList">   
                        [#if projectOutcome.crpProgramOutcome.srfTargetUnit?has_content]
                        <input type="hidden" name="projectOutcome.expectedUnit.id" value="${(projectOutcome.crpProgramOutcome.srfTargetUnit.id)!}" class="">
                        <p>${(projectOutcome.crpProgramOutcome.srfTargetUnit.name)!}</p>
                        [/#if]
                    </div> 
                  </div>
                </div>
              </div>
              <div class="form-group">
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
                    <div class="input"><p>${(projectOutcome.achievedValue)!'Prefilled if available'}</p></div>
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
            [#if action.hasSpecificities('crp_baseline_indicators') && ((projectOutcome.crpProgramOutcome.crpProgram.baseLine)!false) && ((projectOutcome.crpProgramOutcome.indicators?has_content)!false)]
              <h5 class="sectionSubTitle">Baseline Indicators</h5>
              <div class="form-group">
                <div class="" id="baseline">
                  <div class="form-group text-right">
                    [#if (projectOutcome.crpProgramOutcome.file.fileName??)!false]
                      <a href="${action.getBaseLineFileURL((projectOutcome.crpProgramOutcome.id.toString())!)}${ (projectOutcome.crpProgramOutcome.file.fileName)!}" target="_blank" class="downloadBaseline"><img src="${baseUrlCdn}/global/images/pdf.png" width="20px" alt="" /> ${ (projectOutcome.crpProgramOutcome.file.fileName)!}</a> 
                    [#else]
                      <p class="note"><i>[@s.text name="projectOutcome.askForBaselineInstructions" /]</i></p>
                    [/#if]
                  </div>
                  [#-- Indicators --]
                  [#list projectOutcome.crpProgramOutcome.indicators as  indicator   ]
                    [@baselineIndicatorMacro element=indicator name="projectOutcome.indicators" index=indicator_index  /]
                  [/#list]
                </div>
              </div>
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
                <p class="emptyMessage text-center">There is not a milestone added.</p>
              [/#if]
            </div>
            [#-- Select a milestone  --]
            [#if editable]
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
            <div class="fullBlock">
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
    [#if editable && (!reportingActive || isNewAtReporting) && (milestoneYear gte currentCycleYear)!true]<div class="removeElement removeIcon removeProjectMilestone" title="Remove"></div>[/#if]
    <div class="leftHead sm">
      <span class="index">${index+1}</span>
      <span class="elementId">[@s.text name="projectOutcomeMilestone.projectMilestoneTarget" /]</span>
    </div>

    [#local showMilestoneValue = element.srfTargetUnit??  && element.srfTargetUnit.id?? && (element.srfTargetUnit.id != -1) /]
    [#local prefilled]<p style="opacity:0.6">[@s.text name="form.values.fieldEmpty" /]</p>[/#local]
    
    [#-- Milestone Title --]
    <div class="form-group grayBox">
      [#if showMilestoneValue]
        <div class="form-group pull-right">
          <strong>Target Value:</strong> ${(element.value)!}
        </div>
      [/#if]
      [#-- Milestone Year --]
      <div class="row">
        <div class="col-md-6">
          <strong>Milestone for <span class="crpMilestoneYear">${(element.year)!} [#if hasExtendedYear] Extended to ${(element.extendedYear)!}[/#if]  </span> </strong> 
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
      <div class="form-group row">
        <div class="col-md-3"><strong>Gender</strong> <br /> ${(element.genderFocusLevel.powbName)!prefilled} </div>
        <div class="col-md-3"><strong>Youth</strong> <br /> ${(element.youthFocusLevel.powbName)!prefilled}</div>
        <div class="col-md-3"><strong>CapDev</strong> <br /> ${(element.capdevFocusLevel.powbName)!prefilled}</div>
        <div class="col-md-3"><strong>Climate Change</strong> <br /> ${(element.climateFocusLevel.powbName)!prefilled}</div>
      </div>
    </div>
    
    <div role="tabpanel" class="tab-pane [#if milestoneYear == currentCycleYear]active[/#if]" id="milestoneYear${index}-${milestoneYear}">
      [#local customName = "${name}[${projectMilestoneIndex}]" /]
      <div class="outcomeMilestoneYear">
        [#-- Hidden inputs --]
        <input type="hidden" name="${customName}.id" value="${(projectMilestone.id)!}" />
        <input type="hidden" name="${customName}.year" class="crpMilestoneYearInput" value="${(year)!}" class="year" />
        <input type="hidden" name="${customName}.crpMilestone.id" value="${(element.id)!}" class="crpMilestoneId" />
        
        
        <div class="row form-group milestoneTargetValue" style="display:${showMilestoneValue?string('block', 'none')}">
          <div class="col-md-4">
            [@customForm.input name="${customName}.expectedValue" i18nkey="projectOutcomeMilestone.expectedValue" type="text"  placeholder="" className="targetValue" required=isYearRequired(milestoneYear) editable=(editable || isTemplate) && !reportingActive && (milestoneYear gte currentCycleYear)!true /]
          </div>
          <div class="col-md-4">
            <div class="select">
              <label for="">[@s.text name="projectOutcomeMilestone.expectedUnit" /]:</label>
              <div class="selectList">   
                  <input type="hidden" class="crpMilestoneTargetUnitInput" name="${customName}.expectedUnit.id" value="${(element.srfTargetUnit.id)!}" class="">
                  <p class="crpMilestoneTargetUnit">${(element.srfTargetUnit.name)!}</p>
              </div> 
            </div>
          </div>
          [#-- REPORTING BLOCK --]
          [#if reportingActive]
          <div class="col-md-4">
            [@customForm.input name="${customName}.achievedValue" i18nkey="projectOutcomeMilestone.achievedValue" type="text"  placeholder="" className=" ${reportingActive?string('fieldFocus','')}" required=isYearRequired(milestoneYear) editable=(editable || isTemplate) /]
          </div>
          [/#if]
        </div>
        
        <div class="form-group">
          [@customForm.textArea name="${customName}.narrativeTarget" i18nkey="projectOutcomeMilestone.expectedNarrative" required=isYearRequired(milestoneYear) className="limitWords-100" editable=(editable || isTemplate) && !reportingActive && (milestoneYear gte currentCycleYear)!true /]
        </div>
        [#-- REPORTING BLOCK --]
        [#if reportingActive]
        <div class="form-group">
          [@customForm.textArea name="${customName}.narrativeAchieved" i18nkey="projectOutcomeMilestone.achievedNarrative" required=isYearRequired(milestoneYear) className="limitWords-100 ${(reportingActive)?string('fieldFocus','')}" editable=(editable || isTemplate) &&( milestoneYear gte currentCycleYear)!true /]
        </div>
        [/#if]
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
      <div class="form-group">
        [@customForm.input name="${customName}.nextUser" i18nkey="projectOutcomeNextUser.title" help="projectOutcomeNextUser.title.help" required=true className="limitWords-20" editable=editable && (!reportingActive || (!element.nextUser?has_content)!true) /]
      </div>
      [#-- Knowledge, attitude, skills and practice changes expected in this next user --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.knowledge" i18nkey="projectOutcomeNextUser.knowledge" help="projectOutcomeNextUser.knowledge.help" required=true className="limitWords-100" editable=editable && (!reportingActive || (!element.knowledge?has_content)!true) /]
      </div>
      [#-- Strategies will be used to encourage and enable this next user to utilize deliverables and adopt changes --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.strategies" i18nkey="projectOutcomeNextUser.strategies" help="projectOutcomeNextUser.strategies.help" required=true className="limitWords-100" editable=editable && (!reportingActive || (!element.strategies?has_content)!true) /]
      </div>
     
    </div>
    
    [#-- Reporting --]
    [#if reportingActive]
      <br /> 
      <div class="" id="nextUserYear-${currentCycleYear}">
        <div class="form-group">
          [@customForm.textArea name="${customName}.knowledgeReport" i18nkey="projectOutcomeNextUser.reportOnProgress" help="" required=true className="limitWords-200 ${reportingActive?string('fieldFocus','')}" editable=editable /]
        </div>
        <div class="form-group">
          [@customForm.textArea name="${customName}.strategiesReport" i18nkey="projectOutcomeNextUser.strategiesEncourage" help="" required=true className="limitWords-100 ${reportingActive?string('fieldFocus','')}" editable=editable /]
        </div> 
        <div class="clearfix"></div>
      </div> 
    [/#if]
  </div>
[/#macro]

[#macro baselineIndicatorMacro element name index isTemplate=false]
  <div id="baselineIndicator-${isTemplate?string('template', index)}" class="baselineIndicator simpleBox" style="display:${isTemplate?string('none','block')}">
    [#local indexIndicator = action.getIndexIndicator(element.id) /]
    [#local projectOutcomeIndicator  = action.getIndicator(element.id) /]
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