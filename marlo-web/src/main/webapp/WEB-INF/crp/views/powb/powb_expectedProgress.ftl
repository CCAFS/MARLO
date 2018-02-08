[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${powbSynthesisID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "expectedProgress" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"expectedProgress", "nameSpace":"powb", "action":"${crpSession}/expectedProgress"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="expectedProgress.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/powb/submenu-powb.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/powb/menu-powb.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/powb/messages-powb.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="expectedProgress.title" /]</h3>
        <div class="borderBox">
        
          [#-- Provide a short narrative of expected highlights of the CRP for 2018 --] 
          [#if PMU]
          <div class="form-group">
            [@customForm.textArea name="powbSynthesis.expectedCrpProgresses[0].expectedHighlights" i18nkey="liaisonInstitution.powb.expectedHighlights" help="liaisonInstitution.powb.expectedHighlights.help" paramText="${(actualPhase.year)!}" required=true className="limitWords-100" editable=editable /]
            [#assign powebElement=action.getPMUPowbExpectedCrpProgress()]
            <input type="hidden" name="powbSynthesis.expectedCrpProgresses[0].id" value="${(powebElement.id)!}" />
          </div>
          [/#if]
          
          [#-- Table A: Planned Milestones 2018 --]
          [#if PMU]
          <div class="form-group">
            <hr />
          
            [#-- Modal Large --]
            <button type="button" class="pull-right btn btn-default btn-xs" data-toggle="modal" data-target="#tableA-bigger"> 
              <span class="glyphicon glyphicon-fullscreen"></span>
            </button>
            <div id="tableA-bigger" class="modal fade bs-example-modal-lg " tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
              <div class="modal-dialog modal-lg bigger" role="document">
                <div class="modal-content">
                  <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                  </div>
                  [@tableAMacro allowPopups=false/]
                </div>
              </div>
            </div>
            
            <h4 class="subTitle headTitle">[@s.text name="expectedProgress.tableA.title" /]</h4>
            [@tableAMacro  /]
          </div>
          [/#if]
          
          
          [#if flagship]
            [#-- Flagship - Outcomes 2022 --]
            [#-- <h4 class="sectionSubTitle">[@s.text name="expectedProgress.flagshipOutcomes"][@s.param]${(liaisonInstitution.crpProgram.acronym)!liaisonInstitution.acronym}[/@s.param][/@s.text]</h4> --]
            [#list outcomes as outcome]
              [@powbOutcomeMacro element=outcome name="outcomes" index=outcome_index /]
            [/#list]
          [/#if]
        
        </div>
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]


[#---------------------------------------------- MACROS ----------------------------------------------]

[#macro tableAMacro allowPopups=true id="" ]
  <div class="">[#-- <div class="table-responsive"> --]
    <table id="tableA" class="table table-bordered">
      <thead>
        <tr>
          <th rowspan="2" >[@s.text name="expectedProgress.tableA.fp" /]</th>
          <th rowspan="2" >[@s.text name="expectedProgress.tableA.subIDO" /]</th>
          <th rowspan="2" >[@s.text name="expectedProgress.tableA.outcomes" /]</th>
          <th rowspan="2" >[@s.text name="expectedProgress.tableA.milestone" /]</th>
          <th rowspan="1" colspan="2" class="text-center"> Budget </th> 
          <th rowspan="2" >[@s.text name="expectedProgress.tableA.assessment" /]</th>
          <th rowspan="2" >[@s.text name="expectedProgress.tableA.meansVerification" /]</th>
        </tr>
        <tr>
          <th class="text-center">[@s.text name="expectedProgress.tableA.w1w2" /]</th>
          <th class="text-center">[@s.text name="expectedProgress.tableA.w3bilateral" /]</th>
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
                [#if isOutcomeRow]<td rowspan="${outcomesSize}"> 
                  <ul>[#list outcome.subIdos as subIdo]<li> [#if subIdo.srfSubIdo.srfIdo.isCrossCutting] <strong title="Cross-Cutting IDO">CC</strong> [/#if]${subIdo.srfSubIdo.description}</li>[/#list]</ul>
                </td>
                [/#if]
                [#-- Outcomes --]
                [#if isOutcomeRow]<td rowspan="${outcomesSize}" class="milestonesSize-${outcomesSize}"> ${outcome.composedName}</td>[/#if]
                [#-- Milestone --]
                <td> ${milestone.composedName} [#if allowPopups] <div class="pull-right">[@milestoneContributions element=milestone tiny=true /] [/#if]</div></td>
                [#-- W1W2 --]
                [#if isFlagshipRow]<td rowspan="${milestoneSize}"> US$ <span >${fp.w1?number?string(",##0.00")}</span> </td>[/#if]
                [#-- W3/Bilateral --]
                [#if isFlagshipRow]<td rowspan="${milestoneSize}"> US$ <span >${fp.w3?number?string(",##0.00")}</span> </td>[/#if]
                [#-- Assessment --]
                <td>[#if (milestoneProgress.assesmentName?has_content)!false]${milestoneProgress.assesmentName}[#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
                [#-- Means Verification --]
                <td>[#if (milestoneProgress.means?has_content)!false]${milestoneProgress.means}[#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
              </tr>
            [/#list]
          [/#list]
        [/#list]
      </tbody>
    </table>
  </div>
  
  [#-- 
  <h5>TEST</h5>
  <ul>
    [#list flagships as fp]
      [#assign milestoneSize = fp.milestones?size]
      <li> ${fp.acronym} - <strong>Milestones Size: ${milestoneSize}</strong>
        <ul>
          [#list fp.outcomes as outcome]
            [#assign outcomesSize = outcome.milestones?size]
            <li> ${outcome.composedName} - <strong>Milestones Size: ${outcomesSize}</strong>
              <ul>
                [#list outcome.milestones as milestone]
                  <li>${milestone.composedName}</li>
                [/#list]
              </ul>
            </li>
          [/#list]
        </ul>
      </li>
    [/#list]
  </ul>
   --]
[/#macro]


[#macro powbOutcomeMacro element name index isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  <div id="powbOutcome-${isTemplate?string('template', index)}" class="powbOutcome simpleBox" style="display:${isTemplate?string('none','block')}">
    [#-- Index --]
    <div class="leftHead sm"><span class="index">${index+1}</span></div>
    [#-- Hidden inputs --]
    [#-- Title --]
    <div class="form-group grayBox"><strong>${(liaisonInstitution.crpProgram.acronym)!liaisonInstitution.acronym} Outcome: </strong> ${(element.description)!}</div>
    
    [#-- Milestones List --]
    <div class="form-group">
      [#list element.milestones as milestone]
        [@powbMilestoneMacro element=milestone name="${customName}.milestones" index=milestone_index /]
      [/#list]
    </div>
    
  </div>
[/#macro]

[#macro powbMilestoneMacro element name index isTemplate=false]
  [#local indexPowb=action.getIndex(element.id)]
  [#local powebElement=action.getPowbExpectedCrpProgress(element.id)]
  
  [#local customName = "powbSynthesis.expectedCrpProgresses[${indexPowb}]" /]
  <div id="powbMilestone-${isTemplate?string('template', index)}" class="powbMilestone simpleBox" style="display:${isTemplate?string('none','block')}">
    [#-- Index --]
    <div class="leftHead gray sm"><span class="index">${index+1}</span></div>
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(powebElement.id)!}" >
    <input type="hidden" name="${customName}.crpMilestone.id" value="${(powebElement.crpMilestone.id)!}" >
    
    [#-- Title --]
    <div class="form-group">
      <div class="pull-right">[@milestoneContributions element=element /]</div>
      <p class="text-justify"><strong>Milestone for ${actualPhase.year}</strong> - ${(element.title)!} ${element.id}</p>
    </div>
    
    [#-- Assessment of risk to achievement --]
    <div class="form-group">
      <label>[@s.text name="liaisonInstitution.powb.milestone.assessment" /] [@customForm.req required=editable  /]</label><br />
      [@customForm.radioFlat id="${customName}-risk-1" name="${customName}.assessment" label="Low"    value="1" checked=(powebElement.assessment == "1")!false editable=editable cssClass="" cssClassLabel=""/]
      [@customForm.radioFlat id="${customName}-risk-2" name="${customName}.assessment" label="Medium" value="2" checked=(powebElement.assessment == "2")!false editable=editable cssClass="" cssClassLabel=""/]
      [@customForm.radioFlat id="${customName}-risk-3" name="${customName}.assessment" label="High"   value="3" checked=(powebElement.assessment == "3")!false editable=editable cssClass="" cssClassLabel=""/]
      
      [#local assessmentSelected = ((powebElement.assessment == "1")!false) || ((powebElement.assessment == "2")!false) || ((powebElement.assessment == "3")!false)]
      [#if !editable && !assessmentSelected][@s.text name="form.values.fieldEmpty"/][/#if]
    </div>
    
    [#-- Means of verification --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.means" i18nkey="liaisonInstitution.powb.milestone.meansVerifications" help="" display=true required=true className="limitWords-100" editable=editable /]
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
      </div>
      <div class="modal-body">
        <p> <strong>Milestone for ${actualPhase.year}</strong> - ${(element.title!)}</p>
        
        <div class="">
          <table class="table table-bordered">
            <thead>
              <tr>
                <th class="col-md-1"> Project ID </th>
                <th class="col-md-4"> Project Title </th>
                <th class="col-md-1"> Target Value and Unit </th>
                <th class="col-md-6"> Narrative of the  expected target </th>
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
                  <td>
                    [#if (contribution.expectedUnit.name??)!false]
                      ${(contribution.expectedValue)!} ( ${(contribution.expectedUnit.name)!})
                    [#else]
                      <i>N/A</i>
                    [/#if]
                  </td>
                  <td> ${contribution.narrativeTarget} </td>
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
