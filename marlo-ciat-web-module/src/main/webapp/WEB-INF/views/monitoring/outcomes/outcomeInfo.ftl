[#ftl]
[#assign title = "MiLE outcome information" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2","jsUri"] /]
[#assign customJS = ["${baseUrl}/js/monitoring/outcomes/outcomeInfo.js","${baseUrl}/js/global/autoSave.js" ] /]
[#assign customCSS = [""] /]
[#assign currentSection = "outcomes" /]


[#assign breadCrumb = [
  {"label":"outcomesList", "nameSpace":"/monitoring", "action":"${(centerSession)!}/monitoringOutcomesList"}
]/]



[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
<span id="programSelected" class="hidden">${selectedProgram.id}</span>

<section class="container">
  
  <article class="row" id="mainInformation">
    <div class="col-md-offset-1 col-md-10">
      [#include "/WEB-INF/views/monitoring/outcomes/submenu-outcomes.ftl" /]

      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
      [#-- Back --]
      <div class="pull-right">
        <a href="[@s.url action='${centerSession}/monitoringOutcomesList'][@s.param name="programID" value=selectedProgram.id /][/@s.url]">
          <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the outcome list
        </a>
      </div>  
      
      [#-- Outcomes List --]
      <h3 class="headTitle text-center"></h3>
      <div class="simpleBox row">
        <div class="col-md-4">
          <label for="">Research topic:  </label>
          <p>${selectedResearchTopic.researchTopic}</p>
        </div>
        <div class="col-md-6">
          <label for="">Outcome statement:  </label>
          <p>${(outcome.description)!}</p>
        </div>
        <div class="col-md-2">
          <label for="">Expected for ${(outcome.targetYear)!"null"}:  </label>
          <p>${(outcome.value)!"Not Applicable"}</p>
        </div>
      </div> 
      [#-- View Porjects contributions --]
      <button type="button" class="btn btn-default btn-xs pull-right outcomeProjects-${outcome.id}" data-toggle="modal" data-target="#outcomeProjectsModal">
        <span class="glyphicon glyphicon-pushpin"></span> View Project Contributions
      </button>
    <div class="">
      [#-- Year Tabs --]
      <ul class="nav nav-tabs" role="tablist">
        [#list outcome.monitorings as year]
          <li class="[#if year.year == action.getYear()]active[/#if]"><a href="#outcomeYear-${year.year}" role="tab" data-toggle="tab">${year.year}[#if year.year == action.getYear()] <span class="red">*</span> [/#if] </a></li>
        [/#list]
      </ul>
      [#-- Years Content --]
      <div class="tab-content col-md-12" style="border: 1px solid #e7e7e7;">
      <br />
        [#list outcome.monitorings as outcome]
        
          <div role="tabpanel" class="outcomeTab tab-pane [#if outcome.year == action.getYear()]active[/#if]" id="outcomeYear-${outcome.year}">
          [#-- element id --]
          <input type="hidden" name="outcome.monitorings[${outcome_index}].id" value="${(outcome.id)!}" />
          [#if outcome_index==0]
          <div class="col-md-2">
            [@customForm.input name="outcome.baseline" className="initialBaseLine" i18nkey="Initial Baseline" required=true editable=editable /]
          </div>
          <div class="clearfix"></div>
          [/#if]
          <div class="col-md-12">
            <h5 class="sectionSubTitle">Milestones/ progress towards your outcome target contribution:</h5>
            <div class="note left">
              When writing your narrative, please consider if you have achieved changes in Attitudes, Skills, and Knowledge. Key words to express progress  measurement that may apply to your outcome may include: Change of Practice→ Use, Adaptation, Adoption → Sustainable use, Scaling out / Scaling up
            </div>
            <br />
            [#-- MILESTONE LIST --]
            <div class="milestoneList simpleBox">
            [#if outcome.milestones?has_content]
              [#list outcome.milestones as milestone]
                [@milestoneMacro milestone=milestone name="outcome.monitorings[${outcome_index}].milestones" index=milestone_index /]
              [/#list]
            [#else]
              <p class="message text-center">[@s.text name="outcome.milestones.emptyMessage"/]</p>
            [/#if]
            </div>
            [#if editable]
            <div class="note left">
            If you want to report on milestones that were not previously defined in the Impact Pathway, please click on the button below:
            </div>
            <div class="text-center">
              <div class="button-green addMilestone"><span class="glyphicon glyphicon-plus-sign"></span>[@s.text name="Add a milestone" /]</div>
            </div>
            [/#if]
          </div>
            [#-- Milestone narrative --]
            <div class="col-md-12 form-group">
            <h5 class="sectionSubTitle">Progress towards your long-term outcome target contribution:</h5>
            <div class="form-group" style="margin-top: 15px;">
              [@customForm.textArea name="outcome.monitorings[${outcome_index}].narrative" i18nkey="outcome.narrative.longTerm" required=true className="outcome-narrative limitWords-100" editable=editable /]
            </div> 
            </div>
            <br />
            [#-- EVIDENCE OF USE --]
            <div class="col-md-12 form-group">
              <h5 class="sectionSubTitle">Evidence of use:</h5>
                [#-- EVIDENCE LIST --]
              <div class="evidenceList simpleBox">
              [#if outcome.evidences?has_content]
                [#list outcome.evidences as evidence]
                  [@evidenceMacro evidence=evidence name="outcome.monitorings[${outcome_index}].evidences" index=evidence_index /]
                [/#list]
              [#else]
                <p class="message text-center">[@s.text name="There are not Evicences associated to this outcome as of yet"/]</p>
              [/#if]
              
              </div>
              [#if editable]
              <div class="text-center">
                <div class="button-green addEvidence"><span class="glyphicon glyphicon-plus-sign"></span>[@s.text name="Add a evidence" /]</div>
              </div>
              [/#if]
            </div>
          
          </div>
        [/#list] 
      </div>
    </div>
      
         
      <div class="clearfix"></div>
      [#-- Section Buttons & hidden inputs--]
      [#include "/WEB-INF/views/impactPathway/buttons-impactPathway-outcome.ftl" /]
    </div>
    [/@s.form] 
  </article>
</section>

[#-- Outcome Projects Popup --]
[#include "/WEB-INF/global/macros/outcomeProjectsPopup.ftl" /]

[#-- Bilateral Co-Funded Project Popup --]
[#include "/WEB-INF/global/macros/milestonePopup.ftl"]

[#-- Milestone macro --]
[@milestoneMacro milestone={} name="outcome.monitorings[-1].milestones" index=-1 isTemplate=true /]

[#-- Evidence macro --]
[@evidenceMacro evidence={} name="outcome.monitorings[-1].evidences" index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro milestoneMacro milestone name index isTemplate=false]
  [#local editable = ((editable) && (milestone.researchMilestone.active))!true /]
  [#assign milestoneCustomName = "${name}[${index}]" /]
  <div id="milestone-${isTemplate?string('template', index)}" class="milestone borderBox" style="display:${isTemplate?string('none','block')}">
    <div class="leftHead green sm">
      <span class="index">${index+1}</span>
      <span >[@s.text name="outcome.milestone.index.title"/]</span>
    </div>
    [#-- element id --]
     <input type="hidden" class="elementId" name="${milestoneCustomName}.id" value="${(milestone.id)!}" />
     <input type="hidden" class="mileStoneId" name="${milestoneCustomName}.researchMilestone.id" value="${(milestone.researchMilestone.id)!}"/>
    [#-- Remove Button --]
    [#if editable=!editable]
      <div class="removeMilestone removeElement sm" title="Remove Milestone"></div>
    [/#if]
    
    [#-- Milestone Statement --]
    <div class="form-group" style="margin-top: 15px;">
      [@customForm.textArea name="${milestoneCustomName}.researchMilestone.title" i18nkey="outcome.milestone.index.statement" required=true className="milestone-statement limitWords-50" editable=false /]
    </div>
    
    <div class="row form-group target-block">   
      <div class="col-md-3 ">
        [@customForm.input name="${milestoneCustomName}.researchMilestone.targetUnit.name" i18nkey="Target Unit" className="milestone-targetYear" required=false editable=false /]
      </div> 
      <div class="col-md-3 col-md-offset-3">
      [#if ((milestone.researchMilestone??) && !(milestone.researchMilestone.targetUnit.id == -1))!false]
        [@customForm.input name="${milestoneCustomName}.researchMilestone.value" i18nkey="Expected Value" className="milestone-targetYear" required=false editable=false /]
      [/#if]
      </div>
    </div>
    
    <div class="row form-group target-block">      
      [#-- Target Unit --]
      <div class="col-md-3 ">
        [@customForm.input name="${milestoneCustomName}.researchMilestone.targetYear" i18nkey="Expected completion year" className="milestone-targetYear" required=false editable=false /]
      </div>
      <div class="col-md-3 col-md-offset-3">
      [#if ((milestone.researchMilestone??)&&!(milestone.researchMilestone.targetUnit.id==-1))!false]
        [@customForm.input name="${milestoneCustomName}.achievedValue" i18nkey="Achieved value" className="achieved"  required=false editable=editable /]
      [/#if]
      </div>
      <div class="col-md-3 note center" style="display:${((milestone.researchMilestone.active)!true)?string("none","block")};">
      <span>This milestone was removed from impactPathway</span>
      </div>
    </div>
    
    [#-- Milestone narrative --]
    <div class="form-group" style="margin-top: 15px;">
      [@customForm.textArea name="${milestoneCustomName}.narrative" i18nkey="outcome.milestone.index.narrative" required=true className="milestone-narrative limitWords-100" editable=editable /]
    </div> 
    
  </div>
[/#macro]

[#macro evidenceMacro evidence name index isTemplate=false]
  [#assign evidenceCustomName = "${name}[${index}]" /]
  <div  id="evidence-${isTemplate?string('template', index)}" class="evidence simpleBox" style="display:${isTemplate?string('none','block')}" >
    [#-- element id --]
    <input type="hidden" name="${evidenceCustomName}.id" value="${(evidence.id)!}" />
    <div class="removeEvidence removeElement sm" title="Remove evidence"></div>
    [@customForm.input name="${evidenceCustomName}.evidenceLink" i18nkey="Evidence" placeholder="Link" className="link" required=true editable=editable /]
  </div>
[/#macro]
