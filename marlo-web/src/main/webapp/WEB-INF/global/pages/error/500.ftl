[#ftl]
[#assign title][@s.text name="server.error.500.title" /][/#assign]
[#assign customCSS = [ "${baseUrlMedia}/css/global/500.css" ] /]

[#if !errorPage??]
  [#if crpSession??]
    [#include "/WEB-INF/global/pages/header.ftl" /]
    [#include "/WEB-INF/global/pages/main-menu.ftl" /]
  [/#if]
  
  [#if centerSession??]
    [#include "/WEB-INF/center/global/pages/header.ftl" /]
    [#include "/WEB-INF/center/global/pages/main-menu.ftl" /]
  [/#if]
  
[#else]
  [#include "/WEB-INF/global/pages/header.ftl" /]
  [#include "/WEB-INF/global/pages/main-menu.ftl" /]
 
[/#if]



<section class="content">
  <br />
  <article class="container">
    <div class="content ">
      <div  class="borderBox errorContent">
        <div  class="text-center" id="info">
          <div>
          <h2 >Oops!</h2>
          <span ><strong>  Internal server error.</strong></span>
          <br />
          <br />
          <p>The server encountered an error  <br />
          and could not complete your request.  <br /><br />

          Please report this problem at the online <br />
          technical chat or by sending an email to <br />
          Hector Tobon < h.f.tobon@cgiar.org >. <br /><br />

          Thank you for helping us improve the platform!</p>
          </div>          
          <div onclick="history.go(-1);" class="button-goBack block-center">Go back!</div>
        </div>
        
      </div>
    </div>
  </article>
</section>

[#if !errorPage??]
  [#if crpSession??]
    [#include "/WEB-INF/global/pages/footer.ftl"]
  [/#if]
  
  [#if centerSession??]
    [#include "/WEB-INF/center/global/pages/footer.ftl"]
  [/#if]
[#else]
  [#include "/WEB-INF/global/pages/footer.ftl"]
[/#if]