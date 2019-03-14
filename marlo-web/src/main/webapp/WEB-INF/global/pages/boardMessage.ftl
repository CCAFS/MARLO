[#ftl]

[#assign boardMessages = (marloMessages)![] /]

[#if logged && (boardMessages?has_content) ]
  [#list boardMessages as bm]
      <p class="note topNote"> <span class="container">${bm.messageValue}</span> </p>
  [/#list]
[/#if]
