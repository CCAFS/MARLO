[#ftl]
[#assign title = "Project CCAFS Outcomes" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2", "jsUri"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/projects/projectCcafsOutcomes.js", 
  "${baseUrl}/global/js/autoSave.js", 
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = [ ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "ccafsOutcomes" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectCcafsOutcomes", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign startYear = (project.startDate?string.yyyy)?number /]
[#assign endYear = (project.endDate?string.yyyy)?number /]
[#assign fieldEmpty]<div class="select"><p>[@s.text name="form.values.fieldEmpty" /]</p></div>[/#assign]
[#-- List of years --]
[#assign years= [currentCycleYear-1, currentCycleYear, currentCycleYear+1, 2019] /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectCcafsOutcomes.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          <h3 class="headTitle">[@s.text name="projectCcafsOutcomes.title" /]</h3>  
          
          [#list midOutcomesSelected as outcome]
            <div class="borderBox">
              <h4 class="sectionSubTitle">${outcome.ipProgram.acronym} - Outcome 2019</h4>
              <p>${outcome.description}</p>
              
              <p><strong>Indicators</strong></p>
             
              [#list outcome.indicators as indicator]
                <div class="simpleBox">
                  [#if indicator.ipIndicator?has_content]
                    <p class="grayBox">${indicator.ipIndicator.description}</p>
                  [#else]
                    <p class="grayBox">${indicator.description}</p>
                  [/#if]
                  
                  [#-- Tabs --]
                  <ul class="nav nav-tabs projectOutcomeYear-tabs" role="tablist">
                    [#list years as year]
                      <li class="[#if year == currentCycleYear]active[/#if]"><a href="#year-${year}-${indicator.id}" aria-controls="settings" role="tab" data-toggle="tab">${year} [@customForm.req required=isYearRequired(year) /] </a></li>
                    [/#list]
                  </ul> 
                  [#-- Content --]
                  <div class="tab-content projectOutcomeYear-content">
                    [#list years as year]
                      [#if indicator.ipIndicator?has_content]
                        [#assign projectIndicator = (action.getIndicator(indicator.ipIndicator.id, outcome.id, year))!{} /]
                        [#assign projectIndicatorIndex = (action.getIndicatorIndex(indicator.ipIndicator.id, outcome.id, year))!-1 /]
                      [#else]
                        [#assign projectIndicator = (action.getIndicator(indicator.id, outcome.id,  year))!{} /]
                        [#assign projectIndicatorIndex = (action.getIndicatorIndex(indicator.id, outcome.id, year))!-1 /]
                      [/#if]
                    
                      [#assign customName = "project.projectIndicators[${projectIndicatorIndex}]" /]
                    
                      <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="year-${year}-${indicator.id}">
                        
                      [#-- Indicator ID --]
                      <input type="hidden" class="projectIndicatorParent" name="${customName}.ipIndicator.id" value="${(indicator.id)!}"  />
                      
                      [#-- Hidden values --]
                      <input type="hidden" class="projectIndicatorID" name="${customName}.id" value="${(projectIndicator.id)!}" [#if projectIndicator?? && projectIndicator.id?? &&  projectIndicator.id == -1 ]disabled="disabled"[/#if]/>
                      <input type="hidden" class="projectIndicatorYear" name="${customName}.year"  value="${year}" /> 
                      <input type="hidden" class="projectIndicatorOutcome" name="${customName}.outcomeId"  value="${outcome.id}" /> 
                        
                      <div class="form-group row">
                        [#--  1. Indicator target value --]
                        <div class="col-md-4">
                          [#assign isTargetValueRequired = isYearRequired(year) && (action.hasPermission("target") || (!projectIndicator.target?has_content)) && !reportingActive /]
                          [#assign isTargetValueEditable = editable && (currentCycleYear lte year) && action.hasPermission("target") && !reportingActive /]
                          <label>[@s.text name="projectCcafsOutcomes.targetValue" /]:[@customForm.req required=isTargetValueRequired /]</label>
                          [#if isTargetValueEditable]
                            <input type="text" class="projectIndicatorTarget form-control input-sm ${(isYearRequired(year))?string('required','optional')}" name="${customName}.target" value="${(projectIndicator.target)!}"/> 
                          [#else]
                            [#if !projectIndicator.target?has_content]
                              [#if currentCycleYear lt year]
                                ${fieldEmpty}
                              [#else]
                                [#if (currentCycleYear == year) && editable]
                                  <input type="text" class="projectIndicatorTarget form-control input-sm ${(isYearRequired(year))?string('required','optional')}" name="${customName}.target" value="${(projectIndicator.target)!}"/>
                                [#else]
                                  <div class="input"><p>[@s.text name="form.values.fieldEmpty"/]</p></div>
                                [/#if]
                              [/#if]
                            [#else]
                              <div class="select"><p>${(projectIndicator.target)!}</p></div>
                              <input type="hidden" class="projectIndicatorTarget ${(isYearRequired(year))?string('required','optional')}" name="${customName}.target" value="${(projectIndicator.target)!}"/>
                            [/#if]
                          [/#if]
                        </div>
                        [#-- 2. Cumulative target --]
                        [#if (currentCycleYear lte year) && reportingActive]
                        <div class="col-md-4">
                          <label>[@s.text name="projectCcafsOutcomes.comulativeTarget" /]:</label>
                          <div class="input"><p>${(action.calculateAcumulativeTarget(year,projectIndicator))!'Cannot be Calculated'}</p></div>
                        </div>
                        [/#if]
                        [#-- 3. Reporting target --]
                        [#-- -- -- REPORTING BLOCK -- -- --]
                        [#if reportingActive && (year == currentCycleYear)]
                          <div class="col-md-4 " >
                            <label title='[@s.text name="projectCcafsOutcomes.achievedTarget.help" /]'>[@s.text name="projectCcafsOutcomes.achievedTarget" /]:[@customForm.req required=isYearRequired(year) && action.hasPermission("achieved") /]</label>
                            [#if editable && (currentCycleYear lte year) && action.hasPermission("achieved")]
                              <input type="text" class=" fieldFocus projectIndicatorAchievedTarget form-control input-sm ${(isYearRequired(year))?string('required','optional')}" name="${customName}.archived" value="${(projectIndicator.archived)!}"/> 
                            [#else]
                              <div class="input"><p>${(projectIndicator.archived)!'Prefilled if available'}</p></div>
                            [/#if]
                          </div>
                        [/#if]
                      </div>
                      
                      [#-- Indicator target narrative description --]
                      <div class="row">
                        <div class="textArea form-group col-md-12 ">
                          <label>[@s.text name="projectCcafsOutcomes.targetNarrative" /]:[@customForm.req required=isYearRequired(year) && action.hasPermission("description") && !reportingActive /]</label>
                          [#if editable && (currentCycleYear lte year) && action.hasPermission("description") && !reportingActive ]
                            <textarea class="projectIndicatorDescription form-control input-sm ${(isYearRequired(year))?string('required','optional')}" name="${customName}.description">${projectIndicator.description!}</textarea>
                          [#else]
                            [#if !projectIndicator.description?has_content]
                              [#if currentCycleYear lt year]${fieldEmpty}[#else]<div class="select"><p>[@s.text name="form.values.fieldEmpty"/]</p></div>[/#if]
                            [#else]
                              <div class="select"><p>${(projectIndicator.description)!}</p></div>
                              <input type="hidden" class="projectIndicatorDescription ${(isYearRequired(year))?string('required','optional')}" name="${customName}.description" value="${(projectIndicator.description)!}"/>
                            [/#if] 
                          [/#if] 
                        </div>
                      </div>
                      
                      [#-- -- -- REPORTING BLOCK -- -- --]
                      [#if reportingActive && (year == currentCycleYear)]
                        [#-- Narrative for your achieved targets, including evidence --]
                        <div class="row">
                          <div class="textArea form-group col-md-12 ">
                            <label>[@s.text name="projectCcafsOutcomes.targetNarrativeAchieved" /]:[@customForm.req required=isYearRequired(year)  && action.hasPermission("narrativeTargets") /]</label>
                            [#if editable && (currentCycleYear lte year) && action.hasPermission("narrativeTargets")]
                              <textarea class="projectIndicatorNarrativeAchieved form-control input-sm fieldFocus limitWords-100 ${(isYearRequired(year))?string('required','optional')}" name="${customName}.narrativeTargets">${(projectIndicator.narrativeTargets)!}</textarea>
                            [#else]
                              [#if !projectIndicator.narrativeTargets?has_content]
                                [#if currentCycleYear lt year]${fieldEmpty}[#else]<div class="select"><p>[@s.text name="form.values.fieldEmpty"/]</p></div>[/#if]
                              [#else]
                                <div class="select"><p>${(projectIndicator.narrativeTargets)!}</p></div>
                                <input type="hidden" class="projectIndicatorNarrativeAchieved ${(isYearRequired(year))?string('required','optional')}" name="${customName}.narrativeTargets" value="${(projectIndicator.narrativeTargets)!}"/>
                              [/#if] 
                            [/#if]
                          </div>
                        </div>
                      [/#if]
                      
                      [#-- The expected annual gender and social inclusion contribution to this CCAFS outcome --]
                      <div class="row">
                        <div class="textArea form-group col-md-12">
                          <label>[@s.text name="projectCcafsOutcomes.targetGender" /]:[@customForm.req required=isYearRequired(year) && action.hasPermission("gender") && !reportingActive /]</label>
                          [#if editable && (currentCycleYear lte year) && action.hasPermission("gender") && !reportingActive]
                            <textarea class="projectIndicatorGender form-control input-sm ${(isYearRequired(year))?string('required','optional')}" name="${customName}.gender">${(projectIndicator.gender)!}</textarea>
                          [#else]
                            [#if !projectIndicator.gender?has_content]
                              [#if currentCycleYear lt year]${fieldEmpty}[#else]<div class="select"><p>[@s.text name="form.values.fieldEmpty"/]</p></div>[/#if]
                            [#else]
                              <div class="select"><p>${(projectIndicator.gender)!}</p></div>
                              <input type="hidden" class="projectIndicatorGender ${(isYearRequired(year))?string('required','optional')}" name="${customName}.gender" value="${(projectIndicator.gender)!}"/>
                            [/#if] 
                          [/#if] 
                        </div>
                      </div>
                      
                      [#-- -- -- REPORTING BLOCK -- -- --]
                      [#if reportingActive && (year == currentCycleYear)]
                        [#--  Narrative for your achieved annual gender and social inclusion contribution to this CCAFS outcome --]
                        <div class="row">
                          <div class="textArea form-group col-md-12">
                            <label>[@s.text name="projectCcafsOutcomes.targetNarrativeGenderAchieved" /]:[@customForm.req required=isYearRequired(year) && action.hasPermission("narrativeGender") /]</label>
                            [#if editable && (currentCycleYear lte year) && action.hasPermission("narrativeGender")]
                              <textarea class="projectIndicatorNarrativeGenderAchieved form-control input-sm fieldFocus limitWords-100 ${(isYearRequired(year))?string('required','optional')}" name="${customName}.narrativeGender" >${(projectIndicator.narrativeGender)!}</textarea>
                            [#else]
                              [#if !projectIndicator.narrativeGender?has_content]
                                [#if currentCycleYear lt year]${fieldEmpty}[#else]<div class="select"><p>[@s.text name="form.values.fieldEmpty"/]</p></div>[/#if]
                              [#else]
                                <div class="select"><p>${(projectIndicator.narrativeGender)!}</p></div>
                                <input type="hidden" class="projectIndicatorNarrativeGenderAchieved ${(isYearRequired(year))?string('required','optional')}" name="${customName}.narrativeGender" value="${(projectIndicator.narrativeGender)!}"/>
                              [/#if] 
                            [/#if]
                          </div>
                        </div>
                      [/#if]
                        
                    </div>
                    [/#list]
                  </div>
                  
                  
                </div>
              [/#list]
             
              <div class="mogs">
                <p><strong>[@s.text name="projectCcafsOutcomes.mogs" /]</strong></p> 
                [#if action.getMidOutcomeOutputs(outcome.id)?has_content]
                  [#assign outputs = action.getMidOutcomeOutputs(outcome.id)]
                  <div class="mogsBlock">
                    [#list outputs as output]
                       <p class="checked"> ${output.ipProgram.acronym} : ${output.description} </p>
                    [/#list]
                  </div>
                [/#if]
              </div>
              
              
              
            </div> 
          [/#list]
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
          
          
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