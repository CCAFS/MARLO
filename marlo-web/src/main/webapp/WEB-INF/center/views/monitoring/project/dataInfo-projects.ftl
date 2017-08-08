[#ftl]
<div id="top-quote">
  [#if project?has_content]
    <div id="projectID-quote" class="quote-id" title="[#if project.name?has_content]${project.name}[/#if]">
      <a href="[@s.url namespace="/monitoring" action='${centerSession}/projectDescription'][@s.param name='projectID']${project.id?c}[/@s.param][/@s.url]">
        <p><span>&nbsp${project.id}</span></p>
      </a>
    </div>
  [/#if]
  [#if deliverable?has_content]
    <div class= "aux-quote"><b> - </b></div>
    <div id="deliverableID-quote" class="quote-id" title="[#if deliverable.name?has_content]${deliverable.name}[/#if]">
      <p><span>&nbsp${deliverable.id}</span></p>
    </div>
  [/#if]
  
</div>
<div class="clearfix"></div>