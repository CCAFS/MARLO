[#ftl]
[#assign title = "Welcome to MiLE" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/home/login.js" ] /]
[#assign currentSection = "home" /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section> 
  <div class="container">
    <div class="col-md-6">
      <h3>[@s.text name="login.marloTitle" /]</h3>
      <!--<p>[@s.text name="login.marloDescription" /]  </p> -->
    </div>
    <div class="col-md-offset-1 col-md-5">
      [#-- Login Form --]
      [#include "/WEB-INF/global/pages/loginForm.ftl" /]
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]
