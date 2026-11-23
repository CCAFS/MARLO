[#ftl]
[#assign title = "Welcome to MARLO" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrlCdn}/global/js/login/login.js?20261105"] /]
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
    [#-- A2-2746: the panel is display:none below 768px (customLogin.css), but an <img> there still downloads.
         The first source matches that width and hands it a 1x1 GIF, so a phone fetches no photo at all.
         AVIF variants follow; sizes is max(50vw, 150vh) because object-fit: cover fills the panel's height
         with a 3:2 photo. The original JPEG stays as the fallback for browsers without AVIF --]
    <picture>
      <source media="(max-width: 767px)" srcset="data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==" />
      <source type="image/avif" sizes="max(50vw, 150vh)"
        srcset="${baseUrlCdn}/global/images/login/login-bg-photo-800.avif 800w, ${baseUrlCdn}/global/images/login/login-bg-photo-1200.avif 1200w, ${baseUrlCdn}/global/images/login/login-bg-photo-1600.avif 1600w" />
      <img class="login-right-photo" src="${baseUrlCdn}/global/images/login/login-bg-photo.jpg" alt="" />
    </picture>
  </div>
</div>

[#assign avoidFooter = true /]
[#include "/WEB-INF/global/pages/footer.ftl" /]
