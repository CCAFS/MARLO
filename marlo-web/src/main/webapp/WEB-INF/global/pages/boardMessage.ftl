[#ftl]
[#if  boardMessages??]
  [#list boardMessages as bm]<p class="note">${bm.message}</p>[/#list]
[/#if]
