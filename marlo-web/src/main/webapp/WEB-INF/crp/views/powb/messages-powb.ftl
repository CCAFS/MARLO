[#ftl]

<span id="powbSynthesisID" class="hidden">${(powbSynthesis.id)!}</span>

[#if (currentMenuItem.onlyFlagship)!false || (currentMenuItem.onlyPMU)!false]
<p class="bg-success text-center" style="padding: 18px;">
  <span class="glyphicon glyphicon-flash"></span>
  [#if (currentMenuItem.onlyFlagship)!false ]
    [@s.text name="powb.messages.infoOnly"][@s.param][@s.text name="global.pmu"/][/@s.param][/@s.text]
  [/#if]
  
  [#if (currentMenuItem.onlyPMU)!false ]
    [@s.text name="powb.messages.infoOnly"][@s.param][@s.text name="CrpProgram.leaders"/][/@s.param][/@s.text]
  [/#if]
</p>
[/#if]

[#-- History Message --]
[#if transaction??]
  <div class="history-mode text-center animated flipInX">
    [#if transaction == "-1"]
      <p>[@s.text name="project.message.historyNotFound" /]</p>
    [#else]
      <p>[@s.text name="project.message.historyVersion" ]  
          [@s.param]<span>${powbSynthesis.modifiedBy.composedName?html}</span>[/@s.param]
          [@s.param]<span>${powbSynthesis.activeSince?datetime}</span>[/@s.param]
          [@s.param]<a href="[@s.url][@s.param name="liaisonInstitutionID" value=liaisonInstitutionID /][@s.param name="edit" value="true"/][/@s.url]">here</a>[/@s.param]
         [/@s.text]
      </p>
      [#-- Differences --]
      [#include "/WEB-INF/global/macros/historyDiff.ftl" /]
      [#-- Justification --]
      <p><i>${(powbSynthesis.modificationJustification)!}</i></p>
    [/#if]
  </div>
[#else]
  [#-- Submission Message --]
  [#if submission]
    <div class="submission-mode text-center animated flipInX">
      [#assign lastSubmission =action.getPowbSynthesisSubmissions(powbSynthesisID)?last /]
      <p>[@s.text name="powb.messages.submittedOn" ][@s.param]${(lastSubmission.dateTime?string["MMMM dd, yyyy"])!}[/@s.param][@s.param]${(lastSubmission.user.composedCompleteName)!}[/@s.param][/@s.text]</p>
    </div>
  [/#if]
  
  [#-- Privileges Message --]
  [#if (!canEdit && !(transaction??) && !(submission)) || crpClosed]
    [#if actualPhase.editable]
      <p class="readPrivileges">[@s.text name="saving.read.privileges.section" /]</p>
    [#else]
      <p class="readPrivileges">[@s.text name="message.noCycleEditable"][@s.param]${(actualPhase.composedName)!'null'}[/@s.param][/@s.text]</p>
    [/#if]
  [/#if]
  
  
  [#-- Completed Message--]
  [#if (canSubmit && !submission && completed) && !crpClosed]
    <div class="completed-mode text-center animated flipInX">
      <p>[@s.text name="powb.messages.completed" /]</p>
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
    [#if (concurrenceEnabled)!true ]
    <div id="concurrenceBlock" class="text-center" style="display:none">
      <div class="layer"></div>
      <div class="content">
        <span class="glyphicon glyphicon-lock"></span>
        <p>[@s.text name="project.message.concurrence" /] [@s.text name="project.message.concurrenceNotEditing"][@s.param] <a href="[@s.url][@s.param name="liaisonInstitutionID" value=liaisonInstitutionID /][/@s.url]">click here</a> [/@s.param][/@s.text]</p>
      </div> 
    </div>
    [/#if]
  [/#if]

  [#-- Draft Message --]
  [#include "/WEB-INF/global/macros/draftMessage.ftl" /]

[/#if]


