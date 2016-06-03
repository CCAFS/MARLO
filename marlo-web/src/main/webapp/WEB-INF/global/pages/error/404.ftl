[#ftl]
[#assign title = "Page you requested was not found!" /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="container">
  <article class="fullContent">
    <div class="borderBox center">
      <h1 id="error">404</h1>
      <img src="${baseUrl}/images/global/page-404-icon.png" />
      <p class="errorText primary">[@s.text name="server.error.404.1" /]</p> 
      <a id="backHome" href="${baseUrl}">[@s.text name="server.error.404.back" /]</a>
    </div>
  </article>
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]