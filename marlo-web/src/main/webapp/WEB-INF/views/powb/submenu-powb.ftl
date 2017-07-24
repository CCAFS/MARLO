[#ftl]

 <p class="bg-primary" style="padding: 18px; display:none;">
  <span class="glyphicon glyphicon-flash"></span>
  This section is currently being developed by the technical team.</br>
  The idea here is that Flagship Leaders and Program Management Unit can be able to report the POWB.
</p>

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