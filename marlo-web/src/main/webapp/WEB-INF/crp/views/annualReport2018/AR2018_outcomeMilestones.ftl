[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "trumbowyg", "components-font-awesome", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [ 
  "https://www.gstatic.com/charts/loader.js",  
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlCdn}/global/js/utils.js",
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js?20211215A",
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js?20211125a"
] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20210924a"] /]

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
[#assign actualPhaseAR2021 = action.isSelectedPhaseAR2021()!false]
[#assign isSubmitted = (action.isAr2018SubmittedCurrentPhase())!false]

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
          <span id="actualCrpID" style="display: none;">${(action.getCurrentCrp().id)!-1}</span>
          <span id="actualPhase" style="display: none;">${(action.isSelectedPhaseAR2021())?c}</span>
          <span id="isSubmitted" style="display: none;">${isSubmitted?c}</span>
          <span id="isOICR" style="display: none;">false</span>
          <span id="isPMU" style="display: none;">${PMU?c}</span>

          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="">

            [#-- Table 5: Status of Planned Outcomes and Milestones --]
            <div class="form-group">
              [#if PMU]
                <div class="borderBox">
                
                  <div class="form-group btn-group btn-group-sm pull-right" role="group" aria-label="...">
                    <button type="button" class="btn btn-default" data-toggle="modal" data-target="#modal-evidenceC"><span class="glyphicon glyphicon-fullscreen"></span> AR Evidence C</button>
                    <button type="button" class="btn btn-default" data-toggle="modal" data-target="#modal-table5"><span class="glyphicon glyphicon-fullscreen"></span> See Full Table 5</button>
                    [#-- Missing fields in FPs --]
                    [#if listOfFlagships?has_content]
                      </br>
                      <div class="missingFieldFp">
                        <div><span class="glyphicon glyphicon-exclamation-sign mffp-icon" title="Incomplete"></span> Missing fields in
                        [#list listOfFlagships as fp]
                         ${fp}[#if fp?index !=(listOfFlagships?size-1) ],[/#if]
                        [/#list]
                        </div>
                       </div>
                    [/#if]
                  </div>

                  [#-- Table 5: Evidence C --]
                  <div class="modal fade" id="modal-evidenceC" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                    <div class="modal-dialog modal-lg" role="document">
                      <div class="modal-content">
                        <div class="modal-header">
                          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                          <h4 class="modal-title" id="myModalLabel"> Evidence C: Outcomes and milestones </h4>
                        </div>
                        <div class="modal-body">
                          [#-- Full table --]
                          <div class="dataTableExport">
                            [@tableEvidenceC  /]
                          </div>
                        </div>
                        <div class="modal-footer">
                          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  [#-- Full table 5 --]
                  <div class="modal fade" id="modal-table5" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                    <div class="modal-dialog modal-lg" role="document">
                      <div class="modal-content">
                        <div class="modal-header">
                          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                          <h4 class="modal-title" id="myModalLabel">[@s.text name="${customLabel}.title" /]</h4>
                        </div>
                        <div class="modal-body">
                          [@tableOutcomesMilestones allowPopups=false  /]
                        </div>
                        <div class="modal-footer">
                          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                      </div>
                    </div>
                  </div>

                  [#-- Table 5--]
                  [@tableOutcomesMilestones  /]
                </div>
              [#else]
                [#list outcomes as outcome]
                  [@annualReport2018OutcomesMacro element=outcome name="${customName}.outcomeList" index=outcome_index /]
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
          [#if !allowPopups]<th rowspan="2"> Sub IDOs </th>[/#if]
          [#if !allowPopups]<th rowspan="2"> Outcome Progress </th>[/#if]
          <th rowspan="2"> Milestone </th>
          <th rowspan="2"> Status</th>
          [#if allowPopups]
            <th rowspan="2">Missing fields status</th>
              <th rowspan="2" class="col-md-1">Include in QA 
                <br>
                <button type="button" class="selectAllCheckMilestones" id="selectAllMilestones" style="color: #1da5ce; font-style: italic; font-weight: 500; background-color: #F9F9F9; border-bottom: none; outline: none">Select All</button>
              </th>
            [#if actualPhaseAR2021 && submission]
              <th rowspan="2"> QA Status</th>
            [/#if]
          [/#if]
          [#if !allowPopups]
          <th rowspan="2">Milestone Evidence</th>
          <th rowspan="2">Link to Evidences</th>
          <th colspan="${cgiarCrossCuttingMarkers?size}" class="text-center">Cross-Cutting Markers</th>
          [/#if]
        </tr>
        [#if !allowPopups]
        <tr>
          [#-- Cross Cutting markers --]
          [#list cgiarCrossCuttingMarkers as marker]
            <th> <small>${marker.name}</small></th>
          [/#list]
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
              [#assign milestoneProgress = (action.getReportSynthesisFlagshipProgressMilestone(milestone.id))!{} ]
              <tr class="fp-index-${fp_index} outcome-index-${outcome_index} milestone-index-${milestone_index}">
                [#-- Flagship --]
                [#if isFlagshipRow]<th rowspan="${milestoneSize}" class="milestoneSize-${milestoneSize}" style="background:${(fp.color)!'#fff'}"><span class="programTag" style="border-color:${(fp.color)!'#fff'}">${fp.acronym}</span></th>[/#if]
                [#-- Outcomes --]
                [#if isOutcomeRow]
                  [#local reportedOutcome= (action.getOutcomeToPmu( fp.id , outcome.id))! ]
                  <td rowspan="${outcomesSize}" class="milestonesSize-${outcomesSize}"> 
                    [#-- Outcome Statement --]
                    ${outcome.composedName}
                    [#-- Sub-IDOs --]
                    [#--
                    [#if !allowPopups]
                    <br />
                    <small>
                      <ul>[#list (outcome.subIdos)![] as subIdo]<li> [#if (subIdo.srfSubIdo.srfIdo.isCrossCutting)!false] <strong title="Cross-Cutting IDO">CC</strong> [/#if]${(subIdo.srfSubIdo.description)!}</li>[/#list]</ul>
                    </small>
                    [/#if]
                    --]
                  </td>
                [/#if]
                [#-- Sub-IDOs --]
                [#if isOutcomeRow && !allowPopups]
                  <td rowspan="${outcomesSize}" class="milestonesSize-${outcomesSize}">
                    <small>
                      <ul>
                        [#list (outcome.subIdos)![] as subIdo]
                          <li> 
                            [#if (subIdo.srfSubIdo.srfIdo.isCrossCutting)!false] 
                              <strong title="Cross-Cutting IDO">CC</strong> 
                            [/#if]
                            ${(subIdo.srfSubIdo.description)!}
                          </li><br />
                        [/#list]
                      </ul>
                    </small>
                  </td>
                [/#if]
                [#-- Outcomes - Narrative --]
                [#if isOutcomeRow && !allowPopups]
                  <td rowspan="${outcomesSize}" class="milestonesSize-${outcomesSize}">
                    [@utils.tableText value=(reportedOutcome.summary)!"" emptyText="global.prefilledByFlagship"/]
                  </td>                
                [/#if]
                [#-- Milestone --]
                [#if milestone.isActive()]
                  <td> ${milestone.composedName} [#if allowPopups] <div class="pull-right">[@milestoneContributions element=milestone tiny=true /] [/#if]</div></td>
                  [#local milestoneReportSynthesis=(action.getReportSynthesisMilestone(fp, outcome.id, milestone.id))!]
                  [#-- Milestone Status --]
                  <td class="text-center"> 
                    [@utils.tableText value=(milestoneReportSynthesis.milestonesStatus.name)!"" emptyText="global.prefilledByFlagship" /]
                  </td>
                  [#if allowPopups]
                    <td class="text-center">
                    [#assign isMilestoneComplete  = action.isMilestoneComplete(milestoneReportSynthesis.id)!false /]
                    [#if isMilestoneComplete ]
                     <span class="glyphicon glyphicon-ok-sign mf-icon check" title="Complete"></span> 
                    [#else]
                      <span class="glyphicon glyphicon-exclamation-sign mf-icon" title="Incomplete"></span> 
                    [/#if] 
                    
                      [#local isChecked = ((!reportSynthesis.reportSynthesisFlagshipProgress.milestoneIds?seq_contains(milestone.id))!true) /]
                      <td class="text-center">
                        <div>
                          [@customForm.checkmark id="milestone-${(milestoneReportSynthesis.id)!}" name="reportSynthesis.reportSynthesisFlagshipProgress.milestonesValue" value="${(milestone.id)!''}" checked=isChecked editable=editable centered=true/] 
                        </div>
                        <span id="milestoneID-${(milestoneReportSynthesis.id)!}" style="display: none">${(milestone.id)!''}</span>
                        <div id="isCheckedAR-${(milestoneReportSynthesis.id)!}" style="display: none">${isChecked?string('1','0')}</div>
                      </td>
                    
                    [#if actualPhaseAR2021 && submission]
                      <td id="QAStatusIcon-${(milestoneReportSynthesis.id)!}" class="text-center">
                        [#if isChecked]
                          <i style="font-weight: normal;opacity:0.8;">[@s.text name="annualReport2018.policies.table2.pendingForReview"/]</i>
                        [#else]
                          <i style="font-weight: normal;opacity:0.8;">Check Include in QA to see status</i>
                        [/#if]
                      </td>
                    [/#if]
                  [/#if]
                  [#if !allowPopups]
                    [#-- Milestone Evidence --]
                    <td class="urlify">[@utils.tableText value=(reportedMilestone.evidence)!"" emptyText="global.prefilledByFlagship" /] </td>
                    [#-- Link to Evidences --]
                    <td class="urlify">[@utils.tableText value=(reportedMilestone.evidenceLink)!"" emptyText="global.prefilledByFlagship" /] </td>
                    [#-- Cross Cutting markers --]
                    [#list cgiarCrossCuttingMarkers as marker]
                      [#local reportedCrossCuting =  (action.getCrossCuttingMarker( ((reportedMilestone.id)!-1), marker.id ))! ]
                      <td class="text-center">
                        <p class="dacMarker level-${(reportedCrossCuting.focus.id)!""}" title="${(reportedCrossCuting.focus.powbName)!""}">${(reportedCrossCuting.focus.acronym)!""}</p>
                      </td>
                    [/#list]
                  [/#if]
                [/#if]
              </tr>
            [/#list]
          [/#list]
        [/#list]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro tableEvidenceC ]
  <div class="table-responsive">[#-- <div class="table-responsive"> --]
    <table id="tableA" class="table table-bordered">
      <thead>
        <tr>
          <th>[@s.text name="expectedProgress.tableA.fp" /]</th>
          <th> Outcome </th>
          <th> Outcome Progress </th>
          <th> Milestone </th>
          <th> Status</th>
          <th> Status predominant reason</th>
          <th> Milestone Evidence</th>
          [#-- Cross Cutting markers --]
          [#list cgiarCrossCuttingMarkers as marker]
            <th> <small>${marker.name}</small></th>
            <th> <small>${marker.name} Justification</small></th>
          [/#list]
        </tr> 
      </thead>
      <tbody>
        [#list (flagships)![] as fp]
          [#list fp.outcomes as outcome]
            [#list outcome.milestones as milestone]
              [#assign milestoneProgress = (action.getReportSynthesisFlagshipProgressMilestone(milestone.id))!{} ]
              <tr class="fp-index-${fp_index} outcome-index-${outcome_index} milestone-index-${milestone_index}">
                [#-- Flagship --]
                <th>${fp.acronym}</th>
                [#local reportedOutcome= (action.getOutcomeToPmu( fp.id , outcome.id))! ]
                [#-- Outcome Statement --]
                <td>${outcome.composedName}</td>
                [#-- Outcomes - Narrative --]
                <td>[@utils.tableText value=(reportedOutcome.summary)!"" emptyText="global.prefilledByFlagship"/]</td>
                [#-- Milestone --]
                <td> [@utils.tableText value=(milestone.composedName)!"" emptyText="" /] </td>
                [#-- Milestone Status --]
                <td class="text-center"> 
                  [#local reportedMilestone= (action.getMilestone((reportedOutcome.id)!-1 , milestone.id))! ]
                  [@utils.tableText value=(reportedMilestone.crpMilestone.milestonesStatus.name)!"" emptyText="global.prefilledByFlagship" /]
                </td>
                [#-- Status predominant reason --]
                <td>
                  [@utils.tableText value=(reportedMilestone.reason.name)!"" emptyText="" nobr=true /] 
                  [#if (reportedMilestone.otherReason?has_content)!false && (reportedMilestone.reason.id == 7)!false] (${reportedMilestone.otherReason}) [/#if]                   
                </td>
                [#-- Milestone Evidence --]
                <td class="urlify">[@utils.tableText value=(reportedMilestone.evidence)!"" emptyText="global.prefilledByFlagship" /] </td>
                [#-- Cross Cutting markers --]
                [#list cgiarCrossCuttingMarkers as marker]
                  [#local reportedCrossCuting =  (action.getCrossCuttingMarker( ((reportedMilestone.id)!-1), marker.id ))! ]
                  <td class="text-center"> [@utils.tableText value=(reportedCrossCuting.focus.powbName)!"" emptyText="" nobr=true /]</td>
                  <td class="text-center"> [@utils.tableText value=(reportedCrossCuting.just)!"" emptyText="" /] </td>
                [/#list]
              </tr>
            [/#list]
          [/#list]
        [/#list]
      </tbody>
    </table>
  </div>
[/#macro]


[#macro annualReport2018OutcomesMacro element name index isTemplate=false]
  [#local annualReportElement= (action.getOutcome(reportSynthesis.reportSynthesisFlagshipProgress.id,element.id))! ]
  [#local customName = "${name}[${index}]" /]
  <div id="powbOutcome-${isTemplate?string('template', index)}" class="powbOutcome borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Index --]
    [#--  <div class="leftHead sm"><span class="index">${index+1}</span></div> --]
    <div class="leftHead sm"><span class="index">${(element.composeID)!}</span></div>
    [#-- Title --]
    <div class="form-group grayBox"><strong>${(liaisonInstitution.crpProgram.acronym)!liaisonInstitution.acronym} Outcome: </strong> ${(element.description)!}</div>
    [#-- Narrative on progress --]
    <div class="form-group">
      [#-- Word Document Tag --]
      [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]      
      <input type="hidden" name="${customName}.id" value="${(annualReportElement.id)!}"/>
      <input type="hidden" name="${customName}.crpProgramOutcome.id" value="${(element.id)!}"/>
      [@customForm.textArea name="${customName}.summary" i18nkey="${customLabel}.outcome.progressNarrative" help="${customLabel}.outcome.progressNarrative.help" className="limitWords-100" helpIcon=false required=true editable=editable allowTextEditor=true /]
    </div>
    [#-- Sub-IDOs List --]
    [#if element.subIdos?has_content]
      [#local hasPrimary=  element.subIdos?size > 1 ]
      <h4 class="simpleTitle">[@s.text name="${customLabel}.subIdos.title" /]</h4>
      <div class="simpleBox">
        <div class="form-group">
          [#list element.subIdos as subIdo]         
            [@annualReport2018SubIdoMacro element=subIdo name="${customName}.subIdos" /]                    
          [/#list]
         </div> 
       </div> 
    [/#if]    
 
    [#-- Milestones List --]
    <h4 class="simpleTitle">[@s.text name="${customLabel}.milestones.title" /]</h4>
    <div class="form-group">
       [#list element.milestones as milestone]
        [@annualReport2018MilestoneMacro element=milestone name="${customName}.milestones" index=milestone_index reportedOutcomeID=(annualReportElement.id)!-1 /]
      [/#list]
    </div> 
  </div>
[/#macro]

[#macro annualReport2018MilestoneMacro element name index reportedOutcomeID isTemplate=false]
  [#local annualReportElement= (action.getMilestone(reportedOutcomeID,element.id))! ]
  [#local customName = "${name}[${index}]" /]
  [#local milestoneNextPOWB = action.getNextPOWBMilestone(element.composeID) /]
  [#--local milestoneAnnualReportStatus = (action.getCurrentMilestoneStatus(element.id))!--]
  <div id="powbMilestone-${isTemplate?string('template', index)}" class="synthesisMilestone simpleBox" style="display:${isTemplate?string('none','block')}"> 
    [#-- Index  Outcome ${reportedOutcomeID}, Milestone ${element.id} --]
    [#-- <div class="leftHead gray sm"><span class="index">${index+1}</span></div> --]
    <div class="leftHead gray sm"><span class="index">${(element.composeID)!}</span></div>
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(annualReportElement.id)!}" >
    <input type="hidden" name="${customName}.crpMilestone.id" value="${(element.id)!}" >
    
    [#-- Title --]
    <br>
    [#assign isQAIncluded = ((!annualReportElement.isQAIncluded)!true)]
    [#if actualPhaseAR2021 && isSubmitted && isQAIncluded]
      <span id="mileID-${(annualReportElement.id)!}" style="display: none;">${(annualReportElement.id)!}</span>
      <div id="containerTitleElementsProject-${(annualReportElement.id)!}" class="containerTitleElementsProject">
        <div class="containerTitleMessage">
          <div id="qualityAssessedIcon-${(annualReportElement.id)!}" class="pendingForReview-mode text-center animated flipInX" style="height: auto;">
            <p>
              [@s.text name="annualReport2018.policies.table2.pendingForReview"][/@s.text]
            </p>
          </div> 
        </div>
      </div>
    [/#if]
    <div class="form-group grayBox">
      <div class="pull-right">[@milestoneContributions element=element /]</div>
      <p class="text-justify"><strong>[#if (element.milestonesStatus.id == 4)!false ]Milestone of ${element.year} extended to ${actualPhase.year}[#else]Milestone for ${actualPhase.year}[/#if]</strong> - ${(element.title)!} </p>
    </div>
    
    
    [#-- Cross-Cutting --]
    <div class="form-group">
      [#-- Word Document Tag --]
      [#assign guideSheetURL = "https://docs.google.com/document/d/1DHNB6OefSqqlw1HbmJq0STfbNkeN0GSL/edit?rtpof=true&sd=true" /]
      <small class="pull-left"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> Milestones  -  Guideline </a> </small>
      [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
      <br>
      
      [#-- 
      <table class="milestones-crosscutting">
        <thead>
          <tr>
            <th></th>
            <th class="text-center">[@s.text name="${customLabel}.milestoneScoreMarker" /][@customForm.req required=editable  /]</th>
            <th class="text-center col-md-7">[@s.text name="${customLabel}.milestoneScoreJustification" /][@customForm.req required=editable  /]</th>
          </tr>
        </thead>
        <tbody>
          [#list cgiarCrossCuttingMarkers as marker]
            [#local ccName=  "${customName}.markers[${marker_index}]"]
            [#local annualReportCrossCuting =  (action.getCrossCuttingMarker( ((annualReportElement.id)!-1), marker.id ))! ]
            <tr>
              <td class="row-title"> 
                <span class="name ">${marker.name}</span>
                <input type="hidden" name="${ccName}.id" value="${(annualReportCrossCuting.id)!}" />
                <input type="hidden" name="${ccName}.marker.id" value="${marker.id}"/>
              </td>
              <td class="text-center">
                [@customForm.select name="${ccName}.focus.id" value="${(annualReportCrossCuting.focus.id)!-1}" label="" listName="focusLevels" keyFieldName="id"  displayFieldName="powbName" required=true showTitle=false className="" editable=editable/]</td>
              <td class="text-center">
                [@customForm.input name="${ccName}.just" value="${(annualReportCrossCuting.just)!}" showTitle=false required=true editable=editable /]
              </td>
            </tr>
          [/#list]
          <tr><div>Next gender: ${milestoneNextPOWB.genderFocusLevel.powbName}</div></tr>
          <tr><div>Next youth: ${milestoneNextPOWB.youthFocusLevel.powbName}</div></tr>
          <tr><div>Next capdev: ${milestoneNextPOWB.capdevFocusLevel.powbName}</div></tr>
          <tr><div>Next climate: ${milestoneNextPOWB.climateFocusLevel.powbName}</div></tr>
        </tbody>
      </table>


      --]
  
        <div class="row">
          <div class="col-sm-3 colTitleCenter boldCell"></div>
          <div class="col-sm-3 colTitleCenter boldCell">[@s.text name="${customLabel}.milestoneScoreMarker" /][@customForm.req required=editable  /]</div>
          <div class="col-sm-6 colTitleCenter boldCell">[@s.text name="${customLabel}.milestoneScoreJustification" /]</div>
        </div>
          
          [#assign markersPOWB=["${(milestoneNextPOWB.genderFocusLevel.powbName)!'Not available'}","${(milestoneNextPOWB.youthFocusLevel.powbName)!'Not available'}","${(milestoneNextPOWB.capdevFocusLevel.powbName)!'Not available'}","${(milestoneNextPOWB.climateFocusLevel.powbName)!'Not available'}"] /]
          
          [#list cgiarCrossCuttingMarkers as marker]
            [#local ccName= "${customName}.markers[${marker_index}]"]
            [#local annualReportCrossCuting = (action.getCrossCuttingMarker( ((annualReportElement.id)!-1), marker.id ))! ]
            <div class="row rectGray">
              <div class="col-sm-3 ">
                <span class="name boldCell">${marker.name}</span>
                <input type="hidden" name="${ccName}.id" value="${(annualReportCrossCuting.id)!}" />
                <input type="hidden" name="${ccName}.marker.id" value="${marker.id}" />
              </div>
              <div class="col-sm-3 colTitleCenter">
                [@customForm.select name="${ccName}.focus.id" value="${(annualReportCrossCuting.focus.id)!-1}" label=""
                listName="focusLevels" keyFieldName="id" displayFieldName="powbName" required=true showTitle=false className="marker"
                editable=editable/]</td>
              </div>
              <div class="col-sm-6 colTitleCenter colAlignItems conditionalRequire">
                <div style="width: 100%;">
                  [@customForm.input name="${ccName}.just" value="${(annualReportCrossCuting.just)!}" showTitle=false required=true className="limitWords-100"
                  editable=editable /]
                </div>
                [@customForm.req required=editable  /]
              </div>
              <div class="col-sm-12 ">
                <p class="helpTextPOWB">In POWB ${(actualPhase.year+1)!} you selected for ${marker.name}: <span style="color: #1ca6ce">${(markersPOWB[marker_index])!}</span> </p>
              </div>
            </div>
          [/#list]


    </div>
    
    
    [#-- Milestone status --]
    <div class="form-group">
      [#-- Word Document Tag --]
      [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
      <label>[@s.text name="${customLabel}.milestoneStatus" /]:[@customForm.req required=editable  /]</label><br />
      [#local milestoneStatus = (annualReportElement.milestonesStatus.id)!-1 /]
      [#if milestoneStatus == 2]
        [#local milestoneStatus = 3 /]
      [/#if]
      [#local statusesList = [
        { "id": 3, "name": "Complete" },
        { "id": 5, "name": "Cancelled" },
        { "id": 6, "name": "Changed" },
        { "id": 7, "name": "Partially complete" }
      ] /]
      [#list statusesList as s]
        [@customForm.radioFlat id="${customName}-status-${s.id}" name="${customName}.milestonesStatus.id" label="${s.name}"   value="${s.id}" checked=(milestoneStatus == s.id)!false editable=editable cssClass="milestoneStatus" cssClassLabel="font-normal"/]
      [/#list]
      [#if !editable && (milestoneStatus == -1)][@s.text name="form.values.fieldEmpty"/][/#if]
      <div class="rectGray" style="padding-left: 10px;">
        <p class="helpTextPOWB">In POWB ${(actualPhase.year+1)!} you selected the status: <span style="color: #1ca6ce">${(milestoneNextPOWB.milestonesStatus.name)!'Not availbale'}</span> </p>
      </div>
    </div>
    [#-- New year if extended --]
    <div class="row form-group extendedYearBlock" style="display:${(milestoneStatus == 4)?string('block', 'none')}">
      <div class="col-md-3">
        [#local milestoneExtendedYear = (annualReportElement.extendedYear)!"-NULL"]
        [@customForm.select name="${customName}.extendedYear" label=""  i18nkey="${customLabel}.year" listName="allPhaseYearsGreater" required=true value=milestoneExtendedYear  className="" editable=editable/]
      </div>
    </div>
    
    [#-- Evidence for completed milestones or explanation for extended or cancelled --]
    <div class="form-group milestoneEvidence">
      [@customForm.textArea name="${customName}.evidence" value="${(annualReportElement.evidence)!}" i18nkey="${customLabel}.milestoneEvidence.help" helpIcon=false display=true required=true className="limitWords-200" editable=editable allowTextEditor=true /]
    </div>
    
    [#-- Links to evidence --]
    <div class="form-group linksToEvidence">
      <span id="warningEmptyLinksTag" class="errorTag glyphicon glyphicon-info-sign" style="display: none; position: relative; left: 730px;" title="" aria-describedby="ui-id-5"></span>
      [@customForm.textArea name="${customName}.evidenceLink" value="${(annualReportElement.evidenceLink)!}" i18nkey="${customLabel}.milestoneEvidenceLink" help="${customLabel}.milestoneEvidenceLink.help" helpIcon=false display=true required=false editable=editable  /]
      [#--  <span>${annualReportElement.id}</span>
      <span>${customName}</span>   --]
      <div class="linksBlock ">
        <div class="linksList">
          [#list (annualReportElement.links)![{}] as link]
            [@customForm.multiInput name="${customName}.links" element=link index=link_index class="links" placeholder="global.webSiteLink.placeholder" /]
          [/#list]
        </div>
        [#if editable]
        <div class="addButtonLink button-green pull-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add Link </div>
        <div class="clearfix"></div>
        [/#if]
        </div>
          [#-- Element item Template --]
        <div style="display:none">
          [@customForm.multiInput name="${customName}.links" element={} index=-1 template=true class="links" placeholder="global.webSiteLink.placeholder" /]
        </div>
    </div>
      
    <div class="form-group milestonesEvidence" style="width: 100%; display:${((milestoneStatus == 4) || (milestoneStatus == 5) || (milestoneStatus == 6) || (milestoneStatus == 7))?string('block', 'none')}">
      [#-- Extendend, cancelled or changed milestones - Main reason --]
      <div class="form-group">
        [@customForm.select name="${customName}.reason.id"  value="${(annualReportElement.reason.id)!}"label=""  i18nkey="${customLabel}.milestoneMainReason" listName="reasons" keyFieldName="id"  displayFieldName="name"   required=true  className="milestoneMainReasonSelect" editable=editable/]
      </div>
      
      [#-- Extendend, cancelled or changed milestones - Other reason --]
      [#local showOther = (annualReportElement.reason.id == 7)!false /]
      <div class="form-group otherBlock" style="display:${showOther?string('block', 'none')}">
        [@customForm.input name="${customName}.otherReason" value="${(annualReportElement.otherReason)!}" i18nkey="${customLabel}.milestoneOtherReason" display=true required=true className="input-sm" editable=editable /]
      </div>
    </div>
    
    
  </div>
[/#macro]


[#macro annualReport2018SubIdoMacro element name ]    
    <div class="form-group grayBox">
      [#if (element.primary)!false]
       <div>
        <span class="pull-left label label-info primaryTag">Primary</span>
       </div>
      [/#if]
      <div>
        <p class="text-justify">${(element.srfSubIdo.description)!} </p>
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
          <h4 class="simpleTitle">[@s.text name="expectedProgress.milestonesContributions.projectsTitle" /]</h4>
            <table class="table table-bordered">
              <thead>
                <tr>
                  <th class="col-md-1"> Project ID </th>
                  <th class=""> Project Title </th>
                  [#if hasTarget]<th class="col-md-1"> ${(element.srfTargetUnit.name!)} Achieved</th>[/#if]
                  <th> [@s.text name="${customLabel}.contributionMilestone.narrativeAchieved" /]  </th>
                  
                </tr>
              </thead>
              <tbody>
                [#list projectContributions as contribution]
                  [#local pURL][@s.url namespace="/projects" action="${(crpSession)!}/contributionsCrpList"][@s.param name='projectID']${contribution.projectOutcome.project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                  [#local poURL][@s.url namespace="/projects" action="${(crpSession)!}/contributionCrp"][@s.param name='projectOutcomeID']${contribution.projectOutcome.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                  <tr>
                    <td> <a href="${pURL}" target="_blank"> P${contribution.projectOutcome.project.id} </a> </td>
                    <td> ${contribution.projectOutcome.project.projectInfo.title} </td>
                    [#if hasTarget]
                    <td class="text-center">[#if (contribution.expectedUnit.name??)!false]${(contribution.achievedValue)!}[#else]<i>N/A</i>[/#if]</td>
                    [/#if]
                    <td>
                      [#if ((contribution.narrativeAchieved)?has_content)!false]
                        ${(contribution.narrativeAchieved?replace('\n', '<br>'))!}
                      [#else]
                        <i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                      [/#if]
                      
                      <a href="${poURL}" target="_blank" class="pull-right"><span class="glyphicon glyphicon-new-window"></span></a>
                    </td>
                     
                  </tr>
                [/#list]
              </tbody>
            </table>
            [#-- Policies --][#--
            [#if projectPolicies?has_content] 
              <h4 class="simpleTitle">[@s.text name="expectedProgress.milestonesContributions.policiesTitle" /]</h4>
              <div class="">
                <table class="table table-bordered">
                  <thead>
                    <tr>
                      <th class="col-md-1"> ID </th>
                      <th class=""> Title </th>
                    </tr>
                  </thead>
                  <tbody> 
                    [#list projectPolicies as policy]
                      [#local ppURL][@s.url namespace="/projects" action="${(crpSession)!}/policy"][@s.param name='projectID']${policy.projectOutcome.project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                      [#local ppoURL][@s.url namespace="/projects" action="${(crpSession)!}/policy"][@s.param name='policyID']${policy.projectOutcome.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                      <tr>
                        <td> <a href="${ppURL}" target="_blank"> P${policy.projectOutcome.project.id} </a> </td>
                        <td> 
                          ${policy.projectOutcome.project.projectInfo.title} 
                          <a href="${ppoURL}" target="_blank" class="pull-right"><span class="glyphicon glyphicon-new-window"></span></a>
                        </td>                       
                      </tr>
                    [/#list]
                  </tbody>
                </table>
              </div>
            [/#if]
            
            [#-- outcome impact case reports --][#--
            [#if outcomeCases?has_content] 
              <h4 class="simpleTitle">[@s.text name="expectedProgress.milestonesContributions.oicrTitle" /]</h4>
              <div class="">
                <table class="table table-bordered">
                  <thead>
                    <tr>
                      <th class="col-md-1"> ID </th>
                      <th class=""> Title </th>
                    </tr>
                  </thead>
                  <tbody>
                    [#list outcomeCases as outcomeCase]
                      [#local ocURL][@s.url namespace="/projects" action="${(crpSession)!}/studies"][@s.param name='projectID']${outcomeCase.projectOutcome.project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                      [#local ocoURL][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${outcomeCase.projectOutcome.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                      <tr>
                        <td> <a href="${ocURL}" target="_blank"> P${outcomeCase.projectOutcome.project.id} </a> </td>
                        <td> 
                          ${outcomeCase.projectOutcome.project.projectInfo.title}  
                          <a href="${ocoURL}" target="_blank" class="pull-right"><span class="glyphicon glyphicon-new-window"></span></a>
                        </td>
                      </tr>
                    [/#list]
                  </tbody>
                </table>
              </div>
            [/#if]
            
            [#-- innovation --][#--
            [#if projectInnovations?has_content] 
              <h4 class="simpleTitle">[@s.text name="expectedProgress.milestonesContributions.innovations" /]</h4>
              <div class="">
                <table class="table table-bordered">
                  <thead>
                    <tr>
                      <th class="col-md-1"> ID </th>
                      <th class=""> Title </th>
                    </tr>
                  </thead>
                  <tbody>
                    [#list projectInnovations as innovation]
                      [#local piURL][@s.url namespace="/projects" action="${(crpSession)!}/innovationsList"][@s.param name='projectID']${innovation.projectOutcome.project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                      [#local pioURL][@s.url namespace="/projects" action="${(crpSession)!}/innovation"][@s.param name='innovationID']${innovation.projectOutcome.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                      <tr>
                        <td> <a href="${piURL}" target="_blank"> P${innovation.projectOutcome.project.id} </a> </td>
                        <td> 
                          ${innovation.projectOutcome.project.projectInfo.title}
                          <a href="${pioURL}" target="_blank" class="pull-right"><span class="glyphicon glyphicon-new-window"></span></a>
                        </td>
                      </tr>
                    [/#list]
                  </tbody>
                </table>
              </div>
            [/#if]--]
          </div>
        </div>
        <div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button></div>
      </div>
    </div>
  </div>
  [/#if]
[/#macro]

[#function getMarker element name]
  [#list (element.crossCuttingMarkers)![] as ccm]
    [#if ccm.cgiarCrossCuttingMarker.name == name]
      [#return (ccm.repIndGenderYouthFocusLevel)!{} ]
    [/#if]
  [/#list]
  [#return {} ]
[/#function]