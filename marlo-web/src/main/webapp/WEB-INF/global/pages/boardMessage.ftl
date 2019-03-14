[#ftl]

[#assign boardMessages = [
  { 'active': false,  'message' : 'MARLO is currently experiencing problems authenticating users.  We are working with the I.T department from CIAT to correct the issue.  We apologise for the inconvience.' },
  { 'active': false,  'message' : 'We have recently implemented major improvements in MARLO’s performance. Please let us know in case you find any issues.' },
  { 'active': true,   'message' : 'We are currently experiencing some technical difficulties with our e-mail provider. As a result, some automated e-mails are not sent. <br > The technical team is working with the provider to fix this as soon as possible. Thank you for your patience.' }
]
/]


[#if logged && (boardMessages?has_content) ]
  [#list boardMessages as bm]
    [#if bm.active]
      <p class="note topNote"> <span class="container">${bm.message}</span> </p>
    [/#if]
  [/#list]
[/#if]
