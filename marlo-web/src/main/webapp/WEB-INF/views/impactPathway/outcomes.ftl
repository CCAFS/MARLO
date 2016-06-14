[#ftl]
[#assign title = "Impact Pathway - Outcomes" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [ "${baseUrl}/js/impactPathway/outcomes.js" ] /]
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
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]  
        
        <h4 class="sectionTitle">Flagship {0} - Outcomes </h4>
        <div class="outcomes-list">
          [#assign outcomes= [
            {
              'name': 'Outcome #1', 
              'milestones': [
                {}
              ],
              'subIdos': [
                { 'assumptions': [ '1', '2'] }
              ]
            }
          ]  /]
          [#list outcomes as outcome]
            [@outcomeMacro outcome=outcome name="outcome" outcome_index=outcome_index /]
          [/#list]
        </div>
        <div class="buttons">
          [@s.submit type="button" name="save" cssClass=""][@s.text name="form.buttons.save" /][/@s.submit]
        </div>
        
        [/@s.form]
      </div>
    </div>
  </div>
</section>

[#-- Templates --]
[@assumptionMacro assumption={} name="" assumption_index=0 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#----------------------------------- Outcomes Macros -------------------------------------------]
[#macro outcomeMacro outcome name outcome_index isTemplate=false]
  [#assign outcomeCustomName = "${name}[${outcome_index}]" /]
  <div class="outcome form-group borderBox">
    <div class="leftHead">
      <span class="index">${outcome_index+1}</span>
      <span class="elementId">Outcome #${outcome_index+1}</span>
    </div>
    [#-- Remove Button --]
    <div class="removeElement" title="Remove Outcome"></div>
    <br />
    [#-- Outcome Statement --]
    <div class="form-group">
      [@customForm.textArea name="${outcomeCustomName}.statement" i18nkey="Outcome Statement" required=true className="outcome-statement" editable=true /]
    </div>
    <div class="row form-group">
      [#-- Target Value --]
      <div class="col-md-4">[@customForm.input name="${outcomeCustomName}.value" type="text" showTitle=false placeholder="Target Value" className="targetValue" required=true editable=true /]</div>
      [#-- Target Year --]
      <div class="col-md-4">[@customForm.input name="${outcomeCustomName}.year" type="text" showTitle=false placeholder="Target Year" className="targetYear" required=true editable=true /]</div>
      [#-- Target Unit --]
      <div class="col-md-4">[@customForm.select name="${outcomeCustomName}.unit" showTitle=false placeholder="Select a target Unit..." listName="" editable=true  /]</div>
    </div>  
    
    [#-- Outcome Milestones List --]
    <h5 class="sectionSubTitle">Milestones</h5>
    [#list outcome.milestones as milestone]
      [@milestoneMacro milestone=milestone name="${outcomeCustomName}.milestones" milestone_index=milestone_index /]
    [/#list]
    [#-- Add Milestone Button --]
    <div class="text-right">
      <div class="button-blue"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add a Milestone</div>
    </div>
    
    [#-- Outcome Sub-IDOs List --]
    <h5 class="sectionSubTitle">Sub-IDOs</h5>
    [#list outcome.subIdos as subIdo]
      [@subIDOMacro subIdo=subIdo name="${outcomeCustomName}.subIdos" subIdo_index=subIdo_index /]
    [/#list]
    [#-- Add Sub-IDO Button --]
    <div class="text-right">
      <div class="button-blue text-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add a Sub-IDO</div>
    </div>
  </div>
[/#macro]


[#macro milestoneMacro milestone name milestone_index isTemplate=false]
  [#assign milestoneCustomName = "${name}[${milestone_index}]" /]
  <div class="milestone simpleBox">
    <div class="leftHead blue">
      <span class="index">${milestone_index+1}</span>
      <span class="elementId">Milestone #${milestone_index+1}</span>
    </div>
    [#-- Remove Button --]
    <div class="removeElement" title="Remove Milestone"></div>
    <br />
    [#-- Milestone Statement --]
    <div class="form-group">
      [@customForm.textArea name="${milestoneCustomName}.statement" i18nkey="Milestone Statement" required=true className="milestone-statement" editable=true /]
    </div>
    <div class="row form-group">
      [#-- Target Value --]
      <div class="col-md-4">[@customForm.input name="${milestoneCustomName}.value" type="text" showTitle=false placeholder="Target Value" className="targetValue" required=true editable=true /]</div>
      [#-- Target Year --]
      <div class="col-md-4">[@customForm.input name="${milestoneCustomName}.year" type="text" showTitle=false placeholder="Target Year" className="targetYear" required=true editable=true /]</div>
      [#-- Target Unit --]
      <div class="col-md-4">[@customForm.select name="${milestoneCustomName}.unit" showTitle=false placeholder="Select a target Unit..." listName="" editable=true  /]</div>
    </div>
  </div>
[/#macro]


[#macro subIDOMacro subIdo name subIdo_index isTemplate=false]
  [#assign subIDOCustomName = "${name}[${subIdo_index}]" /]
  <div class="sub-ido simpleBox">
    <div class="leftHead blue">
      <span class="index">${subIdo_index+1}</span>
      <span class="elementId">Sub-IDO #${subIdo_index+1}</span>
    </div>
    [#-- Remove Button --]
    <div class="removeElement" title="Remove Sub IDO"></div>
    <br />
    <div class="row form-group">
      <div class="col-md-4">[@customForm.select name="" i18nkey="IDO" placeholder="Select an IDO..." listName="" required=true editable=true  /]</div>
      <div class="col-md-4">[@customForm.select name="${subIDOCustomName}.subIdo" i18nkey="SubIDO" placeholder="Select a Sub-IDO..." listName="" required=true editable=true  /]</div>
      <div class="col-md-4">[@customForm.input name="${subIDOCustomName}.contribution" type="text" i18nkey="Contribution" placeholder="% of contribution" className="contribution" required=true editable=true /]</div>
    </div>
    <label for="">Assumptions:</label>
    <div class="assumptions">
      [#list subIdo.assumptions as assumption]
        [@assumptionMacro assumption=assumption name="${subIDOCustomName}.assumptions" assumption_index=assumption_index /]
      [/#list]
    </div>
    [#-- Add Assumption Button --]
    <div class="button-green text-right">
      <p><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add an assumption</p>
    </div>
  </div>
[/#macro]

[#macro assumptionMacro assumption name assumption_index isTemplate=false]
  [#assign assumptionCustomName = "${name}[${assumption_index}]" /]
  <div id="assumption-${isTemplate?string('template', assumption_index)}" class="assumption form-group" style="display:${isTemplate?string('none','block')}">
    <input type="hidden" name="${assumptionCustomName}.id" value="-1"/>
    [@customForm.input name="${assumptionCustomName}.statement" type="text" showTitle=false placeholder="Assumption statement #${assumption_index+1}" className="" required=true editable=true /]
  </div>
[/#macro]



