[#ftl]
[#assign title = "Unauthorized Access!" /]
[#assign customJS = ["${baseUrlMedia}/js/home/login.js" ] /]

[#include "/WEB-INF/${(headerPath)!'crp'}/pages/header.ftl" /]
[#include "/WEB-INF/${(headerPath)!'crp'}/pages/main-menu.ftl" /]

<section class="content">
  <div class="col-md-10 col-center">
    <p class="errorText primary center">[@s.text name="server.error.401" /]</p> 
    <div class="col-md-offset-3 col-xs-8 col-sm-6 col-md-5 col-center">
      [#-- Login Form --]
      [#include "/WEB-INF/global/pages/newLoginForm.ftl" /]
    </div>
  </div>
</section>

[#include "/WEB-INF/${(headerPath)!'crp'}/pages/footer.ftl"]
