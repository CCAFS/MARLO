[#ftl]
[#-- Program (Flagships) --]
<ul id="liaisonInstitutions" class="horizontalSubMenu">
  [#list programs as program]
    [#assign programURL][@s.url][@s.param name ="crpProgramID"]${program.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign]
    [#assign isActive = (program.id == crpProgramID)/]
    <li class="${isActive?string('active','')}">
      <a href="${programURL}">[#if !centerGlobalUnit][@s.text name="global.flagship" /] ${(program.acronym)!} [#else] ${(program.composedName)!} [/#if] </a>
    </li>
  [/#list]
</ul>