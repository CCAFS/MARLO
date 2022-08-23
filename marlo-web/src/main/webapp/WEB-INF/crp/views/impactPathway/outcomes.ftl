[#ftl]
[#assign title = "Impact Pathway - Outcomes" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${crpProgramID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload", "cytoscape","cytoscape-panzoom"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/impactPathway/programSubmit.js",
  "${baseUrlMedia}/js/impactPathway/outcomes.js?20201709",
  [#-- "${baseUrlCdn}/global/js/autoSave.js", --]
  "${baseUrlCdn}/global/js/impactGraphic.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ]
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/impactPathway/outcomes.css?20202209",
  "${baseUrlCdn}/global/css/impactGraphic.css"
  ]
/]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "outcomes" /]


[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"", "action":"outcomes"},
  {"label":"outcomes", "nameSpace":"", "action":""}
]/]



[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/relationsPopupMacro.ftl" as popUps /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

<!--
<div class="container helpText viewMore-block">
  <div style="display:none;" class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="outcomes.help" /] </p>
  </div>
  <div style="display:none" class="viewMore closed"></div>
</div>
-->

<div class="animated flipInX container  viewMore-block containerAlertMargin">
  <div class=" containerAlert alert-leftovers alertColorBackgroundInfo "> 
    <div class="containerLine alertColorInfo"></div>
    <div class="containerIcon">
      <div class="containerIcon alertColorInfo">
        <img src="${baseUrlCdn}/global/images/icon-question.png" />         
      </div>
    </div>
    <div class="containerText col-md-12 alertCollapse">
      <p class="alertText">
       [@s.text name="outcomes.help" /]
      </p>
    </div>
    <div  class="viewMoreCollapse closed"></div>
  </div>  
</div>

