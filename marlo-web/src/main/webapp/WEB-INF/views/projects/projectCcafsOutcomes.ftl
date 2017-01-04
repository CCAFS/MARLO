[#ftl]
[#assign title = "Project CCAFS Outcomes" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2", "jsUri"] /]
[#assign customJS = ["${baseUrl}/js/global/fieldsValidation.js"] /]
[#assign customCSS = [ ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "ccafsOutcomes" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectCcafsOutcomes", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign startYear = (project.startDate?string.yyyy)?number /]
[#assign endYear = (project.endDate?string.yyyy)?number /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectCcafsOutcomes.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          <h3 class="headTitle">[@s.text name="projectCcafsOutcomes.title" /]</h3>  
          
          [#list midOutcomesSelected as outcome]
            <div class="borderBox">
              <h4 class="sectionSubTitle">${outcome.ipProgram.acronym} - Outcome 2019</h4>
              <p>${outcome.description}</p>
              
              <p><strong>Indicators</strong></p>
             
              [#list outcome.indicators as indicator]
                <div class="simpleBox">
                  <p class="grayBox">${indicator.description}</p>
                  
                  <ul class="nav nav-tabs projectOutcomeYear-tabs" role="tablist">
                    [#list startYear .. endYear as year]
                      <li class="[#if year == currentCycleYear]active[/#if]"><a href="#year-${year}" aria-controls="settings" role="tab" data-toggle="tab">${year} [@customForm.req required=isYearRequired(year) /] </a></li>
                    [/#list]
                  </ul> 
                  
                  <div class="tab-content projectOutcomeYear-content">
                    [#list startYear .. endYear as year]
                      <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="year-${year}">
                        <span class="label">${year}</span>
                        [#--  1. Indicator target value --]
                        <div class="thirdPartBlock">
                          [#assign isTargetValueRequired = isYearRequired(year) && (action.hasPermission("target") || (projectIndicator?? && !projectIndicator.target?has_content)) /]
                          [#assign isTargetValueEditable = editable && (currentCycleYear lte year) && action.hasPermission("target") /]
                          <label><h6>[@s.text name="planning.projectImpactPathways.targetValue" /]:[@customForm.req required=isTargetValueRequired /]</h6></label>
                          [#if isTargetValueEditable]
                            <input type="text" class="projectIndicatorTarget ${(isYearRequired(year))?string('required','optional')}" name="project.indicators.target" value="${(projectIndicator.target)!}"/> 
                          [#else]
                            [#if projectIndicator?? && !projectIndicator.target?has_content]
                              [#if currentCycleYear lt year]
                                ${fieldEmpty}
                              [#else]
                                [#if (currentCycleYear == year) && editable]
                                  <input type="text" class="projectIndicatorTarget ${(isYearRequired(year))?string('required','optional')}" name="project.indicators.target" value="${(projectIndicator.target)!}"/>
                                [#else]
                                  <div class="input"><p>[@s.text name="form.values.fieldEmpty"/]</p></div>
                                [/#if]
                              [/#if]
                            [#else]
                              <div class="select"><p>${(projectIndicator.target)!}</p></div>
                              <input type="hidden" class="projectIndicatorTarget ${(isYearRequired(year))?string('required','optional')}" name="project.indicators.target" value="${(projectIndicator.target)!}"/>
                            [/#if]
                          [/#if]
                        </div>
                        [#-- 2. Cumulative target --]
                        [#if (currentCycleYear lte year) && reportingActive]
                        <div class="thirdPartBlock">
                          <label><h6>[@s.text name="reporting.projectImpactPathways.comulativeTarget" /]:</h6></label>
                          <div class="input"><p>${(project.calculateAcumulativeTarget(year,projectIndicator))!}</p></div>
                        </div>
                        [/#if]
                        [#-- 3. Reporting target --]
                        [#-- -- -- REPORTING BLOCK -- -- --]
                        [#if reportingActive && (year == currentCycleYear)]
                          <div class="thirdPartBlock">
                            <label><h6 title='[@s.text name="reporting.projectImpactPathways.achievedTarget.help" /]'>[@s.text name="reporting.projectImpactPathways.achievedTarget" /]:[@customForm.req required=isYearRequired(year) && action.hasPermission("achieved") /]</h6></label>
                            [#if editable && (currentCycleYear lte year) && action.hasPermission("achieved")]
                              <input type="text" class="projectIndicatorAchievedTarget ${(isYearRequired(year))?string('required','optional')}" name="project.indicators.archivedText" value="${(projectIndicator.archivedText)!}"/> 
                            [#else]
                              <div class="input"><p>${(projectIndicator.archived)!}</p></div>
                            [/#if]
                          </div>
                        [/#if]
                      </div>
                      
                      [#-- Indicator target narrative description --]
                      <div class="textArea fullBlock">
                        <label><h6>[@s.text name="planning.projectImpactPathways.targetNarrative" /]:[@customForm.req required=isYearRequired(year) && action.hasPermission("description") /]</h6></label>
                        [#if editable && (currentCycleYear lte year) && action.hasPermission("description")]
                          <textarea class="projectIndicatorDescription ${(isYearRequired(year))?string('required','optional')}" name="project.indicators.description">${projectIndicator.description!}</textarea>
                        [#else]
                          [#if projectIndicator?? && !projectIndicator.description?has_content]
                            [#if currentCycleYear lt year]${fieldEmpty}[#else]<div class="select"><p>[@s.text name="form.values.fieldEmpty"/]</p></div>[/#if]
                          [#else]
                            <div class="select"><p>${(projectIndicator.description)!}</p></div>
                            <input type="hidden" class="projectIndicatorDescription ${(isYearRequired(year))?string('required','optional')}" name="project.indicators.description" value="${(projectIndicator.description)!}"/>
                          [/#if] 
                        [/#if] 
                      </div>
                      
                      [#-- -- -- REPORTING BLOCK -- -- --]
                      [#if reportingActive && (year == currentCycleYear)]
                        [#-- Narrative for your achieved targets, including evidence --]
                        <div class="textArea fullBlock">
                          <label><h6>[@s.text name="reporting.projectImpactPathways.targetNarrativeAchieved" /]:[@customForm.req required=isYearRequired(year) && action.hasPermission("narrativeTargets") /]</h6></label>
                          [#if editable && (currentCycleYear lte year) && action.hasPermission("narrativeTargets")]
                            <textarea class="projectIndicatorNarrativeAchieved ${(isYearRequired(year))?string('required','optional')}" name="project.indicators.narrativeTargets">${(projectIndicator.narrativeTargets)!}</textarea>
                          [#else]
                            [#if projectIndicator?? && !projectIndicator.narrativeTargets?has_content]
                              [#if currentCycleYear lt year]${fieldEmpty}[#else]<div class="select"><p>[@s.text name="form.values.fieldEmpty"/]</p></div>[/#if]
                            [#else]
                              <div class="select"><p>${(projectIndicator.narrativeTargets)!}</p></div>
                              <input type="hidden" class="projectIndicatorNarrativeAchieved ${(isYearRequired(year))?string('required','optional')}" name="project.indicators.narrativeTargets" value="${(projectIndicator.narrativeTargets)!}"/>
                            [/#if] 
                          [/#if]
                        </div>
                      [/#if]
                      
                      [#-- The expected annual gender and social inclusion contribution to this CCAFS outcome --]
                      <div class="textArea fullBlock">
                        <label><h6>[@s.text name="planning.projectImpactPathways.targetGender" /]:[@customForm.req required=isYearRequired(year) && action.hasPermission("gender") /]</h6></label>
                        [#if editable && (currentCycleYear lte year) && action.hasPermission("gender")]
                          <textarea class="projectIndicatorGender ${(isYearRequired(year))?string('required','optional')}" name="project.indicators.gender">${(projectIndicator.gender)!}</textarea>
                        [#else]
                          [#if projectIndicator?? && !projectIndicator.gender?has_content]
                            [#if currentCycleYear lt year]${fieldEmpty}[#else]<div class="select"><p>[@s.text name="form.values.fieldEmpty"/]</p></div>[/#if]
                          [#else]
                            <div class="select"><p>${(projectIndicator.gender)!}</p></div>
                            <input type="hidden" class="projectIndicatorGender ${(isYearRequired(year))?string('required','optional')}" name="project.indicators.gender" value="${(projectIndicator.gender)!}"/>
                          [/#if] 
                        [/#if] 
                      </div>
                      
                      [#-- -- -- REPORTING BLOCK -- -- --]
                      [#if reportingActive && (year == currentCycleYear)]
                        [#--  Narrative for your achieved annual gender and social inclusion contribution to this CCAFS outcome --]
                        <div class="textArea fullBlock">
                          <label><h6>[@s.text name="reporting.projectImpactPathways.targetNarrativeGenderAchieved" /]:[@customForm.req required=isYearRequired(year) && action.hasPermission("narrativeGender") /]</h6></label>
                          [#if editable && (currentCycleYear lte year) && action.hasPermission("narrativeGender")]
                            <textarea class="projectIndicatorNarrativeGenderAchieved ${(isYearRequired(year))?string('required','optional')}" name="project.indicators.narrativeGender" >${(projectIndicator.narrativeGender)!}</textarea>
                          [#else]
                            [#if projectIndicator?? && !projectIndicator.narrativeGender?has_content]
                              [#if currentCycleYear lt year]${fieldEmpty}[#else]<div class="select"><p>[@s.text name="form.values.fieldEmpty"/]</p></div>[/#if]
                            [#else]
                              <div class="select"><p>${(projectIndicator.narrativeGender)!}</p></div>
                              <input type="hidden" class="projectIndicatorNarrativeGenderAchieved ${(isYearRequired(year))?string('required','optional')}" name="project.indicators.narrativeGender" value="${(projectIndicator.narrativeGender)!}"/>
                            [/#if] 
                          [/#if]
                        </div>
                      [/#if]
                        
                      
                    [/#list]
                  </div>
                  
                  
                </div>
              [/#list]
             
              <p><strong>Mogs</strong></p>
              <div class="mogs">
                <h6>[@s.text name="planning.projectImpactPathways.mogs" /]</h6>
                [#if action.getMidOutcomeOutputs(outcome.id)?has_content]
                  [#assign outputs = action.getMidOutcomeOutputs(outcome.id)]
                  <div class="mogsBlock">
                    [#list outputs as output]
                      <div class="mog">
                       [#if (project.containsOutput(output.id, midOutcome.id))!false] 
                       <label class="checked"> ${output.program.acronym} - MOG #${action.getMOGIndex(output)}: ${output.description} </label>
                       [/#if] 
                      </div>
                    [/#list]
                  </div>
                [/#if]
              </div>
              
              
              
            </div> 
          [/#list]
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
          
          
        [/@s.form] 
      </div>
    </div>  
</section>


[#include "/WEB-INF/global/pages/footer.ftl"]


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