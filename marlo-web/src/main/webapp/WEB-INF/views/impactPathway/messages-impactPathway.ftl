[#ftl]
[#-- History Message --]
[#if transaction??]
  <div class="history-mode text-center animated flipInX">
    [#if transaction == "-1"]
      <p>[@s.text name="CrpProgram.message.historyNotFound" /]</p>
    [#else]
      <p>[@s.text name="CrpProgram.message.historyVersion" ]  
          [@s.param]<span>${selectedProgram.modifiedBy.composedName?html}</span>[/@s.param]
          [@s.param]<span>${selectedProgram.activeSince?datetime}</span>[/@s.param]
          [@s.param]<a href="[@s.url][@s.param name="crpProgramID" value=crpProgramID /][@s.param name="edit" value="true"/][/@s.url]">here</a>[/@s.param]
         [/@s.text]
      </p>
    [/#if]
  </div>
[/#if]

[#-- Submission Message --]
[#if submission?has_content]
  <div class="submission-mode text-center animated flipInX">
    <p>[@s.text name="CrpProgram.message.submittedOn" ][@s.param]${submission.dateTime}[/@s.param][/@s.text]</p>
  </div>
[/#if]

[#-- Privileges Message --]
[#if !canEdit && !transaction??]
  <p class="readPrivileges">[@s.text name="saving.read.privileges.section" /]</p>
[/#if] 

[#-- Program completed Message--]
[#if canSubmit && !submission?has_content && completed]
  <div class="completed-mode text-center animated flipInX">
    <p>[@s.text name="CrpProgram.message.completed" /]</p>
  </div>
[/#if]
