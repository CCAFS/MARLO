[#ftl]
[#assign title = "MARLO Funding sources" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs", "malihu-custom-scrollbar-plugin", "select2", "vue"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/fundingSources/fundingSourcesList.js?20190902" 
  ] 
/]
[#assign customCSS = [
  "${baseUrl}/global/css/customDataTable.css",
  "${baseUrlMedia}/css/fundingSources/fundingSourcesList.css?20190902"
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
    <div class="loadingBlock"></div>
    <div style="display:none">
    
      [@fundingSourcesList.institutionsFilter institutions=fundingSourceInstitutions/]
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
          [@fundingSourcesList.selectedInstitutions institutions=filteredInstitutions/]
          <h3 class="headTitle text-center">[@s.text name="fundingSourcesList.active"/] [@s.text name="fundingSourcesList.title"/]</h3>
          [@fundingSourcesList.list projects=myProjects canValidate=true canEdit=true namespace="/fundingSources" defaultAction="${(crpSession)!}/fundingSource" /]
          <hr/>
        </div>
        <div role="tabpanel" class="tab-pane" id="archived-tab">
          [#-- Finished/Archived  -  Funding Sources  --]
          [@fundingSourcesList.selectedInstitutions institutions=filteredInstitutions/]
          <h3 class="headTitle text-center">[@s.text name="fundingSourcesList.archived"/] [@s.text name="fundingSourcesList.title"/]</h3>
          [@fundingSourcesList.archivedList projects=closedProjects canValidate=true canEdit=true namespace="/fundingSources" defaultAction="${(crpSession)!}/fundingSource" /]
        </div>
      </div>
    </div>
    <div class="clearfix"></div>
    
    [#-- Add --]
    <div class="buttons">
      <div class="buttons-content">
        [#if action.canAddFunding() && (!crpClosed) && action.getActualPhase().editable]
          <button type="button" class="addButton" data-toggle="modal" data-target="#fundingSourceAddPopup">Add Funding Source</button>
        [/#if]
        <div class="clearfix"></div>
      </div>
    </div>
    
    
    [#-- Modal to add a Funding source --]
    <!-- Modal -->
    [#if action.canAddFunding() && (!crpClosed) && action.getActualPhase().editable]
      <div class="modal fade" id="fundingSourceAddPopup" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog" role="document">
          [@s.form namespace="/fundingSources" action='${(crpSession)!}/addNewFundingSources'  method="GET" enctype="multipart/form-data" cssClass="addNewFundingSource"]
          <div  class="modal-content">
              <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Add Funding Source</h4>
              </div>
              <div class="modal-body">
                [#-- Partner(s) managing the funding source --]
                <div class="form-group">
                  [@customForm.elementsListComponent name="ins" elementType="institution" elementList=[] label="fundingSourcesList.add.institutions" listName="managingInstitutionsList" keyFieldName="id" displayFieldName="composedName" forceEditable=true onlyElementIDs=true /]
                </div>
                [#-- Agreement status --]
                <div class="row form-group">
                  <div class="col-md-6">
                    [@customForm.select name="budgetTypeID" i18nkey="fundingSourcesList.add.budgetType" className="budgetType"  listName="budgetTypes" keyFieldName=""  displayFieldName="" required=true editable=true /]
                  </div>
                  <div class="col-md-6">
                    [@customForm.select name="agreementStatus" i18nkey="fundingSourcesList.add.status" className="agreementStatus"  listName="agreementStatus" keyFieldName=""  displayFieldName="" required=false editable=true /]
                  </div>
                </div>
                <hr />
                <br>
                [#-- Center and Finance code --]
                <div class="row form-group">
                  <div class="col-md-6">
                    [@customForm.select name="institutionLead" i18nkey="fundingSourcesList.add.institutionLead" className="institutionLead"  listName="" keyFieldName=""  displayFieldName="" required=true editable=true /]
                  </div>
                  <div class="col-md-6">
                    [@customForm.input name="financeCode" i18nkey="fundingSourcesList.add.financeCode" help="" placeholder="e.g. OCS (Agresso) Code" className="financeCode" editable=true required=true /]
                  </div>
                </div>
                
                [#-- VueJS App --]
                <div id="vueApp" class="form-group">
                  <span v-for="message in messages">
                    <span v-bind:class="'label label-' + message.type">{{ message.title }}</span>
                  </span>
                  
                  <div v-if="fundingSources.length" class="messagesBlock">
                    <hr />
                    <p> <strong>This finance code is already used. Please click on the following one if you want to edit.</strong></p>
                    <ul class="list-group">
                      <li class="list-group-item" v-for="fs in fundingSources">
                        <span class="pull-right label label-info">{{ fs.type }}</span>
                        <a target="_blank" v-bind:href="'${baseUrl}/fundingSources/${crpSession}/fundingSource.do?fundingSourceID='+ fs.id +'&edit=true&phaseID=${(actualPhase.id)!}'">
                          <small><strong> FS{{ fs.id }}</strong> - {{ fs.financeCode }} | {{ fs.name }} </small>
                        </a>
                      </li>
                    </ul>
                  </div>
                
                  <br> 
                  [#-- Progress Bar --]
                  <div class="progress" style="height: 4px;">
                    <div class="progress-bar progress-bar-info" role="progressbar" v-bind:aria-valuenow="progress()" aria-valuemin="0" aria-valuemax="100" :style="{width: progress() + '%'}">
                      <span class="sr-only">{{ progress() }}% Complete</span>
                    </div>
                  </div>
                  
                  <div class="text-right">
                    <input type="hidden" name="partnerIDs" value="" />
                    <input type="hidden" name="phaseID" value="${(actualPhase.id)!}" />
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary addFundingSourceFromPopup" v-bind:disabled="!isValid" v-if="!fundingSources.length">Create Funding Source</button>
                  </div>
                  
                  
                </div>
              </div>
               
          </div>
          [/@s.form]
        </div>
      </div>
    [/#if]
    
  </article>
</section>
[@customForm.confirmJustification action="deleteFundingSource.do" namespace="/${currentSection}" nameId="fundingSourceID" title="Remove Funding Source" /]


[#include "/WEB-INF/global/pages/footer.ftl"]
