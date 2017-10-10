[#ftl]
[#assign title = "Project Contributions to CRP" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2", "jsUri"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectContributionsCrpList.js",
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectContributionsCrpList.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "contributionsCrpList" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectContributionsCrpList", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#assign startYear = (project.startDate?string.yyyy)?number /]
[#assign endYear = (project.endDate?string.yyyy)?number /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectContributionsCrpList.help" /] </p>
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
        [#include "/WEB-INF/crp/views/projects/messages-projectOutcomes.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          <h3 class="headTitle">[@s.text name="projectContributionsCrpList.title" /]</h3>  
          <div id="projectContributionsCrpList" class="borderBox">
            [#-- Your project contributes to the flagships --]
            <div class="form-group">
              <p>
                <strong>Your Project contributes to the following Flagships:</strong><br />
                [#if project.flagships?has_content][#list project.flagships as element]<span class="programTag" style="border-color:${element.color}">${element.acronym}</span>[/#list][/#if]
                <div class="clearfix"></div>
              </p>
            </div>
            
            [#-- Project Outcomes List --]
            <table id="projectOutcomesList" class="table table-striped table-hover ">
              <thead>
                <tr>
                  <th>Flagship</th>
                  <th>Outcome 2022</th>
                  <th>Status</th>
                  <th>Remove</th>
                </tr>
              </thead>
              <tbody>
              [#if project.outcomes?has_content]
                [#list project.outcomes as projectOutcome]
                  [@outcomeContributionMacro projectOutcome=projectOutcome name="" index=projectOutcome_index  /]
                [/#list] 
              [/#if]
              </tbody> 
            </table>
            
            [#if !project.outcomes?has_content]
              <p class="emptyMessage text-center">[@s.text name="projectContributionsCrpList.contributionsEmpty"/]</p>
            [/#if]
            
            [#-- Add a new Outcomes --]
            [#if canEdit]
              [#if !reportingActive]
              <div class="addNewOutcome">
                <div class="outcomesListBlock">
                  <span id="outcomesSelectedIds" style="display:none">[#if project.outcomes?has_content][#list project.outcomes as e]${e.crpProgramOutcome.id}[#if e_has_next],[/#if][/#list][/#if]</span>  
                  [@customForm.select name="outcomeId" label="" disabled=!canEdit i18nkey="projectContributionsCrpList.selectOutcome" listName="outcomes" keyFieldName="id" displayFieldName="name" className="" /]
                </div>
                <div class="addOutcomeBlock">
                  <a href="${baseUrl}/projects/${crpSession}/addNewProjectOuctome.do?projectID=${projectID}&outcomeId=-1">
                    <div class="button-blue"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addOutcome"/]</div>
                  </a>
                </div>
              </div>
              [/#if]
            [/#if] 
          </div> 
           
          [#-- Further Flagship Contributions  --]
          [#if reportingActive]
            <br />
            <h3 class="headTitle">[@customForm.text name="projectContributionsCrpList.flagshipContribution" /] </h3>
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
                  [#-- Contribution(s) to other flagships outcomes --]
                  [@customForm.text name="projectContributionsCrpList.projectContributedOtherFlagships" readText=!editable /]
                  [#-- Others impact pathways contributions --]
                  <div class="otherContributionsBlock">
                    [#if project.otherContributions?has_content]
                      [#list project.otherContributions as element]
                        [@otherContributionMacro element=element name="" index=element_index /] 
                      [/#list]
                    [#else]
                      [@otherContributionMacro element={} name="" index=0 /] 
                      [#-- <div class="emptyMessage simpleBox center"><p>There is not other contributions added</p></div> --]
                    [/#if]
                  </div>
                  [#-- Add contribution button --]
                  [#if editable] 
                    <div class="addOtherContribution bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="projectOtherContributions.addOtherContribution"/]</div>
                  [/#if]
                  <div class="clearfix"></div> 
                </div>
              [/#list]
            </div>
          [/#if]
          
          [#if reportingActive]  
            [#-- Section Buttons & hidden inputs--]
            [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
          [#else]
            [#-- Hidden parameters --]
            <input type="hidden" name="projectID" value="${projectID}"/>
          [/#if]
          
        [/@s.form] 
      </div>
    </div>  
</section>

[#-- Template Outcome List --]
[#include "/WEB-INF/crp/macros/outcomesListSelectMacro.ftl"]

[#-- Other contribution template --]
[@otherContributionMacro element={} name="name" index=-1 template=true /]

[#include "/WEB-INF/crp/pages/footer.ftl"]

[#macro outcomeContributionMacro projectOutcome name index isTemplate=false ]
  [#local projectOutcomeID =  projectOutcome.id /] 
  [#local projectOutcomeUrl = "${baseUrl}/projects/${crpSession}/contributionCrp.do?projectOutcomeID=${projectOutcomeID}&edit=true" /]
  [#local hasDraft = (action.getAutoSaveFilePath(projectOutcome.class.simpleName, "contributionCrp", projectOutcome.id))!false /]
  <tr class="projectOutcome">
      [#-- Flagship outcome --]
      <td class="text-center">${projectOutcome.crpProgramOutcome.crpProgram.acronym}</td>
      [#-- Title --]
      <td>
        [#-- Draft Tag --]
        [#if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if]
        <a href="${projectOutcomeUrl}">
          ${projectOutcome.crpProgramOutcome.description}
          [#if action.hasSpecificities('crp_ip_outcome_indicator')]
            [#if (projectOutcome.crpProgramOutcome.indicator?has_content)!false]<i class="indicatorText"><br /><strong>Indicator: </strong>${(projectOutcome.crpProgramOutcome.indicator)!'No Indicator'}</i>[/#if]
          [/#if]
        </a>
      </td>
      [#-- Contribution Status --]
      <td class="text-center">
        [#if action.getProjectOutcomeStatus(projectOutcome.id)??]
          [#if !((action.getProjectOutcomeStatus(projectOutcome.id)).missingFields)?has_content]
            <span class="icon-20 icon-check" title="Complete"></span>
          [#else]
            <span class="icon-20 icon-uncheck" title=""></span> 
          [/#if]
        [#else]
            <span class="icon-20 icon-uncheck" title=""></span>
        [/#if]
      </td>
      [#-- Remove Contribution--]
      <td class="text-center">
        [#if ((action.hasPermission("delete"))!true) && canEdit]
          <a id="removeOutcome-${projectOutcomeID}" class="removeOutcome" href="${baseUrl}/projects/${crpSession}/removeProjectOuctome.do?projectID=${projectID}&outcomeId=${projectOutcomeID}" title="">
            <img src="${baseUrl}/global/images/trash.png" />
          </a>
        [#else]
          <img src="${baseUrl}/global/images/trash_disable.png" title="" />
        [/#if]
      </td>
  </tr>
[/#macro]

[#macro otherContributionMacro element name index template=false ]
  [#assign customName = "${name}[${template?string('-1',index)}]" /]
  [#assign contribution = (element)!{} /]
  <div id="otherContribution-${template?string('template',index)}" class="otherContribution simpleBox" style="display:${template?string('none','block')}">
    <div class="loading" style="display:none"></div>
    [#-- Edit/Back/remove buttons --]
    [#if editable]<div class="removeElement" title="[@s.text name="projectOtherContributions.removeOtherContribution" /]"></div>[/#if]
    [#-- Other Contribution ID --]
    <input type="hidden" name="${customName}.id" class="otherContributionId" value="${(contribution.id)!-1}"/>
    [#-- Indicator --]
    <div class="form-group ${reportingActive?string('fieldFocus','')}">
      <div class="row">
        <div class="col-md-12">
          [@customForm.select name="${customName}.outcome" className="otherContributionIndicator" label="" i18nkey="projectOtherContribution.outcome" listName="outcomes" keyFieldName="id" displayFieldName="composedName" required=true editable=editable /]
        </div>
      </div>
    </div>
    [#-- Describe how you are contributing to the selected outcome --]
    <div class="form-group">
      <div class="row">
        <div class="col-md-12">
        <label>[@customForm.text name="projectOtherContribution.description" param="${currentCycleYear}" readText=!editable /]:[@customForm.req required=editable /]</label>
        [@customForm.textArea name="${customName}.description" className="otherContributionDescription limitWords-100 ${reportingActive?string('fieldFocus','')}"  i18nkey="" showTitle=false required=true editable=editable  /]
        </div>
      </div>
    </div>
    [#-- Target contribution --]
    <div class="form-group">
      <div class="row">
        <div class="col-md-5">
          <label>[@customForm.text name="projectOtherContribution.target" readText=!editable /]:</label>
          [@customForm.input name="${customName}.target" className="otherContributionTarget ${reportingActive?string('fieldFocus','')}" i18nkey="" showTitle=false editable=editable  /]
        </div>
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