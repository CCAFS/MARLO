[#ftl]
[#assign title = "Project Outcome Contribution to CRP" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectContributionCrp.js", "${baseUrl}/js/global/autoSave.js"] /]
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
        
          
          
          <h3 class="headTitle">${projectOutcome.crpProgramOutcome.crpProgram.acronym} - Outcome 2022</h3>  
          [#-- Hidden Inputs --]
          <input type="hidden" name="projectOutcome.id" value="${(projectOutcome.id)!}" />
          
          [#-- Outcomen name --]
          <p>${projectOutcome.crpProgramOutcome.description}</p>
          
          <div class="borderBox">
            
            [#-- Project Outcome expected target (AT THE BEGINNING) --]
            <h5 class="sectionSubTitle">Expected Target</h5>
            <div class="form-group">
              <div class="row form-group">
                <div class="col-md-5">
                  [@customForm.input name="projectOutcome.expectedValue" type="text"  placeholder="" className=" " required=true editable=editable /]
                </div>
                <div class="col-md-7">
                  [@customForm.select name="projectOutcome.expectedUnit.id" placeholder="" className="" listName="targetUnits"  keyFieldName="id" displayFieldName="name"  required=true editable=editable  /]
                </div>
              </div>
              <div class="form-group">
                [@customForm.textArea name="projectOutcome.narrativeTarget" required=true className="limitWords-100" editable=editable /]
              </div>
            </div>
            
            [#-- Project Outcome achieved target (AT THE END) --]
            <h5 class="sectionSubTitle">Achieved Target</h5>
            <div class="form-group">
              <div class="row form-group">
                <div class="col-md-5">
                  [@customForm.input name="projectOutcome.achievedValue" type="text"  placeholder="" className=" " required=true editable=editable /]
                </div>
                <div class="col-md-7">
                  [@customForm.select name="projectOutcome.achievedUnit.id" placeholder="" className="" listName="targetUnits" keyFieldName="id" displayFieldName="name"  required=true editable=editable  /]
                </div>
              </div>
              <div class="form-group">
                [@customForm.textArea name="projectOutcome.narrativeAchieved" required=true className="limitWords-100" editable=editable /]
              </div>
            </div>
          </div>
          [#-- Project Milestones and Communications contributions per year--]
          <div class="">  
            <br />
            [#assign startYear = (project.startDate?string.yyyy)?number]
            [#assign endYear = (project.endDate?string.yyyy)?number]
            
            <ul class="nav nav-tabs projectOutcomeYear-tabs" role="tablist">
              [#list startYear .. endYear as year]
                <li class="[#if year == currentCycleYear]active[/#if]"><a href="#year-${year}" aria-controls="settings" role="tab" data-toggle="tab">${year}</a></li>
              [/#list]
            </ul>
            
            <div class="tab-content projectOutcomeYear-content">
              [#list startYear .. endYear as year]
                <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="year-${year}">
                  <h5 class="sectionSubTitle">Milestones/ progress towards your outcome target contribution </h5>
                  [#-- Hidden Inputs --]
           
                  
                  [#-- List milestones per year --]
                  <div class="milestonesYearBlock">
                    <div class="milestonesYearList">
                      [#if action.loadProjectMilestones(year)?has_content]
                        [#list ( action.loadProjectMilestones(year)) as milestone]
                          [@milestoneMacro element=milestone name="projectOutcome.milestones" index=action.getIndexMilestone(milestone.id) /]
                        [/#list]
                      [/#if]
                    </div>
                    [#-- Select a milestone --]
                    <div class="milestonesYearSelect">
                      [@customForm.select name="" label="" disabled=!canEdit i18nkey="projectContributionCrp.selectMilestone"  listName="milestones" keyFieldName="id" displayFieldName="title" className="" value="" /]
                    </div>
                    
                  </div>
                  
                  <hr />
                  
                  [#-- Communications --]
                  <h5 class="sectionSubTitle">Communications </h5>
                  <div class="communicationsBlock form-group">
                    <div class="form-group">
                      [@customForm.textArea name="projectOutcome.projectCommunications[${year_index}].communicationEngagement" i18nkey="projectOutcome.communicationEngagement" required=true className="limitWords-100" editable=editable /]
                    </div>
                    <div class="form-group">
                      [@customForm.textArea name="projectOutcome.projectCommunications[${year_index}].analysisCommunication" i18nkey="projectOutcome.analysisCommunication" className="limitWords-100" editable=editable /]
                    </div>
                  </div>
                  <div class="fileUpload">
                    <label>[@customForm.text name="projectOutcome.uploadSummary" readText=!editable /]:</label>
                    <div class="uploadContainer">
                      [@customForm.inputFile name="file" fileUrl="${(summaryURL)!}" fileName="projectOutcome.projectCommunications[${year_index}].file.fileName" editable=editable /]
                    </div>  
                  </div>
                  
                </div>
              [/#list]
            </div>
              
          </div> 
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
          <input type="hidden" name="projectOutcomeID" value="${projectOutcomeID}"/>
          
          
         
        [/@s.form] 
      </div>
    </div>  
</section>

[#-- Milestone Template --]]
[@milestoneMacro element={} name="projectOutcome.milestones" index="-1" isTemplate=true /]
  
[#include "/WEB-INF/global/pages/footer.ftl"]


[#macro milestoneMacro element name index isTemplate=false]
  <div id="milestoneYear-${isTemplate?string('template', index)}" class="milestoneYear simpleBox" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]" /]
    [#-- Remove Button --]
    [#if editable]<div class="removeIcon removeProjectMilestone" title="Remove"></div>[/#if]
    <div class="leftHead sm">
      <span class="elementId"> Project Milestone Target </span>
    </div>
    <br />
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <input type="hidden" name="${customName}.year" value="${(element.year)!}" class="year" />
    <input type="hidden" name="${customName}.crpMilestone.id" value="${(element.crpMilestone.id)!}" class="crpMilestoneId" />
   
    [#-- Milestone Title --]
    <p class="title">${(element.crpMilestone.title)!}</p>
    
    [#-- Milestone content --]
    <div class="form-group">
      <div class="row form-group">
        <div class="col-md-4">
          [@customForm.input name="${customName}.expectedValue" i18nkey="projectOutcomeMilestone.expectedValue" type="text"  placeholder="" className=" " required=true editable=editable /]
        </div>
        <div class="col-md-4">
          [@customForm.select name="${customName}.expectedUnit.id" i18nkey="projectOutcomeMilestone.expectedUnit" placeholder="" className="" listName="targetUnits"  keyFieldName="id" displayFieldName="name"  required=true editable=editable  /]
        </div>
        [#-- REPORTING BLOCK --]
        <div class="col-md-4">
          [@customForm.input name="${customName}.achievedValue" i18nkey="projectOutcomeMilestone.achievedValue" type="text"  placeholder="" className=" " required=true editable=editable /]
        </div>
      </div>
      
      [#-- REPORTING BLOCK --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.narrativeTarget" i18nkey="projectOutcomeMilestone.expectedNarrative" required=true className="limitWords-100" editable=editable /]
      </div>
      <div class="form-group">
        [@customForm.textArea name="${customName}.expectedGender" i18nkey="projectOutcomeMilestone.expectedGenderSocialNarrative" required=true className="limitWords-100" editable=editable /]
      </div>
      
      [#-- REPORTING BLOCK --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.narrativeAchieved" i18nkey="projectOutcomeMilestone.achievedNarrative" required=true className="limitWords-100" editable=editable /]
      </div>
      <div class="form-group">
        [@customForm.textArea name="${customName}.narrativeGender" i18nkey="projectOutcomeMilestone.achievedGenderSocialNarrative" required=true className="limitWords-100" editable=editable /]
      </div>
    </div>
    
  </div>
[/#macro]