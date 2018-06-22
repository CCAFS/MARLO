[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "datatables.net", "datatables.net-bs"  ] /]
[#assign customJS = [ 
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js",
  "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js" 
] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"annualReport", "nameSpace":"annualReport", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}", "nameSpace":"annualReport", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis.reportSynthesisCrossCgiar" /]
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
              [#if PMU]
                [@customForm.textArea name="${customName}.highlights" i18nkey="${customLabel}.summarize" help="${customLabel}.summarize.help" className="" helpIcon=false required=true editable=editable && PMU /]
              [#else]
                <div class="textArea">
                  <label for="">[@customForm.text name="${customLabel}.summarize" readText=true /]:</label>
                  <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
                </div>
              [/#if]
            </div>
            
            [#-- (Flagship Form) Table H: Status of Internal (CGIAR) Collaborations ... --]
            [#if flagship]
              <div class="form-group">
                <h4 class="subTitle headTitle">[@s.text name="${customLabel}.collaboration.title" /]</h4>
                <div class="listProgramCollaborations">                 
                 [#if reportSynthesis.reportSynthesisCrossCgiar.collaborations?has_content]
                  [#list reportSynthesis.reportSynthesisCrossCgiar.collaborations as item]
                    [@flagshipCollaborationMacro element=item name="${customName}.collaborations" index=item_index  isEditable=editable/]
                  [/#list]
                 [/#if]
                </div>
                [#if canEdit && editable]
                <div class="text-right">
                  <div class="addProgramCollaboration bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addProgramCollaboration"/]</div>
                </div> 
                [/#if]      
                
                [#-- Hidden: Global Unit list for Select2 widget --]
                <ul style="display:none">
                  [#list globalUnitList as globalUnit]
                    <li id="globalUnit-${(globalUnit.id)!}">
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
              <h4 class="subTitle headTitle">[@s.text name="${customLabel}.tableH.title" /] (${(flagshipCollaborations?size)!'0'})</h4>              
              <div class="viewMoreSyntesis-block" >
                [@tableHMacro list=flagshipCollaborations /]
              </div>
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
[@flagshipCollaborationMacro element={} name="${customName}.collaborations" index=-1 template=true /]

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
        [@customForm.select name="${customName}.globalUnit.id" label="" keyFieldName="id" displayFieldName="acronym" i18nkey="${customLabel}.collaboration.crp" listName="globalUnitList"  required=true  className="globalUnitSelect" editable=isEditable/]
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
        [#local collaborationTypeSelected = (element.repIndCollaborationType.id)!-1]
        
        [#list (collaborationList)![] as collaboration]
          [@customForm.radioFlat id="${customName}-collaboration-${collaboration_index}" name="${customName}.repIndCollaborationType.id" label="${collaboration.name}" value="${collaboration.id}" checked=(collaborationTypeSelected == collaboration.id)!false editable=isEditable cssClass="" cssClassLabel="font-normal"/]
        [/#list]
        
        [#if !editable && (collaborationTypeSelected == -1)][@s.text name="form.values.fieldEmpty"/][/#if]
      </div>
      
      [#-- Status --]
      <div class="col-md-5">
        [@customForm.select name="${customName}.status" i18nkey="${customLabel}.collaboration.status" value="${(element.status)!-1}" listName="statuses"  required=true  className="" editable=isEditable/]
      </div>
    </div>
    
    [#-- Brief Description --] 
    <div class="form-group"> 
      [@customForm.textArea name="${customName}.description" i18nkey="${customLabel}.collaboration.description" className="" required=true editable=isEditable /]
    </div>
    
  </div>
[/#macro]

[#macro tableHMacro list ]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-flagship" width="8%">FP</th>
        <th id="tb-crp" width="20%">[@s.text name="${customLabel}.tableH.crp" /]</th>
        <th id="tb-description" width="50%">[@s.text name="${customLabel}.tableH.description" /]</th>
        <th id="tb-relevantFP" width="20%">[@s.text name="${customLabel}.tableH.relevantFP" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        [#local crpProgram = (item.reportSynthesisCrossCgiar.reportSynthesis.liaisonInstitution.crpProgram)!{} ]
        <tr>
          [#-- Flagship --]
          <td class="tb-flagship text-center">
            <span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}">${(crpProgram.acronym)!}</span>
          </td>
          [#-- CRP/Platform --]
          <td class="text-center">
          [#if item.globalUnit?has_content]
            ${item.globalUnit.acronym}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
          [/#if]
          </td>
          [#-- Description of collaboration --]
          <td class="">
          [#if item.description?has_content]
            ${item.description?replace('\n', '<br>')}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
          [/#if]
          </td>
          [#-- Relevant FP --]
          <td class="text-center">
          [#if item.flagship?has_content]
            ${item.flagship}
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