[#ftl]
[#assign title = "Global Unit Management" /]
[#assign pageLibs = ["select2", "blueimp-file-upload"] /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign customJS = [ "${baseUrlCdn}/global/js/superadmin/globalUnitManagement.js?20260410" ] /]
[#assign customCSS = [ "${baseUrlCdn}/global/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "globalUnitManagement" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"globalUnitManagement", "nameSpace":"", "action":""}
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

<!-- Delete Confirmation Modal -->
<div id="confirm-delete-modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="confirmDeleteLabel" aria-hidden="true" data-backdrop="false" data-keyboard="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title" id="confirmDeleteLabel">Confirm Deletion</h4>
      </div>
      <div class="modal-body">
        <p>Are you sure you want to remove this Global Unit?</p>
        <p><strong id="delete-unit-label"></strong></p>
        <div class="alert alert-info" style="margin-top: 15px; margin-bottom: 0;">
          <small><strong>Note:</strong> Remember to click the <strong>Save</strong> button to apply the changes.</small>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-danger" id="confirm-delete-btn">Delete</button>
      </div>
    </div>
  </div>
</div>

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
          <div class="logo-drop-zone">
            <div class="logo-drop-zone-text">Drag and drop an image here, or click to choose a file.</div>
            <button type="button" class="btn btn-default btn-sm logo-file-browse-btn">Choose PNG file</button>
            <input class="form-control logo-file-input" type="file" name="file"
              data-url="${baseUrl}/globalUnitLogoUpload.do" accept="image/png" />
            <small class="logo-selected-file text-muted">No file selected.</small>
          </div>
          <div class="logo-upload-status" style="margin-top:4px;"></div>
          [#if !isTemplate && element.acronym?has_content]
            [#assign currentLogoUrl = action.getLogoUrl(element.acronym) /]
            <div class="logo-preview-block">
              [#if action.hasExistingLogo(element.acronym)]
                <small class="help-block">Logo: <strong>${element.acronym?upper_case}</strong>.</small>
                <img class="logo-preview-img"
                  src="${currentLogoUrl}"
                  alt="${element.acronym} logo" style="max-height:48px; margin-top:4px; display:block;" />
              [#else]
                <small class="help-block">No logo for <strong>${element.acronym?upper_case}</strong>. Using default.</small>
                <img class="logo-preview-img"
                  src="${baseUrlCdn}/global/images/crps/default.png"
                  alt="default logo" style="max-height:48px; margin-top:4px; display:block;" />
              [/#if]
            </div>
          [#else]
            <div class="logo-preview-block" style="display:none;">
              <img class="logo-preview-img" src="" alt="" style="max-height:48px; margin-top:4px; display:block;" />
            </div>
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
