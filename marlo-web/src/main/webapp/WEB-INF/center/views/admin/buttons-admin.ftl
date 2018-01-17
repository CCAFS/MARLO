[#ftl]
[#-- Hidden parameters --]
<input type="hidden"  name="modifiedBy.id" value="${(currentUser.id)!}"/>
<input type="hidden"  name="actionName" value="${(actionName)!}"/>
<input id="redirectionUrl" type="hidden" name="url" value="" />
<input type="hidden"  name="phaseID" value="${(actualPhase.id)!}"/>

<div class="buttons">
  <div class="buttons-content">
      [#-- Save Button --]
      [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /] [/@s.submit]
  </div>
</div>



