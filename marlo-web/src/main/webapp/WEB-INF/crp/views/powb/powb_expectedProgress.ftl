[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
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
            [@customForm.textArea name="liaisonInstitution.powb.expectedHighlights" help="liaisonInstitution.powb.expectedHighlights.help" paramText="${(actualPhase.year)!}" required=true className="limitWords-100" editable=editable /]
          </div>
          [/#if]
          
          [#-- Table A: Planned Milestones 2018 --]
          [#if PMU]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="expectedProgress.tableA.title" /]</h4>
            <hr />
            [@tableAMacro /]
          </div>
          [/#if]
          
          
          [#if flagship]
            [#assign flagshipOutcomes = [ 
              { "title": " # of policy decisions taken (in part) based on engagement and information dissemination by CCAFS", 
                "milestones": [
                  { "title": "Training materials are developed and workshops held to strengthen national/state capacities for scenario-based strategic planning, as well as targeted materials for other partner organisations (e.g. NGOs) developed (linked to CoA 1.2)"},
                  { "title": " National/state level decision makers supported in developing CSA investment portfolios for international climate finance providers that meet funding requirements and are informed by CCAFS science; decision makers made aware..."}
                ] 
              },
              { "title": "Outcome #2", 
                "milestones": [
                  { "title": "Milestone #1"}
                ] 
              },
              { "title": "Outcome #3", 
                "milestones": [
                  { "title": "Milestone #1"}
                ] 
              }
              ]
            /]
            
            [#-- Flagship - Outcomes 2022 --]
            [#-- <h4 class="sectionSubTitle">[@s.text name="expectedProgress.flagshipOutcomes"][@s.param]${(liaisonInstitution.crpProgram.acronym)!liaisonInstitution.acronym}[/@s.param][/@s.text]</h4> --]
            [#list flagshipOutcomes as outcome]
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

[#macro tableAMacro ]
  [#assign flagships = [ 
    { "acronym": "FP1", 
      "milestones": [
        { "title": "Milestone #1"},
        { "title": "Milestone #2"},
        { "title": "Milestone #3"},
        { "title": "Milestone #4"}
      ] 
    },
    { "acronym": "FP2", 
      "milestones": [
        { "title": "Milestone #1"},
        { "title": "Milestone #2"},
        { "title": "Milestone #3"}
      ] 
    },
    { "acronym": "FP3", 
      "milestones": [
        { "title": "Milestone #1"},
        { "title": "Milestone #2"},
        { "title": "Milestone #3"}
      ] 
    }
    ]
  /]

  <div class="table-responsive">
    <table class="table table-bordered">
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
          [#list fp.milestones as milestone]
            <tr>
              [#if milestone_index == 0]<th rowspan="${milestoneSize}" > ${fp.acronym}</th>[/#if]
              <td> <i>Pre-filled</i> </td>
              <td> <i>Pre-filled</i> </td>
              <td> <i>${milestone.title}</i> </td>
              [#if milestone_index == 0]<td rowspan="${milestoneSize}"> <i>Pre-filled</i> </td>[/#if]
              [#if milestone_index == 0]<td rowspan="${milestoneSize}"> <i>Pre-filled</i> </td>[/#if]
              <td> FL to fill this info. </td>
              <td> FL to fill this info. </td>
            </tr>
          [/#list]
        [/#list]
      </tbody>
    </table>
  </div>
[/#macro]


[#macro powbOutcomeMacro element name index isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  <div id="powbOutcome-${isTemplate?string('template', index)}" class="powbOutcome simpleBox" style="display:${isTemplate?string('none','block')}">
    [#-- Index --]
    <div class="leftHead sm"><span class="index">${index+1}</span></div>
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" >
    [#-- Title --]
    <div class="form-group grayBox"><strong>${(liaisonInstitution.crpProgram.acronym)!liaisonInstitution.acronym} Outcome: </strong> ${(element.title)!}</div>
    
    [#-- Milestones List --]
    <div class="form-group">
      [#list element.milestones as milestone]
        [@powbMilestoneMacro element=milestone name="${customName}.milestones" index=milestone_index /]
      [/#list]
    </div>
    
  </div>
[/#macro]

[#macro powbMilestoneMacro element name index isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  <div id="powbMilestone-${isTemplate?string('template', index)}" class="powbMilestone simpleBox" style="display:${isTemplate?string('none','block')}">
    [#-- Index --]
    <div class="leftHead gray sm"><span class="index">${index+1}</span></div>
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" >
    [#-- Title --]
    <div class="form-group row">
      <div class="col-md-9"><strong>Milestone for ${actualPhase.year}</strong> - ${(element.title)!}</div>
      <div class="col-md-3">[@milestoneContributions element=element /]</div>
    </div>
    
    [#-- Assessment of risk to achievement --]
    <div class="form-group">
      <p><label>[@s.text name="liaisonInstitution.powb.milestone.assessment" /] [@customForm.req required=editable  /]</label></p>
      [#if editable]
        [@customForm.radioFlat id="${customName}-risk-1" name="${customName}.assessment" label="Low"    value="1" checked=false cssClass="" cssClassLabel=""/]
        [@customForm.radioFlat id="${customName}-risk-2" name="${customName}.assessment" label="Medium" value="2" checked=false cssClass="" cssClassLabel=""/]
        [@customForm.radioFlat id="${customName}-risk-3" name="${customName}.assessment" label="High"   value="3" checked=false cssClass="" cssClassLabel=""/]
      [#else]
        
      [/#if]
    </div>
    
    [#-- Means of verification --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.meansVerifications" i18nkey="liaisonInstitution.powb.milestone.meansVerifications" help="" display=true required=true className="limitWords-100" editable=editable /]
    </div>
    
  </div>
[/#macro]

[#macro milestoneContributions element]
<!-- Button trigger modal -->
<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#myModal">
  [@s.text name="expectedProgress.milestonesContributions" /]
</button>

<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Modal title</h4>
      </div>
      <div class="modal-body">
        ...
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Save changes</button>
      </div>
    </div>
  </div>
</div>
[/#macro]
