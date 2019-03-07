[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "blueimp-file-upload", "trumbowyg" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis.reportSynthesisKeyPartnership" /]
[#assign customLabel= "annualReport2018.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="${customLabel}.help" /]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
  [#else]
    [#-- Program (Flagships and PMU) --]
    [#include "/WEB-INF/crp/views/annualReport2018/submenu-AR2018.ftl" /]
    
    <div class="row">
      [#-- POWB Menu --]
      <div class="col-md-3">[#include "/WEB-INF/crp/views/annualReport2018/menu-AR2018.ftl" /]</div>
      [#-- POWB Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/annualReport2018/messages-AR2018.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
            [#-- 2.2.1 Highlights of External Partnerships  --]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.highlights.title" /]</h5>
            
            [#-- Partnerships summary --]
            [#if PMU]
              <div class="form-group">
                [@customForm.textArea name="${customName}.summary" i18nkey="${customLabel}.highlights" help="${customLabel}.highlights.help" className="limitWords-300" helpIcon=false required=true editable=editable allowTextEditor=true /]
              </div>
            [#else]
              <div class="textArea">
                  <label for="">[@customForm.text name="${customLabel}.summary" readText=true /]</label>:
                  <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
              </div>
            [/#if]
            
            [#-- Table 7: Key external partnerships --]
              <div class="form-group">
                <h4 class="simpleTitle headTitle">[@s.text name="${customLabel}.table7.title" /]</h4>
              </div>
                <div class="listProgramCollaborations">                  
              [#--       [#if reportSynthesis.reportSynthesisCrossCgiar.collaborations?has_content]--] 
                  [#if reportSynthesis.reportSynthesisKeyPartnership.partnerships?has_content]
                  [#list reportSynthesis.reportSynthesisKeyPartnership.partnerships as item]
                    [@addKeyExternalPartnership element=item name="${customName}.partnerships" index=item_index  isEditable=editable/]
                  [/#list]
                 [/#if]
                </div>
              [#if canEdit && editable]
                <div class="text-right">


                  <div class="addKeyPartnership bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="annualReport2018.externalPartnerships.addPartnershipButton"/]</div>
                </div> 
              [/#if]
         
             [#-- Projects Key Partnerships --]
              <h4 class="simpleTitle">[@customForm.text name="${customLabel}.projectsPartnerships.title" param="${currentCycleYear}" /] ([#if PMU]${flagshipPlannedList?size}[#else]${projectKeyPartnerships?size}[/#if])</h4>
              <div class="form-group margin-panel">                 
                  [@projectsKeyPartnershipsTable name="${customName}.projectsPartnerships" list=projectKeyPartnerships /]
              </div>
              
              [#-- 2.2.2 Cross-CGIAR Partnerships  --]
              <h5 class="sectionSubTitle">[@s.text name="${customLabel}.crossCGIAR.title" /]</h5>
              [#-- Summary --]
              [#if PMU]
                <div class="form-group">
                  [@customForm.textArea name="${customName}.crossCGIAR.summary" i18nkey="${customLabel}.crossCGIAR.summary" help="${customLabel}.crossCGIAR.summary.help" className="limitWords-300" helpIcon=false required=true editable=editable allowTextEditor=true /]
                </div>
              [#else]
                <div class="textArea">
                  <label for="">[@customForm.text name="${customLabel}.crossCGIAR.summary" readText=true /]:</label>
                  <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
                </div>
              [/#if]
              
              [#assign crossCGIARp = [
                { 
                  "title": "Title",
                  "maturity": "Maturity Level",
                  "status": "Status"
                }
              ] /]
              
              [#-- Table 8: Internal Cross-CGIAR Collaborations --]
              <div class="form-group">
                <h4 class="headTitle annualReport-table">[@s.text name="${customLabel}.table8.title" /]</h4>
                [@customForm.helpLabel name="${customLabel}.table8.help" showIcon=false/]
                [#if crossCGIARp?has_content]
                    [#list crossCGIARp as item]
                      [@addCrossCGIARPartnerships element=item name="${customName}.externalPartnerships.table8" index=item_index  isEditable=editable/]
                    [/#list]
                [/#if]
                [#if canEdit && editable]
                  <div class="text-right">
                    <div class="addPlatformCollaboration bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="annualReport2018.externalPartnerships.addPlatformCollaborationButton"/]</div>
                  </div> 
                [/#if]
              </div>
            
          </div>
          [#-- Section Buttons & hidden inputs --]
          [#include "/WEB-INF/crp/views/annualReport2018/buttons-AR2018.ftl" /]
        [/@s.form]
      </div>
    </div>
  [/#if] 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro addKeyExternalPartnership element name index template=false isEditable=true]

    [#local customName = "${name}[${index}]" /]
    <div id="flagshipCollaboration-${template?string('template', index)}" class="flagshipCollaboration borderBox form-group" style="position:relative; display:${template?string('none','block')}">

    [#-- Remove Button --]
    [#if isEditable]<div class="removeProgramCollaboration removeElement sm" title="Remove"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
    <br />

    <div class="form-group">
      [#-- Description --]
        [@customForm.input name="${customName}.description" i18nkey="${customLabel}.table7.description" helpIcon=false className="limitWords-30" required=true editable=editable /]
    </div>

    <div class="form-group row">
      [#-- Main area of partnership --]
      <div class="col-md-6">
        [@customForm.elementsListComponent name="${customName}.mainAreas" elementType="repIndPartnershipMainArea" elementList=(element.mainAreas)![] label="${customLabel}.table7.mainArea" help=""  listName="mainAreas" keyFieldName="id" displayFieldName="name"/]
      </div>
      [#local otherArea = true /]
      <div class="col-md-6 block-pleaseSpecify" style="display:${otherArea?string('block', 'none')}">
        [@customForm.input name="${customName}.otherPartnershipMainArea" i18nkey="${customLabel}.table7.otherMainArea" className="" required=false editable=editable /]
      </div>
    </div>
    
    <div class="form-group">
      [#-- Partners --]
        [@customForm.elementsListComponent name="${customName}.institutions" elementType="institution" elementList=(element.institutions)![] label="${customLabel}.table7.partners" help=""  listName="partners" keyFieldName="id" displayFieldName="composedName"/]
    </div>
    
    [#-- Upload Template --]
    <div class="form-group" style="position:relative" listname="">
      [@customForm.fileUploadAjax 
        fileDB={}  
        name="annualReport2018.externalPartnerships.table7.documentation" 
        label="annualReport2018.externalPartnerships.table7.documentation"
        dataUrl=""  
        path=""
        isEditable=editable
        labelClass=""
        required=true
       /]
    </div>

    
  </div>

[/#macro]


[#macro projectsKeyPartnershipsTable name="" list=["",""]]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-projectId">[@s.text name="${customLabel}.projectsPartnerships.id" /]</th>
        <th id="tb-phase">[@s.text name="${customLabel}.projectsPartnerships.phase" /]</th>
      </tr>
    </thead>
    <tbody>
      [#if list?has_content]
          [#list list as item]
          <tr>
            <td class="text-center">
              [#if (item.project.id?has_content)!false]
                ${item.project.id}
              [#else]
                <i style="opacity:0.5">PID</i>
              [/#if]
            </td>
            <td class="text-center">
              [#if (item.lessons?has_content)!false]
                ${item.lessons}
              [#else]
                <i style="opacity:0.5">From Projects</i>
              [/#if]
            </td>           
          </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="6"><i>No entries added yet.</i></td>
          </tr>
      [/#if]
    </tbody>
  </table>
[/#macro]


[#macro addCrossCGIARPartnerships element name index template=false isEditable=true]
  [#local customName = "${name}[${index}]" /]
  <div id="flagshipCollaboration-${template?string('template', index)}" class="flagshipCollaboration borderBox form-group" style="position:relative; display:${template?string('none','block')}">

    [#-- Index --]
    <div class="leftHead blue sm"><span class="index">${index+1}</span></div>
    [#-- Remove Button --]
    [#if isEditable]<div class="removeProgramCollaboration removeElement sm" title="Remove"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
    <br />

    <div class="form-group">
      [#-- CRP/Platform --] 
        [@customForm.select name="${customName}.table8.crp" label="" keyFieldName="id" displayFieldName="acronym" i18nkey="${customLabel}.table8.crp" listName="globalUnitList"  required=true  className="globalUnitSelect" editable=isEditable/]
    </div>
    
    <div class="form-group">
      [#-- Description of collaboration --]
        [@customForm.textArea name="${customName}.table8.description" i18nkey="${customLabel}.table8.description" help="${customLabel}.table8.description.help" helpIcon=false required=true editable=editable allowTextEditor=true /]
    </div>
    
    <div class="form-group">
      [#-- Value added --]
        [@customForm.input name="${customName}.table8.value" i18nkey="${customLabel}.table8.value" help="${customLabel}.table8.value.help" helpIcon=false required=true editable=editable /]
    </div>
    
    
    [#assign collaborationList = [
       { 
        "id": "1",
        "name": "Contribution"
       },
       { 
        "id": "2",
        "name": "Service needed from"
       },
       { 
        "id": "3",
        "name": "Both"
       }
    ] /]
    
    
    [#-- Collaboration type --]
    <div class="form-group">
        <label>[@s.text name="${customLabel}.table8.collaborationType" /]:[@customForm.req required=editable  /]</label><br />
        [#-- [#local collaborationTypeSelected = (element.repIndCollaborationType.id)!-1] --]
        [#local collaborationTypeSelected = "1"]
        
        [#list (collaborationList)![] as collaboration]
          [@customForm.radioFlat id="${customName}-collaboration-${collaboration_index}" name="${customName}.repIndCollaborationType.id" label="${collaboration.name}" value="${collaboration.id}" checked=(collaborationTypeSelected == collaboration.id)!false editable=isEditable cssClass="" cssClassLabel="font-normal"/]
        [/#list]
        
        [#if !editable && (collaborationTypeSelected == -1)][@s.text name="form.values.fieldEmpty"/][/#if]
    </div>
    
  </div>
[/#macro]