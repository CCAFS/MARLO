[#ftl]
[#-- Project identifier --]
<input type="hidden"  name="projectOutcomeID" value="${projectOutcomeID}"/>
<input type="hidden"  name="projectID" value="${project.id}" />

<input type="hidden"  name="projectOutcome.id" value="${(projectOutcome.id)!}" />
<input type="hidden"  name="projectOutcome.project.id" value="${(projectOutcome.project.id)!}" />
<input type="hidden"  name="className" value="${(projectOutcome.class.name)!}"/>
<input type="hidden"  name="id" value="${(projectOutcome.id)!}"/>
<input type="hidden"  name="modifiedBy.id" value="${(currentUser.id)!}"/>
<input type="hidden"  name="actionName" value="${(actionName)!}"/>

<div class="buttons">
  <div class="buttons-content">
    [#-- History Log --]
    [#if action.getListLog(projectOutcome)?has_content]
      [#import "/WEB-INF/global/macros/logHistory.ftl" as logHistory /]
      [@logHistory.logList list=action.getListLog(projectOutcome) itemName="projectOutcomeID" itemId=projectOutcomeID /]
      <a href="" onclick="return false" class="form-button button-history"><span class="glyphicon glyphicon-glyphicon glyphicon-list-alt" aria-hidden="true"></span> [@s.text name="form.buttons.history" /]</a>
    [/#if]
    [#if editable]
      [#-- Back Button --]
      <a href="[@s.url][@s.param name="projectOutcomeID" value=projectOutcomeID /][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> [@s.text name="form.buttons.back" /]</a>
      [#-- Cancel Button --]                
      [@s.submit type="button" cssStyle="display:${draft?string('inline-block','none')}"   name="cancel" cssClass="button-cancel"]<span class="glyphicon glyphicon-trash" aria-hidden="true"></span> [@s.text name="form.buttons.discard" /] [/@s.submit]
      [#-- Save Button --]
      [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /] <span class="draft">${draft?string('(Draft Version)','')}</span>[/@s.submit]
    [#elseif canEdit]
      [#-- Back Button --]
      <a href="[@s.url][@s.param name="projectOutcomeID" value=projectOutcomeID /][@s.param name="edit" value="true"/][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
    [/#if]
  </div>
</div>