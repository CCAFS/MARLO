[#ftl]
[#-- Projects data information --]
[#include "/WEB-INF/center/views/capDev/dataInfo-capdev.ftl" /]

[#-- History Message --]
[#if transaction??]
  <div class="history-mode text-center animated flipInX">
    [#if transaction == "-1"]
      <p>[@s.text name="message.historyNotFound" /]</p>
    [#else]
      <p>
        [@s.text name="message.historyVersion" ]  
          [@s.param]<span>${capdev.modifiedBy.composedName?html}</span>[/@s.param]
          [@s.param]<span>${capdev.activeSince?datetime}</span>[/@s.param]
          [@s.param]<a href="[@s.url][@s.param name="capdevID" value=capdevID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">here</a>[/@s.param]
        [/@s.text]
      </p>
      [#-- Differences --]
      [#include "/WEB-INF/global/macros/historyDiff.ftl" /]
      [#-- Justification --]
      <p><i>${(capdev.modificationJustification)!}</i></p>
    [/#if]
  </div>
[#else]
  [#-- Submission Message --]
  [#if submission]
    <div class="submission-mode text-center animated flipInX">
      [#assign lastSubmission =action.getCapdevSubmissions(capdevID)?last /]
      <p>
        [@s.text name="message.submittedOn" ]
          [@s.param]${(lastSubmission.dateTime?string["MMMM dd, yyyy"])!}[/@s.param]
          [@s.param]${(lastSubmission.user.composedCompleteName)!}[/@s.param]
          [@s.param]Capacity Development[/@s.param]
        [/@s.text]
      </p>
    </div>
  [/#if]
 
  [#-- Privileges Message --]
  [#if (!canEdit && !(transaction??) && !(submission)) || crpClosed]
    [#if crpClosed]
      <p class="readPrivileges">MARLO is closed.</p>
    [#else]
      [#if !action.getActualPhase().editable]
        <p class="readPrivileges">[@s.text name="phase.read.privileges.section" /]</p>
      [/#if]
    [/#if]
  [/#if]
  
  
  [#-- Completed Message--]
  [#if (canSubmit && !submission && completed) && !crpClosed]
    <div class="completed-mode text-center animated flipInX">
      <p>
        [@s.text name="message.completed"]
          [@s.param]Capacity Development[/@s.param]
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
        <p>
          [@s.text name="message.concurrence" /]
          [@s.text name="message.concurrenceNotEditing"]
            [@s.param] <a href="[@s.url][@s.param name="capdevID" value=capdevID /]
            [@s.param name="phaseID" value="${(actualPhase.id)!}" /][/@s.url]">click here</a> [/@s.param]
          [/@s.text]
        </p>
        
        [#if action.canAccessSuperAdmin()]<i><small> Only for superadmins: <a href="#" class="removeConcurrenceBlock">[CLOSE]</a></small></i>[/#if]
      </div> 
    </div>
  [/#if]

  [#-- Draft Message --]
  [#include "/WEB-INF/global/macros/draftMessage.ftl" /]

[/#if]


