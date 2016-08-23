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
        
          [#assign projectOutcome = 
            {'name': 'National level decision-makers (Gov. ministries), national agricultural research systems, NGOs, civil society organizations, regional organizations use CCAFS science-derived decision support tools and systems to mainstream climate change into national plans and policies from local to national levels.',   'fp': 'FP3', 'canDelete': true}
          /]
          
          <h3 class="headTitle">${projectOutcome.fp} - Outcome 2022</h3>  
          [#-- Outcomen name --]
          <p>${projectOutcome.name}</p>
          
          <div id="projectOutcome" class="borderBox">
            
            [#-- Project Outcome expected target (AT THE BEGINNING) --]
            <h5 class="sectionSubTitle">Expected Target</h5>
            <div class="form-group">
              <div class="row form-group">
                <div class="col-md-5">
                  [@customForm.input name="projectOutcome.expectedValue" type="text"  placeholder="" className=" " required=true editable=editable /]
                </div>
                <div class="col-md-7">
                  [@customForm.select name="projectOutcome.expectedUnit" placeholder="" className="" listName="targetUnitList"  required=true editable=editable  /]
                </div>
              </div>
              <div class="form-group">
                [@customForm.textArea name="projectOutcome.expectedNarrative" required=true className="limitWords-100" editable=editable /]
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
                  [@customForm.select name="projectOutcome.achievedUnit" placeholder="" className="" listName="targetUnitList"  required=true editable=editable  /]
                </div>
              </div>
              <div class="form-group">
                [@customForm.textArea name="projectOutcome.achievedNarrative" required=true className="limitWords-100" editable=editable /]
              </div>
            </div>
            
            [#-- Project Milestones and Communications contributions per year--]
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
                  [#-- List milestones per year --]
                  [#list 1..2 as milestone]
                    [@milestoneMacro element={} name="" index=milestone_index year=year /]
                  [/#list]
                  [#-- Select a milestone --]
                  [@customForm.select name="" label="" disabled=!canEdit i18nkey="projectContributionCrp.selectMilestone" listName="milestoneList" keyFieldName="id" displayFieldName="description" className="" value="" /]
                  
                  <br />
                  <hr />
                  <br />
                  
                  <h5 class="sectionSubTitle">Communications </h5>
                  <div>
                    <div class="form-group">
                      [@customForm.textArea name="projectOutcome.communicationEngagement" required=true className="limitWords-100" editable=editable /]
                    </div>
                    <div class="form-group">
                      [@customForm.textArea name="projectOutcome.analysisCommunication" required=true className="limitWords-100" editable=editable /]
                    </div>
                  </div>
                  
                  
                </div>
              [/#list]
            </div>
              
          </div> 
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
          
         
        [/@s.form] 
      </div>
    </div>  
</section>
  
[#include "/WEB-INF/global/pages/footer.ftl"]


[#macro milestoneMacro element name index year isTemplate=false]
  <div id="milestoneYear-${isTemplate?string('template', index)}" class="milestoneYear simpleBox" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]" /]
    [#-- Remove Button --]
    [#if editable]<div class="removeIcon" title="Remove"></div>[/#if]
    <div class="leftHead">
      <span class="index">${index+1}</span>
      <span class="elementId">Milestone Target ${year}</span>
    </div>
    <br />
    
    [#-- Milestone content --]
    <div class="form-group">
      [#-- PLANNING BLOCK --]
      <div class="row form-group">
        <div class="col-md-5">
          [@customForm.input name="projectOutcomeMilestone.expectedValue" type="text"  placeholder="" className=" " required=true editable=editable /]
        </div>
        <div class="col-md-7">
          [@customForm.select name="projectOutcomeMilestone.expectedUnit" placeholder="" className="" listName="targetUnitList"  required=true editable=editable  /]
        </div>
      </div>
    
      [#-- REPORTING BLOCK --]
      <div class="row form-group">
        <div class="col-md-5">
          [@customForm.input name="projectOutcomeMilestone.achievedValue" type="text"  placeholder="" className=" " required=true editable=editable /]
        </div>
        <div class="col-md-7">
          [@customForm.select name="projectOutcomeMilestone.achievedUnit" placeholder="" className="" listName="targetUnitList"  required=true editable=editable  /]
        </div>
      </div>
      
      [#-- PLANNING BLOCK --]
      <div class="form-group">
        [@customForm.textArea name="projectOutcomeMilestone.expectedNarrative" required=true className="limitWords-100" editable=editable /]
      </div>
      <div class="form-group">
        [@customForm.textArea name="projectOutcomeMilestone.expectedGenderSocialNarrative" required=true className="limitWords-100" editable=editable /]
      </div>
      
      [#-- REPORTING BLOCK --]
      <div class="form-group">
        [@customForm.textArea name="projectOutcomeMilestone.achievedNarrative" required=true className="limitWords-100" editable=editable /]
      </div>
      <div class="form-group">
        [@customForm.textArea name="projectOutcomeMilestone.achievedGenderSocialNarrative" required=true className="limitWords-100" editable=editable /]
      </div>
    </div>
    
  </div>
[/#macro]