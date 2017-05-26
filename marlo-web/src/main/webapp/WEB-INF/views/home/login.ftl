[#ftl]
[#assign title = "Welcome to MARLO" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/home/login.js" ] /]
[#assign currentSection = "home" /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section> 
  <div class="container">
    <div class="form-group row">
      <div class="col-md-10 col-md-offset-1">
        <h3 class="text-center">[@s.text name="login.marloTitle" /]</h3>
        <p class="text-center">[@s.text name="login.marloDescription" /]  </p>
        [#if !config.production]
        <div class="note">
          <p>[@s.text name="login.testersMessage2"/]</p>
          <p>[@s.text name="login.testersMessage3"/]</p>
        </div>
        [/#if]
      </div>
    </div>
    <div class="form-group row">
      <div class="col-md-10 col-md-offset-1">
        [#include "/WEB-INF/global/pages/loginForm.ftl" /]
      </div>
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]