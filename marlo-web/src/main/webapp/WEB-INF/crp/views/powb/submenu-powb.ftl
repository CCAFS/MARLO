[#ftl]
<ul id="liaisonInstitutions" class="horizontalSubMenu text-center">
  [#if liaisonInstitutions?has_content]
    
    [#--  --]
    <div>
    [#assign itemSizePercentage = (98)/liaisonInstitutions?size /]
    [#list liaisonInstitutions as institution]
      [#assign isActive = (institution.id == liaisonInstitutionID)/]
      [#attempt]
        [#assign isCompleted = (action.isCompleteLiaisonSection(institution.id))!false /]
      [#recover]
        [#assign isCompleted = false /]
      [/#attempt]
      [#assign hasPermission = false/]
      [#assign itemURL][@s.url][@s.param name ="liaisonInstitutionID"]${institution.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign]
      <a class="powb-submenuItem  ${isActive?string('active','')} ${isCompleted?string('completed','')}" style="width:${(itemSizePercentage)}%;" href="${itemURL}">
        [#assign itemName="${(institution.crpProgram.acronym)!institution.acronym}: ${(institution.crpProgram.name)!institution.name}" /]
        <p title="${itemName}">[@utilities.letterCutter string=itemName maxPos=(141)/liaisonInstitutions?size /]</p>
        [#if isCompleted]<img class="check" src="${baseUrl}/global/images/icon-check-tiny${isActive?string('-white','')}.png" />[/#if]
      </a>
    [/#list]
    </div>
    <br />

  [/#if]
</ul>

[#if powb2019]
  <div class="alert alert-warning text-center" role="alert"> 
    <strong> <span class="glyphicon glyphicon-warning-sign"></span> <a href="[@s.url namespace='/powb${powb2019?string("2019", "")}' action="${(crpSession)!}/adjustmentsChanges"][@s.param name ="liaisonInstitutionID"]${(liaisonInstitutionID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" class="alert-link"> Go to POWB ${actualPhase.year} Version </a> </strong>
  </div>
[/#if]