[#ftl]
<div id="top-quote">
  [#if project?has_content]
    [#if action.isAiccra()]
      <div id="clusterID-quote" class="quote-id" title="C${(project.id)!} - [#if (project.projectInfo.title?has_content)!false]${(project.projectInfo.title)!}[/#if]">
        <a href="[@s.url namespace="/projects" action='${crpSession}/description'][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
          <p><span>&nbsp${(project.acronym)!}</span></p>
        </a>
      </div>
    [#else]
      <div id="projectID-quote" class="quote-id" title="[#if (project.projectInfo.title?has_content)!false]${(project.projectInfo.title)!}[/#if]">
        <a href="[@s.url namespace="/projects" action='${crpSession}/description'][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
          <p><span>&nbsp${project.id}</span></p>
        </a>
      </div>
    [/#if]
  [/#if]
  [#if deliverable?has_content]
    <div class= "aux-quote"><b> - </b></div>
    <div id="deliverableID-quote" class="quote-id" title="[#if (deliverable.deliverableInfo.title?has_content)!false]${(deliverable.deliverableInfo.title)!}[/#if]">
      <p><span>&nbsp${deliverable.id}</span></p>
    </div>
  [/#if]
  
</div>
<div class="clearfix"></div>