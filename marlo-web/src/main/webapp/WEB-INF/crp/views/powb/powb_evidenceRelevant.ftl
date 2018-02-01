[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = ["${baseUrlMedia}/js/projects/projectExpectedStudies.js"] /]
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
          <div class="form-group evidence-panel">
            [#-- Change display=true for display=PMU to show just for PMU --]
            [@customForm.textArea name="evidenceRelevant.narrative" help="evidenceRelevant.help" display=true required=true className="limitWords-100" editable=editable /]
          </div>
          <div class="form-group evidence-panel">
            [#-- Table Title --]
            <h4 class="subTitle headTitle">[@s.text name="evidenceRelevant.table.title" /]</h4>
            <hr />
            [@tableBMacro/]
          </div>
          <div class="form-group evidence-panel">
            [#-- Planned Studies Title --]
            <div class="deliverables-table-header">
              <h4 class="subTitle headTitle">[@s.text name="evidenceRelevant.plannedStudies" /]</h4>
              <span class=".flagship-planned-studies-button" data-toggle="modal" data-target=".flagship-planned-studies-modal">[@s.text name="evidenceRelevant.plannedStudies.projectPlannedStudies" /]</span>
            </div>
            [@plannedStudiesMacro /]
            [#if canEdit && editable]
              <div class="text-right">
                <div class="addExpectedStudy bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addPlannedTopicStudy"/]</div>
              </div> 
            [/#if]
          </div>
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]

[#macro plannedStudiesMacro isEditable=true]
  <div id="plannedStudies" class="plannedStudies borderBox form-group" style="position:relative; display:block">
    
    [#-- Index --]
    <div class="leftHead"><span class="index">1</span></div>
    [#-- Remove Button --]
    [#if isEditable]<div class="removePlannedStudies removeElement" title="Remove Planned Studie"></div>[/#if]
    [#-- Hidden inputs --]
    [#-- <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> --]
    <br />
    
    <div class="form-group row"> 
      [#-- Planned topic of study --] 
      <div class="col-md-7">
        [@customForm.input name="" i18nkey="evidenceRelevant.pannedStudies.plannedTopic" placeholder="" className="" required=true editable=isEditable/]
      </div>
      [#-- Geographic Scope --]
      <div class="col-md-5">
        [@customForm.select name="" label=""  i18nkey="evidenceRelevant.pannedStudies.geographicScope" listName="scopes"  required=true  className="" editable=isEditable/]
      </div>
    </div>
    
    [#-- Relevant to Sub-IDO --] 
    <div class="form-group "> 
      [@customForm.select name="" label=""  i18nkey="evidenceRelevant.pannedStudies.relevant" listName="subIdos"  required=true  className="" editable=isEditable/]
    </div>
    
    [#-- SRF target if appropriate --] 
    <div class="form-group "> 
      [@customForm.select name="" label=""  i18nkey="evidenceRelevant.pannedStudies.srfTarget" listName="targets"  required=false  className="" editable=isEditable/]
    </div>
    
    [#-- Comments --] 
    <div class="form-group"> 
      [@customForm.textArea name="" i18nkey="evidenceRelevant.pannedStudies.comments"  placeholder="" className="limitWords-100" required=true editable=isEditable /]
    </div>
    
    <div class="clearfix"></div>
    
  </div>
[/#macro]

[#macro tableBMacro]
  <table class="table-plannedStudies" id="table-plannedStudies">
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
    [#-- if deliverables?has_content 1--]
      [#-- list deliverables as deliverable 2--]
        <tr>
          [#-- FP --]
          <td class="tb-fp text-center">
            <a href="[#-- @s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url--]">
              [#-- F${deliverable.id} --]
              F123
            </a>
          </td>
          [#-- Planned topic of study --]
          <td class="left">
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
          </td>
          [#-- Geographic scope --]
          <td >
            Sub-national: Single district or municipality
          </td>
          [#-- Relevant to Sub-IDO, or SRF target if appropiate --]
          <td class="">
            <ul>
              <li>[@utilities.wordCutter string="Increased capacity for innovation in partner development organizations and in poor and vulnerable communities" maxPos=50 /]</li>
              <li>[@utilities.wordCutter string="# of more people, of which 50% are women, without deficiencies of one or more of the following essentials micronutrients: iron, zinc, iodine, vitamin A, folate and vitamin B12" maxPos=50 /]</li>
            </ul>
          </td>
          [#-- Comments --]
          <td class="fair text-center"> 
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.Lorem ipsum dolor sit amet, consectetur adipiscing elit.
          </td>
        </tr>
      [#-- [/#list] 2--]
      [#-- [/#if] 1--]
    </tbody>
  </table>
[/#macro]