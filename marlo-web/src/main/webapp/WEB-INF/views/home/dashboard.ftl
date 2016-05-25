[#ftl]
[#assign title = "Welcome to CCAFS P&R" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/home/login.js" ] /]
[#assign currentSection = "home" /]

[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


<section>
  <div class="container">
   [#include "/WEB-INF/global/pages/breadcrumb.ftl"]
  </div>
  
  <div class="container">
    You are logged in. 
    <br>
    <p> Users Online : ${online} </p>
    
    <h3> users list </h3>
    [#assign users = action.getUsersOnline()]
    [#list users as us]
    <p>${(us.firstName)!'null'}</p>
    [/#list]
    
    <a id="userLogOut" href="[@s.url action="logout" namespace="/" /]">[@s.text name="header.logout" /]</a>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]