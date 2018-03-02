[#ftl]

[#--  --assign boardMessages = [
  {'message' : 'MARLO is currently experiencing problems authenticating users.  We are working with the I.T department from CIAT to correct the issue.  We apologise for the inconvience.' },
  {'message' : 'We have recently implemented major improvements in MARLO’s performance. Please let us know in case you find any issues.' }
]
/--]

[#if  boardMessages??]
  [#list boardMessages as bm]<p class="note topNote">${bm.message}</p>[/#list]
[/#if]
