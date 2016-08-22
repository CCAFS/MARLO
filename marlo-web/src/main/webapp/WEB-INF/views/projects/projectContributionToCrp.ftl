[#ftl]
[#assign title = "Project Contribution to CRP" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectContributionCrp.js", "${baseUrl}/js/global/autoSave.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectContributionCrp.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "contributionCrp" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
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
          
          <h3 class="headTitle">[@s.text name="projectContributionCrp.title" /]</h3>  
          <div id="projectContributionCrp" class="borderBox">
            [#-- Your project contributes to the flagships --]
            <p>
              <strong>Your Project contributes to the following Flagships:</strong>  
              [#if project.flagships?has_content][#list project.flagships as element]<span>${element.acronym}</span>[/#list][/#if]
            </p>
            
            [#-- Project Outcomes List --]
            [#assign projectOutcomesList = [
              {'name': 'National level decision-makers (Gov. ministries), national agricultural research systems, NGOs, civil society organizations, regional organizations use CCAFS science-derived decision support tools and systems to mainstream climate change into national plans and policies from local to national levels.',   'fp': 'FP3', 'canDelete': true},
              {'name': 'Policy makers enhancing the design, investment decisions, implementation and monitoring and evaluation of agro - sectoral climate change policies through a transparent, coordinative and consultative mode from local to national level.',                                                                       'fp': 'FP4', 'canDelete': false},
              {'name': 'Public sector institutions, innovate, plan, invest, regulate/reform/enforce laws and provide incentives for understanding, accessing and implementing low-emission/CSA technologies appropriate for local contexts through multi-stakeholder consultation.',                                                      'fp': 'FP4', 'canDelete': true}
            ] /]
            <ul id="projectOutcomesList" class="simpleBox">
              [#if projectOutcomesList?has_content]
                [#list projectOutcomesList as projectOutcome]
                  <li class="projectOutcome">
                    <div class="row">
                      <div class="col-md-1">${projectOutcome.fp}</div>
                      <div class="col-md-10">${projectOutcome.name}</div>
                      <div class="col-md-1">
                        [#if projectOutcome.canDelete]
                          <a id="removeOutcome-projectOutcomeId" class="removeOutcome" href="#" title=""><img src="${baseUrl}/images/global/trash.png" /></a>
                        [#else]
                          <img src="${baseUrl}/images/global/trash_disable.png" title="" />
                        [/#if]
                      </div>
                    </div>
                    [#if projectOutcome_has_next]<hr />[/#if]
                  </li>
                [/#list]
              [#else]
              
              [/#if]
            </ul>
            
            [#-- Add a new Outcomes --]
            <div class="addNewOutcome">
              <div class="outcomesListBlock">
                [@customForm.select name="" label="" disabled=!canEdit i18nkey="projectContributionCrp.selectOutcome" listName="outcomesList" keyFieldName="id" displayFieldName="description" className="" value="" /]
              </div>
              <div class="addOutcomeBlock">
                <div class="button-blue"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addOutcome"/]</div>
              </div>
            </div>
            
          </div> 
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
          
         
        [/@s.form] 
      </div>
    </div>  
</section>
  
[#include "/WEB-INF/global/pages/footer.ftl"]