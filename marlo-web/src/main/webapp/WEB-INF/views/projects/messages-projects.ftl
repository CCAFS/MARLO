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
      [#-- Differences --]
      [#include "/WEB-INF/global/macros/historyDiff.ftl" /]
      [#-- Justification --]
      <p><i>${(project.modificationJustification)!}</i></p>
    [/#if]
  </div>
[#else]
  [#-- Submission Message --]
  [#if submission]
    <div class="submission-mode text-center animated flipInX">
      [#assign lastSubmission =action.getProjectSubmissions(projectID)?last /]
      <p>[@s.text name="project.message.submittedOn" ][@s.param]${(lastSubmission.dateTime?string["MMMM dd, yyyy"])!}[/@s.param][@s.param]${(lastSubmission.user.composedCompleteName)!}[/@s.param][/@s.text]</p>
    </div>
  [/#if]
  
  [#-- Privileges Message --]
  [#if (!canEdit && !(transaction??) && !(submission)) || crpClosed]
    [#if crpClosed]
      <p class="readPrivileges">MARLO is closed.</p>
    [#else]
      [#if project.projectEditLeader]
        <p class="readPrivileges">[@s.text name="saving.read.privileges.section" /]</p>
      [#else]
        <p class="readPrivileges">This project is being preset by [@s.text name="global.managementLiaison" /], it will be able for editing by the project leader soon...</p>
      [/#if]    
    [/#if]
  [/#if]
  
  
  [#-- Completed Message--]
  [#if (canSubmit && !submission && completed) && !crpClosed]
    <div class="completed-mode text-center animated flipInX">
      <p>[@s.text name="project.message.completed" /]</p>
    </div>
  [/#if]
  
  [#-- Concurrence Message --]
  [#if !(isListSection??)]
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
        <p>[@s.text name="project.message.concurrence" /] [@s.text name="project.message.concurrenceNotEditing"][@s.param] <a href="[@s.url][@s.param name="projectID" value=projectID /][/@s.url]">click here</a> [/@s.param][/@s.text]</p>
      </div> 
    </div>
  [/#if]

  [#-- Draft Message --]
  [#include "/WEB-INF/global/macros/draftMessage.ftl" /]

[/#if]


