[#ftl]
[#-- Project identifier --]
<input type="hidden"  name="projectOutcomeID" value="${projectOutcomeID}"/>
<input type="hidden"  name="projectID" value="${project.id}" />

<input type="hidden"  name="projectOutcome.id" value="${(projectOutcome.id)!}" />
<input type="hidden"  name="projectOutcome.crpProgramOutcome.crpProgram.acronym" value="${(projectOutcome.crpProgramOutcome.crpProgram.acronym)!}" />
<input type="hidden"  name="projectOutcome.crpProgramOutcome.id" id="outcomeId" value="${(projectOutcome.crpProgramOutcome.id)!}" />
<input type="hidden"  name="projectOutcome.crpProgramOutcome.description" value="${(projectOutcome.crpProgramOutcome.description)!}" />
<input type="hidden"  name="projectOutcome.project.id" value="${(projectOutcome.project.id)!}" />
<input type="hidden"  name="className" value="${(projectOutcome.class.name)!}"/>
<input type="hidden"  name="id" value="${(projectOutcome.id)!}"/>
<input type="hidden"  name="modifiedBy.id" value="${(currentUser.id)!}"/>
<input type="hidden"  name="actionName" value="${(actionName)!}"/>

<input id="redirectionUrl" type="hidden" name="url" value="" />

[#assign recordsList = (action.getListLog(projectOutcome))!{} /]

<div class="buttons">
  <div class="buttons-content">
    [#-- History Log --]
    [#if recordsList?has_content]
      [#import "/WEB-INF/global/macros/logHistory.ftl" as logHistory /]
      [@logHistory.logList list=recordsList itemName="projectOutcomeID" itemId=projectOutcomeID /]
      <a href="" onclick="return false" class="form-button button-history"><span class="glyphicon glyphicon-glyphicon glyphicon-list-alt" aria-hidden="true"></span> [@s.text name="form.buttons.history" /]</a>
    [/#if]
    [#if editable]
      [#-- Back Button 
      <a href="[@s.url][@s.param name="projectOutcomeID" value=projectOutcomeID /][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> [@s.text name="form.buttons.back" /]</a>
      --]
      [#-- Discard Button --]
      [@s.submit type="button" cssStyle="display:none"   name="cancel" cssClass="button-cancel"]<span class="glyphicon glyphicon-trash" aria-hidden="true"></span> [@s.text name="form.buttons.discard" /] [/@s.submit]
      
      [#-- Save Button --]
      [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> <span class="saveText">[@s.text name="form.buttons.save" /]</span> [/@s.submit]
    [#elseif canEdit]
      [#-- Edit Button --]
      <a href="[@s.url][@s.param name="projectOutcomeID" value=projectOutcomeID /][@s.param name="edit" value="true"/][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
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