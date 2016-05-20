[#ftl]
[#assign title = "Welcome to CCAFS P&R" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/home/login.js" ] /]
[#assign currentSection = "home" /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]

  [#-- Login Form --]
  [#include "/WEB-INF/global/pages/loginForm.ftl" /]

[#include "/WEB-INF/global/pages/footer.ftl" /]