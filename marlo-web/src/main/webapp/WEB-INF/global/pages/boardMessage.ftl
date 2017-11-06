[#ftl]

[#assign boardMessages = [
  {'message' : 'We have recently implemented major improvements in MARLO’s performance. Please let us know in case you find any issues.' }
]
/]

[#if  boardMessages??]
  [#list boardMessages as bm]<p class="note topNote">${bm.message}</p>[/#list]
[/#if]
