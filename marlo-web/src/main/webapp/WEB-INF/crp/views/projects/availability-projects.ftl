[#ftl]
[#assign title = "Information not available in this phase!"]
[#assign customCSS = [ "${baseUrlCdn}/global/css/404.css" ] /]

<section class="content">
  <br />
  <article class="container">
    <div class="content ">
      <div  class="borderBox errorContent">
        <div  class="text-center" id="info">
          <div>
          <h2 >Sorry! Information not available!</h2>
          <br />
          <span ><strong>  This information is not available in this phase (${(actualPhase.composedName)!})</strong></span>
          
          </div>          
          <div onclick="history.go(-1);" class="button-goBack block-center">Go back!</div>
        </div>
        
      </div>
    </div>
  </article>
</section>
