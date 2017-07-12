[#ftl]
[#assign title = "Unauthorized Access!" /]
[#assign customJS = ["${baseUrlMedia}/js/home/login.js" ] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]

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
[#include "/WEB-INF/global/pages/footer.ftl"]