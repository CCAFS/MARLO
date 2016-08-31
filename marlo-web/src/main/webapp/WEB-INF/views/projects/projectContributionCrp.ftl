[#ftl]
[#assign title = "Project Outcome Contribution to CRP" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectContributionCrp.js"] /] [#-- , "${baseUrl}/js/global/autoSave.js" --]]
[#assign customCSS = ["${baseUrl}/css/projects/projectContributionCrp.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "contributionsCrpList" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectContributionsCrpList", "nameSpace":"/projects", "action":""},
  {"label":"projectContributionCrp", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign startYear = (project.startDate?string.yyyy)?number /]
[#assign endYear = (project.endDate?string.yyyy)?number /]

<div class="container">
  <div class="helpMessage"><img src="${baseUrl}/images/global/icon-help.png" /><p> [@s.text name="projectContributionCrp.help" /] </p></div> 
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          [#-- Back --]
          <small class="pull-right">
            <a href="[@s.url action='${crpSession}/contributionsCrpList'][@s.param name="projectID" value=project.id /][/@s.url]">
              <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the project contributions
            </a>
          </small>
          
          [#-- Title --]
          <h3 class="headTitle"> Project Contribution</h3>  
          
          [#-- Outcomen name --]
          <p><strong>${(projectOutcome.crpProgramOutcome.crpProgram.acronym)!} - Outcome ${(projectOutcome.crpProgramOutcome.year)!}</strong>: ${projectOutcome.crpProgramOutcome.description}</p>
          
          [#-- Project Targets --]
          [#assign showExpectedTarget = (!reportingActive && (startYear == currentCycleYear)) /]
          [#assign showAchievedTarget = (reportingActive && (endYear == currentCycleYear)) /]
          
          [#if showExpectedTarget || showAchievedTarget]
          <div class="borderBox">
            [#-- Project Outcome expected target (AT THE BEGINNING) --]
            [#if showExpectedTarget]
            <h5 class="sectionSubTitle">Expected Target</h5>
            <div class="form-group">
              <div class="row form-group">
                <div class="col-md-5">
                  [@customForm.input name="projectOutcome.expectedValue" type="text"  placeholder="" className="targetValue" required=true editable=editable /]
                </div>
                <div class="col-md-7">
                  [@customForm.select name="projectOutcome.expectedUnit.id" i18nkey="projectOutcome.expectedUnit" placeholder="" className="" listName="targetUnits"  keyFieldName="id" displayFieldName="name"  required=true editable=editable  /]
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
              <div class="row form-group">
                <div class="col-md-5">
                  [@customForm.input name="projectOutcome.achievedValue" type="text"  placeholder="" className="targetValue" required=true editable=editable /]
                </div>
                <div class="col-md-7">
                  [@customForm.select name="projectOutcome.achievedUnit.id" i18nkey="projectOutcome.achievedUnit" placeholder="" className="" listName="targetUnits" keyFieldName="id" displayFieldName="name"  required=true editable=editable  /]
                </div>
              </div>
              <div class="form-group">
                [@customForm.textArea name="projectOutcome.narrativeAchieved" required=true className="limitWords-100" editable=editable /]
              </div>
            </div>
            [/#if]
          </div>
          [/#if]
          [#-- Project Milestones and Communications contributions per year--]
          <h4 class="headTitle">Milestones/ progress towards your outcome target contribution </h4>
          
          [#-- List milestones  --]
          <div class="milestonesYearBlock borderBox">
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
              <span class="milestonesSelectedIds" style="display:none">[#if milestonesProject?has_content][#list milestonesProject as e]${(e.crpMilestone.id)!}[#if e_has_next],[/#if][/#list][/#if]</span>
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
                <li class="[#if year == currentCycleYear]active[/#if]"><a href="#year-${year}" aria-controls="settings" role="tab" data-toggle="tab">${year} [#if isYearRequired(year)]*[/#if]</a></li>
              [/#list]
            </ul> 
            
            <div class="tab-content projectOutcomeYear-content">
              [#list startYear .. endYear as year]
                <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="year-${year}">
                    [#assign comunication = action.loadProjectCommunication(year) /]
                    [#assign comunicationIndex = action.getIndexCommunication(year) /]
                   
                    <input type="hidden" name="projectOutcome.communications[${comunicationIndex}].year" value="${year}"/>
                    <div class="communicationsBlock form-group">
                      <div class="form-group">
                        [@customForm.textArea name="projectOutcome.communications[${comunicationIndex}].communication" i18nkey="projectOutcome.communicationEngagement" required=true className="limitWords-100" editable=editable /]
                      </div>
                      <div class="form-group">
                        [@customForm.textArea name="projectOutcome.communications[${comunicationIndex}].analysisCommunication" i18nkey="projectOutcome.analysisCommunication" className="limitWords-100" editable=editable /]
                      </div>
                    </div>
                    <div class="fileUpload">
                      <label>[@customForm.text name="projectOutcome.uploadSummary" readText=!editable /]:</label>
                      <div class="uploadContainer">
                        [@customForm.inputFile name="projectOutcome.communications[${comunicationIndex}].file" fileUrl="${(summaryURL)!}" fileName="projectOutcome.communications[${comunicationIndex}].summary.fileName" editable=editable /]
                      </div>  
                    </div>
                </div>
              [/#list]
            </div>
          </div>
          [/#if]
          
          [#-- Next Users --]
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
          
          [#-- Lessons and progress --]
          <div id="lessons" class="borderBox">
            [#-- Lessons learnt from last planning/reporting cycle --]
            [#if (projectOutcome.projectComponentLessonPreview.lessons?has_content)!false]
            <div class="fullBlock">
              <h6>[@customForm.text name="projectOutcome.projectComponentLessonPreview" i18nkey="projectOutcome.previousLessons.${reportingActive?string('reporting','planning')}" param="${reportingActive?string(reportingYear,planningYear-1)}" /]:</h6>
              <div class="textArea "><p>${projectOutcome.projectComponentLessonPreview.lessons}</p></div>
            </div>
            [/#if]
            [#-- Planning/Reporting lessons --]
            <div class="fullBlock">
              <input type="hidden" name="projectOutcome.projectComponentLesson.id" value=${(project.projectComponentLesson.id)!"-1"} />
              <input type="hidden" name="projectOutcome.projectComponentLesson.year" value=${reportingActive?string(reportingYear,planningYear)} />
              <input type="hidden" name="projectOutcome.projectComponentLesson.componentName" value="${actionName}">
              [@customForm.textArea name="projectOutcome.projectComponentLesson.lessons" i18nkey="projectOutcome.lessons.${reportingActive?string('reporting','planning')}" required=true editable=editable /]
            </div>
          </div>
        
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projectOutcomes.ftl" /]

        [/@s.form] 
      </div>
    </div>  
</section>

[#-- Milestone Template --]
[@milestoneMacro element={} name="projectOutcome.milestones" index="-1" isTemplate=true /]

[#-- Next user Template --]
[@nextUserMacro element={} name="projectOutcome.nextUsers" index="-1" isTemplate=true /]
  
[#include "/WEB-INF/global/pages/footer.ftl"]


[#macro milestoneMacro element name index isTemplate=false]
  <div id="milestoneYear-${isTemplate?string('template', index)}" class="milestoneYear simpleBox" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    [#if editable]<div class="removeIcon removeProjectMilestone" title="Remove"></div>[/#if]
    <div class="leftHead sm">
      <span class="index">${index+1}</span>
      <span class="elementId"> Project Milestone Target </span>
    </div>

    [#-- Milestone Title --]
    <div class="form-group grayBox">
      <div class="row">
        <div class="col-md-6">
          <strong>Milestone target value for <span class="crpMilestoneYear">${(element.year)!}</span> </strong> <span class="crpMilestoneValue">${(element.value)!}</span> 
        </div>
      </div>
      <span class="title">${(element.title)!}</span>
    </div>
    
    [#-- Milestone content --]
    <ul class="nav nav-tabs projectOutcomeYear-tabs" role="tablist">
      [#list startYear .. endYear as year]
        <li class="[#if year == currentCycleYear]active[/#if]"><a href="#milestoneYear-${year}" aria-controls="settings" role="tab" data-toggle="tab">${year} [#if isYearRequired(year)]*[/#if]</a></li>
      [/#list]
    </ul> 
    
    <div class="tab-content projectOutcomeYear-content">
      [#list startYear .. endYear as year]
        <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="milestoneYear-${year}">
            
            [#local projectMilestone = action.getMilestone(element.id, year) /]
            [#local projectMilestoneIndex = action.getIndexMilestone(element.id, year) /]
            
            [#local customName = "${name}[${projectMilestoneIndex}]" /]
            
                     
            [#if year gt element.year]
              This milestone finalize on ${element.year}
            [#else]
              [#-- Hidden inputs --]
              <input type="hidden" name="${customName}.id" value="${(projectMilestone.id)!}" />
              <input type="hidden" name="${customName}.year" value="${(year)!}" class="year" />
              <input type="hidden" name="${customName}.crpMilestone.id" value="${(element.id)!}" class="crpMilestoneId" />
              
              <div class="row form-group">
                <div class="col-md-4">
                  [@customForm.input name="${customName}.expectedValue" i18nkey="projectOutcomeMilestone.expectedValue" type="text"  placeholder="" className="targetValue" required=true editable=editable /]
                </div>
                <div class="col-md-4">
                  [@customForm.select name="${customName}.expectedUnit.id" i18nkey="projectOutcomeMilestone.expectedUnit" placeholder="" className="" listName="targetUnits"  keyFieldName="id" displayFieldName="name"  required=true editable=editable  /]
                </div>
                [#-- REPORTING BLOCK --]
                [#if reportingActive]
                <div class="col-md-4">
                  [@customForm.input name="${customName}.achievedValue" i18nkey="projectOutcomeMilestone.achievedValue" type="text"  placeholder="" className=" " required=true editable=editable /]
                </div>
                [/#if]
              </div>
              
              <div class="form-group">
                [@customForm.textArea name="${customName}.narrativeTarget" i18nkey="projectOutcomeMilestone.expectedNarrative" required=true className="limitWords-100" editable=editable /]
              </div>
              <div class="form-group">
                [@customForm.textArea name="${customName}.expectedGender" i18nkey="projectOutcomeMilestone.expectedGenderSocialNarrative" required=true className="limitWords-100" editable=editable /]
              </div>
              [#-- REPORTING BLOCK --]
              [#if reportingActive]
              <div class="form-group">
                [@customForm.textArea name="${customName}.narrativeAchieved" i18nkey="projectOutcomeMilestone.achievedNarrative" required=true className="limitWords-100" editable=editable /]
              </div>
              <div class="form-group">
                [@customForm.textArea name="${customName}.narrativeGender" i18nkey="projectOutcomeMilestone.achievedGenderSocialNarrative" required=true className="limitWords-100" editable=editable /]
              </div>
              [/#if]
            [/#if]
        </div>
      [/#list]
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
        [@customForm.input name="${customName}.nextUser" i18nkey="projectOutcomeNextUser.title" help="projectOutcomeNextUser.title.help" required=true className="limitWords-20" editable=editable /]
      </div>
      [#-- Knowledge, attitude, skills and practice changes expected in this next user --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.knowledge" i18nkey="projectOutcomeNextUser.knowledge" help="projectOutcomeNextUser.knowledge.help" required=true className="limitWords-50" editable=editable /]
      </div>
      [#-- Strategies will be used to encourage and enable this next user to utilize deliverables and adopt changes --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.strategies" i18nkey="projectOutcomeNextUser.strategies" help="projectOutcomeNextUser.strategies.help" required=true className="limitWords-50" editable=editable /]
      </div>
    </div>
  </div>
[/#macro]

[#-- Get if the year is required--]
[#function isYearRequired year]
  [#if project.endDate??]
    [#assign endDate = (project.endDate?string.yyyy)?number]
    [#if reportingActive]
      [#return  (year == currentCycleYear)  && (endDate gte year) ]
    [#else]
      [#return  (year == currentCycleYear) && (endDate gte year) ]
    [/#if]
  [#else]
    [#return false]
  [/#if]
[/#function]