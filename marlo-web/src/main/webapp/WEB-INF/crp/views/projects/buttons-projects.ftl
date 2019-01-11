[#ftl]

[#-- Project identifiers --]
[#assign auditObject = (project)!{} ]
[#assign auditObjectID = auditObject['id'] ]
[#assign auditObjectName = "projectID" ]
<input type="hidden"  name="projectID"              value="${(project.id)!}" />
<input type="hidden"  name="project.projectInfo.id" value="${(project.projectInfo.id)!}"  class="projectInfo" />
<input type="hidden"  name="project.type"           value="${(project.type)!}" />

[#-- Policy identifiers --]
[#if (policy.id?has_content)!false]
  [#assign auditObject = (policy)!{} ]
  [#assign auditObjectID = auditObject['id'] ]
  [#assign auditObjectName = "policyID" ]
  <input type="hidden"  name="${auditObjectName}"   value="${(auditObjectID)!}" />
[/#if]

[#-- General identifiers --]
<input type="hidden"  name="id"                     value="${(auditObjectID)!}"/>
<input type="hidden"  name="className"              value="${(auditObject.class.name)!}"/>
<input type="hidden"  name="modifiedBy.id"          value="${(currentUser.id)!}"/>
<input type="hidden"  name="actionName"             value="${(actionName)!}"/>
<input type="hidden"  name="phaseID"                value="${(actualPhase.id)!}"/>
<input type="hidden"  name="url"                    value=""                              id="redirectionUrl" />

[#assign recordsList = (action.getListLog(auditObject))!{} /]

<div class="buttons">
  <div class="buttons-content">
    [#-- History Log  --]
    [#if recordsList?has_content]
      [#import "/WEB-INF/global/macros/logHistory.ftl" as logHistory /]
      [@logHistory.logList list=recordsList itemName=auditObjectName itemId=auditObjectID /]
      <a href="" onclick="return false" class="form-button button-history"><span class="glyphicon glyphicon-glyphicon glyphicon-list-alt" aria-hidden="true"></span> [@s.text name="form.buttons.history" /]</a>
    [/#if]
    [#-- (editable || (reportingActive && action.hasPermission("statusDescription")) || (editStatus && action.isProjectDescription())) && !(transaction??) --]
    [#if editable && !(transaction??)]
      [#-- Discard Button --]
      [@s.submit type="button" cssStyle="display:none" name="cancel" cssClass="button-cancel"]<span class="glyphicon glyphicon-trash" aria-hidden="true"></span> [@s.text name="form.buttons.discard" /] [/@s.submit]
      [#-- Save Button --]
      [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> <span class="saveText">[@s.text name="form.buttons.save" /]</span> [/@s.submit]
      [#-- Replicate to the next upkeep --]
      [#include "/WEB-INF/global/pages/replicateButton.ftl" /]
    [#elseif canEdit]
      [#-- Edit Button --]
      <a href="[@s.url][@s.param name=auditObjectName value=auditObjectID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
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

