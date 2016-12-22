[#ftl]

[#assign boardMessages = [
  {'message' : 'The MARLO Support team will be out of the office from Dec 22 to Jan 3. We will be back online to respond to your message after the new year.' }
]
/]

[#if  boardMessages??]
  [#list boardMessages as bm]<p class="note">${bm.message}</p>[/#list]
[/#if]
