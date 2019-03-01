[#ftl]
<ul id="liaisonInstitutions" class="horizontalSubMenu text-center">
  [#if liaisonInstitutions?has_content]
    [#--  --]
    <div>
    [#assign itemSizePercentage = (98)/liaisonInstitutions?size /]
    [#list liaisonInstitutions as institution]
      [#assign isActive = (institution.id == liaisonInstitutionID)/]
      [#attempt]
       [#assign isCompleted = (action.isCompleteLiaisonSectionReport2018(institution.id))!false /]
      [#recover]
        [#assign isCompleted = false /]
      [/#attempt]
      
      [#assign hasPermission = false/]
      [#assign itemURL][@s.url][@s.param name ="liaisonInstitutionID"]${institution.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign]
      <a class="powb-submenuItem  ${isActive?string('active','')} ${isCompleted?string('completed','')}" style="width:${(itemSizePercentage)}%;" href="${itemURL}">
        [#assign itemName="${(institution.crpProgram.acronym)!institution.acronym}: ${(institution.crpProgram.name)!institution.name}" /]
        <p title="${itemName}">[@utilities.letterCutter string=itemName maxPos=(140)/liaisonInstitutions?size /]</p>
        [#if isCompleted]<img class="check" src="${baseUrl}/global/images/icon-check-tiny${isActive?string('-white','')}.png" />[/#if]
      </a>
    [/#list]
    </div>
    <br />
  [#else]
    {LiaisonInstitutions not loaded}
  [/#if]
</ul>

[#if !annualReport2018]
  <div class="alert alert-warning text-center" role="alert"> 
    <strong> <span class="glyphicon glyphicon-warning-sign"></span> <a href="[@s.url namespace='/annualReport${annualReport2018?string("2018", "")}' action="${(crpSession)!}/crpProgress"][@s.param name ="liaisonInstitutionID"]${(liaisonInstitutionID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" class="alert-link"> Go to Annual Report ${actualPhase.year} Version </a> </strong>
  </div>
[/#if]