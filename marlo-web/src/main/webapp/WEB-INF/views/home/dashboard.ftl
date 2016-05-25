[#ftl]
[#assign title = "Welcome to CCAFS P&R" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/home/login.js" ] /]
[#assign currentSection = "home" /]


[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


<section class="marlo-content">
  <div class="container">
    <p> Users Online : ${online} </p>
    
    <h3> Users list </h3>
    [#assign users = action.getUsersOnline()]
    [#list users as us]
    <p>${(us.firstName)!'null'}</p>
    [/#list]
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]