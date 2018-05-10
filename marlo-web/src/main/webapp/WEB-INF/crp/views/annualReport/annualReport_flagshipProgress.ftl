[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${powbSynthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "components-font-awesome" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"annualReport", "nameSpace":"annualReport", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}", "nameSpace":"annualReport", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="annualReport.${currentStage}.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/annualReport/submenu-annualReport.ftl" /]
  
  <div class="row">
    [#-- Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/annualReport/menu-annualReport.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/annualReport/messages-annualReport.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
      
        [#assign customName= "annualReport.${currentStage}" /]
        [#assign customLabel= "annualReport.${currentStage}" /]
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="${customName}.title" /]</h3>
        <div class="borderBox">
          [#-- Flagship summary of major results achieved in the past reporting period --]
          <div class="form-group">
            [@customForm.textArea name="${customName}.flagshipSummary" i18nkey="${customLabel}.flagshipSummary" help="${customLabel}.flagshipSummary.help" className="" helpIcon=false required=true editable=editable /]
          </div>
          
          <hr />
          
          [#-- Table B: Status of Planned Milestones --]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="${customLabel}.tableB.title" /]</h4>
            
            [#if PMU]
              [#-- Modal Large --]
              <button type="button" class="pull-right btn btn-default " data-toggle="modal" data-target="#tableA-bigger"> 
                <span class="glyphicon glyphicon-fullscreen"></span> See Full Table B
              </button>
              <div id="tableA-bigger" class="modal fade bs-example-modal-lg " tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
                <div class="modal-dialog modal-lg bigger" role="document">
                  <div class="modal-content">
                    <div class="modal-header">
                      <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    </div>
                    [@tablePlannedMilestonesMacro allowPopups=false/]
                  </div>
                </div>
              </div>
            
              [@tablePlannedMilestonesMacro allowPopups=true/]
            [/#if]
            [#if flagship]
              [#list outcomes as outcome]
                [@annualReportOutcomeMacro element=outcome name="${customName}" index=outcome_index /]
              [/#list]
            [/#if]
          </div>
          
        </div>
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/annualReport/buttons-annualReport.ftl" /]
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro tablePlannedMilestonesMacro allowPopups=true id="" ]
  <div class="">[#-- <div class="table-responsive"> --]
    <table id="tableA" class="table table-bordered">
      <thead>
        <tr>
          <th>[@s.text name="${customLabel}.tableB.fp" /]</th>
          [#if !allowPopups]<th class="col-md-3">[@s.text name="${customLabel}.tableB.subIDO" /]</th>[/#if]
          [#if !allowPopups]<th class="col-md-3">[@s.text name="${customLabel}.tableB.outcomes" /]</th>[/#if]
          <th class="col-md-4">[@s.text name="${customLabel}.tableB.milestone" /]</th>
          <th class="col-md-1">[@customForm.text name="${customLabel}.tableB.explanation" param="${actualPhase.year}" /]</th>
          <th class="col-md-4">[@s.text name="${customLabel}.tableB.status" /]</th>
        </tr>
      </thead>
      <tbody>
        [#list flagships as fp]
          [#assign milestoneSize = fp.milestones?size]
          [#list fp.outcomes as outcome]
            [#assign outcomesSize = outcome.milestones?size]
            [#list outcome.milestones as milestone]
              [#assign isFlagshipRow = (outcome_index == 0) && (milestone_index == 0)]
              [#assign isOutcomeRow = (milestone_index == 0)]
              [#assign milestoneProgress = action.getPowbExpectedCrpProgressProgram(milestone.id,fp.id) ]
              <tr class="fp-index-${fp_index} outcome-index-${outcome_index} milestone-index-${milestone_index}">
                [#-- Flagship --]
                [#if isFlagshipRow]<th rowspan="${milestoneSize}" class="milestoneSize-${milestoneSize}" style="background:${(fp.color)!'#fff'}"><span class="programTag" style="border-color:${(fp.color)!'#fff'}">${fp.acronym}</span></th>[/#if]
                [#-- Sub-IDO --]
                [#if isOutcomeRow && !allowPopups]<td rowspan="${outcomesSize}"> 
                  <ul>[#list outcome.subIdos as subIdo]<li> [#if subIdo.srfSubIdo.srfIdo.isCrossCutting] <strong title="Cross-Cutting IDO">CC</strong> [/#if]${subIdo.srfSubIdo.description}</li>[/#list]</ul>
                </td>
                [/#if]
                [#-- Outcomes --]
                [#if isOutcomeRow && !allowPopups]<td rowspan="${outcomesSize}" class="milestonesSize-${outcomesSize}"> ${outcome.composedName}</td>[/#if]
                [#-- Milestone --]
                <td> ${milestone.composedName} [#if allowPopups] <div class="pull-right">[@milestoneContributions element=milestone tiny=true /] [/#if]</div></td>
                [#-- Explanation --]
                <td class="col-md-4">[#if (milestoneProgress.means?has_content)!false]${milestoneProgress.means}[#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
                [#-- Milestone status --]
                <td>[#if (milestoneProgress.assesmentName?has_content)!false]${milestoneProgress.assesmentName}[#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
              </tr>
            [/#list]
          [/#list]
        [/#list]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro annualReportOutcomeMacro element name index isTemplate=false]
  [#local customName = "${name}" /]
  <div id="powbOutcome-${isTemplate?string('template', index)}" class="powbOutcome simpleBox" style="display:${isTemplate?string('none','block')}">
    [#-- Index --]
    <div class="leftHead sm"><span class="index">${index+1}</span></div>
    [#-- Title --]
    <div class="form-group grayBox"><strong>${(liaisonInstitution.crpProgram.acronym)!liaisonInstitution.acronym} Outcome: </strong> ${(element.description)!}</div>
    [#-- Milestones List --]
    <div class="form-group">
      [#list element.milestones as milestone]
        [@annualReportMilestoneMacro element=milestone name="${customName}.milestoneProgresses" index=milestone_index /]
      [/#list]
    </div>
    
  </div>
[/#macro]

[#macro annualReportMilestoneMacro element name index isTemplate=false]
  [#local annualReportElement= action.getPowbExpectedCrpProgress(element.id)]
  [#local customName = "${name}[${action.getIndex(element.id)}]" /]
  
  <div id="powbMilestone-${isTemplate?string('template', index)}" class="powbMilestone simpleBox" style="display:${isTemplate?string('none','block')}">
    [#-- Index --]
    <div class="leftHead gray sm"><span class="index">${index+1}</span></div>
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(annualReportElement.id)!}" >
    <input type="hidden" name="${customName}.crpMilestone.id" value="${(annualReportElement.crpMilestone.id)!}" >
    
    [#-- Title --]
    <div class="form-group">
      <div class="pull-right">[@milestoneContributions element=element /]</div>
      <p class="text-justify"><strong>Milestone for ${actualPhase.year}</strong> - ${(element.title)!} </p>
    </div>
    
    [#-- Milestone status --]
    <div class="form-group">
      <label>[@s.text name="${customLabel}.milestoneStatus" /]:[@customForm.req required=editable  /]</label><br />
      [#local milestoneStatus = (annualReportElement.status)!"-1" /]
      [@customForm.radioFlat id="${customName}-status-1" name="${customName}.status" label="Complete"   value="1" checked=(milestoneStatus == "1")!false editable=editable cssClass="" cssClassLabel="font-normal"/]
      [@customForm.radioFlat id="${customName}-status-2" name="${customName}.status" label="Extended"   value="2" checked=(milestoneStatus == "2")!false editable=editable cssClass="" cssClassLabel="font-normal"/]
      [@customForm.radioFlat id="${customName}-status-3" name="${customName}.status" label="Cancelled"  value="3" checked=(milestoneStatus == "3")!false editable=editable cssClass="" cssClassLabel="font-normal"/]
      
      [#if !editable && (milestoneStatus = "-1")][@s.text name="form.values.fieldEmpty"/][/#if]
    </div>
    
    [#-- Provide evidence for completed milestones** or explanation for extended or cancelled --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.explanation" i18nkey="${customLabel}.explanation" help="${customLabel}.explanation.help" helpIcon=false display=true required=true className="" editable=editable /]
    </div>
    
    
  </div>
[/#macro]

[#macro milestoneContributions element tiny=false]
[#local projectContributions = action.getContributions(element.id) ]
[#if projectContributions?size > 0]
<button type="button" class="milestoneContributionButton btn btn-default btn-xs" data-toggle="modal" data-target="#milestone-${element.id}">
  <span class="icon-20 project"></span> <strong>${projectContributions?size}</strong> [#if !tiny][@s.text name="expectedProgress.milestonesContributions" /][/#if]
</button>

<!-- Modal -->
<div class="modal fade" id="milestone-${element.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">[@s.text name="expectedProgress.milestonesContributions" /]</h4>
        <hr />
        <p><strong>Milestone for ${actualPhase.year}</strong> - ${(element.title!)}</p>
        [#assign hasTarget = element.srfTargetUnit?? && (element.srfTargetUnit.id != -1) /]
        [#if hasTarget]
          <p><strong>Target unit:</strong> ${(element.srfTargetUnit.name!)} <br /> <strong>Target value:</strong> ${(element.value!)}</p>
        [/#if]
      </div>
      <div class="modal-body">
        <div class="">
          <table class="table table-bordered">
            <thead>
              <tr>
                <th class="col-md-1"> Project ID </th>
                <th class="col-md-4"> Project Title </th>
                [#if hasTarget]<th class="col-md-1"> ${(element.srfTargetUnit.name!)} Achieved</th>[/#if]
                <th class="col-md-6"> [@s.text name="${customLabel}.contributionMilestone.target" /]  </th>
                <th class="col-md-6"> [@s.text name="${customLabel}.contributionMilestone.narrativeAchieved" /]  </th>
                <th> </th>
              </tr>
            </thead>
            <tbody>
              [#list projectContributions as contribution]
                [#local pURL][@s.url namespace="/projects" action="${(crpSession)!}/contributionsCrpList"][@s.param name='projectID']${contribution.projectOutcome.project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                [#local poURL][@s.url namespace="/projects" action="${(crpSession)!}/contributionCrp"][@s.param name='projectOutcomeID']${contribution.projectOutcome.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                <tr>
                  <td> <a href="${pURL}" target="_blank"> P${contribution.projectOutcome.project.id} </a> </td>
                  <td> <a href="${pURL}" target="_blank"> ${contribution.projectOutcome.project.projectInfo.title} </a></td>
                  [#if hasTarget]
                  <td class="text-center">
                    [#if (contribution.expectedUnit.name??)!false]${(contribution.achievedValue)!}[#else]<i>N/A</i>[/#if]
                  </td>
                  [/#if]
                  <td>${(contribution.narrativeTarget?replace('\n', '<br>'))!} </td>
                  <td>${(contribution.narrativeAchieved?replace('\n', '<br>'))!} </td>                  
                  <td> <a href="${poURL}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>  </td>
                </tr>
              [/#list]
            </tbody>
          </table>
        </div>
        
      </div>
      <div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button></div>
    </div>
  </div>
</div>
[/#if]
[/#macro]