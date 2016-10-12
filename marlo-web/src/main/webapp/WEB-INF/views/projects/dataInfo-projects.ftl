[#ftl]
<div id="top-quote">
  [#if project?has_content]
    <div id="projectID-quote" class="quote-id" title="[#if project.title?has_content]${project.title}[/#if]">
      <a href="[@s.url namespace="/projects" action='${crpSession}/description'][@s.param name='projectID']${project.id?c}[/@s.param][/@s.url]">
        <p><span>&nbsp${project.id}</span></p>
      </a>
    </div>
  [/#if]
  [#if deliverable?has_content]
    <div class= "aux-quote"><b> - </b></div>
    <div id="deliverableID-quote" class="quote-id" title="[#if deliverable.title?has_content]${deliverable.title}[/#if]">
      <p><span>&nbsp${deliverable.id}</span></p>
    </div>
  [/#if]
  
</div>
<div class="clearfix"></div>