[#ftl]
[#assign title = "Cluster Contributions to Performance Indicators" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "jsUri"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectContributionsCrpList.js?20180131_2",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ]
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectContributionsCrpList.css?20220512a",
  "${baseUrlMedia}/css/projects/projectsContributionToLP6.css"
  ] /]
[#assign currentStage = "contributionsCrpList" /]
[#assign isListSection = true /]


[#if !action.isAiccra()]
  [#assign currentSection = "projects" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"P${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"projectContributionsCrpList", "nameSpace":"${currentSection}", "action":""}
  ] /]
[#else]
  [#assign currentSection = "clusters" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"C${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"projectContributionsCrpList", "nameSpace":"${currentSection}", "action":""}
  ] /]
[/#if]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/relationsPopupMacro.ftl" as popUps /]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]

[#assign startYear = (project.projectInfo.startDate?string.yyyy)?number /]
[#assign endYear = (project.projectInfo.endDate?string.yyyy)?number /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectContributionsCrpList.help" /] </p>
  </div>
  <div style="display:none" class="viewMore closed"></div>
</div>

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
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
          [#--  Feedback Status --]
          <div class="form-group col-md-12 legendContent">
            <div class="colors">
              <div class="col-md-12 form-group "><b>Feedback status:</b></div>
              <div class="color col-md-4"><img src="${baseUrlCdn}/global/images/comment.png" class="qaComment feedbackStatus">New comment</div>
              <div class="color col-md-4"><img src="${baseUrlCdn}/global/images/comment_yellow.png" class="qaComment feedbackStatus">Pending to tackle</div>
              <div class="color col-md-4"><img src="${baseUrlCdn}/global/images/comment_green.png" class="qaComment feedbackStatus">Agreed</div>
            </div>
          </div>

          <h3 class="headTitle">[@s.text name="projectContributionsCrpList.title" /]</h3>
          <div id="projectContributionsCrpList" class="borderBox">
            [#-- Your project contributes to the flagships --]
            <div class="form-group">
              <p>
                <strong>[@s.text name="projectContributionsCrpList.flagships" /]:</strong><br />
                [#if project.flagships?has_content][#list project.flagships as element]<span class="programTag" style="border-color:${element.color}">${element.acronym}</span>[/#list][/#if]
                <div class="clearfix"></div>
              </p>
            </div>

            [#if reportingActive && canEdit] <p class="note">[@s.text name="projectContributionsCrpList.reportingHelp"/]</p>[/#if]

            [#-- Project Outcomes List --]
            <table id="projectOutcomesList" class="table table-striped table-hover ">
              <thead>
                <tr>
                  <th>[@s.text name="global.flagship" /]</th>
                  <th>Performance Indicator 2023</th>
                  <th></th>
                  <th>Status</th>
                  <th>Remove</th>
                </tr>
              </thead>
              <tbody>
              [#if project.outcomes?has_content]
                [#list project.outcomes?sort_by("order") as projectOutcome]
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
              <div class="addNewOutcome">
                <div class="outcomesListBlock">
                  <span id="outcomesSelectedIds" style="display:none">[#if project.outcomes?has_content][#list project.outcomes as e]${e.crpProgramOutcome.id}[#if e_has_next],[/#if][/#list][/#if]</span>
                  [@customForm.select name="outcomeId" label="" disabled=!canEdit i18nkey="projectContributionsCrpList.selectOutcome" listName="outcomes" keyFieldName="id" displayFieldName="name" className="" /]
                </div>
                <div class="addOutcomeBlock">
                  <a href="${baseUrl}/projects/${crpSession}/addNewProjectOuctome.do?projectID=${projectID}&outcomeId=-1&phaseID=${(actualPhase.id)!}">
                    <div class="button-blue"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addOutcome"/]</div>
                  </a>
                </div>
              </div>
            [/#if]
          </div>
      [@contributionToLP6 /]
      </div>
    </div>
</section>
[/#if]

[#-- Template Outcome List --]
[#include "/WEB-INF/crp/macros/outcomesListSelectMacro.ftl"]

[#-- Other contribution template --]
[@otherContributionMacro element={} name="name" index=-1 template=true /]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro outcomeContributionMacro projectOutcome name index isTemplate=false ]
  [#local projectOutcomeID =  projectOutcome.id /]
  [#local projectOutcomeUrl = "${baseUrl}/projects/${crpSession}/contributionCrp.do?projectOutcomeID=${projectOutcomeID}&edit=true&phaseID=${(actualPhase.id)!}" /]
  [#local hasDraft = (action.getAutoSaveFilePath(projectOutcome.class.simpleName, "contributionCrp", projectOutcome.id))!false /]
  <tr class="projectOutcome">
      [#-- Flagship outcome --]
      <td class="text-center">${projectOutcome.crpProgramOutcome.crpProgram.acronym}</td>
      [#-- Title --]
      <td>
        [#-- isThisComplete --]
        [#local objectStatus = (action.getProjectOutcomeStatus(projectOutcome.id))!{}]
        [#if objectStatus?has_content]
          [#if !(objectStatus.missingFields)?has_content]
            [#assign isThisComplete = true /]
          [#else]
            [#assign isThisComplete = false /]
          [/#if]
        [#else]
            [#assign isThisComplete = false /]
        [/#if]

        [#-- Draft Tag --]
        [#if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if]

        [#-- Report --]
        [#if reportingActive && !isThisComplete]
          <span class="label label-primary" title="Required for reporting"><span class="glyphicon glyphicon-flash" ></span> Report</span>
        [/#if]

        <a href="${projectOutcomeUrl}">
          ${projectOutcome.crpProgramOutcome.description}
          [#if action.hasSpecificities('crp_ip_outcome_indicator')]
            [#if (projectOutcome.crpProgramOutcome.indicator?has_content)!false]<i class="indicatorText"><br /><strong>Indicator: </strong>${(projectOutcome.crpProgramOutcome.indicator)!'No Indicator'}</i>[/#if]
          [/#if]
        </a>

      </td>
      <td>
        [#if !isTemplate][@popUps.relationsMacro element=projectOutcome labelText=true /]</div>[/#if]
        [#if !isTemplate][@popUps.relationsMacro element=projectOutcome tag="expectedOutcomes" labelText=true /]</div>[/#if]
        [#if !isTemplate][@popUps.relationsMacro element=projectOutcome tag="innovationOutcomes" labelText=true /]</div>[/#if]
      </td>
      [#-- Contribution Status --]
      <td class="text-center">
        [#if action.getProjectOutcomeStatus(projectOutcome.id)?? ]
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
        [#if ((action.hasPermission("delete"))!true) && action.canBeDeleted((projectOutcome.id)!-1,(projectOutcome.class.name)!"" ) && !action.isCenterGlobalUnit() ]
          <a id="removeOutcome-${projectOutcomeID}" onclick="return confirm('Are you sure you want to delete this?');" class="removeOutcome" href="${baseUrl}/projects/${crpSession}/removeProjectOuctome.do?projectID=${projectID}&outcomeId=${projectOutcomeID}&phaseID=${(actualPhase.id)!}" title="Remove indicator mapping to this cluster">
            <img src="${baseUrlCdn}/global/images/trash.png" />
          </a>
        [#else]
          <img src="${baseUrlCdn}/global/images/trash_disable.png" title="To remove this indicator mapping from this cluster, ensure there are no Deliverables, OICRs & MELIAs or Innovations mapped to this indicator" />
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

[#macro contributionToLP6 template=false]
[#assign isContributing = ((action.getProjectLp6ContributionValue(project.id, actualPhase.id))!false) ]
[#if action.hasSpecificities('crp_lp6_active') && reportingActive]
  <div id="projectContributionToLP6" class="borderBox project-${project.id} phase-${actualPhase.id}">

   [#-- <a class="btn lp6-pdf btn-link" role="button" data-toggle="popover" data-trigger="focus" title="[@s.text name="projects.LP6Contribution.disabledPDF"/]"><img src="${baseUrlCdn}/global/images/pdf.png" height="25"/>[[@s.text name="projects.LP6Contribution.explanatoryPDF" /]]</a>--]
   <h4>[@s.text name="projects.LP6Contribution.title" /]</h4>
   <p class="note lp6-contribution-note"><small>[@s.text name="projects.LP6Contribution.infoText"/] (<span class="lp6-view-more" data-toggle="modal" data-target=".lp6info-modal">view more</span>)</small></p>
   <div class="form-group">
       <label>[@s.text name="projects.LP6Contribution.contribution"/][@customForm.req required=true /]</label>
       [@customForm.radioFlat id="lp6Contribution-yes" name="lp6Contribution" label="Yes" value="true" checked=(projectLp6Contribution.contribution)!false cssClassLabel="radio-label-yes" editable=Editable /]
       [@customForm.radioFlat id="lp6Contribution-no" name="lp6Contribution" label="No" value="false" checked=!((projectLp6Contribution.contribution)!true) cssClassLabel="radio-label-no" editable=Editable /]
  </div>
  <div class="form-group contributionNote note" style="display:${(isContributing)?string('block','none')}">
     [@s.text name="projects.LP6Contribution.contributionNote"/]
  </div>
  </div>

  [#-- LP6 Help Text expanded --]
    <div class="modal fade extended-table-modal lp6info-modal" tabindex="-1" role="dialog" aria-labelledby="extendedTableModal" aria-hidden="true">
      <div class="modal-dialog modal-lg">
         <div class="modal-content">
           <button type="button" class="close lp6-close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
             <div class="lp6-help">[@s.text name="projects.LP6Contribution.helpText"/]</div>
          </div>
      </div>
    </div>
[/#if]
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
