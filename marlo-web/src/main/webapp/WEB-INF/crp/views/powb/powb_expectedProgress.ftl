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
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="expectedProgress.tableA.title" /]</h4>
            <hr />
            [@tableAMacro /]
          </div>
          
          [#if flagship]
            [#assign outcomesFake = [ 
              { "title": "Ourcome title fake"} 
              ]
            /]
            [#-- Flagship - Outcomes 2022 --]
            [#list outcomesFake as outcome]
              [@powbOutcomeMacro element=outcome name="" index=outcome_index /]
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

[#macro powbOutcomeMacro element name index isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  <div id="powbOutcome-${isTemplate?string('template', index)}" class="powbOutcome simpleBox" style="display:${isTemplate?string('none','block')}">
    [#-- Index --]
    <div class="leftHead gray sm"><span class="index">${index+1}</span></div>
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" >
    [#-- Title --]
    <div class="form-group grayBox"><strong>${(element.title)!}</strong></div>
  </div>
[/#macro]

[#macro tableAMacro ]
  [#assign flagships = [ 
    { "acronym": "FP1", "milestones": [
        { "title": "Milestone #1"},
        { "title": "Milestone #2"},
        { "title": "Milestone #3"},
        { "title": "Milestone #4"}
      ] 
    },
    { "acronym": "FP2", "milestones": [
        { "title": "Milestone #1"},
        { "title": "Milestone #2"},
        { "title": "Milestone #3"}
      ] 
    },
    { "acronym": "FP3", "milestones": [
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
