[#ftl]
[#assign title][@s.text name="privatePage.title" /][/#assign]
[#assign customCSS = [ "${baseUrlCdn}/global/css/privatePage.css" ] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="content">
  <br />
  <article class="container">
    <div class="content ">
      <div  class="borderBox errorContent">
        <div class="text-center" id="info">
          <div>
            <h2> [@s.text name="privatePage.title" /] </h2>
            <span>  [@s.text name="privatePage.message" /]  </span>
            <br />
            ${(projectExpectedStudy)!''}
            <br />
          </div>
        </div>
      </div>
    </div>
  </article>
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]
