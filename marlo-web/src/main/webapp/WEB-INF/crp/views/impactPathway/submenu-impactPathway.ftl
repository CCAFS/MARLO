[#ftl]
[#-- Program (Flagships) --]
<ul id="liaisonInstitutions" class="horizontalSubMenu">
  [#list programs?sort_by("orderIndex") as program]
    [#assign programURL][@s.url][@s.param name ="crpProgramID"]${program.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign]
    [#assign isActive = (program.id == crpProgramID)/]
    <li class="${isActive?string('active','')}" data-toggle="popover" data-trigger="hover" data-placement="top" data-content="${program.composedName}">
      <a href="${programURL}">[#if !centerGlobalUnit]${(program.orderIndex)!}. [@s.text name="global.flagship" /] ${(program.acronym)!} [#else] ${(program.orderIndex)!}. ${(program.composedName)!} [/#if] </a>
    </li>
  [/#list]
</ul>