[#ftl]
[#assign title = "Unauthorized Access!" /]
[#assign customJS = ["${baseUrl}/global/js/login/login.js?20180504"] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="content">
  <div class="col-md-8 col-center">
    <p class="errorText primary center">[@s.text name="server.error.401" /]</p> 
    <div class="col-md-offset-3 col-xs-8 col-sm-6 col-md-5 col-center">
      [#-- Login Form --]
      [#include "/WEB-INF/global/pages/loginForm.ftl" /]
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]
