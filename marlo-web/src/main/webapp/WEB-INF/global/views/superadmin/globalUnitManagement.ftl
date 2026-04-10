[#ftl]
[#assign title = "Global Unit Management" /]
[#assign pageLibs = ["select2"] /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign customJS = [ "${baseUrlCdn}/global/js/superadmin/globalUnitManagement.js?20261109" ] /]
[#assign customCSS = [ "${baseUrlCdn}/global/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "globalUnitManagement" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"Global Unit Management", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

<section class="marlo-content">
  <div class="container">
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data"]
          <input type="hidden" name="managementMode" value="true" />
          <h4 class="sectionTitle">Global Unit Management</h4>

          <div class="globalUnits-list">
            [#if globalUnits?has_content]
              [#list globalUnits as gu]
                [@globalUnitMacro element=gu index=gu_index isTemplate=false /]
              [/#list]
            [/#if]
          </div>

          <div class="addGlobalUnit bigAddButton text-center">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add Global Unit
          </div>

          <div class="buttons">
            <div class="buttons-content">
              [@s.submit type="button" name="save" cssClass="button-save"]
                <span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /]
              [/@s.submit]
            </div>
          </div>
        [/@s.form]
      </div>
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]

[@globalUnitMacro element={} index=-1 isTemplate=true /]

[#macro globalUnitMacro element index isTemplate=false]
  <div id="globalUnit-${isTemplate?string('template',index)}" class="globalUnit borderBox" style="display:${isTemplate?string('none','block')}">
    <div class="remove-element removeElement sm" title="Remove"></div>

    <div class="blockTitle closed">
      <strong>Global Unit ${index + 1}: </strong>
      [#if element.name?has_content]
        ${element.name}
      [#else]
        New Global Unit
      [/#if]
      [#if element.acronym?has_content]
        - ${element.acronym}
      [/#if]
    </div>

    <div class="blockContent" style="display:none">
      <hr />
      <input type="hidden" name="globalUnits[${index}].id" value="${(element.id)!}" />

      <div class="row">
        <div class="col-md-6 form-group">
          <label>Name</label>
          <input class="form-control" type="text" name="globalUnits[${index}].name" value="${(element.name)!}" />
        </div>
        <div class="col-md-6 form-group">
          <label>Acronym</label>
          <input class="form-control acronym-input" type="text" name="globalUnits[${index}].acronym"
            value="${(element.acronym)!}" />
        </div>
      </div>

      <div class="row">
        <div class="col-md-12 form-group">
          <label>Logo</label>
          <input class="form-control logo-file-input" type="file" name="logoFiles" accept="image/png,image/*" />
          <input type="hidden" class="logo-files-acronym-slot" name="logoFilesAcronym" value="" disabled="disabled" />
          [#if !isTemplate && element.acronym?has_content]
            [#if action.hasExistingLogo(element.acronym)]
              <small class="help-block">Detected logo for acronym <strong>${element.acronym?upper_case}</strong>.</small>
              <img src="${baseUrlCdn}/global/images/crps/${element.acronym?upper_case}.png" alt="${element.acronym} logo"
                style="max-height:48px; margin-top:4px;" />
            [#else]
              <small class="help-block">No logo detected for acronym <strong>${element.acronym?upper_case}</strong>. Using default logo.</small>
              <img src="${baseUrlCdn}/global/images/crps/default.png" alt="default logo" style="max-height:48px; margin-top:4px;" />
            [/#if]
          [/#if]
        </div>
      </div>

      <div class="row">
        <div class="col-md-6 form-group">
          <label>Type</label>
          <select class="form-control" name="globalUnits[${index}].globalUnitType.id">
            <option value="">Select type</option>
            [#if globalUnitTypes?has_content]
              [#list globalUnitTypes as type]
                <option value="${(type.id)!}" [#if element.globalUnitType?? && element.globalUnitType.id?? && element.globalUnitType.id == type.id]selected[/#if]>
                  ${(type.name)!}
                </option>
              [/#list]
            [/#if]
          </select>
        </div>
        <div class="col-md-6 form-group">
          <label>Institution</label>
          <select class="form-control institution-select" name="globalUnits[${index}].institution.id">
            <option value="">None</option>
            [#if institutions?has_content]
              [#list institutions as institution]
                <option value="${(institution.id)!}" [#if element.institution?? && element.institution.id?? && element.institution.id == institution.id]selected[/#if]>
                  ${(institution.name)!}
                </option>
              [/#list]
            [/#if]
          </select>
        </div>
      </div>

      <div class="row">
        <div class="col-md-6 form-group">
          <label>
            <input type="checkbox" name="globalUnits[${index}].marlo" value="true" [#if isTemplate || (element.marlo)!false]checked[/#if] />
            Is MARLO
          </label>
        </div>
        <div class="col-md-6 form-group">
          <label>
            <input type="checkbox" name="globalUnits[${index}].login" value="true" [#if isTemplate || (element.login)!false]checked[/#if] />
            Login enabled
          </label>
        </div>
      </div>

    </div>
  </div>
[/#macro]
