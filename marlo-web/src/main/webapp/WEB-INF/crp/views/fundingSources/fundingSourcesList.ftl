[#ftl]
[#assign title = "MARLO Funding sources" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs", "malihu-custom-scrollbar-plugin", "select2"] /]
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

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/fundingSourcesListTemplate.ftl" as fundingSourcesList /]
    
<section class="container">
  <article class="fullBlock" id="mainInformation">
     [@fundingSourcesList.institutionsFilter institutions=fundingSourceInstitutions/]
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
        [#-- 
        [#if action.canAddFunding() && (!crpClosed) && action.getActualPhase().editable]<a class="addButton" href="[@s.url namespace="/fundingSources" action='${(crpSession)!}/addNewFundingSources' ][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"><span class="saveText">Add Funding Source </span></a>[/#if]
         --]
        <button type="button" class="addButton" data-toggle="modal" data-target="#myModal">Add Funding Source</button>
        <div class="clearfix"></div>
      </div>
    </div>
    
    
    [#-- Modal to add a Funding source --]
    <!-- Modal -->
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          [@s.form namespace="/fundingSources" action='${(crpSession)!}/addNewFundingSources'  method="GET" enctype="multipart/form-data" cssClass="addNewFundingSource"]
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
              <h4 class="modal-title" id="myModalLabel">Add Funding Source</h4>
            </div>
            <div class="modal-body">
              [#-- Partner(s) managing the funding source --]
              <div class="form-group">
                [@customForm.elementsListComponent name="institutions" elementType="institution" elementList=[] label="fundingSourcesList.add.institutions" listName="managingInstitutionsList" keyFieldName="id" displayFieldName="composedName" forceEditable=true /]
              </div>
              [#-- Agreement status --]
              <div class="row form-group">
                <div class="col-md-6">
                  [@customForm.select name="agreementStatus" i18nkey="fundingSourcesList.add.status" className="agreementStatus"  listName="agreementStatus" keyFieldName=""  displayFieldName="" required=true editable=true /]
                </div>
              </div>
              <hr />
              [#-- Center and Finance code --]
              <div class="row form-group">
                <div class="col-md-6">
                  [@customForm.select name="institutionLead" i18nkey="fundingSourcesList.add.institutionLead" className="institutionLead"  listName="" keyFieldName=""  displayFieldName="" required=true editable=true /]
                </div>
                <div class="col-md-6">
                  [@customForm.input name="financeCode" i18nkey="fundingSourcesList.add.financeCode" help="" placeholder="e.g. OCS (Agresso) Code" className="financeCode" editable=true /]
                </div>
              </div>
              
            </div>
            <div class="modal-footer">
              <input type="hidden" name="phaseID" value="${(actualPhase.id)!}" />
              <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
              <button type="submit" class="btn btn-primary addFundingSourceFromPopup" disabled>Create Funding Source</button>
            </div>
          [/@s.form]
        </div>
      </div>
    </div>
    
    
  </article>
</section>
[@customForm.confirmJustification action="deleteFundingSource.do" namespace="/${currentSection}" nameId="fundingSourceID" title="Remove Funding Source" /]


[#include "/WEB-INF/global/pages/footer.ftl"]
