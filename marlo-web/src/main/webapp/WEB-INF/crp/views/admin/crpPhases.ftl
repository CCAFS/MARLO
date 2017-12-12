[#ftl]
[#assign title = "CRP Phases" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["bootstrap-select","jquery-ui", "pickadate"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/admin/crpPhases.js",
  "${baseUrl}/global/js/fieldsValidation.js" 
  ] 
/]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/crpPhases.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "crpPhases" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"crpPhases", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
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
        
        <h4 class="sectionTitle">CRP Phases</h4>
        <div class="borderBox row ">
          [#if phases?size > 1]
            [#list phases as phase]
              [#assign customName = "phase[${phase_index}]"]
              <div id="crpPhase-${phase.id}" class="crpPhase simpleBox ${(phase.isReporting())?string('reporting','planning')} [#if (actualPhase.id == phase.id)][#assign currenPhaseIndex = phase_index  /]current[/#if]">
                <div class="leftHead sm">
                  <span class="index">${phase_index+1}</span>
                </div>
                [#-- Hidden Inputs --]
                <input type="hidden" name="${customName}.id" value="${phase.id}" />
                
                [#-- Title --]
                <h4> ${(phase.description)!} ${(phase.year)!}</h4><hr />
                <div class="form-group row"> 
                  [#-- Visible Phase --]
                  <div class="col-md-6">
                    <label>Visible:</label>
                    [@customForm.radioFlat id="visible-yes-${phase_index}" name="${customName}.visible" label="Yes" value="true" checked=phase.visible cssClassLabel="radio-label-yes"/]
                    [@customForm.radioFlat id="visible-no-${phase_index}" name="${customName}.visible" label="No" value="true" checked=!phase.visible cssClassLabel="radio-label-no"/]
                  </div>
                  [#-- Editable Phase --]
                  <div class="col-md-6">
                    <label>Editable:</label>
                    [@customForm.radioFlat id="editable-yes-${phase_index}" name="${customName}.editable" label="Yes" value="true" checked=phase.editable  cssClassLabel="radio-label-yes"/]
                    [@customForm.radioFlat id="editable-no-${phase_index}" name="${customName}.editable" label="No" value="true" checked=!phase.editable  cssClassLabel="radio-label-no"/]
                  </div>
                </div>
                <div class="form-group row">
                  [#-- From Date  --]
                  <div class="col-md-6">
                    [@customForm.input name="${customName}.startDate" value="${phase.startDate?string.medium}" i18nkey="From" placeholder="" editable=editable  className="startDate datePicker"/]
                  </div>
                  [#-- Until Date  --]
                  <div class="col-md-6">
                    [@customForm.input name="${customName}.endDate" value="${phase.endDate?string.medium}" i18nkey="Until" placeholder="" editable=editable  className="endDate datePicker"/]
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


[#include "/WEB-INF/crp/pages/footer.ftl" /]

