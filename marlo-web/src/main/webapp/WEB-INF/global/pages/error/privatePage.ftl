[#ftl]
[#assign title]Private Page[/#assign]
[#assign customCSS = [ "${baseUrl}/global/css/privatePage.css" ] /]

[#include "/WEB-INF/global/pages/header.ftl" /]

<section class="content">
  <br />
  <article class="container">
    <div class="content ">
      <div  class="borderBox errorContent">
        <div  class="text-center" id="info">
          <div>
            <h2 >Private Page</h2>
            <span ><strong>  This report is only visible to authorized users and Administrators.  </strong></span>
            <br />
            <br /> 
          </div>
        </div>
      </div>
    </div>
  </article>
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]
