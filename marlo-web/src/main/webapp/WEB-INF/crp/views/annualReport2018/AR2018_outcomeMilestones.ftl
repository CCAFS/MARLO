[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "trumbowyg" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"table5.${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis" /]
[#assign customLabel= "annualReport2018.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="${customLabel}.help" /]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
  [#else]
    [#-- Program (Flagships and PMU) --]
    [#include "/WEB-INF/crp/views/annualReport2018/submenu-AR2018.ftl" /]
    
    <div class="row">
      [#-- POWB Menu --]
      <div class="col-md-3">[#include "/WEB-INF/crp/views/annualReport2018/menu-AR2018.ftl" /]</div>
      [#-- POWB Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/annualReport2018/messages-AR2018.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="">
          
            [#-- Table 5: Status of Planned Outcomes and Milestones --]
            <div class="form-group">
              <h4 class="headTitle">[@s.text name="${customLabel}.table5.title" /]</h4>
                [#list outcomes as outcome]
                  [@annualReport2018OutcomesMacro element=outcome name="${customLabel}" index=outcome_index /]
                [/#list]
            </div>
          
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport2018/buttons-AR2018.ftl" /]
        [/@s.form] 
      </div> 
    </div>
  [/#if] 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro annualReport2018OutcomesMacro element name index isTemplate=false]
  [#local customName = "${name}" /]
     
    <div id="powbOutcome-${isTemplate?string('template', index)}" class="powbOutcome borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Index --]
    <div class="leftHead sm"><span class="index">${index+1}</span></div>
    [#-- Title --]
    <div class="form-group grayBox"><strong>${(liaisonInstitution.crpProgram.acronym)!liaisonInstitution.acronym} Outcome: </strong> ${(element.description)!}</div>
    [#-- Narrative on progress --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.outcome.progressNarrative" i18nkey="${customLabel}.outcome.progressNarrative" help="${customLabel}.outcome.progressNarrative.help" className="limitWords-100" helpIcon=false required=true editable=editable allowTextEditor=true /]
    </div>
    [#-- Milestones List --]
    <h4 class="simpleTitle">[@s.text name="${customLabel}.milestones.title" /]</h4>
    <div class="form-group">
       [#list element.milestones as milestone]
        [@annualReport2018MilestoneMacro element=milestone name="${customName}.milestones" index=milestone_index /]
      [/#list]
    </div> 
    
  </div>
[/#macro]

[#macro annualReport2018MilestoneMacro element name index isTemplate=false]
  [#local annualReportElement= action.getReportSynthesisFlagshipProgressMilestone(element.id)]
  [#local customName = "${name}[${action.getIndex(element.id)}]" /]
  
  <div id="powbMilestone-${isTemplate?string('template', index)}" class="powbMilestone simpleBox" style="display:${isTemplate?string('none','block')}">
    [#-- Index --]
    <div class="leftHead gray sm"><span class="index">${index+1}</span></div>
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(annualReportElement.id)!}" >
    <input type="hidden" name="${customName}.crpMilestone.id" value="${(annualReportElement.crpMilestone.id)!}" >
    
    [#-- Title --]
    <div class="form-group grayBox">
      <div class="pull-right">[@milestoneContributions element=element /]</div>
      <p class="text-justify"><strong>Milestone for ${actualPhase.year}</strong> - ${(element.title)!} </p>
    </div>
    
    [#-- Gender Marker --]
    <div class="form-group row">
      <div class="col-md-3 cc-milestones">
        <h5>[@s.text name="${customLabel}.milestoneGenderScore" /]</h5>
      </div>
      <div class="col-md-3">
         [@customForm.select name="${customName}.milestoneGenderScoreMarker" label=""  i18nkey="${customLabel}.milestoneScoreMarker" listName="" keyFieldName=""  displayFieldName=""   required=true  className="" editable=editable/]
      </div>
      <div class="col-md-6">
         [@customForm.input name="${customName}.milestoneGenderScoreJustification" i18nkey="${customLabel}.milestoneScoreJustification" help="${customLabel}.milestoneGenderScoreJustification.help" className="input-sm" helpIcon=true required=true editable=editable /]
      </div>
    </div>
    
    [#-- Youth Marker --]
    <div class="form-group row">
      <div class="col-md-3 cc-milestones">
        <h5>[@s.text name="${customLabel}.milestoneYouthScore" /]</h5>
      </div>
      <div class="col-md-3">
         [@customForm.select name="${customName}.milestoneYouthScoreMarker" label=""  i18nkey="${customLabel}.milestoneScoreMarker" listName="" keyFieldName=""  displayFieldName=""   required=true  className="" editable=editable/]
      </div>
      <div class="col-md-6">
         [@customForm.input name="${customName}.milestoneYouthScoreJustification" i18nkey="${customLabel}.milestoneScoreJustification" help="${customLabel}.milestoneYouthScoreJustification.help" className="input-sm" helpIcon=true required=true editable=editable /]
      </div>
    </div>
    
    [#-- CapDev Marker --]
    <div class="form-group row">
      <div class="col-md-3 cc-milestones">
        <h5>[@s.text name="${customLabel}.milestoneCapDevScore" /]</h5>
      </div>
      <div class="col-md-3 cc-milestones">
         [@customForm.select name="${customName}.milestoneCapDevScoreMarker" label=""  i18nkey="${customLabel}.milestoneScoreMarker" listName="" keyFieldName=""  displayFieldName=""   required=true  className="" editable=editable/]
      </div>
      <div class="col-md-6">
         [@customForm.input name="${customName}.milestoneCapDevScoreJustification" i18nkey="${customLabel}.milestoneScoreJustification" help="${customLabel}.milestoneCapDevScoreJustification.help" className="input-sm" helpIcon=true required=true editable=editable /]
      </div>
    </div>
    
    [#-- Climate Change Marker --]
    <div class="form-group row">
      <div class="col-md-3 cc-milestones">
        <h5>[@s.text name="${customLabel}.milestoneClimateChangeScore" /]</h5>
      </div>
      <div class="col-md-3">
         [@customForm.select name="${customName}.milestoneClimateChangeScoreMarker" label=""  i18nkey="${customLabel}.milestoneScoreMarker" listName="" keyFieldName=""  displayFieldName=""   required=true  className="" editable=editable/]
      </div>
      <div class="col-md-6">
         [@customForm.input name="${customName}.milestoneClimateChangeScoreJustification" i18nkey="${customLabel}.milestoneScoreJustification" help="${customLabel}.milestoneClimateChangeScoreJustification.help" className="input-sm" helpIcon=true required=true editable=editable /]
      </div>
    </div>
    
    [#-- Milestone status --]
    <div class="form-group">
      <label>[@s.text name="${customLabel}.milestoneStatus" /]:[@customForm.req required=editable  /]</label><br />
      [#-- [#local milestoneStatus = (annualReportElement.milestonesStatus)!-1 /] --]
      [#local milestoneStatus = -1 /]
      [@customForm.radioFlat id="${customName}-status-1" name="${customName}.milestonesStatus" label="Complete"   value="1" checked=(milestoneStatus == 1)!false editable=editable cssClass="" cssClassLabel="font-normal"/]
      [@customForm.radioFlat id="${customName}-status-2" name="${customName}.milestonesStatus" label="Extended"   value="2" checked=(milestoneStatus == 2)!false editable=editable cssClass="" cssClassLabel="font-normal"/]
      [@customForm.radioFlat id="${customName}-status-3" name="${customName}.milestonesStatus" label="Cancelled"  value="3" checked=(milestoneStatus == 3)!false editable=editable cssClass="" cssClassLabel="font-normal"/]
      [@customForm.radioFlat id="${customName}-status-4" name="${customName}.milestonesStatus" label="Changed"    value="4" checked=(milestoneStatus == 4)!false editable=editable cssClass="" cssClassLabel="font-normal"/]
      
      [#if !editable && (milestoneStatus == -1)][@s.text name="form.values.fieldEmpty"/][/#if]
    </div>
    
    [#-- Evidence for completed milestones or explanation for extended or cancelled --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.milestoneEvidence" i18nkey="${customLabel}.milestoneEvidence" help="${customLabel}.milestoneEvidence.help" helpIcon=false display=true required=false className="limitWords-50" editable=editable allowTextEditor=true /]
    </div>
    
    [#-- Extendend, cancelled or changed milestones - Main reason --]
    <div class="form-group">
      [@customForm.select name="${customName}.milestoneMainReason" label=""  i18nkey="${customLabel}.milestoneMainReason" listName="" keyFieldName=""  displayFieldName=""   required=true  className="" editable=editable/]
    </div>
    
    [#-- Extendend, cancelled or changed milestones - Other reason --]
    [#local display = true /]
    <div class="form-group" style="display:${display?string('block','none')}">
      [@customForm.input name="${customName}.milestoneOtherReason" i18nkey="${customLabel}.milestoneOtherReason" display=true required=false className="input-sm" editable=editable /]
    </div>
    
    
  </div>
[/#macro]


[#macro milestoneContributions element tiny=false]
  [#local projectContributions = (action.getContributions(element.id))![] ]
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
                  <th class=""> Project Title </th>
                  [#if hasTarget]<th class="col-md-1"> ${(element.srfTargetUnit.name!)} Achieved</th>[/#if]
                  <th> [@s.text name="${customLabel}.contributionMilestone.narrativeAchieved" /]  </th>
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
                    <td class="text-center">[#if (contribution.expectedUnit.name??)!false]${(contribution.achievedValue)!}[#else]<i>N/A</i>[/#if]</td>
                    [/#if]
                    <td>
                      [#if ((contribution.narrativeAchieved)?has_content)!false]
                        ${(contribution.narrativeAchieved?replace('\n', '<br>'))!}
                      [#else]
                        <i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                      [/#if]
                    </td>
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