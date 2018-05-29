[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"annualReport", "nameSpace":"annualReport", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}", "nameSpace":"annualReport", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "annualReport.${currentStage}" /]
[#assign customLabel= "annualReport.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="annualReport.${currentStage}.help" /]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
  [#else]
    [#-- Program (Flagships and PMU) --]
    [#include "/WEB-INF/crp/views/annualReport/submenu-annualReport.ftl" /]
    
    <div class="row">
      [#-- POWB Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/annualReport/menu-annualReport.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/annualReport/messages-annualReport.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
          
            [#-- Summarize highlights, value added and points to improve/learning points from this year on Cross-CGIAR partnerships --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.summarize" i18nkey="${customLabel}.summarize" help="${customLabel}.summarize.help" className="" helpIcon=false required=true editable=editable && PMU /]
            </div>
            
            [#-- (Flagship Form) Table H: Status of Internal (CGIAR) Collaborations ... --]
            [#if flagship]
              <div class="form-group">
                <h4 class="subTitle headTitle">[@s.text name="${customLabel}.collaboration.title" /]</h4>
                <div class="listProgramCollaborations">
                
                 [#-- REMOVE TEMPORAL LIST ASSIGN --]
                 [#assign list=[{},{},{},{}] /]
                 
                 [#if list?has_content]
                  [#list list as item]
                    [@flagshipCollaborationMacro element=item name="list" index=item_index  isEditable=editable/]
                  [/#list]
                 [/#if]
                </div>
                [#if canEdit && editable]
                <div class="text-right">
                  <div class="addProgramCollaboration bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addProgramCollaboration"/]</div>
                </div> 
                [/#if]
                
               [#-- REMOVE TEMPORAL LIST ASSIGN --]
               [#assign globalUnits=[{},{},{},{}] /]
                
                [#-- Hidden: Global Unit list for Select2 widget --]
                <ul style="display:none">
                  [#list globalUnits as globalUnit]
                    <li id="globalUnit-${globalUnit.id!}">
                      <strong>${(globalUnit.acronym)!}</strong>
                      <span class="pull-right"><i>(${(globalUnit.globalUnitType.name)!})</i> </span>
                      <p>${(globalUnit.name)!}</p>
                    </li>
                  [/#list]
                </ul>
              </div>
            [/#if]
            
            [#-- Flagships - Table H: Status of Internal(CGIAR) Collaborations --]
            [#if PMU]
            <div class="form-group margin-panel">
              <h4 class="subTitle headTitle">[@s.text name="${customLabel}.tableH.title" /]</h4>
              
              <hr />
              [@tableHMacro list=[{},{},{},{}] /]
            </div>
            [/#if]
          
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport/buttons-annualReport.ftl" /]
        [/@s.form] 
      </div> 
    </div> 
  [/#if]
</section>

[#--  Program collaboration Template --]
[@flagshipCollaborationMacro element={} name="list" index=-1 template=true /]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro flagshipCollaborationMacro element name index template=false isEditable=true]
  [#local customName = "${name}[${index}]" /]
  <div id="flagshipCollaboration-${template?string('template', index)}" class="flagshipCollaboration borderBox form-group" style="position:relative; display:${template?string('none','block')}">

    [#-- Index --]
    <div class="leftHead blue sm"><span class="index">${index+1}</span></div>
    [#-- Remove Button --]
    [#if isEditable]<div class="removeProgramCollaboration removeElement sm" title="Remove"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
    <br />

    <div class="form-group row">
      [#-- CRP/Platform --] 
      <div class="col-md-5">
        [@customForm.select name="${customName}.globalUnit.id" label="" keyFieldName="id"  displayFieldName="acronymValid" i18nkey="${customLabel}.collaboration.crp" listName="globalUnits"  required=true  className="globalUnitSelect" editable=isEditable/]
      </div>
      [#-- Flagship/Module --]
      <div class="col-md-7">
        [@customForm.input name="${customName}.flagship" i18nkey="${customLabel}.collaboration.flagship" required=true className="globalUnitPrograms" editable=isEditable /]
      </div>
    </div>
    
    [#-- Collaboration type --]
    <div class="form-group row">
      <div class="col-md-7">
        <label>[@s.text name="${customLabel}.collaboration.type" /]:[@customForm.req required=editable  /]</label><br />
        [@customForm.radioFlat id="${customName}-type-1" name="${customName}.collaborationType" label="Contribution to"     value="1" checked=(element.collaborationType == "1")!false editable=isEditable cssClass="" cssClassLabel=""/]
        [@customForm.radioFlat id="${customName}-type-2" name="${customName}.collaborationType" label="Service needed from" value="2" checked=(element.collaborationType == "2")!false editable=isEditable cssClass="" cssClassLabel=""/]
        [@customForm.radioFlat id="${customName}-type-3" name="${customName}.collaborationType" label="Both"                value="3" checked=(element.collaborationType == "3")!false editable=isEditable cssClass="" cssClassLabel=""/]
        
        [#local collaborationTypeSelected = ((element.collaborationType == "1")!false) || ((element.collaborationType == "2")!false) || ((element.collaborationType == "3")!false)]
        [#if !editable && !collaborationTypeSelected][@s.text name="form.values.fieldEmpty"/][/#if]
      </div>
      
      [#-- Status --]
      <div class="col-md-5">
        [@customForm.select name="${customName}.status" i18nkey="${customLabel}.collaboration.status" value="${(project.projectInfo.status)!}" listName="statusList"  required=true  className="" editable=isEditable/]
      </div>
    </div>
    
    [#-- Brief Description --] 
    <div class="form-group"> 
      [@customForm.textArea name="${customName}.brief" i18nkey="${customLabel}.collaboration.description" className="" required=true editable=isEditable /]
    </div>
    
  </div>
[/#macro]

[#macro tableHMacro list ]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-flagship" width="8%">[@s.text name="${customLabel}.tableH.flagship" /]</th>
        <th id="tb-crp" width="20%">[@s.text name="${customLabel}.tableH.crp" /]</th>
        <th id="tb-description" width="36%">[@s.text name="${customLabel}.tableH.description" /]</th>
        <th id="tb-relevantFP" width="36%">[@s.text name="${customLabel}.tableH.relevantFP" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        <tr>
          [#-- Flagship --]
          <td class="tb-flagship text-center">
            [#if item.liaisonInstitution?has_content]
            <span class="programTag" style="border-color:${(item.liaisonInstitution.crpProgram.color)!'#fff'}">${item.liaisonInstitution.crpProgram.acronym!''}</span>
            [/#if]
          </td>
          [#-- CRP/Platform --]
          <td class="text-center">
          [#if item.crp?has_content]
            ${item.crp}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
          [/#if]
          </td>
          [#-- Description of collaboration --]
          <td class="text-center">
          [#if item.description?has_content]
            ${item.description}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
          [/#if]
          </td>
          [#-- Relevant FP --]
          <td class="text-center">
          [#if item.relevant?has_content]
            ${item.relevant}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
          [/#if]
          </td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="4">
          <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]