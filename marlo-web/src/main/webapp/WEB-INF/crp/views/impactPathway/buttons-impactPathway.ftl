[#ftl]
[#-- Hidden Parameters --]
<input type="hidden" id="crpProgramID"  name="crpProgramID" value="${(crpProgramID)!}"/>
<input type="hidden"  name="className" value="${(selectedProgram.class.name)!}"/>
<input type="hidden"  name="id" value="${(selectedProgram.id)!}"/>
<input type="hidden"  name="modifiedBy.id" value="${(currentUser.id)!}"/>
<input type="hidden"  name="actionName" value="${(actionName)!}"/>
<input type="hidden"  name="phaseID" value="${(actualPhase.id)!}"/>
<input id="redirectionUrl" type="hidden" name="url" value="" />

[#assign recordsList = (action.getListLog(selectedProgram))!{} /]

<div class="buttons">
  <div class="buttons-content">
    [#-- History Log --]
    [#if recordsList?has_content]
      [#import "/WEB-INF/global/macros/logHistory.ftl" as logHistory /]
      [@logHistory.logList list=recordsList itemName="crpProgramID" itemId=crpProgramID /]
      <a href="" onclick="return false" class="form-button button-history"><span class="glyphicon glyphicon-glyphicon glyphicon-list-alt" aria-hidden="true"></span> [@s.text name="form.buttons.history" /]</a>
    [/#if]
    [#if editable]
      [#-- Back Button 
      <a href="[@s.url][@s.param name="crpProgramID" value=crpProgramID /][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> [@s.text name="form.buttons.back" /]</a>
      --]
      [#-- Discard Button --]
      [@s.submit type="button" cssStyle="display:none"   name="cancel" cssClass="button-cancel"]<span class="glyphicon glyphicon-trash" aria-hidden="true"></span> [@s.text name="form.buttons.discard" /] [/@s.submit]
      
      [#-- Save Button --]
      [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> <span class="saveText">[@s.text name="form.buttons.save" /]</span>[/@s.submit]
    [#elseif canEdit]
      [#-- Edit Button --]
      <a href="[@s.url][@s.param name="crpProgramID" value=crpProgramID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
    [/#if]
  </div>
</div>

[#-- Last update message --]
[#if recordsList?has_content]
[#assign lastRecord = recordsList[0] /]
<div class="clearfix"></div>
<span id="lastUpdateMessage" class="pull-right"> 
  Last edit was made on <span class="datetime">${(lastRecord.createdDate)?datetime} ${(timeZone)!}</span> by <span class="modifiedBy">${lastRecord.user.composedCompleteName}</span>  
</span>
[/#if]




