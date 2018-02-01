[#ftl]
<div id="top-quote">
  [#if capdev?has_content]
    <div id="capdevID-quote" class="quote-id" title="[#if capdev.title?has_content]${capdev.title}[/#if]">
      <a href="[@s.url namespace="/capdev" action='${centerSession}/detailCapdev'][@s.param name='capdevID']${capdev.id?c}[/@s.param][/@s.url]">
        <p><span>&nbsp${capdev.id}</span></p>
      </a>
    </div>
  [/#if]
  
  
</div>
<div class="clearfix"></div>