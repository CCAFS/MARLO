[#ftl]
[#assign title = "Impact Pathway - Outcomes" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [ "${baseUrl}/js/impactPathway/programSubmit.js", "${baseUrl}/js/impactPathway/outcomes.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/impactPathway/outcomes.css" ] /]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "outcomes" /]


[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"", "action":"outcomes"},
  {"label":"outcomes", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

<section class="marlo-content">
  [#if transactionId??]
    <div class="history-mode container text-center">
      <p>
      [#if transactionId == "-1"]
        History not found
      [#else]
        This is a version history edited by ${selectedProgram.modifiedBy.composedName?html} on ${selectedProgram.activeSince?datetime}
      [/#if]
      </p>
    </div>
  [/#if]
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        <div class="">
          [#-- Program (Flagships) --]
          <ul id="liaisonInstitutions" class="horizontalSubMenu text-left">
            [#list programs as program]
              [#assign isActive = (program.id == crpProgramID)/]
              <li class="${isActive?string('active','')}">
                <a href="[@s.url][@s.param name ="crpProgramID"]${program.id}[/@s.param][@s.param name ="edit"]true[/@s.param][/@s.url]">[@s.text name="flagShip.menu"/] ${program.acronym}</a>
              </li>
            [/#list]
          </ul>
        </div>
        [@s.form action=actionName enctype="multipart/form-data" ]  
        [#-- Outcomes List --]
        <h4 class="sectionTitle">[@s.text name="outcomes.title"/]</h4>
        [#if programs?has_content]
          <div class="outcomes-list">
          [#if outcomes?has_content]
            [#list outcomes as outcome]
              [@outcomeMacro outcome=outcome name="outcomes" index=outcome_index /]
            [/#list]
          [/#if]
          </div>
          [#-- Add Outcome Button --]
          [#if editable]
            <div class="addOutcome bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addOutcome"/]</div>
          [/#if]
            
          [#-- Section Buttons--]
          <div class="buttons">
            <div class="buttons-content">
              [#-- History Log --]
              [#if action.getListLog(selectedProgram)?has_content]
                [#import "/WEB-INF/global/macros/logHistory.ftl" as logHistory /]
                [@logHistory.logList list=action.getListLog(selectedProgram) itemId=crpProgramID /]
                <a href="" onclick="return false" class="form-button button-history"><span class="glyphicon glyphicon-glyphicon glyphicon-list-alt" aria-hidden="true"></span> [@s.text name="form.buttons.history" /]</a>
              [/#if]
              [#if editable]
                <a href="[@s.url][@s.param name="crpProgramID" value=crpProgramID /][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> [@s.text name="form.buttons.back" /]</a>
                [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
              [#else]
                [#if canEdit]
                  <a href="[@s.url][@s.param name="crpProgramID" value=crpProgramID /][@s.param name="edit" value="true"/][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
                [/#if]
              [/#if]
            </div>
          </div>

          <input type="hidden"  name="crpProgramID" value="${(crpProgramID)!}"/>
        [#else]
          <p class="text-center borderBox">There is not flagships added</p>
        [/#if]
        [/@s.form]
    </div>
  </div>
</div>
  
  
  
</section>

[#-- PopUp to select SubIDOs --]
<div id="subIDOs-graphic" style="overflow:auto; display:none;" >
  <div class="graphic-container" >
  <div class="filterPanel panel-default">
  <div class="panel-heading">Filter By: 
    <form role="form">
    <label class="checkbox-inline">
      <input type="checkbox" value="" checked>IDOs
    </label>
    <label class="checkbox-inline">
      <input type="checkbox" value="" checked>CrossCutting IDOs
    </label>
    </form>
  </div>
  </div>        
  [#list srfIdos as ido]
    <div class="idoWrapper ${ido.isCrossCutting?string("crossCutting","")} ">    
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
      
[#-- Outcome Template --]
[@outcomeMacro outcome={} name="" index=0 isTemplate=true /]

[#-- Milestone Template --]
[@milestoneMacro milestone={} name="" index=0 isTemplate=true /]

[#-- Sub-Ido Template --]
[@subIDOMacro subIdo={} name="" index=0 isTemplate=true /]

[#-- Assumption Template --]
[@assumptionMacro assumption={} name="" index=0 isTemplate=true /]

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
      [@customForm.textArea name="${outcomeCustomName}.description" i18nkey="outcome.statement" required=true className="outcome-statement" editable=editable /]
    </div>
    <div class="row form-group">
      [#-- Target Year --]
      <div class="col-md-4">[@customForm.input name="${outcomeCustomName}.year" type="text" i18nkey="outcome.targetYear"  placeholder="outcome.inputTargetYear.placeholder" className="targetYear outcomeYear" required=true editable=editable /]</div>
      [#-- Target Unit --]
      <div class="col-md-4">[@customForm.select name="${outcomeCustomName}.srfTargetUnit.id" i18nkey="outcome.selectTargetUnit"  placeholder="outcome.selectTargetUnit.placeholder" className="targetUnit" listName="targetUnitList" editable=editable  /]</div>
      [#-- Target Value --]
      <div class="col-md-4">[@customForm.input name="${outcomeCustomName}.value" type="text" i18nkey="outcome.targetValue" placeholder="outcome.inputTargetValue.placeholder" className="targetValue" required=true editable=editable /]</div>
    </div>  
    <br />
    [#-- Outcome Milestones List --]
    <h5 class="sectionSubTitle">[@s.text name="outcome.milestone.sectionTitle"/]</h5>
    <div class="milestones-list">
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
    
    [#-- Outcome Sub-IDOs List --]
    <h5 class="sectionSubTitle">[@s.text name="outcome.subIDOs.sectionTitle"/] <p class="contributioRem pull-right">Contribution <span class="value">0%</span></p></h5>
    <div class="subIdos-list">
    [#if outcome.subIdos?has_content]
      [#list outcome.subIdos as subIdo]
        [@subIDOMacro subIdo=subIdo name="${outcomeCustomName}.subIdos" index=subIdo_index /]
      [/#list]
    [#else]
      <p class="message text-center">[@s.text name="outcome.subIDOs.section.notSubIDOs.span"/]</p>
    [/#if]
    </div>
    [#-- Add Sub-IDO Button --]
    [#if editable]
    <div class="text-right">
      <div class="addSubIdo button-blue text-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addSubIDO"/]</div>
    </div>
    [/#if]
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
    <br />
    [#-- Milestone Statement --]
    <div class="form-group">
      [@customForm.textArea name="${milestoneCustomName}.title" i18nkey="outcome.milestone.statement" required=true className="milestone-statement" editable=editable /]
    </div>
    <div class="row form-group">
      [#-- Target Year --]
      <div class="col-md-4">[@customForm.input name="${milestoneCustomName}.year" type="text" showTitle=false  i18nkey="outcome.milestone.inputTargetYear.placeholder" placeholder="outcome.milestone.inputTargetYear.placeholder" className="targetYear milestoneYear" required=true editable=editable /]</div>
      [#-- Target Unit --]
      <div class="col-md-4">[@customForm.select name="${milestoneCustomName}.srfTargetUnit.id" showTitle=false placeholder="outcome.selectTargetUnit.placeholder" className="targetUnit" listName="targetUnitList" editable=editable  /]</div>
      [#-- Target Value --]
      <div class="col-md-4">[@customForm.input name="${milestoneCustomName}.value" type="text" showTitle=false placeholder="outcome.milestone.inputTargetValue.placeholder" className="targetValue" required=true editable=editable /]</div>

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
        <label for="">[@s.text name="outcome.subIDOs.inputSubIDO.label"/]:</label>
        <div class="subIdoSelected" title="${(subIdo.getSrfSubIdo().getDescription())!}">[@utils.wordCutter string=(subIdo.getSrfSubIdo().getDescription())!"<i>Select a subIDO clicking the button...</i>" maxPos=50 substr=" "/]</div>
        <input type="hidden" class="subIdoId" name="${subIDOCustomName}.srfSubIdo.id" value="${(subIdo.srfSubIdo.id)!}"/>
      </div>
      <div class="buttonSubIdo-block" >
       [#if editable]
        <div class="buttonSubIdo-content">
          <br>
          <div class="button-blue selectSubIDO" ><span class=""></span> Select a sub-IDO</div>
        </div>
      [/#if]
      </div>
        
      <div class="contributionBlock">[@customForm.input name="${subIDOCustomName}.contribution" type="text" i18nkey="outcome.subIDOs.inputContribution.label" placeholder="% of contribution" className="contribution" required=true editable=editable /]</div>
      <div class="clearfix"></div>
    </div>
    
    [#-- Assumptions List --]
    <label for="">[@s.text name="outcome.subIDOs.assumptions.label" /]</label>
    <div class="assumptions-list">
    [#if subIdo.assumptions?has_content]
      [#list subIdo.assumptions as assumption]
        [@assumptionMacro assumption=assumption name="${subIDOCustomName}.assumptions" index=assumption_index /]
      [/#list]
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
      [@customForm.input name="${assumptionCustomName}.description" type="text" showTitle=false placeholder="outcome.subIDOs.assumptions.statement" className="statement" required=true editable=editable /]
    [/#if] 
  </div>
[/#macro]