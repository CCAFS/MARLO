[#ftl]
[#assign title = "MARLO Publications" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrlMedia}/js/publications/publicationsList.js" ] /]
[#assign customCSS = [
  "${baseUrlCdn}/global/css/customDataTable.css", 
  "${baseUrlMedia}/css/projects/projectDeliverable.css"
  ] 
/]
[#assign currentSection = "additionalReporting" /]
[#assign currentStage = (filterBy)!"all" /]


[#assign breadCrumb = [
  {"label":"publicationsList", "nameSpace":"/publications", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/publicationsListTemplate.ftl" as publicationsList /]

<section class="container">
  [#if reportingActive]
  <article class="row" id="mainInformation">
    <div class="col-md-12">
    
      [#-- Publications not directly linked to a Project (My publications) --]
      <h3 class="headTitle text-center">[@s.text name="publicationsList.yourPublications"/] <br /> <small>([@s.text name="publicationsList.yourPublications.help" /])</small></h3>
      <div class="loadingBlock"></div>
      <div style="display:none">[@publicationsList.publicationsList publications=action.getPublications(true) canValidate=true canEdit=true namespace="/publications" defaultAction="${(crpSession)!}/publication" /]</div>
  
      [#-- Section Buttons --]
      [#if (action.getActualPhase().editable)]
        <div class="buttons">
          <div class="buttons-content">
            <a class="addButton" href="[@s.url action='${crpSession}/addNewPublication'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"> <span class="saveText">[@s.text name="publicationsList.addPublication" /]</span></a>
            <div class="clearfix"></div>
          </div>
        </div>
      [/#if]
      <div class="clearfix"></div>
      <hr/>
      
      [#-- Publications List (Other publications) --]
      <h3 class="headTitle text-center">[@s.text name="publicationsList.otherPublications" /] </h3>
      <div class="loadingBlock"></div>
      <div style="display:none">[@publicationsList.publicationsList publications=action.getPublications(false) canValidate=true canEdit=false namespace="/publications" defaultAction="${(crpSession)!}/publication"/]</div>
    </div>
    
  </article>
  [#else]
    <div class="borderBox text-center">[@s.text name="global.availableReporting" /]</div>
  [/#if]
</section>

[@customForm.confirmJustification action="deletePublication.do" namespace="/${currentSection}" nameId="deliverableID" title="Remove Publications" /]


[#include "/WEB-INF/global/pages/footer.ftl"]
