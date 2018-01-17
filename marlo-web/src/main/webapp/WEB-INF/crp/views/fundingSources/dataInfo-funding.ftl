[#ftl]
<div id="top-quote">
  [#if fundingSource?has_content]
    <div id="fundingID-quote" class="quote-id" title="[#if fundingSource.title?has_content]${fundingSource.title}[/#if]">
      <a href="[@s.url namespace="/fundingSources" action='${crpSession}/fundingSource'][@s.param name='fundingSourceID']${fundingSource.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
        <p><span>&nbsp${fundingSource.id}</span></p>
      </a>
    </div>
  [/#if]
  
</div>
<div class="clearfix"></div>