<section class="marlo-content">
  <div class="container">
    [#if programs?has_content]
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Sub Menu --]
        [#include "/WEB-INF/crp/views/impactPathway/submenu-impactPathway.ftl" /]

        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/impactPathway/messages-impactPathway.ftl" /]

        [@s.form action=actionName enctype="multipart/form-data" ]
        [#-- Outcomes List --]
        <h4 class="sectionTitle">[@s.text name="outcomes.title"][@s.param]${(selectedProgram.acronym)!}[/@s.param] [/@s.text]</h4>

          [#-- Check if the programID is Valid --]
          [#assign hasAvailableProgramID = false ]
          [#list programs as program]
            [#if (crpProgramID == program.id)!false]
              [#assign hasAvailableProgramID = true ]
              [#break]
            [/#if]
          [/#list]

          [#if hasAvailableProgramID]
            <div class="outcomes-list" listname="outcomes">
             <div class="cont-btn-min">
              [#if action.isAiccra()]
                <button type="button" class="btn-expand-all-outcomes btn btn-link">Collapse all indicators<i class="fas fa-expand-arrows-alt"></i></button>
              [#else]
                <button type="button" class="btn-expand-all-outcomes btn btn-link">Collapse all outcomes<i class="fas fa-expand-arrows-alt"></i></button>
              [/#if]
             </div>
            [#if outcomes?has_content]
              [#list outcomes as outcome]
                [@outcomeMacro outcome=outcome name="outcomes" index=outcome_index /]
              [/#list]
            [#else]
              [@outcomeMacro outcome={} name="outcomes" index=0 /]
            [/#if]
            </div>
            [#-- Add Outcome Button --]
            [#if editable]
              <div class="addOutcome bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addOutcome"/]</div>
            [/#if]
          [#else]
            <p>Please select a [@s.text name="global.flagship" /]</p>
          [/#if]



          [#-- Section Buttons--]
          [#include "/WEB-INF/crp/views/impactPathway/buttons-impactPathway.ftl" /]

        [/@s.form]
    </div>
    [#else]
      <p class="text-center borderBox">[@s.text name="impactPathway.noFlagshipsAdded" /]</p>
    [/#if]
  </div>
</div>



</section>

[#-- PopUp to select SubIDOs --]
<div id="subIDOs-graphic" style="overflow:auto; display:none;" >
  <div class="graphic-container" >
  <div class="filterPanel panel-default">
    <div class="panel-heading">
      <form id="filterForm"  role="form">
        <label class="checkbox-inline">Filter By:</label>
        <label class="checkbox-inline">
          <input type="checkbox" value="IDO" checked>IDOs
        </label>
        <label class="checkbox-inline">
          <input type="checkbox" value="CCIDO" checked>Cross-cutting IDOs
        </label>
      </form>
    </div>
  </div>
  [#list srfIdos as ido]
    <div class="idoWrapper ${ido.isCrossCutting?string("crossCutting","ido")} ">
      <div class="IDO${ido.isCrossCutting?string("-CrossCutting","")}"><strong>${ido.isCrossCutting?string("CrossCutting:","")} ${ido.description}</strong></div>
      <div class="subIdoWrapper">
        [#list ido.subIdos as subIdo]
          <div class="line"></div>
          <div id="subIdo-${subIdo.id}" class="subIDO subIDO${ido.isCrossCutting?string("-CrossCutting","")}">${subIdo.smoCode} ${subIdo.description}</div>
        [/#list]
      </div>
    </div>
  [/#list]
  </div>
</div>

[#-- Add other target unit --]
<div id="dialog-targetUnit" class="text-center" style="display:none" title="New Target Unit">
  <div class="form-group text-center">
    <label for="targetUnitName">Insert the new Target Unit</label>
    <input type="text" class="form-control" id="targetUnitName" placeholder="">
  </div>
</div>

[#-- Outcome Template --]
[@outcomeMacro outcome={} name="outcomes" index=-1 isTemplate=true /]

[#-- Milestone Template --]
[@milestoneMacro milestone={} name="outcomes[0].milestones" index=-1 isTemplate=true  /]

[#-- Sub-Ido Template --]
[@subIDOMacro subIdo={} name="outcomes[0].subIdos" index=-1 isTemplate=true /]

[#-- Assumption Template --]
[@assumptionMacro assumption={} name="outcomes[-1].subIdos[-1].assumptions" index=-1 isTemplate=true /]

[#-- Baseline Indicator Template --]
[@baselineIndicatorMacro indicator={} name="outcomes[-1].indicators" index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#-----------------------------------  Outcomes Macros  -------------------------------------------]

[#macro outcomeMacro outcome name index isTemplate=false]
  [#assign outcomeCustomName= "${name}[${index}]" /]
  <div id="outcome-${isTemplate?string('template', index)}" class="outcome form-group borderBox" style="display:${isTemplate?string('none','block')}">
    <div class="leftHead">
      <!--<span class="index">${index+1}</span>-->
      <span class="index"> ${(outcome.composeID)! "[New]"}</span>
      <span class="elementId">${(selectedProgram.acronym)!} - [@s.text name="outcome.index.title"/]</span>
    </div>
    [#-- Outcome ID Parameter --]
    <input type="hidden" class="outcomeId" name="${outcomeCustomName}.id" value="${(outcome.id)!}"/>
    <input type="hidden" class="outcomeComposeId" name="${outcomeCustomName}.composeID" value="${(outcome.composeID)!}"/>
    [#-- Remove Button --]
    [#if editable && action.canBeDeleted((outcome.id)!-1,(outcome.class.name)!"" )]
      <div class="removeOutcome removeElement" title="Remove Outcome"></div>
    [#elseif editable]
      <div class="removeElement disable" title="[@s.text name="global.CrpProgramOutcome"/] can not be deleted"></div>
    [/#if]

    [#if !isTemplate]
      <div class="pull-right">
        [@popUps.relationsMacro element=outcome /]
      </div>
    [/#if]

    <br />
    <div class="cont-btn-min">
     <button   type="button" class="btn-expand-Outcome btn btn-link">Collapse Outcome<i class="fas fa-expand-arrows-alt"></i></button>
    </div>
    [#-- Outcome Statement --]
    <div class="form-group">
      [@customForm.textArea name="${outcomeCustomName}.description"  i18nkey="outcome.statement" required=true className="outcome-statement limitWords-100" editable=editable /]
    </div>
    [#-- Outcome Indicator --]
    [#if action.hasSpecificities('crp_ip_outcome_indicator')]
    <div class="form-group">
      [@customForm.textArea name="${outcomeCustomName}.indicator"  i18nkey="outcome.inidicator" required=false className="outcome-inidicator limitWords-100" editable=editable /]
    </div>
    [/#if]

    <div class="row form-group target-block to-minimize-outcome">
      [#-- Target Year --]
      <div class="col-md-4">[@customForm.input name="${outcomeCustomName}.year" value="${(outcome.year)!2023}" type="text" i18nkey="outcome.targetYear"  placeholder="outcome.inputTargetYear.placeholder" className="targetYear outcomeYear" required=true editable=editable /]</div>
      [#-- Target Unit --]
      [#if targetUnitList?has_content]
      <div class="col-md-4">
        [@customForm.select name="${outcomeCustomName}.srfTargetUnit.id" i18nkey="outcome.selectTargetUnit"  placeholder="outcome.selectTargetUnit.placeholder" className="targetUnit" listName="targetUnitList" editable=editable  /]
        [#-- If you dont find the target unit in the list, please add a new one clicking here --]
        [#--  --if editable]<div class="addOtherTargetUnit text-center"><a href="#">([@s.text name = "outcomes.addNewTargetUnit" /])</a></div>[/#if --]
      </div>
      [#else]
      <input type="hidden" name="${outcomeCustomName}.srfTargetUnit.id" value="-1"/>
      [/#if]
      [#-- Target Value --]
      [#local showTargetValue = (targetUnitList?has_content) && (outcome.srfTargetUnit??) && (outcome.srfTargetUnit.id??) && (outcome.srfTargetUnit.id != -1) /]
      <div class="col-md-4 targetValue-block" style="display:${showTargetValue?string('block', 'none')}">
        [@customForm.input name="${outcomeCustomName}.value" i18nkey="outcome.targetValue" help="outcomes.addNewTargetUnit"  placeholder="outcome.inputTargetValue.placeholder" className="targetValue" required=true editable=editable /]
      </div>

    </div>

    <!-- Nav tabs -->
    <ul class="nav nav-tabs to-minimize-outcome" role="tablist">
      <li role="presentation" class="active"><a href="#milestones-tab-${index}" aria-controls="messages" role="tab" data-toggle="tab">Intermediate Targets <span class="badge">${(outcome.milestones?size)!'0'}</span></a></li>
      [#if action.hasSpecificities('crp_baseline_indicators') && (selectedProgram.baseLine)!false]
        [#if action.isAiccra()]
          <li role="presentation"><a href="#baseline-tab-${index}" aria-controls="profile" role="tab" data-toggle="tab">Progress to Target Indicators <span class="badge">${(outcome.indicators?size)!'0'}</span></a></li>
        [#else]
          <li role="presentation"><a href="#baseline-tab-${index}" aria-controls="profile" role="tab" data-toggle="tab">Baseline Indicators <span class="badge">${(outcome.indicators?size)!'0'}</span></a></li>
        [/#if]
      [/#if]
      [#if !action.isAiccra()]
        <li role="presentation" ><a href="#subIdos-tab-${index}" aria-controls="home" role="tab" data-toggle="tab">Sub-IDOs <span class="badge">${(outcome.subIdos?size)!'0'}</span></a></li>
      [/#if]
     </ul>

    <!-- Tab panes -->
    <div class="tab-content impactpathwayTabContent  to-minimize-outcome">

      [#if !action.isAiccra()]
        [#-- Outcome Sub-IDOs List --]
        <div role="tabpanel" class="tab-pane fade " id="subIdos-tab-${index}">

          [#-- <h5 class="sectionSubTitle">[@s.text name="outcome.subIDOs.sectionTitle"/] <p class="contributioRem pull-right">Contribution <span class="value">0%</span></p></h5>--]
          <div class="subIdos-list" listname="${outcomeCustomName}.subIdos">
            [#if outcome.subIdos?has_content]
              [#list outcome.subIdos as subIdo]
                [@subIDOMacro subIdo=subIdo name="${outcomeCustomName}.subIdos" index=subIdo_index /]
              [/#list]
            [#else]
              [@subIDOMacro subIdo={} name="${outcomeCustomName}.subIdos" index=0 /]
              [#-- <p class="message text-center">[@s.text name="outcome.subIDOs.section.notSubIDOs.span"/]</p> --]
            [/#if]
          </div>
          [#-- Add Sub-IDO Button --]
          [#if editable]
            <div class="text-right">
              <div class="addSubIdo button-blue text-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addSubIDO"/]</div>
            </div>
          [/#if]
        </div>
      [/#if]

      [#-- Baseline indicators --]
      [#if action.hasSpecificities('crp_baseline_indicators') && (selectedProgram.baseLine)!false]
      <div role="tabpanel" class="tab-pane fade" id="baseline-tab-${index}">

        [#-- Upload a PDF with baseline instructions --]
        <div class="form-group fileUploadContainer">
          <label>[@customForm.text name="outcome.baselineInstructions" readText=!editable /]:</label>
          [#if !isTemplate]
            [#local hasFile = outcome.file?? && outcome.file.id?? /]
            <input class="fileID" type="hidden" name="${outcomeCustomName}.file.id" value="${(outcome.file.id)!}" />
            [#-- Input File --]
            [#if editable]
            <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="upload" type="file" name="file" data-url="${baseUrl}/uploadBaseLine.do"></div>

            [/#if]
            [#-- Uploaded File --]
            <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
              <span class="contentResult">[#if outcome.file??]
                <a target="_blank" href="${action.getBaseLineFileURL((outcome.id?string)!-1)}&filename=${(outcome.file.fileName)!}" target="_blank" class="downloadBaseline"><img src="${baseUrlCdn}/global/images/pdf.png" width="38px" alt="Download document" /> ${(outcome.file.fileName)!('No file name')} </a>
                [/#if]</span>
              [#if editable]<span class="removeIcon"> </span> [/#if]
            </p>
          [#else]
            <p><i>[@customForm.text name="outcome.baselineInstructionsUnavailbale" readText=!editable /] </i></p>
          [/#if]
        </div>
        <br />
        [#-- Baseline indicators list --]
        <h5 class="sectionSubTitle">[@s.text name="outcome.baselineIndicators" /]:</h5>
        <div class="baselineIndicators-list"">

        [#if outcome.indicators?has_content]
          [#list outcome.indicators as baselineIndicator]
            [@baselineIndicatorMacro indicator=baselineIndicator name="${outcomeCustomName}.indicators" index=baselineIndicator_index /]
          [/#list]
        [#else]
          [#-- @baselineIndicatorMacro indicator={} name="${outcomeCustomName}.indicators" index=0 / --]
        [/#if]
        </div>
        [#-- Add Baseline Indicator Button --]
        [#if editable]
        <div class="text-right">
          <div class="addBaselineIndicator button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addBaselineIndicator"/]</div>
        </div>
        [/#if]
      </div>
      [/#if]

      [#-- Outcome Milestones List --]
      <div role="tabpanel" class="tab-pane fade in active" id="milestones-tab-${index}">

        [#--<h5 class="sectionSubTitle">[@s.text name="outcome.milestone.sectionTitle"/]</h5>--]
        <div class="milestones-list" listname="${outcomeCustomName}.milestones">

        [#if outcome.milestones?has_content]
           <div class="cont-btn-min">
             <button   type="button" class="btn-expand-all btn btn-link">Collapse all<i class="fas fa-expand-arrows-alt"></i></button>
           </div>
          [#list outcome.milestones as milestone]
            [@milestoneMacro milestone=milestone name="${outcomeCustomName}.milestones" index=milestone_index editable=editable canEditMilestone=action.canEditMileStone(milestone) /]
          [/#list]
        [#else]
          <p class="message text-center">[@s.text name="outcome.milestone.section.notMilestones.span"/]</p>
        [/#if]
        </div>
        [#-- Add Milestone Button --]
        [#if editable]
        <div class="">
          <div class="addMilestone bigAddButton text-center form-group"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addMilestone"/]</div>
        </div>
        [/#if]
        [#if editable]<div class="form-group note"><small>[@s.text name = "outcomes.addNewTargetUnit" /]</small></div>[/#if]
      </div>
    </div>

    <br />

  </div>
[/#macro]


[#macro milestoneMacro milestone name index isTemplate=false editable=true canEditMilestone=true]
  [#local milestoneCustomName = "${name}[${index}]" /]
  [#local editableMilestone = editable && canEditMilestone]
  [#local hasExtendedYear = (milestone.extendedYear?has_content) && (milestone.extendedYear != -1) && milestone.extendedYear != milestone.year]
  [#local showExtendedYear =  hasExtendedYear || ((milestone.milestonesStatus.id == 4)!false) ]
  [#local milestoneYear =  (milestone.year)!currentCycleYear ]
  [#--if hasExtendedYear
    [#local milestoneYear =  milestone.extendedYear ]
  [/#if --]
  [#local reqMilestonesFields = (milestoneYear == actualPhase.year)!false /]

  [#local isMilestoneNew =  true ]
  [#if !isTemplate]
    [#local isMilestoneNew =  milestone.isNew(actualPhase.id) ]
  [/#if]
  <div id="srfSlo-${isTemplate?string('template',index)}" class="srfSlo borderBox-no-padding" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    [#if editableMilestone && action.canBeDeleted((milestone.id)!-1,(milestone.class.name)!"" )]
      <div class="removeMilestone removeElement sm" title="Remove Milestone"></div>
    [#elseif editableMilestone]
      <div class="removeElement sm disable" title="[@s.text name="global.CrpMilestone"/] can not be deleted"></div>
    [/#if]

    [#-- SLO Title --]
    <div class="blockTitle opened">
      <div class="leftHead ${reqMilestonesFields?string('green', '')} sm">
        <!--<span class="index">${index+1}</span>-->
        <span class="index">${(milestone.composeID)! "[New]"}</span>
        <span class="elementId">${(milestoneYear)!} [@s.text name="outcome.milestone.index.title"/][#if hasExtendedYear] [@s.text name="outcome.milestone.extended.text"/] ${milestone.extendedYear} [/#if][#if isMilestoneNew][New][/#if]</span>
      </div>
      <!-- <strong>SLO ${index+1}: </strong>  -->
     ${(milestone.title)!""}
      <!-- <small>(Alerts: 5) </small> -->
    </div>

    <div class="blockContent" style="display:block">
      <div id="milestone-${isTemplate?string('template', index)}" class="milestone borderBox-no-border isNew-${isMilestoneNew?string}" style="display:${isTemplate?string('none','block')}">





        <input type="hidden" class="mileStoneId" name="${milestoneCustomName}.id" value="${(milestone.id)!}"/>
        <input type="hidden" class="mileStoneComposeId" name="${milestoneCustomName}.composeID" value="${(milestone.composeID)!}"/>

        <div class="pull-right">
          [@popUps.relationsMacro element=(milestone)!{} /]
        </div>

        [#-- Milestone Statement --]
        <div class="form-group">
          [@customForm.textArea name="${milestoneCustomName}.title" i18nkey="outcome.milestone.statement" required=true className="milestone-statement limitWords-100" editable=editableMilestone /]
        </div>

        <div class="form-group row to-minimize">
          [#-- Year --]
          <div class="col-md-4">
            [@customForm.select name="${milestoneCustomName}.year" value="${(milestone.year)!-1}"  i18nkey="outcome.milestone.inputTargetYear" listName="milestoneYears"  required=true  className=" targetYear milestoneYear" editable=editableMilestone /]
           </div>
          [#--  Status  --]
          <div class="col-md-4">
            [@customForm.select name="${milestoneCustomName}.milestonesStatus.id" forcedValue="${(milestone.milestonesStatus.name)!}" i18nkey="outcome.milestone.inputStatus" listName="generalStatuses" keyFieldName="id" displayFieldName="name" required=true  className="milestoneStatus" editable=editable /]
          </div>
          [#-- Extended Year --]
          <div class="col-md-4 extendedYearBlock" style="display:${showExtendedYear?string('block', 'none')}">
           [@customForm.select name="${milestoneCustomName}.extendedYear" value="${(milestone.extendedYear)!-1}"  i18nkey="outcome.milestone.inputNewTargetYear" listName="milestoneYears"  required=true  className=" targetYear milestoneExtendedYear" editable=editable /]
           [#if !editableMilestone][#if (milestone.extendedYear != -1)!false ]${(milestone.extendedYear)!}[/#if][/#if]
          </div>
        </div>

        <div class="row form-group target-block to-minimize">
          [#-- Target Unit --]
          [#if targetUnitList?has_content]
          <div class="col-md-4">
            [@customForm.select name="${milestoneCustomName}.srfTargetUnit.id"  i18nkey="outcome.milestone.selectTargetUnit" placeholder="outcome.selectTargetUnit.placeholder" className="targetUnit" listName="targetUnitList" editable=editableMilestone  /]
          </div>
          [/#if]
          [#-- Target Value --]
          [#local showTargetValue = (targetUnitList?has_content) && (milestone.srfTargetUnit??) && (milestone.srfTargetUnit.id??) && (milestone.srfTargetUnit.id != -1) /]
          <div class="col-md-4 targetValue-block" style="display:${showTargetValue?string('block', 'none')}">
            [@customForm.input name="${milestoneCustomName}.value" type="text"  i18nkey="outcome.milestone.inputTargetValue" placeholder="outcome.milestone.inputTargetValue.placeholder" className="targetValue" required=true editable=editableMilestone /]
          </div>
        </div>

        [#-- POWB 2019 REQUIREMENTS --]
        <div class="form-group to-minimize">
          <div class="row">
            [#-- Indicate of the following --]
            <div class="col-md-5">
              [@customForm.select name="${milestoneCustomName}.powbIndFollowingMilestone.id"  i18nkey="outcome.milestone.powbIndFollowingMilestone" className="" keyFieldName="id" displayFieldName="name" listName="followingMilestones" editable=editable required=reqMilestonesFields /]
            </div>
            [#-- Assessment of risk to achievement --]
            <div class="col-md-7">
              [#if globalUnitType != 3]
                <div class="form-group listname="${milestoneCustomName}.powbIndAssesmentRisk.id">
                  <label>[@s.text name="outcome.milestone.powbIndAssesmentRisk" /]:[@customForm.req required = true && editable = editable && reqMilestonesFields  /]</label> <br />
                  [#list (assessmentRisks)![] as assesment]
                    [@customForm.radioFlat id="${milestoneCustomName}-risk-${assesment.id}" name="${milestoneCustomName}.powbIndAssesmentRisk.id" label="${assesment.name}" value="${assesment.id}" checked=(milestone.powbIndAssesmentRisk.id == assesment.id)!false editable=editable cssClass="assesmentLevels" cssClassLabel=""/]
                  [/#list]
                  [#if !editable && (!(milestone.powbIndAssesmentRisk??))!true][@s.text name="form.values.fieldEmpty"/][/#if]
                </div>
              [/#if]
            </div>
          </div>

          [#if globalUnitType != 3]
            <div class="row form-group">
              [#-- For medium/high please select the main risk --]
              [#local showRisk = (milestone.powbIndAssesmentRisk.id >= 2)!false ]
              <div class="col-md-6 milestoneRisk" style="display:${showRisk?string('block', 'none')}">
                [@customForm.select name="${milestoneCustomName}.powbIndMilestoneRisk.id"  i18nkey="outcome.milestone.powbIndMilestoneRisk" className="risksOptions" keyFieldName="id" displayFieldName="name" listName="milestoneRisks" editable=editable required=reqMilestonesFields /]
              </div>
              [#-- Other Risk --]
              [#local showOtherRiskField = (milestone.powbIndMilestoneRisk.id == 7)!false ]
              <div class="col-md-6 milestoneOtherRiskField" style="display:${showOtherRiskField?string('block', 'none')}">
                [@customForm.input name="${milestoneCustomName}.powbMilestoneOtherRisk"  i18nkey="outcome.milestone.powbMilestoneOtherRisk" className="" editable=editable required=reqMilestonesFields /]
              </div>
            </div>
          [/#if]

          [#-- Means of verification --]
          <div class="form-group">
            [@customForm.textArea name="${milestoneCustomName}.powbMilestoneVerification" i18nkey="outcome.milestone.powbMilestoneVerification" required=true className="milestone-powbMilestoneVerification" editable=editable required=reqMilestonesFields /]
          </div>
          [#-- DAC Markers for the milestone --]
          <div class="row form-group">
            <p class="subTitle col-md-12"><i> [@s.text name="outcome.milestone.milestoneMarkers" /] </i> </p><br />
            <div class="col-md-3">
              [@customForm.select name="${milestoneCustomName}.genderFocusLevel.id"  i18nkey="outcome.milestone.genderFocusLevel" className="" keyFieldName="id" displayFieldName="powbName" listName="focusLevels" editable=editable required=reqMilestonesFields  /]
            </div>
            <div class="col-md-3">
              [@customForm.select name="${milestoneCustomName}.youthFocusLevel.id"  i18nkey="outcome.milestone.youthFocusLevel" className="" keyFieldName="id" displayFieldName="powbName" listName="focusLevels" editable=editable required=reqMilestonesFields /]
            </div>
            <div class="col-md-3">
              [@customForm.select name="${milestoneCustomName}.capdevFocusLevel.id"  i18nkey="outcome.milestone.capdevFocusLevel" className="" keyFieldName="id" displayFieldName="powbName" listName="focusLevels" editable=editable required=reqMilestonesFields /]
            </div>
            <div class="col-md-3">
              [@customForm.select name="${milestoneCustomName}.climateFocusLevel.id"  i18nkey="outcome.milestone.climateFocusLevel" className="" keyFieldName="id" displayFieldName="powbName" listName="focusLevels" editable=editable required=reqMilestonesFields /]
            </div>
            <br />
          </div>
        </div>
      </div>

    </div>
  </div>
  <!-- //MILESTONE NORMAL -->

[/#macro]


[#macro subIDOMacro subIdo name index isTemplate=false]
  [#local subIDOCustomName = "${name}[${index}]" /]
  [#local subIDOCustomID = "${name}_${index}"?replace("\\W+", "", "r") /]
  <div id="subIdo-${isTemplate?string('template', index)}" class="subIdo simpleBox" style="display:${isTemplate?string('none','block')}">
    <div class="loading" style="display:none"></div>
    <div class="leftHead blue sm">
      <span class="index">${index+1}</span>
      <span class="elementId">[@s.text name="outcome.subIDOs.index.title"/]</span>
    </div>
    [#-- Hidden inputs --]
    <input type="hidden" class="programSubIDOId" name="${subIDOCustomName}.id" value="${(subIdo.id)!}"/>

    [#-- Remove Button --]
    [#if editable && action.canBeDeleted((subIdo.id)!-1,(subIdo.class.name)!"" )]
      <div class="removeSubIdo removeElement sm" title="Remove Sub IDO"></div>
    [#elseif editable]
      <div class="removeElement sm disable" title="[@s.text name="global.SrfSubIdo"/] can not be deleted"></div>
    [/#if]
    [#-- Primary Option --]
    <div class="">
      [@customForm.radioFlat id="${subIDOCustomName}.primary" name="${subIDOCustomName}.primary" label="Set this Sub-IDO as primary" disabled=false editable=editable value="true" checked=(subIdo.primary)!false cssClass="setPrimaryRadio" cssClassLabel="radio-label-yes" inline=false /]
    </div>
    [#-- Sub IDO --]
    <div class="form-group">
      <div class="subIdoBlock" >
        <label for="">[@s.text name="outcome.subIDOs.inputSubIDO.label"/]:[#if editable]<span class="red">*</span>[/#if]</label>
        <div id="" class="${subIDOCustomID} subIdoSelected">
          [@utils.letterCutter string="${(subIdo.srfSubIdo.description)!'<i>No Sub-IDO Selected</i>'}" maxPos=65 /]
        </div>
        <input type="hidden" class="subIdoId" name="${subIDOCustomName}.srfSubIdo.id" value="${(subIdo.srfSubIdo.id)!}" />
      </div>
      <div class="buttonSubIdo-block" >
        [#if editable]
          <div class="buttonSubIdo-content"><br> <div class="button-blue selectSubIDO" ><span class=""></span> Select a Sub-IDO</div></div>
        [/#if]
      </div>
      <div class="contributionBlock">[@customForm.input name="${subIDOCustomName}.contribution" type="text" i18nkey="outcome.subIDOs.inputContribution.label" placeholder="% of contribution" className="contribution" required=true editable=editable /]</div>
      <div class="clearfix"></div>
    </div>
    <hr />
    [#-- Assumptions List --]
    <div class="row" style="position: relative;">
      <div class="col-md-9">
        <label for="">[@s.text name="outcome.subIDOs.assumptions.label" /]:</label>
        <div class="assumptions-list" listname="${subIDOCustomName}.assumptions">
          [#if subIdo.assumptions?has_content]
            [#list subIdo.assumptions as assumption]
              [@assumptionMacro assumption=assumption name="${subIDOCustomName}.assumptions" index=assumption_index /]
            [/#list]
          [#else]
          [@assumptionMacro assumption={} name="${subIDOCustomName}.assumptions" index=0 /]
          [#-- <p class="message text-center">[@s.text name="outcome.subIDOs.section.notAssumptions.span"/]</p> --]
          [/#if]
        </div>
      </div>
      [#-- Add Assumption Button --]
      [#if editable]<div class="addAssumption button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addAssumption"/]</div>[/#if]
    </div>
  </div>
[/#macro]

[#macro assumptionMacro assumption name index isTemplate=false]
  [#assign assumptionCustomName = "${name}[${index}]" /]
  <div id="assumption-${isTemplate?string('template', index)}" class="assumption form-group" style="position:relative; display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    [#if editable]
    <div class="removeAssumption removeIcon" title="Remove assumption"></div>
    [/#if]
    <input type="hidden" class="assumptionId" name="${assumptionCustomName}.id" value="${(assumption.id)!}"/>
    [#if !editable]
      [#if assumption.description?has_content]
        <div class="input"><p> <strong>${index+1}.</strong> ${(assumption.description)!}</p></div>
      [/#if]
    [#else]
      [@customForm.input name="${assumptionCustomName}.description" type="text" showTitle=false placeholder="" className="statement limitWords-100" required=true editable=editable /]
    [/#if]
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro baselineIndicatorMacro indicator name index isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  <div id="baselineIndicator-${isTemplate?string('template', index)}" class="baselineIndicator simpleBox form-group" style="position:relative; display:${isTemplate?string('none','block')}">
    [#-- Index --]
    <div class="leftHead gray sm">
      <span class="index">${index+1}</span>
    </div>
    [#-- Remove Button --]
    [#if editable]<div class="removeBaselineIndicator removeElement sm" title="Remove Indicators"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" class="baselineIndicatorId" name="${customName}.id" value="${(indicator.id)!}"/>

    <input type="hidden"  name="${customName}.composeID" value="${(indicator.composeID)!}"/>

    [#if editable]
      [@customForm.input name="${customName}.indicator" value=indicator.title i18nkey="baselineIndicator.title" type="text" showTitle=true placeholder="" className="statement limitWords-50" required=true editable=editable /]
    [#else]
      [#if indicator.indicator?has_content]
        <div class="input"><p>${(indicator.indicator)!}</p></div>
      [/#if]
    [/#if]
    <div class="clearfix"></div>
  </div>
[/#macro]
