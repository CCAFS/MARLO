[#ftl]
<ul id="liaisonInstitutions" class="horizontalSubMenu text-center">
  [#list liaisonInstitutions as institution]
    [#assign isActive = (institution.id == liaisonInstitutionID)/]
    [#assign isCompleted = false /]
    [#assign hasPermission = true/]
    <li class="${isActive?string('active','')} ${hasPermission?string('canEdit','')}">
      <a href="[@s.url][@s.param name ="liaisonInstitutionID"]${institution.id}[/@s.param][@s.param name ="edit"]true[/@s.param][/@s.url]">
        ${(institution.crpProgram.acronym)!institution.acronym}: [@utilities.wordCutter string=(institution.crpProgram.name)!institution.name maxPos=25 /]</a>
      [#if isCompleted] <p class="synthesisCompleted"> <img src="${baseUrlMedia}/images/global/icon-check-tiny${isActive?string('-white','')}.png" /> </p> [/#if]
    </li>
  [/#list]
</ul>