[#ftl]
[#-- Research Areas --]
<div class="sectionSubMenu">
  [#-- Nav tabs --]
  <ul class="nav nav-tabs" role="tablist">
    [#list researchAreas as area]
    [#assign isActive = (area.id == areaID)/]
    [#assign link = "#area-${area.id}" /]
      <li role="areas" class="${isActive?string('active','')}">
        [#-- Getting first program --]
        [#list area.researchPrograms as program][#if program_index == 0][#assign link][@s.url action='${centerSession}/projectList'][@s.param name="programID" value=program.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign][/#if][/#list]
        <a href="${link}" aria-controls="home" role="tab" [#-- data-toggle="tab" --]>${area.acronym}</a>
      </li>
    [/#list]
  </ul>
  [#-- Tab panes --]
  <div class="tab-content">
    [#list researchAreas as area ]
    [#assign isActive = (area.id == areaID)/]
    <div role="tabpanel" class="tab-pane ${isActive?string('active','')}" id="area-${area.id}">
      [#if area.researchPrograms?has_content]
      <ul>
        [#list area.researchPrograms as program]
          [#assign isProgramActive = (program.id == programID)/]           
          <li class="${isProgramActive?string('active','')}"> <a href="[@s.url action='${centerSession}/projectList'][@s.param name="programID" value=program.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">${program.name}</a> </li>
        [/#list]
      </ul>
      [#else]
        <p class="emptyMessage text-center">No programs added.</p>
      [/#if]
    </div>
    [/#list]
  </div>
</div>