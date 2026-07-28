[#ftl]
[#assign title][@s.text name="globalUnitManagement.title" /][/#assign]
[#assign pageLibs = ["select2", "blueimp-file-upload"] /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign customJS = [
  "${baseUrlCdn}/global/js/usersManagement.js",
  "${baseUrlCdn}/global/js/superadmin/globalUnitManagement.js?20260723f"
] /]
[#assign customCSS = [ "${baseUrlCdn}/global/css/superadmin/superadmin.css?20260723b" ] /]
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
          [#-- Explicit list of existing Global Unit ids to delete (comma separated). Empty by default: nothing is
               deleted unless the user explicitly removes an existing Global Unit. --]
          <input type="hidden" name="deletedGlobalUnitIds" id="deleted-global-unit-ids" value="" />
          <h4 class="sectionTitle">[@s.text name="globalUnitManagement.title" /]</h4>

          <div class="globalUnits-list">
            [#if globalUnits?has_content]
              [#list globalUnits as gu]
                [@globalUnitMacro element=gu index=gu_index isTemplate=false /]
              [/#list]
            [/#if]
          </div>

          <div class="addGlobalUnit bigAddButton text-center">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="globalUnitManagement.add" /]
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

[#import "/WEB-INF/global/macros/usersPopup.ftl" as usersForm/]
[@usersForm.searchUsers/]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#-- Delete Confirmation Modal --]
<div id="confirm-delete-modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="confirmDeleteLabel" aria-hidden="true" data-backdrop="false" data-keyboard="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="[@s.text name="form.buttons.cancel" /]">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title" id="confirmDeleteLabel">[@s.text name="globalUnitManagement.delete.confirmTitle" /]</h4>
      </div>
      <div class="modal-body">
        <p>[@s.text name="globalUnitManagement.delete.confirmMessage" /]</p>
        <p><strong id="delete-unit-label"></strong></p>
        <div class="alert alert-info" style="margin-top: 15px; margin-bottom: 0;">
          <small><strong>[@s.text name="globalUnitManagement.delete.noteLabel" /]</strong> [@s.text name="globalUnitManagement.delete.note" /]</small>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">[@s.text name="form.buttons.cancel" /]</button>
        <button type="button" class="btn btn-danger" id="confirm-delete-btn">[@s.text name="globalUnitManagement.delete.button" /]</button>
      </div>
    </div>
  </div>
</div>

[#-- Messages for JavaScript --]
<div id="globalUnitManagement-messages" style="display:none;" aria-hidden="true">
  <span id="msg-gu-itemTitle">[@s.text name="globalUnitManagement.itemTitle" /]</span>
  <span id="msg-gu-thisItem">[@s.text name="globalUnitManagement.thisItem" /]</span>
  <span id="msg-gu-deleteCurrentSession">[@s.text name="globalUnitManagement.delete.currentSessionError" /]</span>
  <span id="msg-gu-logoAcronymMismatch">[@s.text name="globalUnitManagement.logo.acronymMismatch" /]</span>
  <span id="msg-gu-logoLibraryMissing">[@s.text name="globalUnitManagement.logo.libraryMissing" /]</span>
  <span id="msg-gu-logoOnlyPng">[@s.text name="globalUnitManagement.logo.onlyPng" /]</span>
  <span id="msg-gu-logoNoFileSelected">[@s.text name="globalUnitManagement.logo.noFileSelected" /]</span>
  <span id="msg-gu-logoSelectedFile">[@s.text name="globalUnitManagement.logo.selectedFile" /]</span>
  <span id="msg-gu-logoPreview">[@s.text name="globalUnitManagement.logo.preview" /]</span>
  <span id="msg-gu-logoUploading">[@s.text name="globalUnitManagement.logo.uploading" /]</span>
  <span id="msg-gu-logoExisting">[@s.text name="globalUnitManagement.logo.existing" /]</span>
  <span id="msg-gu-logoUploaded">[@s.text name="globalUnitManagement.logo.uploaded" /]</span>
  <span id="msg-gu-logoUploadFailed">[@s.text name="globalUnitManagement.logo.uploadFailed" /]</span>
  <span id="msg-gu-logoUploadError">[@s.text name="globalUnitManagement.logo.uploadError" /]</span>
  <span id="msg-gu-logoAcronymRequired">[@s.text name="globalUnitManagement.logo.acronymRequired" /]</span>
  <span id="msg-gu-acronymWhitespace">[@s.text name="globalUnitManagement.validation.acronymNoWhitespace" /]</span>
  <span id="msg-gu-crpAdminDuplicate">[@s.text name="globalUnitManagement.crpAdminTeam.duplicate" /]</span>
  <span id="msg-gu-validationRequired">[@s.text name="globalUnitManagement.validation.requiredFields" /]</span>
  <span id="msg-gu-institutionPlaceholder">[@s.text name="globalUnitManagement.institution.placeholder" /]</span>
</div>

<ul style="display:none">
  [@crpAdminUserItem element={} index=0 name="" template=true /]
</ul>

[@globalUnitMacro element={} index=-1 isTemplate=true /]

[#macro globalUnitMacro element index isTemplate=false]
  [#assign isCurrentGlobalUnit = !isTemplate && element.id?? && action.currentCrp?? && action.currentCrp.id?? && element.id == action.currentCrp.id /]
  <div id="globalUnit-${isTemplate?string('template',index)}" class="globalUnit borderBox[#if isCurrentGlobalUnit] current-global-unit[/#if]"
    data-current-global-unit="${isCurrentGlobalUnit?string('true','false')}"
    style="display:${isTemplate?string('none','block')}">
    [#if !isCurrentGlobalUnit]
      <div class="remove-element removeElement sm" title="[@s.text name="form.buttons.remove" /]"></div>
    [#else]
      <span class="label label-info current-global-unit-badge" title="[@s.text name="globalUnitManagement.currentBadge.title" /]">
        [@s.text name="globalUnitManagement.currentBadge" /]
      </span>
    [/#if]

    <div class="blockTitle closed">
      <strong>[@s.text name="globalUnitManagement.itemTitle"][@s.param]${index + 1}[/@s.param][/@s.text] </strong>
      [#if element.name?has_content]
        ${element.name}
      [#else]
        [@s.text name="globalUnitManagement.newItem" /]
      [/#if]
      [#if element.acronym?has_content]
        - ${element.acronym}
      [/#if]
    </div>

    <div class="blockContent" style="display:none">
      <hr />
      <input class="gu-id-input" type="hidden" name="globalUnits[${index}].id" value="${(element.id)!}" />

      <div class="row">
        <div class="col-md-6 form-group">
          <label>[@s.text name="globalUnitManagement.name" /] <span class="red requiredTag">*</span></label>
          <input class="form-control required" type="text" name="globalUnits[${index}].name" value="${(element.name)!}" />
        </div>
        <div class="col-md-6 form-group">
          <label>[@s.text name="globalUnitManagement.acronym" /] <span class="red requiredTag">*</span></label>
          <input class="form-control acronym-input required" type="text" name="globalUnits[${index}].acronym"
            value="${(element.acronym)!}" />
          <div class="acronym-validation-message text-danger" style="display:none; margin-top:4px;" aria-live="polite"></div>
        </div>
      </div>

      <div class="row">
        <div class="col-md-12 form-group">
          <label>[@s.text name="globalUnitManagement.logo" /]</label>
          <div class="logo-drop-zone">
            <div class="logo-drop-zone-text">[@s.text name="globalUnitManagement.logo.dropZone" /]</div>
            <button type="button" class="btn btn-default btn-sm logo-file-browse-btn">[@s.text name="globalUnitManagement.logo.chooseFile" /]</button>
            <input class="form-control logo-file-input" type="file" name="file"
              data-url="${baseUrl}/globalUnitLogoUpload.do" accept="image/png" />
            <small class="logo-selected-file text-muted">[@s.text name="globalUnitManagement.logo.noFileSelected" /]</small>
          </div>
          <div class="logo-upload-status" style="margin-top:4px;"></div>
          <div class="logo-acronym-warning text-warning" style="margin-top:4px; display:none;"></div>
          [#if !isTemplate && element.acronym?has_content]
            [#assign currentLogoUrl = action.getLogoUrl(element.acronym) /]
            <div class="logo-preview-block">
              [#if action.hasExistingLogo(element.acronym)]
                <small class="help-block">[@s.text name="globalUnitManagement.logo.existing"][@s.param]${element.acronym?upper_case}[/@s.param][/@s.text]</small>
                <img class="logo-preview-img"
                  src="${currentLogoUrl}"
                  alt="${element.acronym} [@s.text name="globalUnitManagement.logo.alt" /]" style="max-height:48px; margin-top:4px; display:block;" />
              [#else]
                <small class="help-block">[@s.text name="globalUnitManagement.logo.missingDefault"][@s.param]${element.acronym?upper_case}[/@s.param][/@s.text]</small>
                <img class="logo-preview-img"
                  src="${baseUrlCdn}/global/images/crps/default.png"
                  alt="[@s.text name="globalUnitManagement.logo.defaultAlt" /]" style="max-height:48px; margin-top:4px; display:block;" />
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
        <div class="col-md-12 form-group">
          <label>[@s.text name="globalUnitManagement.institution" /] <span class="red requiredTag">*</span></label>
          <select class="form-control institution-select required" name="globalUnits[${index}].institution.id">
            <option value="">[@s.text name="globalUnitManagement.institution.select" /]</option>
            [#if institutions?has_content]
              [#list institutions as institution]
                <option value="${(institution.id)!}" [#if element.institution?? && element.institution.id?? && element.institution.id == institution.id]selected[/#if]>
                  ${(institution.composedName)!}
                </option>
              [/#list]
            [/#if]
          </select>
        </div>
      </div>

      <div class="form-group crp-admin-team-field">
        <label>[@s.text name="globalUnitManagement.crpAdminTeam" /] <span class="red requiredTag">*</span></label>
        <div class="usersBlock crp-admin-team-block borderBox clearfix"
          data-list-name="globalUnits[${index}].crpAdminTeam">
          <div class="users items-list simpleBox">
            <ul>
              [#if element.crpAdminTeam?has_content]
                [#list element.crpAdminTeam as userRole]
                  [@crpAdminUserItem element=userRole index=userRole_index
                    name="globalUnits[${index}].crpAdminTeam" /]
                [/#list]
              [/#if]
            </ul>
            <p class="text-center usersMessage"
              style="display:${(element.crpAdminTeam?has_content)?string('none','block')}">
              [@s.text name="globalUnitManagement.crpAdminTeam.empty" /]
            </p>
          </div>
          <div class="text-right">
            <div class="searchUser button-blue">
              <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
              [@s.text name="form.buttons.searchUser" /]
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
[/#macro]

[#macro crpAdminUserItem element index name template=false]
  [#local customName = "${name}[${index}]" /]
  <li id="crp-admin-user-${template?string('template',index)}" class="user userItem"
    style="display:${template?string('none','block')}">
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
    <span class="name">
      [#if element.user?? && element.user.composedName?has_content]
        ${element.user.composedName}
      [#else]
        [@s.text name="globalUnitManagement.unknownUser" /]
      [/#if]
    </span>
    <input class="user" type="hidden" name="${customName}.user.id" value="${(element.user.id)!}" />
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <span class="glyphicon glyphicon-remove pull-right remove-crp-admin-user"
      aria-hidden="true" title="[@s.text name="form.buttons.remove" /]"></span>
  </li>
[/#macro]
