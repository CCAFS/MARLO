[#ftl]
[#if differences??]
  [#-- Changed fields is updated by global.js --]
  <p class="changesDetected">Showing  <strong></strong> changed fields.</p>
  <ul style="display:none">
    [#list differences as diff]
      <li class="diffContent-${diff.id}">
        <span class="id"><strong>${(diff?string)!}</strong></span>
        <span class="added">${(diff.added?string)!}</span>
        <span class="oldValue">${(diff.oldValue)!}</span>
        <span class="newValue">${(diff.newValue)!}</span>
      </li>
    [/#list]
  </ul>
  
  [#-- Inject Javascript dependences --]
  [#assign globalLibs = globalLibs + [  "google-diff-match-patch", "jquery-pretty-text-diff" ]   /]
  [#assign customJS = customJS +  [ "${baseUrl}/global/js/historyDiff.js" ]   /]
[/#if]