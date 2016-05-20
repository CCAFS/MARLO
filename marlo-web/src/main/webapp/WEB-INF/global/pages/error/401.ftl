[#ftl]
[#assign title = "Unauthorized Access!" /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
<section class="content">
  <article class="fullContent">
    <div class="content center">
      <div class="borderBox">
        [#--<h1 id="error">401</h1>--]
        [#--<img src="${baseUrl}/images/global/page-404-icon.png" />--]
        <p class="errorText primary">[@s.text name="server.error.401" /]</p> 
        [#-- Login Form --]
        [#include "/WEB-INF/global/pages/loginForm.ftl" /]
        [#--<a id="backHome" href="${baseUrl}">[@s.text name="server.error.404.back" /]</a>--]
      </div>
    </div>
  </article>
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]