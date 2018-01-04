[#ftl]
[#assign title = "MARLO Funding sources" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrlMedia}/js/fundingSources/fundingSourcesList.js" ] /]
[#assign customCSS = [
  "${baseUrl}/global/css/customDataTable.css",
  "${baseUrlMedia}/css/fundingSources/fundingSourcesList.css"
  ] 
/]
[#assign currentSection = "fundingSources" /] 

[#assign breadCrumb = [
  {"label":"fundingSourcesList", "nameSpace":"/fundingSources", "action":""}
]/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/fundingSourcesListTemplate.ftl" as fundingSourcesList /]
    
<section class="container">
  <article class="fullBlock" id="mainInformation">
    
    <div class="loadingBlock"></div>
    <div style="display:none">
      <!-- Nav tabs -->
      <ul class="nav nav-tabs" role="tablist">
        <li role="presentation" class="active">
          <a href="#active-tab" aria-controls="home" role="tab" data-toggle="tab">
            <strong><span class="glyphicon glyphicon-flag"></span>  [@s.text name="fundingSourcesList.active"/] </strong> <br /><small>[@s.text name="fundingSourcesList.active.help"/]</small>
          </a>
        </li>
        <li role="presentation">
          <a href="#archived-tab" aria-controls="profile" role="tab" data-toggle="tab">
            <strong><span class="glyphicon glyphicon-inbox"></span> [@s.text name="fundingSourcesList.archived"/] </strong> <br /><small>[@s.text name="fundingSourcesList.archived.help"/]</small>
          </a>
        </li>
      </ul>
    
      <!-- Tab panes -->
      <div class="tab-content">
        <div role="tabpanel" class="tab-pane active" id="active-tab">
          [#-- On Going -  Funding Sources --]
          <h3 class="headTitle text-center">[@s.text name="fundingSourcesList.active"/] [@s.text name="fundingSourcesList.title"/]</h3>
          [@fundingSourcesList.list projects=myProjects canValidate=true canEdit=true namespace="/fundingSources" defaultAction="${(crpSession)!}/fundingSource" /]
          <hr/>
        </div>
        <div role="tabpanel" class="tab-pane" id="archived-tab">
          [#-- Finished/Archived  -  Funding Sources  --]
          <h3 class="headTitle text-center">[@s.text name="fundingSourcesList.archived"/] [@s.text name="fundingSourcesList.title"/]</h3>
          [@fundingSourcesList.archivedList projects=closedProjects canValidate=true canEdit=true namespace="/fundingSources" defaultAction="${(crpSession)!}/fundingSource" /]
        </div>
      </div>
    </div>
    <div class="clearfix"></div>
    
    [#-- Add --]
    <div class="buttons">
      <div class="buttons-content">
        [#if action.canAddFunding() && (!crpClosed) && action.getActualPhase().editable]<a class="addButton" href="[@s.url namespace="/fundingSources" action='${(crpSession)!}/addNewFundingSources' ][/@s.url]"><span class="saveText">Add Funding Source </span></a>[/#if]
        <div class="clearfix"></div>
      </div>
    </div>
    
    
  </article>
</section>
[@customForm.confirmJustification action="deleteFundingSource.do" namespace="/${currentSection}" nameId="fundingSourceID" title="Remove Funding Source" /]


[#include "/WEB-INF/crp/pages/footer.ftl"]
