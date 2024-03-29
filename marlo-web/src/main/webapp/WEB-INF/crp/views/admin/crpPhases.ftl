[#ftl]
[#assign title = "Phases" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["bootstrap-select","jquery-ui", "pickadate"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/admin/crpPhases.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js" 
  ] 
/]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/crpPhases.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "crpPhases" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"crpPhases", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectPhases.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        
        <h4 class="sectionTitle">Phases</h4>
        <div class="borderBox row ">
          [#if phases??]
            [#list phasesAction as phase]
              [#assign customName = "phasesAction[${phase_index}]"]
              <div id="crpPhase-${phase.id}" class="crpPhase simpleBox ${(phase.isReporting())?string('reporting','planning')} ${(phase.visible)?string('','displayOn')} [#if (actualPhase.id == phase.id)][#assign currenPhaseIndex = phase_index  /]current[/#if]">
                <div class="leftHead sm">
                  <span class="index">${phase_index+1}</span>
                </div>
                [#-- Hidden Inputs --]
                <input type="hidden" name="${customName}.id" value="${phase.id}" />
                <input type="hidden" name="${customName}.description" value="${phase.description}" />
                <input type="hidden" name="${customName}.name" value="${phase.name}" />
                <input type="hidden" name="${customName}.upkeep" value="${phase.upkeep?string}" />
                <input type="hidden" name="${customName}.year" value="${phase.year}" />
                <input type="hidden" name="${customName}.next.id" value="${(phase.next.id)!}" />
                <input type="hidden" name="${customName}.crp.id" value="${phase.crp.id}" />
                
                <div class="pull-right">
                  [@customForm.radioFlat id="defaultPhaseID-${phase.id}" name="defaultPhaseID" label="Set as default landing" disabled=false editable=editable value="${phase.id}" checked=(defaultPhaseID == phase.id)!false cssClass="" cssClassLabel="text-bold" inline=false /]
                </div>
                
                [#-- Title --]
                <h4> ${(phase.name)!} ${(phase.year)!}</h4>
                
                <hr />
                [#assign yearLimit = 2018 /]
                [#assign canOpenClose = ((phase.year > yearLimit) && !((phase.year = (yearLimit + 1)) && (phase.name == "POWB")))  || phase.editable /]
                <div class="form-group row">
                  [#-- Visible Phase --]
                  <div class="col-md-6">
                    <label>[@s.text name="projectPhases.visible" /]:</label>
                    [@customForm.radioFlat id="visible-yes-${phase_index}" name="${customName}.visible" label="Yes" value="true" checked=phase.visible cssClass="visible-yes" cssClassLabel="radio-label-yes"/]
                    [@customForm.radioFlat id="visible-no-${phase_index}" name="${customName}.visible" label="No" value="false" checked=!phase.visible cssClass="visible-no" cssClassLabel="radio-label-no"/]
                  </div>
                  [#-- Editable Phase --]
                  <div class="col-md-6" style="display: ${canOpenClose?string('block','none')}">
                    <label>[@s.text name="projectPhases.editable" /]:</label>
                    [@customForm.radioFlat id="editable-yes-${phase_index}" name="${customName}.editable" label="Open" value="true" checked=phase.editable cssClass="editable-yes" cssClassLabel="radio-label-yes"/]
                    [@customForm.radioFlat id="editable-no-${phase_index}" name="${customName}.editable" label="Close" value="false" checked=!phase.editable cssClass="editable-no" cssClassLabel="radio-label-no"/]
                  </div>
                </div>
                <div class="form-group row" style="display:none">
                  [#-- From Date  --]
                  <div class="col-md-6">
                    [@customForm.input name="${customName}.startDate" value="${(phase.startDate?string.medium)!}" i18nkey="From" placeholder="" editable=editable  className="startDate datePicker"/]
                  </div>
                  [#-- Until Date  --]
                  <div class="col-md-6">
                    [@customForm.input name="${customName}.endDate" value="${(phase.endDate?string.medium)!}" i18nkey="Until" placeholder="" editable=editable  className="endDate datePicker"/]
                  </div>
                </div>
                 
              </div>
            [/#list]
          [#else]
            <p>No Phases in the system</p>
          [/#if]
        </div>
        
        [#-- Section Buttons--]
        [#include "/WEB-INF/crp/views/admin/buttons-admin.ftl" /]
        
        [/@s.form]
        
      </div>
    </div>
  </div>
</section>


[#include "/WEB-INF/global/pages/footer.ftl" /]

