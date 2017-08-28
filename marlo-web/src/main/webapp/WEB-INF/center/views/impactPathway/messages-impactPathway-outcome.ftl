[#ftl]
[#-- MESSAGES PARAMETERS --]
[#assign object = outcome /]
[#assign nameParameter = "outcomeID" /]
[#assign parameterID = outcomeID /]

[#-- History Message --]
[#if transaction??]
  <div class="history-mode text-center animated flipInX">
    [#if transaction == "-1"]
      <p>[@s.text name="message.historyNotFound" /]</p>
    [#else]
      <p>[@s.text name="message.historyVersion" ]  
          [@s.param]<span>${object.modifiedBy.composedName?html}</span>[/@s.param]
          [@s.param]<span>${object.activeSince?datetime}</span>[/@s.param]
          [@s.param]<a href="[@s.url][@s.param name=nameParameter value=parameterID /][@s.param name="edit" value="true"/][/@s.url]">here</a>[/@s.param]
         [/@s.text]
      </p>
    [/#if]
  </div>
[/#if]

[#-- Submission Message --]
[#if submission]
  <div class="submission-mode text-center animated flipInX">
    <p>[@s.text name="message.submittedOn" ][/@s.text]</p>
  </div>
[/#if]

[#-- Privileges Message --]
[#if !canEdit && !transaction?? && !submission ]
  <p class="readPrivileges">[@s.text name="saving.read.privileges.section" /]</p>
[/#if] 

[#-- Completed Message--]
[#if canSubmit && !submission && completed]
  <div class="completed-mode text-center animated flipInX">
    <p>[@s.text name="message.completed" /]</p>
  </div>
[/#if]

[#-- Concurrence Message --]
<div id="concurrenceMessage" class="text-center" style="display:none">
  <p><span class="glyphicon glyphicon-flash"></span> 
  [@s.text name="message.sectionSaved"]
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
    <p>[@s.text name="message.concurrence" /] [@s.text name="message.concurrenceNotEditing"][@s.param] <a href="[@s.url][@s.param name=nameParameter value=parameterID /][/@s.url]">click here</a> [/@s.param][/@s.text]</p>
  </div> 
</div>

[#-- Draft Message --]
[#include "/WEB-INF/global/macros/draftMessage.ftl" /]