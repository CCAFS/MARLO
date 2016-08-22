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
  {"label":"projectContributionCrp", "nameSpace":"/projects", "action":""},
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
          
          <h3 class="headTitle">${projectOutcome.fp} - Outcome</h3>  
          [#-- Outcomen name --]
          <p>${projectOutcome.name}</p>
          
          <div id="projectOutcome" class="borderBox">
            
            [#-- Project Outcome expected target (AT THE BEGINNING) --]
            <h4>Expected Target</h4>
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
            <h4>Achieved Target</h4>
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
            <h4>Milestone Progress towards to outcome  ${currentCycleYear}</h4>
            
            
            <ul class="nav nav-tabs" role="tablist">
              [#list startYear .. endYear as year]
                <li role="presentation" class="[#if year == currentCycleYear]active[/#if]"><a href="#year-${year}" aria-controls="settings" role="tab" data-toggle="tab">${year}</a></li>
              [/#list]
            </ul>

            <!-- Tab panes -->
            <div class="tab-content">
              [#list startYear .. endYear as year]
                <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="year-${year}">Content ${year}</div>
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