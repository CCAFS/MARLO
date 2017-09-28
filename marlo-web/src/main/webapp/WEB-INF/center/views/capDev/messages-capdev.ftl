[#ftl]
[#-- Projects data information --]


[#-- History Message --]
[#if transaction??]
  <div class="history-mode text-center animated flipInX">
    [#if transaction == "-1"]
      <p>[@s.text name="capdev.message.historyNotFound" /]</p>
    [#else]
      <p>[@s.text name="capdev.message.historyVersion" ]  
          [@s.param]<span>${capdev.modifiedBy.composedName?html}</span>[/@s.param]
          [@s.param]<span>${capdev.activeSince?datetime}</span>[/@s.param]
          [@s.param]<a href="[@s.url][@s.param name="capdevID" value=capdevID /][@s.param name="edit" value="true"/][/@s.url]">here</a>[/@s.param]
         [/@s.text]
      </p>
      [#-- Differences --]
      [#include "/WEB-INF/global/macros/historyDiff.ftl" /]
      [#-- Justification --]
      <p><i>${(capdev.modificationJustification)!}</i></p>
    [/#if]
  </div>


[/#if]


