[#ftl]
[#assign title = "Outcome" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${outcomeID}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs","select2"] /]
[#assign customJS = ["${baseUrlMedia}/js/global/usersManagement.js", "${baseUrlMedia}/js/impactPathway/outcome.js", "${baseUrlMedia}/js/global/fieldsValidation.js", "${baseUrlMedia}/js/global/autoSave.js"] /]
[#assign customCSS = ["${baseUrlMedia}/css/global/customDataTable.css","${baseUrlMedia}/css/impactPathway/outcomeList.css"] /]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "outcomes" /]

[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"", "action":"topics"},
  {"label":"outcomesList", "nameSpace":"", "action":"outcomesList"},
  {"label":"outcome", "nameSpace":"", "action":""}
]/]
[#assign leadersName = "leaders"/]
[#include "/WEB-INF/center/global/pages/header.ftl" /]
[#include "/WEB-INF/center/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/center/global/macros/utils.ftl" as utils /]
[#import "/WEB-INF/center/views/impactPathway/outcomeListTemplate.ftl" as outcomesList /]
[#--  Research Otcomes Help Text--] 
[@utils.helpInfos hlpInfo="researchOutcomes.help" /]
[#--  marlo cluster of activities--]
<section class="marlo-content">
  <div class="container">
  
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/center/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Impact pathway sub menu --]
        [#include "/WEB-INF/center/views/impactPathway/submenu-impactPathway-outcome.ftl" /]
        [#-- Section Messages --]
        [#include "/WEB-INF/center/views/impactPathway/messages-impactPathway-outcome.ftl" /]

        <span id="programSelected" class="hidden">${(selectedProgram.id)!}</span>
        [#-- Impact pathway sub menu --]
        <div class="simpleBox col-md-12">
            <label for="">Research Topic:  </label>
            <p>${selectedResearchTopic.researchTopic}</p>
         </div>
        
        <br />
        <br />
        
        [#-- Back --]
        <h5 class="pull-right">
          <a href="[@s.url action='${centerSession}/outcomesList'][@s.param name="programID" value=programID /][@s.param name="topicID" value=topicID /][@s.param name="edit" value=true /][/@s.url]">
            <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the outcomes list
          </a>
        </h5>
        
        <div class="clearfix"></div>
        
        [@s.form action=actionName enctype="multipart/form-data" ]
        
        <div class="borderBox">
          <h5 class="sectionSubTitle"> Outcome Information</h5>
          [#assign outcomeCustomName= "outcome" /]
          
          [#-- Research impact --]
          <div class="form-group">
            [@customForm.select name="${outcomeCustomName}.researchImpact.id"  i18nkey="outcome.researchImpact" listName="researchImpacts" keyFieldName="id"  displayFieldName="description" required=true  className=""  editable=editable/]
          </div>
          [#-- Outcome Statement --]
          <div class="form-group">
            [@customForm.textArea name="${outcomeCustomName}.description"  i18nkey="outcome.statement" required=false className="outcome-statement limitWords-50" editable=editable /]
          
            <div class="row">
              <div class="col-sm-7">[@customForm.input name="${outcomeCustomName}.shortName" i18nkey="outcome.shortName" className="limitChar-30" required=false editable=editable /]</div>       
            </div> 
          </div>
          <div class="row form-group target-block">            
            [#-- Target Unit --]
            <div class="col-md-4">
              [@customForm.select name="${outcomeCustomName}.targetUnit.id" i18nkey="outcome.selectTargetUnit"  placeholder="outcome.selectTargetUnit.placeholder" className="targetUnit" listName="targetUnitList" editable=editable  /]
              [#-- If you dont find the target unit in the list, please add a new one clicking here --]
              [#--  --if editable]<div class="addOtherTargetUnit text-center"><a href="#">([@s.text name = "outcomes.addNewTargetUnit" /])</a></div>[/#if --]
            </div>
            [#-- Target Value --]
            [#assign showTargetValue =  (outcome??) && (outcome.targetUnit??) && (outcome.targetUnit.id??) && (outcome.targetUnit.id != -1) /]
            <div class="col-md-4 targetValue-block" style="display:${showTargetValue?string('block', 'none')}">
              [@customForm.input name="${outcomeCustomName}.value" type="text" i18nkey="outcome.targetValue" placeholder="outcome.inputTargetValue.placeholder" className="targetValue" required=true editable=editable /]
            </div> 
            [#-- Target Year --]            
            <div class="col-md-4">
              [#if editable]
                [@customForm.select name="${outcomeCustomName}.targetYear" value="${(outcome.targetYear)!'none'}" stringKey=false label=""  i18nkey="outcome.year" listName="allYears"  required=true  className="yearExpected outcomeYear" header=true placeholder="Select a Year..."  editable=editable/]
              [#else]
                <div class="select">
                  <label for=""> [@s.text name="outcome.year" /]:  </label>
                  <div class="selectList"><p> [#if outcome.targetYear?has_content][#if outcome.targetYear == -1] Not Selected [#else]${(outcome.targetYear)!'none'}[/#if][#else]Not Selected[/#if] </p></div> 
                </div>
              [/#if]
            </div>
          </div>   
          <br />
          <br />
          [#-- Outcome Milestones List --]
          <h5 class="sectionSubTitle"> Outcome Milestones</h5>
          <div class="milestones-list" listname="${outcomeCustomName}.milestones">
          [#if outcome.milestones?has_content]
            [#list outcome.milestones as milestone]
              [@milestoneMacro milestone=milestone name="${outcomeCustomName}.milestones" index=milestone_index /]
            [/#list]
          [#else]
            [@milestoneMacro milestone={} name="${outcomeCustomName}.milestones" index=0 /]          
          [/#if]
          </div>
          [#-- Add Milestone Button --]
          [#if editable]
          <div class="text-right">
            <div class="addMilestone button-blue"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addMilestone"/]</div>
          </div>
          [/#if]
          
        </div>
          
        [#-- Section Buttons--]
        [#include "/WEB-INF/center/views/impactPathway/buttons-impactPathway-outcome.ftl" /]
        

          
        [/@s.form]
        
      </div>
    </div>
    
  </div>
</section>

[#-- Milestone Template --]
[@milestoneMacro milestone={} name="${outcomeCustomName}.milestones" index=-1 isTemplate=true /]

[#include "/WEB-INF/center/global/pages/footer.ftl" /]


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
      [@customForm.textArea name="${milestoneCustomName}.title" i18nkey="outcome.milestone.index.statement" required=true className="milestone-statement limitWords-50" editable=editable /]
    </div>
    <div class="row form-group target-block">      
      [#-- Target Unit --]
      <div class="col-md-4">
        [@customForm.select name="${milestoneCustomName}.targetUnit.id"  i18nkey="outcome.milestone.index.selectTargetUnit" placeholder="outcome.selectTargetUnit.placeholder" className="targetUnit" listName="targetUnitList" editable=editable  /]
        [#--  --if editable]<div class="addOtherTargetUnit text-center"><a href="#">([@s.text name = "outcomes.addNewTargetUnit" /])</a></div>[/#if--]
      </div>
      [#-- Target Value --]
      [#local showTargetValue = (milestone??) && (milestone.targetUnit??) && (milestone.targetUnit.id??) && (milestone.targetUnit.id != -1) /]
      <div class="col-md-4 targetValue-block" style="display:${showTargetValue?string('block', 'none')}">
        [@customForm.input name="${milestoneCustomName}.value" type="text"  i18nkey="outcome.milestone.index.inputTargetValue" placeholder="outcome.milestone.index.placeholder" className="targetValue" required=true editable=editable /]
      </div>
      [#-- Target Year --]
      <div class="col-md-4">
        [#if editable]
          [#-- This select is changed by the Javascript --]
          [@customForm.select name="${milestoneCustomName}.targetYear"  i18nkey="outcome.milestone.index.inputTargetYear" listName="allYears"  required=true  className=" targetYear milestoneYear" header=true placeholder="Select a Year..."  disabled=!editable/]
        [#else]
          <div class="select">
            <label for=""> [@s.text name="outcome.milestone.index.inputTargetYear" /]:  </label>
            <div class="selectList"><p> [#if milestone.targetYear?has_content][#if milestone.targetYear == -1] Not Selected [#else]${(milestone.targetYear)!'none'}[/#if][#else]Not Selected[/#if]  </p></div> 
          </div>
        [/#if]
      </div>
    </div> 
    
  </div>
[/#macro]
