[#ftl]
[#assign title = "Unauthorized Access!" /]
[#assign customJS = ["${baseUrlMedia}/js/home/login.js" ] /]

[#include "/WEB-INF/${headerPath}/pages/header.ftl" /]
[#include "/WEB-INF/${headerPath}/pages/main-menu.ftl" /]

<section class="content">
    <div class="container"> 
      <div class="borderBox center">
        <p class="errorText primary">[@s.text name="server.error.401" /]</p> 
        <div class="col-md-6 col-md-offset-3">
          [#-- Login Form --]
          [#include "/WEB-INF/global/pages/loginForm.ftl" /]
        </div>
      </div>
    </div>
</section>

[#include "/WEB-INF/${headerPath}global/pages/footer.ftl"]
