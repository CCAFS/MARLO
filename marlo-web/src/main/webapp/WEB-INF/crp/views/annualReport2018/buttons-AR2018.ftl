[#ftl]

[#-- Hidden Inputs --] 
<input type="hidden"  name="liaisonInstitutionID" value="${(liaisonInstitutionID)!}" />
<input type="hidden"  name="synthesisID" value="${(reportSynthesis.id)!}" />
<input type="hidden"  name="id" value="${(reportSynthesis.id)!}"/>
<input type="hidden"  name="liaisonInstitutionAcronym" value="${(reportSynthesis.liaisonInstitution.acronym)!}" />
<input type="hidden"  name="phaseID" value="${(actualPhase.id)!}"/>
<input type="hidden"  name="className" value="${(reportSynthesis.class.name)!}"/>
<input type="hidden"  name="modifiedBy.id" value="${(currentUser.id)!}"/>
<input type="hidden"  name="actionName" value="${(actionName)!}"/>
<input type="hidden"  name="indexTab" value="${(indexTab)!}"/>

<input id="redirectionUrl" type="hidden" name="url" value="" />

[#attempt]
  [#assign recordsList = (action.getListLog(reportSynthesis))!{} /]
[#recover]
  [#assign recordsList = [] /]
[/#attempt]

<div class="buttons">
  <div class="buttons-content">
   [#-- History Log TODO: Fix history --]
    [#if false && recordsList?has_content]
      [#import "/WEB-INF/global/macros/logHistory.ftl" as logHistory /]
      [@logHistory.logList list=recordsList itemName="liaisonInstitutionID" itemId=liaisonInstitutionID /]
      <a href="" onclick="return false" class="form-button button-history"><span class="glyphicon glyphicon-glyphicon glyphicon-list-alt" aria-hidden="true"></span> [@s.text name="form.buttons.history" /]</a>
    [/#if]
  [#assign actionName = action.getActionName()?split("/")[1] /]
[#if actionName != "publications" && actionName != "innovations" && actionName!= "oicr" && actionName != "policies" || PMU]
    [#if editable ]
      [#-- Save Button --]
      [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> <span class="saveText">[@s.text name="form.buttons.save" /]</span> [/@s.submit]
    [#elseif canEdit]
      [#-- Edit Button --]
      <a href="[@s.url][@s.param name="liaisonInstitutionID" value=liaisonInstitutionID /][@s.param name="edit" value="true"/][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
    [/#if]
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