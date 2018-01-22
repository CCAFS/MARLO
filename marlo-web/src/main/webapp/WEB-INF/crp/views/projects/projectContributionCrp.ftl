[#ftl]
[#assign title = "Project Outcome Contribution to CRP" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectOutcomeID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/projects/projectContributionCrp.js", 
  "${baseUrl}/global/js/autoSave.js", 
  "${baseUrl}/global/js/fieldsValidation.js"
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


[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]
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
          <h3 class="headTitle"> Project Contribution </h3>
          [#-- Outcomen name --]
          [#assign showOutcomeValue = projectOutcome.crpProgramOutcome.srfTargetUnit??  && projectOutcome.crpProgramOutcome.srfTargetUnit.id?? && (projectOutcome.crpProgramOutcome.srfTargetUnit.id != -1) /]
          [#assign pimText][@s.text name="contribution.message" /][/#assign]
          [#if editable && pimText?has_content]
           <div class="note">${pimText}</div>
          [/#if]
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
                    [@customForm.input name="projectOutcome.expectedValue" type="text"  placeholder="" className="targetValue" required=true  /]
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
                [@customForm.textArea name="projectOutcome.narrativeTarget" required=true className="limitWords-100" editable=editable /]
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
                        <input type="hidden" name="projectOutcome.achievedUnit.id" value="${projectOutcome.crpProgramOutcome.srfTargetUnit.id}" class="">
                        <p>${projectOutcome.crpProgramOutcome.srfTargetUnit.name}</p>
                    </div> 
                  </div>
                </div>
              </div>
              <div class="form-group">
                [@customForm.textArea name="projectOutcome.narrativeAchieved" required=true className="limitWords-100 ${reportingActive?string('fieldFocus','')}" editable=editable /]
              </div>
            </div>
            [/#if]
            
            [#-- Cross-cutting contributions --]
            [#if ((project.projectInfo.crossCuttingGender)!false) || ((project.projectInfo.crossCuttingYouth)!false)]
            <h5 class="sectionSubTitle">Cross-cutting contributions</h5>
            <div class="form-group">
              [#if (project.projectInfo.crossCuttingGender)!false]
                <div class="form-group">
                  [@customForm.textArea name="projectOutcome.genderDimenssion" required=true className="limitWords-100" editable=editable /]
                </div>
              [/#if]
              [#if (project.projectInfo.crossCuttingYouth)!false]
                <div class="form-group">
                  [@customForm.textArea name="projectOutcome.youthComponent" required=true className="limitWords-100" editable=editable /]
                </div> 
              [/#if]
            </div>
            [/#if]
            
            [#-- Baseline Indicators --]
            [#if action.hasSpecificities('crp_baseline_indicators') && ((projectOutcome.crpProgramOutcome.crpProgram.baseLine)!false)]
              <h5 class="sectionSubTitle">Baseline Indicators</h5>
              <div class="form-group">
                <div class="" id="baseline">
                  <div class="form-group text-right">
                    [#if (projectOutcome.crpProgramOutcome.file.fileName??)!false]
                      <a href="${action.getBaseLineFileURL((projectOutcome.crpProgramOutcome.id.toString())!)}${ (projectOutcome.crpProgramOutcome.file.fileName)!}" target="_blank" class="downloadBaseline"><img src="${baseUrl}/global/images/pdf.png" width="20px" alt="" /> ${ (projectOutcome.crpProgramOutcome.file.fileName)!}</a> 
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
              [@customForm.select name="" label="" disabled=!canEdit i18nkey="projectContributionCrp.selectMilestone"  listName="" keyFieldName="id" displayFieldName="title" className="" value="" /]
            </div>
            [/#if]
          </div>
          
          
          [#-- Communications --]
          [#if reportingActive]  
          <div class="">
            <h4 class="headTitle">Communications </h4>
            <ul class="nav nav-tabs projectOutcomeYear-tabs" role="tablist">
              [#list startYear .. endYear as year]
                <li class="[#if year == currentCycleYear]active[/#if]"><a href="#year-${year}" aria-controls="settings" role="tab" data-toggle="tab">${year} [@customForm.req required=isYearRequired(year) /] </a></li>
              [/#list]
            </ul> 
            
            <div class="tab-content projectOutcomeYear-content">
              [#list startYear .. endYear as year]
                <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="year-${year}">
                    [#assign comunication = action.loadProjectCommunication(year) /]
                    [#assign comunicationIndex = action.getIndexCommunication(year) /]
                    <input type="hidden" name="projectOutcome.communications.id" value=${(projectOutcome.communications.id)!"-1"} />
                    <input type="hidden" name="projectOutcome.communications[${comunicationIndex}].year" value="${year}"/>
                    <div class="communicationsBlock form-group">
                      <div class="form-group">
                        [@customForm.textArea name="projectOutcome.communications[${comunicationIndex}].communication" i18nkey="projectOutcome.communicationEngagement" required=isYearRequired(year) className="limitWords-100 fieldFocus" editable=editable /]
                      </div>
                      <div class="form-group">
                        [@customForm.textArea name="projectOutcome.communications[${comunicationIndex}].analysisCommunication" i18nkey="projectOutcome.analysisCommunication" className="limitWords-100 ${reportingActive?string('fieldFocus','')}" editable=editable /]
                      </div>
                    </div>
                    <br />
                    <div class="form-group">
                      <div class="fileUpload col-md-6 ${reportingActive?string('fieldFocus','')}">
                        <label>[@customForm.text name="projectOutcome.uploadSummary" readText=!editable /]:</label>
                        <div class="uploadContainer">
                          [@customForm.inputFile name="projectOutcome.communications[${comunicationIndex}].file" fileUrl="${(summaryURL)!}" fileName="projectOutcome.communications[${comunicationIndex}].summary.fileName" editable=editable /]
                        </div>  
                      </div>
                      <div class="clearfix"></div>
                    </div>
                </div>
              [/#list]
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
            [#if editable]
              <div class="addNextUser bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addNextUser"/]</div>
            [/#if]
          </div>
          [/#if]
          
          [#-- Lessons and progress --]
          [#if !action.isProjectNew(project.id)]
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
              [@customForm.textArea name="projectOutcome.projectComponentLesson.lessons" i18nkey="projectOutcome.lessons.${reportingActive?string('reporting','planning')}" className=" ${reportingActive?string('fieldFocus','')}" required=true editable=editable /]
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



  
[#include "/WEB-INF/crp/pages/footer.ftl"]


[#macro milestoneMacro element name index isTemplate=false]
  <div id="milestoneYear-${isTemplate?string('template', index)}" class="milestoneYear simpleBox" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    [#if editable]<div class="removeIcon removeProjectMilestone" title="Remove"></div>[/#if]
    <div class="leftHead sm">
      <span class="index">${index+1}</span>
      <span class="elementId"> Project Milestone Target </span>
    </div>

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
    

    [#local showMilestoneValue = element.srfTargetUnit??  && element.srfTargetUnit.id?? && (element.srfTargetUnit.id != -1) /]
    
    [#-- Milestone Title --]
    <div class="form-group grayBox">
      [#if showMilestoneValue]
        <div class="form-group pull-right">
          <strong>Target Value:</strong> ${(element.value)!}
        </div>
      [/#if]
      <div class="row">
        <div class="col-md-6">
          <strong>Milestone for <span class="crpMilestoneYear">${(element.year)!}</span> </strong> 
        </div>
      </div>
      
      <span class="title">${(element.title)!}</span>
    </div>
    
    <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="milestoneYear${index}-${year}">
      [#local customName = "${name}[${projectMilestoneIndex}]" /]
      <div class="outcomeMilestoneYear">
        [#-- Hidden inputs --]
        <input type="hidden" name="${customName}.id" value="${(projectMilestone.id)!}" />
        <input type="hidden" name="${customName}.year" class="crpMilestoneYearInput" value="${(year)!}" class="year" />
        <input type="hidden" name="${customName}.crpMilestone.id" value="${(element.id)!}" class="crpMilestoneId" />
        
        
        <div class="row form-group milestoneTargetValue" style="display:${showMilestoneValue?string('block', 'none')}">
          <div class="col-md-4">
            [@customForm.input name="${customName}.expectedValue" i18nkey="projectOutcomeMilestone.expectedValue" type="text"  placeholder="" className="targetValue" required=isYearRequired(year) editable=editable && !reportingActive /]
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
            [@customForm.input name="${customName}.achievedValue" i18nkey="projectOutcomeMilestone.achievedValue" type="text"  placeholder="" className=" ${reportingActive?string('fieldFocus','')}" required=isYearRequired(year) editable=editable /]
          </div>
          [/#if]
        </div>
        
        <div class="form-group">
          [@customForm.textArea name="${customName}.narrativeTarget" i18nkey="projectOutcomeMilestone.expectedNarrative" required=isYearRequired(year) className="limitWords-100" editable=editable && !reportingActive &&( projectMilestone.crpMilestone.year gte action.getActualPhase().year)!true /]
        </div>
        [#-- REPORTING BLOCK --]
        [#if reportingActive]
        <div class="form-group">
          [@customForm.textArea name="${customName}.narrativeAchieved" i18nkey="projectOutcomeMilestone.achievedNarrative" required=isYearRequired(year) className="limitWords-100 ${reportingActive?string('fieldFocus','')}" editable=editable &&( projectMilestone.crpMilestone.year gte action.getActualPhase().year)!true /]
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
    [#if editable]<div class="removeIcon removeNextUser" title="Remove"></div>[/#if]
    <div class="leftHead sm">
      <span class="index">${index+1}</span>
      <span class="elementId"> Project Next User </span>
    </div>
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
     
    <div class="form-group">
      [#-- Title --]
      <div class="form-group">
        [@customForm.input name="${customName}.nextUser" i18nkey="projectOutcomeNextUser.title" help="projectOutcomeNextUser.title.help" required=true className="limitWords-20" editable=editable && !reportingActive /]
      </div>
      [#-- Knowledge, attitude, skills and practice changes expected in this next user --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.knowledge" i18nkey="projectOutcomeNextUser.knowledge" help="projectOutcomeNextUser.knowledge.help" required=true className="limitWords-100" editable=editable && !reportingActive /]
      </div>
      [#-- Strategies will be used to encourage and enable this next user to utilize deliverables and adopt changes --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.strategies" i18nkey="projectOutcomeNextUser.strategies" help="projectOutcomeNextUser.strategies.help" required=true className="limitWords-100" editable=editable && !reportingActive /]
      </div>
      [#-- Status (Active, Inactive) --]
     
    </div>
    
    [#-- Reporting --]
    [#if reportingActive]
      <br /> 
      [#-- Tabs --]
      <ul class="nav nav-tabs projectOutcomeYear-tabs" role="tablist">
        [#list startYear .. endYear as year]
          <li class="[#if year == currentCycleYear]active[/#if]"><a href="#year-${year}" aria-controls="settings" role="tab" data-toggle="tab">${year} [@customForm.req required=isYearRequired(year) /] </a></li>
        [/#list]
      </ul> 
      [#-- Tabs Content --]
      <div class="tab-content projectOutcomeYear-content">
        [#list startYear .. endYear as year]
          <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="year-${year}">
            <div class="form-group">
              [@customForm.textArea name="${customName}.reportOnProgress" i18nkey="projectOutcomeNextUser.reportOnProgress" help="" required=true className="limitWords-200 ${reportingActive?string('fieldFocus','')}" editable=editable /]
            </div>
            <div class="form-group">
              [@customForm.textArea name="${customName}.strategiesEncourage" i18nkey="projectOutcomeNextUser.strategiesEncourage" help="" required=true className="limitWords-100 ${reportingActive?string('fieldFocus','')}" editable=editable /]
            </div> 
            <div class="clearfix"></div>
          </div>
        [/#list]
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
        [@customForm.input name="${customName}.value" i18nkey="projectOutcomeBaseline.expectedValue" className="" value="${(projectOutcomeIndicator.value)!}" required=true editable=editable && !reportingActive /]
      </div>
      <div class="col-md-3">
        [#if reportingActive]
          [@customForm.input name="${customName}.valueReporting" i18nkey="projectOutcomeBaseline.achievedValue" className="" required=true editable=editable /]
        [/#if]
      </div>
      <div class="col-md-3"></div>
    </div>
    <div class="form-group">
      [@customForm.textArea name="${customName}.narrative" i18nkey="projectOutcomeBaseline.expectedNarrative" value="${(projectOutcomeIndicator.narrative)!}" required=true className="limitWords-100" editable=editable && !reportingActive /]
    </div>
    [#if reportingActive]
      <div class="form-group">
        [@customForm.textArea name="${customName}.achievedNarrative" i18nkey="projectOutcomeBaseline.achievedNarrative" required=isYearRequired(year) className="limitWords-100" editable=editable /]
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