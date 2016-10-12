[#ftl]

[#-- Projects data information --]
[#include "/WEB-INF/views/projects/dataInfo-projects.ftl" /]

[#-- History Message --]
[#if transaction??]
  <div class="history-mode text-center animated flipInX">
    [#if transaction == "-1"]
      <p>[@s.text name="project.message.historyNotFound" /]</p>
    [#else]
      <p>[@s.text name="project.message.historyVersion" ]  
          [@s.param]<span>${project.modifiedBy.composedName?html}</span>[/@s.param]
          [@s.param]<span>${project.activeSince?datetime}</span>[/@s.param]
          [@s.param]<a href="[@s.url][@s.param name="projectID" value=projectID /][@s.param name="edit" value="true"/][/@s.url]">here</a>[/@s.param]
         [/@s.text]
      </p>
    [/#if]
  </div>
[/#if]

[#-- Submission Message --]
[#if submission?has_content]
  <div class="submission-mode text-center animated flipInX">
    <p>[@s.text name="project.message.submittedOn" ][@s.param]${submission.dateTime}[/@s.param][/@s.text]</p>
  </div>
[/#if]

[#-- Privileges Message --]
[#if !canEdit && !(transaction??) && !(submission?has_content)]
  <p class="readPrivileges">[@s.text name="saving.read.privileges.section" /]</p>
[/#if]


[#-- Program completed Message--]
[#if canSubmit && !submission?has_content && completed]
  <div class="completed-mode text-center animated flipInX">
    <p>[@s.text name="project.message.completed" /]</p>
  </div>
[/#if]

[#-- Concurrence Message --]
<div id="concurrenceMessage" class="text-center" style="display:none">
  <p><span class="glyphicon glyphicon-flash"></span> 
  [@s.text name="project.message.sectionSaved"]
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
    <p>[@s.text name="project.message.concurrence" /]</p>
    <p>[@s.text name="project.message.concurrenceNotEditing"][@s.param] <a href="[@s.url][@s.param name="projectID" value=projectID /][/@s.url]">click here</a> [/@s.param][/@s.text]</p>
  </div>
  
</div>
