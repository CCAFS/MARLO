[#ftl]
[#assign title = "MARLO Funding sources" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrlMedia}/js/fundingSources/fundingSourcesList.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/global/customDataTable.css"] /]
[#assign currentSection = "fundingSources" /] 

[#assign breadCrumb = [
  {"label":"fundingSourcesList", "nameSpace":"/fundingSources", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#import "/WEB-INF/global/macros/fundingSourcesListTemplate.ftl" as fundingSourcesList /]
    
<section class="container">
  <article class="fullBlock" id="mainInformation">
  
    [#-- Projects List (My Projects) --]
    <h3 class="headTitle text-center">[@s.text name="Funding Sources"/]</h3>
    <div class="loadingBlock"></div>
    <div style="display:none">[@fundingSourcesList.list projects=myProjects canValidate=true canEdit=true namespace="/fundingSources" defaultAction="${(crpSession)!}/fundingSource" /]</div>
    <div class="clearfix"></div>
    
    [#-- Add --]
    <div class="buttons">
      <div class="buttons-content">
        [#if action.canAddFunding() && (!crpClosed)]<a class="addButton" href="[@s.url namespace="/fundingSources" action='${(crpSession)!}/addNewFundingSources' ][/@s.url]"><span class="saveText">Add Funding Source </span></a>[/#if]
        <div class="clearfix"></div>
      </div>
    </div>
    
    
  </article>
</section>
[@customForm.confirmJustification action="deleteFundingSource.do" namespace="/${currentSection}" nameId="fundingSourceID" title="Remove Funding Source" /]


[#include "/WEB-INF/global/pages/footer.ftl"]
