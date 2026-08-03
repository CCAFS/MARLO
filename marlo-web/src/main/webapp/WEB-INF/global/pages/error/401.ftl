[#ftl]
[#assign title = "Welcome to MARLO" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrlCdn}/global/js/login/login.js?20260803"] /]
[#assign currentSection = "home" /]
[#assign avoidHeader = true /]
[#-- Unauthorized access: the login form headline replaces the generic 401 error message --]
[#assign loginHeadlineKey = "login.headline.unauthorized" /]

[#include "/WEB-INF/global/pages/header.ftl" /]

<div class="login-split">
  <div class="login-left">
    <div class="login-brand">
      <img class="login-brand-logo" src="${baseUrlCdn}/global/images/cgiar_logo_black.png" alt="Alliance of Bioversity International and CIAT / CGIAR" />
      <span class="login-brand-divider"></span>
      <img class="login-brand-marlo" src="${baseUrlCdn}/global/images/login/marlo-logo.png" alt="MARLO" />
    </div>
    [#include "/WEB-INF/global/pages/loginForm.ftl" /]
  </div>
  <div class="login-right">
    <img class="login-right-photo" src="${baseUrlCdn}/global/images/login/login-bg-photo.jpg" alt="" />
  </div>
</div>

[#assign avoidFooter = true /]
[#include "/WEB-INF/global/pages/footer.ftl" /]
