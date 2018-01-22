[#ftl]
[#-- History Message --]
[#if transaction??]
  <div class="history-mode text-center animated flipInX">
    [#if transaction == "-1"]
      <p>[@s.text name="deliverable.message.historyNotFound" /]</p>
    [#else]
      <p>[@s.text name="deliverable.message.historyVersion" ]  
          [@s.param]<span>${deliverable.modifiedBy.composedName?html}</span>[/@s.param]
          [@s.param]<span>${deliverable.activeSince?datetime}</span>[/@s.param]
          [@s.param]<a href="[@s.url][@s.param name="deliverableID" value=deliverableID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">here</a>[/@s.param]
         [/@s.text]
      </p>
      [#-- Differences --]
      [#include "/WEB-INF/global/macros/historyDiff.ftl" /]
    [/#if]
  </div>
[#else]

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
    [@s.text name="deliverable.message.sectionSaved"]
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
      <p>[@s.text name="deliverable.message.concurrence" /] [@s.text name="deliverable.message.concurrenceNotEditing"][@s.param] <a href="[@s.url][@s.param name="deliverableID" value=deliverableID /][/@s.url]">click here</a> [/@s.param][/@s.text]</p>
    </div>
  </div>
  
  [#-- Draft Message --]
  [#include "/WEB-INF/global/macros/draftMessage.ftl" /]

[/#if]



