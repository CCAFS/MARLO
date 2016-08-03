[#ftl]
[#assign title = "Permission denied!" /]
[#assign customCSS = [ "${baseUrl}/css/global/403.css" ] /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="content">
  <br />
  <article class="container">
    <div class="content ">
      <div  class="borderBox errorContent">
        <div  class="text-center" id="info">
          <div>
          <h2 >Forbidden!</h2>
          [#-- <p>Access is denied. Administrator regulations do not allow you to view this page,<br />
             due either to process deadlines or invalid user credentials.
          </p> --]
          <p>Access is denied.<br /> 
          You don't have permission to access the requested page.
          </p>
          
          </div>          
          <div onclick="history.go(-2);" class="button-goBack block-center">Go back!</div>
        </div>
        
      </div>
    </div>
  </article>
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]