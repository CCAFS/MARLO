[#ftl]
[#-- History Message --]
[#if transaction??]
  <div class="history-mode text-center animated flipInX">
    [#if transaction == "-1"]
      <p>[@s.text name="crpIndicators.message.historyNotFound" /]</p>
    [#else]
      <p>[@s.text name="crpIndicators.message.historyVersion" ]  
          [@s.param]<span>${currentLiaisonInstitution.modifiedBy.composedName?html}</span>[/@s.param]
          [@s.param]<span>${currentLiaisonInstitution.activeSince?datetime}</span>[/@s.param]
          [@s.param]<a href="[@s.url][@s.param name="liaisonInstitutionID" value=liaisonInstitutionID /][@s.param name="edit" value="true"/][/@s.url]">here</a>[/@s.param]
         [/@s.text]
      </p>
      [#-- Differences --]
      [#include "/WEB-INF/global/macros/historyDiff.ftl" /]
    [/#if]
  </div>
[#else]

  [#-- Informing user that he/she doesn't have enough privileges to edit.  --]
  [#if submission?has_content]
    <p class="projectSubmitted">[@s.text name="submit.projectSubmitted" ][@s.param]${(submission.dateTime?date)?string.full}[/@s.param][/@s.text]</p>
  [/#if]
  
  
  [#-- Privileges Message --]
  [#if (!canEdit && !(transaction??) && !(submission?has_content)) || crpClosed]
    [#if crpClosed]
      <p class="readPrivileges">MARLO is closed.</p>
    [#else]
      <p class="readPrivileges">[@s.text name="saving.read.privileges.section" /]</p>
    [/#if]
  [/#if]
  
  
  [#-- Concurrence Message --]
  <div id="concurrenceMessage" class="text-center" style="display:none">
    <p><span class="glyphicon glyphicon-flash"></span> 
    [@s.text name="synthesis.message.sectionSaved"]
      [@s.param]<span class="person"></span>[/@s.param]
      [@s.param] <a href="#" onclick="location.reload()">click here</a> [/@s.param]
    [/@s.text]
    </p>
  </div>
  
  
  [#-- Concurrence Hidden Block --]
  <div id="concurrenceBlock" class="text-center" style="display:none">
    <div class="layer"></div>
    <div class="content">
      <span class="glyphicon glyphicon-lock"></span>
      <p>[@s.text name="synthesis.message.concurrence" /] [@s.text name="synthesis.message.concurrenceNotEditing"][@s.param] <a href="[@s.url][@s.param name="liaisonInstitutionID" value=liaisonInstitutionID /][/@s.url]">click here</a> [/@s.param][/@s.text]</p>
    </div>
  </div>

[/#if]


