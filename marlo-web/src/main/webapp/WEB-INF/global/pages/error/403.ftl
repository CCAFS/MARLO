[#ftl]
[#assign title = "Permission denied!" /]
[#assign customCSS = [ "${baseUrl}/global/css/403.css" ] /]

[#include "/WEB-INF/${(headerPath)!'crp'}/pages/header.ftl" /]
[#include "/WEB-INF/${(headerPath)!'crp'}/pages/main-menu.ftl" /]

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
          <p>It seems you do not have permissions to access the requested site. <br /> 
          Please contact the technical staff.
          </p>
          
          </div>          
          <div onclick="history.go(-1);" class="button-goBack block-center">Go back!</div>
        </div>
        
      </div>
    </div>
  </article>
</section>

[#include "/WEB-INF/${(headerPath)!'crp'}/pages/footer.ftl"]