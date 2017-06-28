[#ftl]
[#assign title = "Welcome to MARLO" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/home/login.js" ] /]
[#assign currentSection = "home" /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section> 
  <div class="container loginPage">
    <div class="form-group row">
      <div class="col-md-offset-1 col-md-6">
        <h3 class="">[@s.text name="login.marloTitle" /]</h3>
        <p class="text-justify">[@s.text name="login.marloDescription" /]  </p>
        [#if !config.production]
        <br />
        <div class="note testerMessage">
          <p class="text-justify">[@s.text name="login.testersMessage2"/]</p>
          <p class="text-justify">[@s.text name="login.testersMessage3"/]</p>
        </div>
        [/#if]
      </div>
      <div class=" col-md-4 ">
        [#include "/WEB-INF/global/pages/loginForm.ftl" /]
      </div>
    </div>
     
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]