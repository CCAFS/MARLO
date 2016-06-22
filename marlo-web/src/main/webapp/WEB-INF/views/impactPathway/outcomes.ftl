[#ftl]
[#assign title = "Impact Pathway - Outcomes" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [ "${baseUrl}/js/impactPathway/outcomes.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/impactPathway/outcomes.css" ] /]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "outcomes" /]

[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"", "action":"outcomes"},
  {"label":"outcomes", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


<section class="marlo-content">
  <div class="container">
    [#-- Program (Flagships) --]
    <ul id="liaisonInstitutions" class="horizontalSubMenu">
      [#list programs as program]
        [#assign isActive = (program.id == crpProgramID)/]
        <li class="${isActive?string('active','')}">
          <a href="[@s.url][@s.param name ="crpProgramID"]${program.id}[/@s.param][@s.param name ="edit"]true[/@s.param][/@s.url]">Flagship ${program.acronym}</a>
        </li>
      [/#list]
    </ul>
  </div>
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]  
        [#-- Outcomes List --]
        <h4 class="sectionTitle">Outcomes</h4>
        <div class="outcomes-list">
          [#list outcomes as outcome]
            [@outcomeMacro outcome=outcome name="outcomes" index=outcome_index /]
          [/#list]
        </div>
        [#-- Add Outcome Button --]
        <div class="addOutcome bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add an Outcome</div>
        
        [#-- Save Button --]
        <div class="buttons">
          [@s.submit type="button" name="save" cssClass=""][@s.text name="form.buttons.save" /][/@s.submit]
        </div>
        <input type="hidden"  name="crpProgramID" value="${(crpProgramID)!}"/>
        [/@s.form]
      </div>
    </div>
  </div>
</section>

[#-- Outcome Template --]
[@outcomeMacro outcome={} name="" index=0 isTemplate=true /]

[#-- Milestone Template --]
[@milestoneMacro milestone={} name="" index=0 isTemplate=true /]

[#-- Sub-Ido Template --]
[@subIDOMacro subIdo={} name="" index=0 isTemplate=true /]

[#-- Assumption Template --]
[@assumptionMacro assumption={} name="" index=0 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#----------------------------------- Outcomes Macros -------------------------------------------]
[#macro outcomeMacro outcome name index isTemplate=false]
  [#assign outcomeCustomName= "${name}[${index}]" /]
  <div id="outcome-${isTemplate?string('template', index)}" class="outcome form-group borderBox" style="display:${isTemplate?string('none','block')}">
    <div class="leftHead">
      <span class="index">${index+1}</span>
      <span class="elementId">${(selectedProgram.acronym)!} - Outcome</span>
    </div>
    [#-- Outcome ID Parameter --]
    <input type="hidden" class="outcomeId" name="${outcomeCustomName}.id" value="${(outcome.id)!}"/>
    [#-- Remove Button --]
    <div class="removeOutcome removeElement" title="Remove Outcome"></div>
    <br />
    [#-- Outcome Statement --]
    <div class="form-group">
      [@customForm.textArea name="${outcomeCustomName}.description" i18nkey="Outcome Statement" required=true className="outcome-statement" editable=true /]
    </div>
    <div class="row form-group">
      [#-- Target Value --]
      <div class="col-md-4">[@customForm.input name="${outcomeCustomName}.value" type="text" i18nkey="Target Value" placeholder="Value" className="targetValue" required=true editable=true /]</div>
      [#-- Target Year --]
      <div class="col-md-4">[@customForm.input name="${outcomeCustomName}.year" type="text" i18nkey="Target Year"  placeholder="Year" className="targetYear" required=true editable=true /]</div>
      [#-- Target Unit --]
      <div class="col-md-4">[@customForm.select name="${outcomeCustomName}.srfTargetUnit.id" i18nkey="Select a target Unit"  placeholder="Select a target Unit..." className="targetUnit" listName="targetUnitList" editable=true  /]</div>
    </div>  
    <br />
    [#-- Outcome Milestones List --]
    <h5 class="sectionSubTitle">Milestones</h5>
    <div class="milestones-list">
    [#if outcome.milestones?has_content]
      [#list outcome.milestones as milestone]
        [@milestoneMacro milestone=milestone name="${outcomeCustomName}.milestones" index=milestone_index /]
      [/#list]
    [#else]
      <p class="message text-center">There is not Milestones related to this Outcome</p>
    [/#if]
    </div>
    [#-- Add Milestone Button --]
    <div class="text-right">
      <div class="addMilestone button-blue"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add a Milestone</div>
    </div>
    [#-- Outcome Sub-IDOs List --]
    <h5 class="sectionSubTitle">Sub-IDOs</h5>
    <div class="subIdos-list">
    [#if outcome.subIdos?has_content]
      [#list outcome.subIdos as subIdo]
        [@subIDOMacro subIdo=subIdo name="${outcomeCustomName}.subIdos" index=subIdo_index /]
      [/#list]
    [#else]
      <p class="message text-center">There is not Sub-IDOs related to this Outcome</p>
    [/#if]
    </div>
    [#-- Add Sub-IDO Button --]
    <div class="text-right">
      <div class="addSubIdo button-blue text-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add a Sub-IDO</div>
    </div>
    <br />
  </div>
[/#macro]


[#macro milestoneMacro milestone name index isTemplate=false]
  [#assign milestoneCustomName = "${name}[${index}]" /]
  <div id="milestone-${isTemplate?string('template', index)}" class="milestone simpleBox" style="display:${isTemplate?string('none','block')}">
    <div class="leftHead green sm">
      <span class="index">${index+1}</span>
      <span class="elementId">Milestone</span>
    </div>
     <input type="hidden" class="mileStoneId" name="${milestoneCustomName}.id" value="${(milestone.id)!}"/>
    [#-- Remove Button --]
    <div class="removeMilestone removeElement sm" title="Remove Milestone"></div>
    <br />
    [#-- Milestone Statement --]
    <div class="form-group">
      [@customForm.textArea name="${milestoneCustomName}.title" i18nkey="Milestone Statement" required=true className="milestone-statement" editable=true /]
    </div>
    <div class="row form-group">
      [#-- Target Value --]
      <div class="col-md-4">[@customForm.input name="${milestoneCustomName}.value" type="text" showTitle=false placeholder="Target Value" className="targetValue" required=true editable=true /]</div>
      [#-- Target Year --]
      <div class="col-md-4">[@customForm.input name="${milestoneCustomName}.year" type="text" showTitle=false placeholder="Target Year" className="targetYear" required=true editable=true /]</div>
      [#-- Target Unit --]
      <div class="col-md-4">[@customForm.select name="${milestoneCustomName}.srfTargetUnit.id" showTitle=false placeholder="Select a target Unit..." className="targetUnit" listName="targetUnitList" editable=true  /]</div>
    </div>
  </div>
[/#macro]


[#macro subIDOMacro subIdo name index isTemplate=false]
  [#assign subIDOCustomName = "${name}[${index}]" /]
  <div id="subIdo-${isTemplate?string('template', index)}" class="subIdo simpleBox" style="display:${isTemplate?string('none','block')}">
    <div class="loading" style="display:none"></div>
    <div class="leftHead blue sm">
      <span class="index">${index+1}</span>
      <span class="elementId">Sub-IDO</span>
    </div>
    
         <input type="hidden" class="programSubIDOId" name="${subIDOCustomName}.id" value="${(subIdo.id)!}"/>

       
    [#-- Remove Button --]
    <div class="removeSubIdo removeElement sm" title="Remove Sub IDO"></div>
    <br />
    <div class="form-group">
      <div class="idoBlock">[@customForm.select name="${subIDOCustomName}.srfSubIdo.srfIdo.id" i18nkey="IDO" placeholder="Select an IDO..." listName="idoList"  className="idoId" required=true editable=true  /]</div>
      <div class="subIdoBlock">[@customForm.select name="${subIDOCustomName}.srfSubIdo.id" i18nkey="SubIDO" placeholder="Select a Sub-IDO..." listName="${subIDOCustomName}.subIdoList" className="subIdoId" disabled=(subIdo.srfSubIdo)!true required=true editable=true  /]</div>
      <div class="contributionBlock">[@customForm.input name="${subIDOCustomName}.contribution" type="text" i18nkey="Contribution" placeholder="% of contribution" className="contribution" required=true editable=true /]</div>
      <div class="clearfix"></div>
    </div>
    [#-- Assumptions List --]
    <label for="">Assumptions:</label>
    <div class="assumptions-list">
    [#if subIdo.assumptions?has_content]
      [#list subIdo.assumptions as assumption]
        [@assumptionMacro assumption=assumption name="${subIDOCustomName}.assumptions" index=assumption_index /]
      [/#list]
  
    [/#if]
    </div>
    [#-- Add Assumption Button --]
    <div class="text-right">
      <div class="addAssumption button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add an assumption</div>
    </div>
  </div>
[/#macro]

[#macro assumptionMacro assumption name index isTemplate=false]
  [#assign assumptionCustomName = "${name}[${index}]" /]
  <div id="assumption-${isTemplate?string('template', index)}" class="assumption form-group" style="position:relative; display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="removeAssumption removeIcon" title="Remove assumption"></div>
   
    [@customForm.input name="${assumptionCustomName}.statement" type="text" showTitle=false placeholder="Assumption statement #${index+1}" className="statement" required=true editable=true /]
  </div>
[/#macro]