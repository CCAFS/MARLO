[#ftl]
[#-- Projects data information --]
[#include "/WEB-INF/crp/views/projects/dataInfo-projects.ftl" /]



[#assign audit = {
    "id": projectID,
    "element": project,
    "name": "projectID"
  } 
/]

[#-- History Message --]
[#if transaction??]
  <div class="history-mode text-center animated flipInX">
    [#if transaction == "-1"]
      <p>[@s.text name="message.historyNotFound" /]</p>
    [#else]
      <p>
        [@s.text name="message.historyVersion" ]
          [@s.param]<span>${audit.element.modifiedBy.composedName?html}</span>[/@s.param]
          [@s.param]<span>${audit.element.activeSince?datetime}</span>[/@s.param]
          [@s.param]<a href="[@s.url][@s.param name=audit.name value=audit.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">here</a>[/@s.param]
        [/@s.text]
      </p>
      [#-- Differences --]
      [#include "/WEB-INF/global/macros/historyDiff.ftl" /]
      [#-- Justification --]
      <p><i>${(audit.element.modificationJustification)!}</i></p>
    [/#if]
  </div>
[#else]
  [#-- Submission Message --]
  [#if submission]
    <div class="submission-mode text-center animated flipInX">
      [#assign lastSubmission =action.getProjectSubmissions(audit.id)?last /]
      <p>
        [@s.text name="message.submittedOn" ]
          [@s.param]${(lastSubmission.dateTime?string["MMMM dd, yyyy"])!}[/@s.param]
          [@s.param]${(lastSubmission.user.composedCompleteName)!}[/@s.param]
          [@s.param]Project[/@s.param]
        [/@s.text]
        [#if centerGlobalUnit]in ${(project.projectInfo.phase.crp.acronym)!}[/#if]
      </p>
    </div>
  [/#if]
 
  [#-- Privileges Message --]
  [#if (!canEdit && !(transaction??)) || crpClosed]
    [#if crpClosed]
      <p class="readPrivileges">MARLO is closed.</p>
    [#else]
      [#if !action.getActualPhase().editable]
        <p class="readPrivileges">[@s.text name="phase.read.privileges.section" /]</p>
      [/#if]
      
      [#--  Project --]
      [#if project??]
        [#if project.projectInfo.isProjectEditLeader()]
          [#if !(action.hasPermission("statusDescription")) ]
            [#assign actualPhaseAR = (action.isReportingActive())!false]
            [#if actualPhaseAR && ((currentStage!'') == "budgetByPartners")]
              <p class="readPrivileges">This section is read-only for Annual Report</p>
            [#else]
              [#if submission]
                <p class="readPrivileges">[@s.text name="saving.submitted.privileges.section" /]</p>
              [#else]
                <p class="readPrivileges">[@s.text name="saving.read.privileges.section" /]</p>
               [/#if]
            [/#if]
          [/#if]
        [#else]
          [#if !editStatus]
            <p class="readPrivileges">[@s.text name="project.preset.messagge" /]</p>
          [/#if]    
        [/#if]
      [/#if]
          
    [/#if]
  [/#if]
  
  
  [#-- Completed Message--]
  [#if (canSubmit && !submission && completed) && !crpClosed]
    <div class="completed-mode text-center animated flipInX">
      <p>
        [@s.text name="message.completed"]
          [@s.param]project[/@s.param]
        [/@s.text]
      </p>
    </div>
  [/#if]
  
  [#-- Concurrence Message --]
  [#if !(isListSection??)]
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
        <p>[@s.text name="message.concurrence" /] [@s.text name="message.concurrenceNotEditing"][@s.param] <a href="[@s.url][@s.param name=audit.name value=audit.id /][@s.param name="phaseID" value="${(actualPhase.id)!}" /][/@s.url]">click here</a> [/@s.param][/@s.text]</p>
      
        [#if action.canAccessSuperAdmin()]<i><small> Only for superadmins: <a href="#" class="removeConcurrenceBlock">[CLOSE]</a></small></i>[/#if]
      </div> 
    </div>
  [/#if]

  [#-- Draft Message --]
  [#include "/WEB-INF/global/macros/draftMessage.ftl" /]

[/#if]