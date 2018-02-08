[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${powbSynthesisID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = ["${baseUrlMedia}/js/powb/powb_evidenceRelevant.js"] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "evidenceRelevant" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"evidenceRelevant", "nameSpace":"powb", "action":"${crpSession}/evidenceRelevant"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="evidenceRelevant.help" /]
    
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
        <h3 class="headTitle">[@s.text name="evidenceRelevant.title" /]</h3>
        <div class="borderBox">
          [#-- Provide a short narrative for any outcome --]
          [#if PMU]
          <div class="form-group margin-panel">
            [#-- Change display=true for display=PMU to show just for PMU --]
            [@customForm.textArea name="powbSynthesis.powbEvidence.narrative" i18nkey="evidenceRelevant.narrative" help="evidenceRelevant.help" display=true required=true className="limitWords-100" paramText="${(actualPhase.year)!}" editable=editable /]
          </div>
          [/#if]
          
          [#-- Table B: Flagships planned Studies for Relevant Outcomes and Impacts --]
          [#if PMU]
          <div class="form-group margin-panel">
            <h4 class="subTitle headTitle">[@s.text name="evidenceRelevant.table.title" /]</h4>
            <hr />
            [@tableBMacro /]
          </div>
          [/#if]
          
          [#-- Planned Studies for Relevant Outcomes and Impacts --]
          [#if Flagship]
          <div class="form-group margin-panel">
            <div class="evidence-plannedStudies-header row">
            [#-- if powbSynthesis.powbEvidence.plannedStudies?has_content --]
              <h4 class="subTitle headTitle col-md-9">[@s.text name="evidenceRelevant.plannedStudies" /]</h4>
              <span class="flagship-planned-studies-button label label-info" data-toggle="modal" data-target=".flagship-planned-studies-modal">[@s.text name="evidenceRelevant.plannedStudies.projectPlannedStudies" /]</span>
            [#-- else ]
              <p>Prefilled if available</p>
            [/#if --]
            </div>
            [#-- Project planned studies (Modal) --]
            <div class="modal fade flagship-planned-studies-modal" tabindex="-1" role="dialog" aria-labelledby="flagship-planned-studies-modal" aria-hidden="true">
              <div class="modal-dialog modal-lg">
                <div class="modal-content">
                  <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                  <h3 class="subTitle headTitle">[@s.text name="evidenceRelevant.plannedStudies.projectPlannedStudies" /]</h3>
                  <hr />  
                  [@tablePlannedStudiesMacro/]
                </div>
              </div>
            </div>
            <div class="expectedStudies-list" listname="list-plannedStudies">
            [#if powbSynthesis.powbEvidence.plannedStudies?has_content]
              [#list powbSynthesis.powbEvidence.plannedStudies as plannedStudy]
                [@plannedStudyMacro element=plannedStudy name="powbSynthesis.powbEvidence.plannedStudies"  index=plannedStudy_index isEditable=editable/]
              [/#list]
            [#else]
              [#if !editable]<p>[@s.text name="evidenceRelevant.plannedStudies.empty" /]</p>[/#if]
            [/#if]
            </div>
            
            [#if canEdit && editable]
            <div class="text-right">
              <div class="addExpectedStudy bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addPlannedTopicStudy"/]</div>
            </div> 
            [/#if]
          </div>
          [/#if]
          
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>

[#-- Planned Study Template --]
[@plannedStudyMacro element={} name="powbSynthesis.powbEvidence.plannedStudies"  index=-1 template=true/]

[#include "/WEB-INF/crp/pages/footer.ftl"]

[#macro plannedStudyMacro element name index template=false isEditable=true]
  [#local customName = "${name}[${index}]" /]
  <div id="expectedStudy-${template?string('template', index)}" class="expectedStudy borderBox form-group" style="position:relative; display:${template?string('none','block')}">
    
    [#-- Index --]
    <div class="leftHead"><span class="index">${index+1}</span></div>
    [#-- Remove Button --]
    [#if isEditable]<div class="removeExpectedStudy removeElement" title="Remove Planned topic of study"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
    <br />
    
    
    <div class="form-group row"> 
      [#-- Planned topic of study --] 
      <div class="col-md-7">
        [@customForm.input name="${customName}.plannedTopic" i18nkey="evidenceRelevant.pannedStudies.plannedTopic" placeholder="" className="" required=true editable=isEditable/]
      </div>
      [#-- Geographic Scope --]
      <div class="col-md-5">
        [@customForm.select name="${customName}.geographicScope" label=""  i18nkey="evidenceRelevant.pannedStudies.geographicScope" listName="scopes"  required=true  className="" editable=isEditable/]
      </div>
    </div>
    
    [#-- Relevant to Sub-IDO --] 
    <div class="form-group "> 
      [@customForm.select name="${customName}.srfSubIdo.id" label=""  i18nkey="evidenceRelevant.pannedStudies.relevant" listName="subIdos"  required=true  className="" editable=isEditable/]
    </div>
    
    [#-- SRF target if appropriate --] 
    <div class="form-group "> 
      [@customForm.select name="${customName}.srfSloIndicator.id" label=""  i18nkey="evidenceRelevant.pannedStudies.srfTarget" listName="targets"  required=false  className="" editable=isEditable/]
    </div>
    
    [#-- Comments --] 
    <div class="form-group"> 
      [@customForm.textArea name="${customName}.comments" i18nkey="evidenceRelevant.pannedStudies.comments"  placeholder="" className="limitWords-100" required=true editable=isEditable /]
    </div>
    
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro tablePlannedStudiesMacro ]
  <table class="table-plannedStudies table-border-powb" id="table-plannedStudies">
    <thead>
      <tr class="subHeader">
        <th id="tb-plannedTopic" width="27%">[@s.text name="evidenceRelevant.tablePlannedStudies.plannedTopic" /]</th>
        <th id="tb-projectId" width="11%">[@s.text name="evidenceRelevant.tablePlannedStudies.projectId" /]</th>
        <th id="tb-geographicScope" width="15%">[@s.text name="evidenceRelevant.tablePlannedStudies.geographicScope" /]</th>
        <th id="tb-relevant" width="28%">[@s.text name="evidenceRelevant.tablePlannedStudies.relevant" /]</th>
        <th id="tb-comments" width="19%">[@s.text name="evidenceRelevant.tablePlannedStudies.comments" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if popUpProjects?has_content]
      [#list popUpProjects as popUp]
        <tr>
          [#-- Planned topic of study --]
          <td>
            ${(popUp.topicStudy)!''}
          </td>
          <td class="tb-projectId text-center">
            P${(popUp.project.id)!''}
          </td>
          [#-- Geographic scope --]
          <td class="text-center">
            ${popUp.scopeName}
          </td>
          [#-- Relevant to Sub-IDO, or SRF target if appropiate --]
          <td class="relevantSubIDO">
            <ul>  
              <li title="${(popUp.srfSubIdo.description)!''}">[@utilities.wordCutter string="${(popUp.srfSubIdo.description)!''}" maxPos=50 /]</li>
              [#if popUp.srfSloIndicator?has_content]
              <li title="${(popUp.srfSloIndicator.title)!''}">[@utilities.wordCutter string="${(popUp.srfSloIndicator.title)!''}" maxPos=50 /]</li>
              [/#if]
            </ul>
          </td>
          [#-- Comments --]
          <td class="comments" title="${(popUp.comments)!''}"> 
            [@utilities.wordCutter string="${(popUp.comments)!''}" maxPos=100 /]
          </td>
        </tr>
      [/#list]
    [/#if]
    </tbody>
  </table>
[/#macro]

[#macro tableBMacro]
  <table class="table-plannedStudies table-border-powb" id="table-plannedStudies">
    <thead>
      <tr class="subHeader">
        <th id="tb-fp" width="11%">[@s.text name="evidenceRelevant.table.fp" /]</th>
        <th id="tb-plannedTopic" width="27%">[@s.text name="evidenceRelevant.table.plannedTopic" /]</th>
        <th id="tb-geographicScope" width="15%">[@s.text name="evidenceRelevant.table.geographicScope" /]</th>
        <th id="tb-relevant" width="28%">[@s.text name="evidenceRelevant.table.relevant" /]</th>
        <th id="tb-comments" width="19%">[@s.text name="evidenceRelevant.table.comments" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if flagshipPlannedList?has_content]
      [#list flagshipPlannedList as flagshipPlanned]
        <tr>
          [#-- FP --]
          <td class="tb-fp text-center">
            <span class="programTag" style="border-color:${(flagshipPlanned.powbEvidence.powbSynthesis.liaisonInstitution.crpProgram.color)!'#fff'}">
              ${flagshipPlanned.powbEvidence.powbSynthesis.liaisonInstitution.crpProgram.acronym}
            </span>
          </td>
          [#-- Planned topic of study --]
          <td>
            ${(flagshipPlanned.plannedTopic)!''}
          </td>
          [#-- Geographic scope --]
          <td class="text-center">
            ${flagshipPlanned.scopeName}
          </td>
          [#-- Relevant to Sub-IDO, or SRF target if appropiate --]
          <td class="relevantSubIDO">
            <ul>
              [#if flagshipPlanned.srfSubIdo?has_content && flagshipPlanned.srfSloIndicator?has_content][#assign maxPosition=50][#else][#assign maxPosition=100][/#if]
              <li title="${(flagshipPlanned.srfSubIdo.description)!''}">[@utilities.wordCutter string="${(flagshipPlanned.srfSubIdo.description)!''}" maxPos=maxPosition /]</li>
              [#if flagshipPlanned.srfSloIndicator?has_content]
              <li title="${(flagshipPlanned.srfSloIndicator.title)!''}">[@utilities.wordCutter string="${(flagshipPlanned.srfSloIndicator.title)!''}" maxPos=maxPosition /]</li>
              [/#if]
            </ul>
          </td>
          [#-- Comments --]
          <td class="comments" title="${(flagshipPlanned.comments)!''}"> 
            [@utilities.wordCutter string="${(flagshipPlanned.comments)!''}" maxPos=100 /]
          </td>
        </tr>
      [/#list]
    [/#if]
    </tbody>
  </table>
[/#macro]