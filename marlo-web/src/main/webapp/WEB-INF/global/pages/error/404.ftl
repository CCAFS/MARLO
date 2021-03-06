[#ftl]
[#assign title = "Page you requested was not found!" /]
[#assign customCSS = [ "${baseUrlCdn}/global/css/404.css" ] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="content">
  <br />
  <article class="container">
    <div class="content ">
      <div  class="borderBox errorContent">
        <div  class="text-center" id="info">
          <div>
          <h2 >Sorry! Page not found!</h2>
          <br />
          <span ><strong>  The page you requested was not found</strong></span>
          
          </div>          
          <div onclick="history.go(-1);" class="button-goBack block-center">Go back!</div>
        </div>
        
      </div>
    </div>
  </article>
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]