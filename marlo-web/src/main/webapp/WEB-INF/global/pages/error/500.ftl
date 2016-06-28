[#ftl]
[#assign title][@s.text name="server.error.500.title" /][/#assign]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="content">
  <br />
  <article class="container">
    <div class="content center">
      <div class="borderBox">
        <h1 id="error">500</h1>
        <img src="${baseUrl}/images/global/page-404-icon.png" />
        <p class="errorText primary">[@s.text name="server.error.500.description" /]</p> 
        <a id="backHome" href="#" onclick="history.go(-2);">[@s.text name="server.error.404.back" /]</a>
      </div>
    </div>
  </article>
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]