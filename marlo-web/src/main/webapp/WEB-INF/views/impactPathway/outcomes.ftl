[#ftl]
[#assign title = "Impact Pathway - Outcomes" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${crpProgramID}" /]
[#assign pageLibs = ["select2","cytoscape","cytoscape-panzoom"] /]
[#assign customJS = [ "${baseUrlMedia}/js/impactPathway/programSubmit.js", "${baseUrlMedia}/js/impactPathway/outcomes.js", "${baseUrlMedia}/js/global/autoSave.js", "${baseUrlMedia}/js/global/impactGraphic.js","${baseUrlMedia}/js/global/fieldsValidation.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/impactPathway/outcomes.css","${baseUrlMedia}/css/global/impactGraphic.css" ] /]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "outcomes" /]


[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"", "action":"outcomes"},
  {"label":"outcomes", "nameSpace":"", "action":""}
]/]



[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

<div class="container helpText viewMore-block">
  <div style="display:none;" class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlMedia}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="outcomes.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

<section class="marlo-content">
  <div class="container"> 
    [#if programs?has_content]
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/views/impactPathway/messages-impactPathway.ftl" /]
        
        [#-- Program (Flagships) --]
        <ul id="liaisonInstitutions" class="horizontalSubMenu text-left">
          [#list programs as program]
            [#assign isActive = (program.id == crpProgramID)/]
            <li class="${isActive?string('active','')}">
              <a href="[@s.url][@s.param name ="crpProgramID"]${program.id}[/@s.param][@s.param name ="edit"]true[/@s.param][/@s.url]">[@s.text name="flagShip.menu"/] ${program.acronym}</a>
            </li>
          [/#list]
        </ul>
        
        [@s.form action=actionName enctype="multipart/form-data" ]  
        [#-- Outcomes List --]
        <h4 class="sectionTitle">[@s.text name="outcomes.title"][@s.param]${(selectedProgram.acronym)!}[/@s.param] [/@s.text]</h4>
        
          <div class="outcomes-list" listname="outcomes">
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
            
          [#-- Section Buttons--]
          [#include "/WEB-INF/views/impactPathway/buttons-impactPathway.ftl" /]
          
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
          <div id="subIdo-${subIdo.id}" class="subIDO subIDO${ido.isCrossCutting?string("-CrossCutting","")}">${subIdo.description}</div>
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
[@outcomeMacro outcome={} name="outcome" index=-1 isTemplate=true /]

[#-- Milestone Template --]
[@milestoneMacro milestone={} name="outcomes[0].milestones" index=-1 isTemplate=true /]

[#-- Sub-Ido Template --]
[@subIDOMacro subIdo={} name="outcomes[0].subIdos" index=-1 isTemplate=true /]

[#-- Assumption Template --]
[@assumptionMacro assumption={} name="outcomes[-1].subIdos[-1].assumptions" index=-1 isTemplate=true /]

[#-- Baseline Indicator Template --]
[@baselineIndicatorMacro indicator={} name="outcomes[-1].baselineIndicators" index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#-----------------------------------  Outcomes Macros  -------------------------------------------]

[#macro outcomeMacro outcome name index isTemplate=false]
  [#assign outcomeCustomName= "${name}[${index}]" /]
  <div id="outcome-${isTemplate?string('template', index)}" class="outcome form-group borderBox" style="display:${isTemplate?string('none','block')}">
    <div class="leftHead">
      <span class="index">${index+1}</span>
      <span class="elementId">${(selectedProgram.acronym)!} - [@s.text name="outcome.index.title"/]</span>
    </div>
    [#-- Outcome ID Parameter --]
    <input type="hidden" class="outcomeId" name="${outcomeCustomName}.id" value="${(outcome.id)!}"/>
    [#-- Remove Button --]
    [#if editable]
      <div class="removeOutcome removeElement" title="Remove Outcome"></div>
    [/#if]
    <br />
    [#-- Outcome Statement --]
    <div class="form-group">
      [@customForm.textArea name="${outcomeCustomName}.description"  i18nkey="outcome.statement" required=true className="outcome-statement limitWords-100" editable=editable /]
    </div>
    <div class="row form-group target-block">
      [#-- Target Year --]
      <div class="col-md-4">[@customForm.input name="${outcomeCustomName}.year" value="${(outcome.year)!2022}" type="text" i18nkey="outcome.targetYear"  placeholder="outcome.inputTargetYear.placeholder" className="targetYear outcomeYear" required=true editable=editable /]</div>
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
        [@customForm.input name="${outcomeCustomName}.value" type="text" i18nkey="outcome.targetValue" placeholder="outcome.inputTargetValue.placeholder" className="targetValue" required=true editable=editable /]
      </div>
      

    </div>
    [#if editable && targetUnitList?has_content]<div class="form-group note">[@s.text name = "outcomes.addNewTargetUnit" /]</div>[/#if]
    
    
    [#assign baselineIndicators = [
            {"title": "Total project area targeted (ha)."},
            {"title": "Numbers of heads of livestock per species in the project area. "}
          ] 
        /]
    
    <!-- Nav tabs -->
    <ul class="nav nav-tabs" role="tablist">
      <li role="presentation" class="active"><a href="#subIdos-tab-${index}" aria-controls="home" role="tab" data-toggle="tab">Sub-IDOs <span class="badge">${(outcome.subIdos?size)!'0'}</span></a></li>
      [#if action.hasSpecificities('crp_baseline_indicators') && (crpProgramID == 86)]
      <li role="presentation"><a href="#baseline-tab-${index}" aria-controls="profile" role="tab" data-toggle="tab">Baseline Indicators <span class="badge">${(baselineIndicators?size)!'0'}</span></a></li>
      [/#if]
      <li role="presentation"><a href="#milestones-tab-${index}" aria-controls="messages" role="tab" data-toggle="tab">Milestones <span class="badge">${(outcome.milestones?size)!'0'}</span></a></li>
    </ul>
  
    <!-- Tab panes -->
    <div class="tab-content">
      [#-- Outcome Sub-IDOs List --]
      <div role="tabpanel" class="tab-pane fade in active" id="subIdos-tab-${index}">
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
      
      [#-- Baseline indicators --]
      [#if action.hasSpecificities('crp_baseline_indicators') && (crpProgramID == 86)]
      <div role="tabpanel" class="tab-pane fade" id="baseline-tab-${index}">
        [#-- Upload a PDF with baseline instructions --]
        <div class="form-group fileUploadContainer">
          <label>[@customForm.text name="outcome.baselineInstructions" readText=!editable /]:</label>
          [#local hasFile = outcome.baselineFile?? && outcome.baselineFile.id?? /]
          <input id="fileID" type="hidden" name="${outcomeCustomName}.file.id" value="${(outcome.baselineFile.id)!}" />
          [#-- Input File --]
          [#if editable]
          <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="upload" type="file" name="file" data-url="${baseUrl}/uploadBaselineInstructions.do"></div>
          [/#if]
          [#-- Uploaded File --]
          <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
            <span class="contentResult">[#if outcome.baselineFile??]${(outcome.baselineFile.fileName)!('No file name')} [/#if]</span> 
            [#if editable]<span class="removeIcon"> </span> [/#if]
          </p>
        </div>
        
        [#-- Baseline indicators list --]
        <h5 class="sectionSubTitle">[@s.text name="outcome.baselineIndicators" /]:</h5>
        <div class="baselineIndicators-list"">
        
        [#if baselineIndicators?has_content]
          [#list baselineIndicators as baselineIndicator]
            [@baselineIndicatorMacro indicator=baselineIndicator name="${outcomeCustomName}.baselineIndicators" index=baselineIndicator_index /]
          [/#list]
        [#else]
          [@baselineIndicatorMacro indicator={} name="${outcomeCustomName}.baselineIndicators" index=0 /]
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
      <div role="tabpanel" class="tab-pane fade" id="milestones-tab-${index}">
        [#--<h5 class="sectionSubTitle">[@s.text name="outcome.milestone.sectionTitle"/]</h5>--]
        <div class="milestones-list" listname="${outcomeCustomName}.milestones">
        [#if outcome.milestones?has_content]
          [#list outcome.milestones as milestone]
            [@milestoneMacro milestone=milestone name="${outcomeCustomName}.milestones" index=milestone_index /]
          [/#list]
        [#else]
          <p class="message text-center">[@s.text name="outcome.milestone.section.notMilestones.span"/]</p>
        [/#if]
        </div>
        [#-- Add Milestone Button --]
        [#if editable]
        <div class="text-right">
          <div class="addMilestone button-blue"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addMilestone"/]</div>
        </div>
        [/#if]
        [#if editable]<div class="form-group note"><small>[@s.text name = "outcomes.addNewTargetUnit" /]</small></div>[/#if]
      </div>
    </div>
    
    <br />
  </div>
[/#macro]


[#macro milestoneMacro milestone name index isTemplate=false]
  [#assign milestoneCustomName = "${name}[${index}]" /]
  <div id="milestone-${isTemplate?string('template', index)}" class="milestone simpleBox" style="display:${isTemplate?string('none','block')}">
    <div class="leftHead green sm">
      <span class="index">${index+1}</span>
      <span class="elementId">[@s.text name="outcome.milestone.index.title"/]</span>
    </div>
     <input type="hidden" class="mileStoneId" name="${milestoneCustomName}.id" value="${(milestone.id)!}"/>
    [#-- Remove Button --]
    [#if editable]
      <div class="removeMilestone removeElement sm" title="Remove Milestone"></div>
    [/#if]
    
    [#-- Milestone Statement --]
    <div class="form-group">
      [@customForm.textArea name="${milestoneCustomName}.title" i18nkey="outcome.milestone.statement" required=true className="milestone-statement limitWords-100" editable=editable /]
    </div>
    <div class="row form-group target-block">
      [#-- Target Year --]
      <div class="col-md-4">
        [@customForm.select name="${milestoneCustomName}.year" value="${(milestone.year)!-1}"  i18nkey="outcome.milestone.inputTargetYear" listName="milestoneYears"  required=true  className=" targetYear milestoneYear" editable=editable  disabled=!editable/]
        [#if !editable][#if (milestone.year??) && (milestone.year != -1)]${(milestone.year)!}[/#if][/#if]
      </div>
      [#-- Target Unit --]
      [#if targetUnitList?has_content]
      <div class="col-md-4">
        [@customForm.select name="${milestoneCustomName}.srfTargetUnit.id"  i18nkey="outcome.milestone.selectTargetUnit" placeholder="outcome.selectTargetUnit.placeholder" className="targetUnit" listName="targetUnitList" editable=editable  /]
        [#--  --if editable]<div class="addOtherTargetUnit text-center"><a href="#">([@s.text name = "outcomes.addNewTargetUnit" /])</a></div>[/#if--]
      </div>
      [/#if]
      [#-- Target Value --]
      [#local showTargetValue = (targetUnitList?has_content) && (milestone.srfTargetUnit??) && (milestone.srfTargetUnit.id??) && (milestone.srfTargetUnit.id != -1) /]
      <div class="col-md-4 targetValue-block" style="display:${showTargetValue?string('block', 'none')}">
        [@customForm.input name="${milestoneCustomName}.value" type="text"  i18nkey="outcome.milestone.inputTargetValue" placeholder="outcome.milestone.inputTargetValue.placeholder" className="targetValue" required=true editable=editable /]
      </div>
    </div>
    
  </div>
[/#macro]


[#macro subIDOMacro subIdo name index isTemplate=false]
  [#assign subIDOCustomName = "${name}[${index}]" /]
  <div id="subIdo-${isTemplate?string('template', index)}" class="subIdo simpleBox" style="display:${isTemplate?string('none','block')}">
    <div class="loading" style="display:none"></div>
    <div class="leftHead blue sm">
      <span class="index">${index+1}</span>
      <span class="elementId">[@s.text name="outcome.subIDOs.index.title"/]</span>
    </div>
    [#-- Hidden inputs --]
    <input type="hidden" class="programSubIDOId" name="${subIDOCustomName}.id" value="${(subIdo.id)!}"/>
    
    
    [#-- Remove Button --]
    [#if editable]
    <div class="removeSubIdo removeElement sm" title="Remove Sub IDO"></div>
    [/#if]
    <br />
    <div class="form-group">
      <div class="subIdoBlock" >
        <label for="">[@s.text name="outcome.subIDOs.inputSubIDO.label"/]:[#if editable]<span class="red">*</span>[/#if]</label>
        <div id="" class="${"${subIDOCustomName}.srfSubIdo.id"?replace("\\W+", "", "r")} subIdoSelected ${editable?string(' selectSubIDO',' ')} " title="${(subIdo.getSrfSubIdo().getDescription())!}">[@utils.wordCutter string=(subIdo.getSrfSubIdo().getDescription())!"${editable?string('<i>Please select a Sub-IDO by clicking here</i>','<i>subIdo not selected yet</i>')}" maxPos=65 substr=" "/]</div>
        <input type="hidden" class="subIdoId" name="${subIDOCustomName}.srfSubIdo.id" value="${(subIdo.srfSubIdo.id)!}" />
      </div>
      <div class="buttonSubIdo-block" >
       [#if editable]
        <div class="buttonSubIdo-content">
          <br>
          <div class="button-blue selectSubIDO" ><span class=""></span> Select a Sub-IDO</div>
        </div>
      [/#if]
      </div>
        
      <div class="contributionBlock">[@customForm.input name="${subIDOCustomName}.contribution" type="text" i18nkey="outcome.subIDOs.inputContribution.label" placeholder="% of contribution" className="contribution" required=true editable=editable /]</div>
      <div class="clearfix"></div>
    </div>
    <hr />
    [#-- Assumptions List --]
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
    [#-- Add Assumption Button --]
    [#if editable]
    <div class="text-right">
      <div class="addAssumption button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addAssumption"/]</div>
    </div>
    [/#if]
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
    [#if !editable] 
      [#if indicator.description?has_content]
        <div class="input"><p> <strong>${index+1}.</strong> ${(indicator.description)!}</p></div>
      [/#if] 
    [#else]
      [@customForm.input name="${customName}.title" value=indicator.title i18nkey="baselineIndicator.title" type="text" showTitle=true placeholder="" className="statement limitWords-50" required=true editable=editable /]
    [/#if]
    <div class="clearfix"></div>
  </div>
[/#macro]