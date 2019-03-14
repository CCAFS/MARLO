[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "trumbowyg" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"table5.${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#import "/WEB-INF/crp/views/annualReport2018/macros-AR2018.ftl" as macrosAR /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis.reportSynthesisFlagshipProgress" /]
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
      [#--  Menu --]
      <div class="col-md-3">[#include "/WEB-INF/crp/views/annualReport2018/menu-AR2018.ftl" /]</div>
      [#--  Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/annualReport2018/messages-AR2018.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="">
          
            [#-- Table 5: Status of Planned Outcomes and Milestones --]
            <div class="form-group">
              [#if PMU]
                <div class="borderBox">
                  [#-- Button --]
                  <button type="button" class="btn btn-default pull-right" data-toggle="modal" data-target="#modal-policies">
                     <span class="glyphicon glyphicon-fullscreen"></span> See Full table 5
                  </button>
                  [#-- Modal --]
                  <div class="modal fade" id="modal-policies" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                    <div class="modal-dialog modal-lg" role="document">
                      <div class="modal-content">
                        <div class="modal-header">
                          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                          <h4 class="modal-title" id="myModalLabel">[@s.text name="${customLabel}.title" /]</h4>
                        </div>
                        <div class="modal-body">
                          [#-- Full table --]
                          <div class="viewMoreSyntesisTable-block">
                            [@tableOutcomesMilestones allowPopups=false  /]
                          </div>
                        </div>
                        <div class="modal-footer">
                          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                      </div>
                    </div>
                  </div>
                  [#-- Table --]
                  <div class="viewMoreSyntesisTable-block">
                    [@tableOutcomesMilestones  /]
                  </div>
                </div>
              [#else]
                [#list outcomes as outcome]
                  [@annualReport2018OutcomesMacro element=outcome name="${customName}.outcomes" index=outcome_index /]
                [/#list]
              [/#if]
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

[#macro tableOutcomesMilestones allowPopups=true id="" ]
  <div class="">[#-- <div class="table-responsive"> --]
    <table id="tableA" class="table table-bordered">
      <thead>
        <tr>
          <th rowspan="2">[@s.text name="expectedProgress.tableA.fp" /]</th>
          <th rowspan="2"> Outcome </th>
          [#if !allowPopups]<th rowspan="2"> Outcome Progress </th>[/#if]
          <th rowspan="2"> Milestone </th>
          <th rowspan="2"> Status</th>
          [#if !allowPopups]
          <th rowspan="2">Milestone Evidence</th>
          <th colspan="4" class="text-center">Cross-Cutting Markers</th>
          <th rowspan="2"></th>
          [/#if]
        </tr>
        [#if !allowPopups]
        <tr>
          <th> <small>Gender</small></th>
          <th> <small>Youth</small></th>
          <th> <small>CapDev</small></th>
          <th> <small>Climate Change</small></th>
        </tr>
        [/#if]
      </thead>
      <tbody>
        [#list (flagships)![] as fp]
          [#assign milestoneSize = fp.milestones?size]
          [#list fp.outcomes as outcome]
            [#assign outcomesSize = outcome.milestones?size]
            [#list outcome.milestones as milestone]
              [#assign isFlagshipRow = (outcome_index == 0) && (milestone_index == 0)]
              [#assign isOutcomeRow = (milestone_index == 0)]
              [#assign milestoneProgress = action.getReportSynthesisFlagshipProgressMilestone(milestone.id) ]
              <tr class="fp-index-${fp_index} outcome-index-${outcome_index} milestone-index-${milestone_index}">
                [#-- Flagship --]
                [#if isFlagshipRow]<th rowspan="${milestoneSize}" class="milestoneSize-${milestoneSize}" style="background:${(fp.color)!'#fff'}"><span class="programTag" style="border-color:${(fp.color)!'#fff'}">${fp.acronym}</span></th>[/#if]
                [#-- Outcomes --]
                [#if isOutcomeRow]
                  <td rowspan="${outcomesSize}" class="milestonesSize-${outcomesSize}"> 
                    [#-- Outcome Statement --]
                    ${outcome.composedName}
                    [#-- Sub-IDOs --]
                    [#if !allowPopups]
                    <br />
                    <small>
                      <ul>[#list outcome.subIdos as subIdo]<li> [#if subIdo.srfSubIdo.srfIdo.isCrossCutting] <strong title="Cross-Cutting IDO">CC</strong> [/#if]${subIdo.srfSubIdo.description}</li>[/#list]</ul>
                    </small>
                    [/#if]
                  </td>
                [/#if]
                [#-- Outcomes - Narrative --]
                [#if isOutcomeRow && !allowPopups]
                  <td rowspan="${outcomesSize}" class="milestonesSize-${outcomesSize}"> 
                    Lorem ipsum dolor sit amet, consectetur adipisicing elit. Tempore saepe illo sapiente quam consectetur eius similique. Soluta nostrum dignissimos rem id pariatur nulla velit facilis excepturi ab fugiat ad rerum.
                  </td>
                [/#if]
                [#-- Milestone --]
                <td> ${milestone.composedName} [#if allowPopups] <div class="pull-right">[@milestoneContributions element=milestone tiny=true /] [/#if]</div></td>
                [#-- Milestone Status --]
                <td> Completed </td>
                [#if !allowPopups]
                [#-- Milestone Evidence --]
                <td>  Lorem ipsum dolor sit amet, consectetur adipisicing elit. Deleniti quibusdam at est nobis provident voluptatum quos voluptas cupiditate aliquam accusamus ratione sunt. Eaque praesentium repellendus quis id repudiandae tempora aliquam.</td>
                [#-- Cross Cutting markers --]
                [#-- Gender --]
                <td class="text-center"> 0 </td>
                [#-- Youth --]
                <td class="text-center"> 1 </td>
                [#-- CapDev --]
                <td class="text-center"> 2 </td>
                [#-- Climate Change --]
                <td class="text-center"> 3 </td>
                [#-- Other --]
                <td class="text-center"> . </td>
                [/#if]
              </tr>
            [/#list]
          [/#list]
        [/#list]
      </tbody>
    </table>
  </div>
[/#macro]


[#macro annualReport2018OutcomesMacro element name index isTemplate=false]
  [#local customName = "${name}[${index}]" /]
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
  [#local annualReportElement= action.getReportSynthesisFlagshipProgressMilestone(element.id) ]
  [#local customName = "${name}[${index}]" /]
  
  <div id="powbMilestone-${isTemplate?string('template', index)}" class="synthesisMilestone simpleBox" style="display:${isTemplate?string('none','block')}">
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
    
    [#-- Cross-Cutting --]
    <div class="form-group">
      <table class="milestones-crosscutting">
        <thead>
          <tr>
            <th></th>
            <th class="text-center">[@s.text name="${customLabel}.milestoneScoreMarker" /]</th>
            <th class="text-center col-md-7">[@s.text name="${customLabel}.milestoneScoreJustification" /]</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="row-title">[@s.text name="${customLabel}.milestoneGenderScore" /]</td>
            <td class="text-center">[@customForm.select name="${customName}.milestoneGenderScoreMarker" label="" listName="" keyFieldName=""  displayFieldName=""   required=true showTitle=false className="" editable=editable/]</td>
            <td class="text-center">[@customForm.input name="${customName}.milestoneGenderScoreJustification" className="input-sm" showTitle=false required=true editable=editable /]</td>
          </tr>
          <tr>
            <td class="row-title">[@s.text name="${customLabel}.milestoneYouthScore" /]</td>
            <td class="text-center">[@customForm.select name="${customName}.milestoneYouthScoreMarker" label="" listName="" keyFieldName=""  displayFieldName=""  showTitle=false required=true  className="" editable=editable/]</td>
            <td class="text-center">[@customForm.input name="${customName}.milestoneYouthScoreJustification" className="input-sm" showTitle=false required=true editable=editable /]</td>
          </tr>
          <tr>
            <td class="row-title">[@s.text name="${customLabel}.milestoneCapDevScore" /]</td>
            <td class="text-center">[@customForm.select name="${customName}.milestoneCapDevScoreMarker" label="" listName="" keyFieldName=""  displayFieldName="" showTitle=false required=true  className="" editable=editable/]</td>
            <td class="text-center">[@customForm.input name="${customName}.milestoneCapDevScoreJustification" className="input-sm" showTitle=false required=true editable=editable /]</td>
          </tr>
          <tr>
            <td class="row-title">[@s.text name="${customLabel}.milestoneClimateChangeScore" /]</td>
            <td class="text-center">[@customForm.select name="${customName}.milestoneClimateChangeScoreMarker" label=""  listName="" keyFieldName=""  displayFieldName="" showTitle=false required=true  className="" editable=editable/]</td>
            <td class="text-center">[@customForm.input name="${customName}.milestoneClimateChangeScoreJustification" className="input-sm" showTitle=false required=true editable=editable /]</td>
          </tr>
        </tbody>
      </table>
    </div>
    
    
    [#-- Milestone status --]
    <div class="form-group">
      <label>[@s.text name="${customLabel}.milestoneStatus" /]:[@customForm.req required=editable  /]</label><br />
      [#local milestoneStatus = (annualReportElement.milestonesStatus)!-1 /]
      [#local isComplete = (milestoneStatus == 1)!false /]

      [@customForm.radioFlat id="${customName}-status-1" name="${customName}.milestonesStatus" label="Complete"   value="1" checked=(milestoneStatus == 1)!false editable=editable cssClass="milestoneStatus" cssClassLabel="font-normal"/]
      [@customForm.radioFlat id="${customName}-status-2" name="${customName}.milestonesStatus" label="Extended"   value="2" checked=(milestoneStatus == 2)!false editable=editable cssClass="milestoneStatus" cssClassLabel="font-normal"/]
      [@customForm.radioFlat id="${customName}-status-3" name="${customName}.milestonesStatus" label="Cancelled"  value="3" checked=(milestoneStatus == 3)!false editable=editable cssClass="milestoneStatus" cssClassLabel="font-normal"/]
      [@customForm.radioFlat id="${customName}-status-4" name="${customName}.milestonesStatus" label="Changed"    value="4" checked=(milestoneStatus == 4)!false editable=editable cssClass="milestoneStatus" cssClassLabel="font-normal"/]
      
      [#if !editable && (milestoneStatus == -1)][@s.text name="form.values.fieldEmpty"/][/#if]
    </div>
    
    <div class="form-group milestonesEvidence" style="display:${isComplete?string('block', 'none')}">
      [#-- Evidence for completed milestones or explanation for extended or cancelled --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.evidence" i18nkey="${customLabel}.milestoneEvidence" help="${customLabel}.milestoneEvidence.help" helpIcon=false display=true required=false className="limitWords-50" editable=editable allowTextEditor=true /]
      </div>
      
      [#-- Extendend, cancelled or changed milestones - Main reason --]
      <div class="form-group">
        [@customForm.select name="${customName}.milestoneMainReason.id" label=""  i18nkey="${customLabel}.milestoneMainReason" listName="" keyFieldName=""  displayFieldName=""   required=true  className="milestoneMainReasonSelect" editable=editable/]
      </div>
      
      [#-- Extendend, cancelled or changed milestones - Other reason --]
      [#local showOther = (annualReportElement.milestoneMainReason.name == "other")!false /]
      <div class="form-group otherBlock" style="display:${showOther?string('block', 'none')}">
        [@customForm.input name="${customName}.milestoneOtherReason" i18nkey="${customLabel}.milestoneOtherReason" display=true required=false className="input-sm" editable=editable /]
      </div>
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