[#ftl]
[#assign title = "MARLO Funding Sources" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-${fundingSource.id}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload", "datatables.net", "datatables.net-bs","flag-icon-css",  "vue"] /]
[#assign customJS = [
  "${baseUrlCdn}/global/js/fieldsValidation.js",
  "${baseUrlMedia}/js/fundingSources/fundingSource.js?20201123",
  "${baseUrlMedia}/js/fundingSources/syncFundingSource.js?20201105",
  "${baseUrlCdn}/global/js/autoSave.js" 
  ]
/]
[#assign customCSS = ["${baseUrlMedia}/css/fundingSources/fundingSource.css?20201007"] /]
[#assign currentSection = "fundingSources" /]
[#assign breadCrumb = [
  {"label":"fundingSourcesList", "nameSpace":"/fundingSources", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#assign showEXtDate = ((fundingSource.fundingSourceInfo.status == 4)!false) || (((fundingSource.fundingSourceInfo.extensionDate?has_content)!false) && !editable)/]
[#assign requiredCode = ((fundingSource.fundingSourceInfo.status != 1) && (fundingSource.fundingSourceInfo.status != 7))!true /]
[#assign startYear = ((fundingSource.fundingSourceInfo.startDate?string.yyyy)?number)!currentCycleYear /]
[#assign endYear = ((fundingSource.fundingSourceInfo.endDate?string.yyyy)?number)!startYear /]
[#assign extensionYear = ((fundingSource.fundingSourceInfo.extensionDate?string.yyyy)?number)!endYear /]

[#if showEXtDate]
  [#assign fundingSourceYears = startYear .. extensionYear/]
[#else]
  [#assign fundingSourceYears = startYear .. endYear/]
[/#if]

[#assign hasW1W2Permission = (action.w1Permission())!false /]
[#assign isW1W2 = (fundingSource.fundingSourceInfo.budgetType.id == 1)!hasW1W2Permission /]
[#assign w1w2TagValue = (fundingSource.fundingSourceInfo.w1w2)!false /]
   

[#-- Finance code module --]
[#assign isSynced =false ]
[#if fundingSource.fundingSourceInfo?has_content]
  [#assign isSynced = (fundingSource.fundingSourceInfo.synced)!false ]
[/#if]

<section class="container">
  <article class="" id="mainInformation">
  [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
  
  <div class="loading-neutral hideOnLoading" style="display: none;"></div>
  <div class="col-md-offset-1 col-md-10">
    [#-- Messages --]
    [#include "/WEB-INF/crp/views/fundingSources/messages-fundingSource.ftl" /]
    
    <div class="showOnLoading">
      [#-- General Information --]
      <div class="row">
        <div class="col-md-8">
          <h4 class="headTitle">General information</h4>
         </div>
         <div class="col-md-4">
         [#if (action.canDuplicateFunding() && !crpClosed) && action.getActualPhase().editable]
            <a id="copyDeliverable-${fundingSource.id}" class="btn btn-default btn-xs duplicate-button" href="[@s.url namespace=namespace action="${(crpSession)!}/copy"][@s.param name='fundingSourceID']${fundingSource.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" title="">
              Duplicate Funding Source <img src="${baseUrlCdn}/global/images/duplicate_enabled.png" height="15px"/> 
            </a>
          [/#if]
         </div>
       </div>
      
      <div class="borderBox">
        [#-- Loading --]
        <div class="loading syncBlock" style="display:none"></div>
        [@initialInformation /]
      </div>
      <div class="borderBox">
        [#-- Loading --]
        <div class="loading contentBlok" style="display:none"></div>
        [@generalInformation /]
      </div>
      
      [#-- Location Information --]
      <h4 class="headTitle">Location information</h4>
      <div class="borderBox">
        
        [@locationInformation /]
      </div>
      
      [#-- Annual funding source contribution and Grand Amount --]
      <h4 class="headTitle" >Annual funding source contribution</h4>
      <div>[@fundingSourceBudget /]</div>
    </div>

    [#-- Section Buttons & hidden inputs--]
    [#include "/WEB-INF/crp/views/fundingSources/buttons-fundingSources.ftl" /]
  </div>
  
  [/@s.form] 
  </article>
</section>

[#-- Modal to map Funding Source to a project --]
[#if editable]
<div class="modal fade" id="mapFundingToProject" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel"> [@s.text name="mapFunding.title" /] in {{ year }}</h4>
      </div>
      <div class="modal-body">
        <div class="loading popup" v-if="modalLoading"></div>
        <p class="note" v-if="message">{{ message }}</p>
      
        [#-- Institution --]
        <div class="form-group">
          [@customForm.select name="institutionID" i18nkey="mapFunding.institution" className=""  listName="fundingSource.institutions" keyFieldName="institution.id"  displayFieldName="institution.composedName" required=true editable=editable /]
        </div>
        
        <div class="step2" style="display:none">
          [#-- Project --]
          <div class="form-group">
            [@customForm.select name="projectID" i18nkey="mapFunding.project" className=""  listName="" keyFieldName=""  displayFieldName="" help="mapFunding.project.help" helpIcon=true required=true editable=editable /]
           </div>
           
           [#if action.hasSpecificities('crp_project_budget_zero')]
              [#assign amountZeroClass = "amountZero" /]
              [#else]
              [#assign amountZeroClass = "" /]
            [/#if]
           
          <div class="form-group ">
              [@customForm.textArea name="amount" i18nkey="mapFunding.amount" className="currencyInput ${amountZeroClass}" required=true editable=editable /]
              <small>Remaining budget: US$ {{ setCurrencyFormat(remainingBudget) }} </small>
          </div>
          
          <div class="form-group">
            [@customForm.textArea name="rationale" i18nkey="mapFunding.justification" help="mapFunding.justification.help" helpIcon=false className="" required=true editable=editable /]
          </div>
          
        </div>
        
      
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary saveBudgetMapping" v-bind:disabled="!isValidForm" >[@s.text name="mapFunding.mapButton" /]</button>
      </div>
    </div>
  </div>
</div>
[/#if]

[#-- Region element template --]
<ul style="display:none">
  <li id="regionTemplate" class="region clearfix col-md-3">
      <div class="removeRegion removeIcon" title="Remove region"></div>
      <input class="id" type="hidden" name="fundingSource.fundingRegions[-1].id" value="" />
      <input class="rId" type="hidden" name="fundingSource.fundingRegions[-1].locElement.id" value="" />
      <input class="regionScope" type="hidden" name="fundingSource.fundingRegions[-1].scope" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>

[#-- Country element template --]
<ul style="display:none">
  <li id="countryTemplate" class="country clearfix col-md-3">
      <div class="removeCountry syncVisibles removeIcon" style="display:${isSynced?string('none', 'block')}" title="Remove country"></div>
      <input class="id" type="hidden" name="fundingSource.fundingCountry[-1].id" value="" />
      <input class="cId" type="hidden" name="fundingSource.fundingCountry[-1].locElement.isoAlpha2" value="" />
      <input class="cPercentage" type="hidden" name="fundingSource.fundingCountry[-1].percentage" value="" />
      
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>

[#-- Budget Types Description --]
<ul style="display:none">
  [#list budgetTypesList as budgetType]
    <li class="budgetTypeDescription-${budgetType.id}">${(budgetType.description)!}</li>
  [/#list]
</ul>

<span class="hidden cgiarConsortium">${action.getCGIARInstitution()}</span>

<input style="display:none" id="actualPhaseValue" type="hidden" name="actualPhase" value="${action.getActualPhase().year}" />

[#include "/WEB-INF/global/pages/footer.ftl"]


[#macro initialInformation]
<div class="row">
  <div class="col-md-6 managingPartners">
    <div class="form-group">
      [@customForm.elementsListComponent name="fundingSource.institutions" elementType="institution" elementList=(fundingSource.institutions)![] label="fundingSource.leadPartner"  help="fundingSource.partners.help" helpIcon=true listName="institutions" keyFieldName="id" displayFieldName="composedName"/]
       
      [#assign ifpriDivision = false /]
      [#list (fundingSource.institutions)![] as item]
        [#if (item.institution.id == action.getIFPRIId())!false][#assign ifpriDivision = true /][#break][/#if]
      [/#list]
    </div>
    
    [#-- Division --]
    [#if action.hasSpecificities('crp_division_fs')]
      <div class="form-group divisionBlock division-${action.getIFPRIId()}"  style="display:${ifpriDivision?string('block','none')}">
           [@customForm.elementsListComponent name="fundingSource.divisions" elementType="division" elementList=fundingSource.divisions label="projectCofunded.division" maxLimit=4 listName="divisions" keyFieldName="id" displayFieldName="composedName"/]
      </div>
    [/#if]
  </div>
  
  <div class="col-md-6 form-group">
    
    <div class="form-group row">
      [#-- Funding Window --]
      <div class="col-md-6 metadataElement-fundingTypeId">
        [@customForm.select name="fundingSource.fundingSourceInfo.budgetType.id" i18nkey="projectCofunded.type" className="type fundingType metadataValue" listName="budgetTypes" header=false required=true disabled=isSynced editable=editable /]
        [#if isSynced && editable && action.canEditType()]<input type="hidden" class="selectHiddenInput" name="fundingSource.fundingSourceInfo.budgetType.id" value="${(fundingSource.fundingSourceInfo.budgetType.id)!}" />[/#if]
        [#-- W1W2 Tag --]
        [#if action.hasSpecificities('crp_fs_w1w2_cofinancing')]
          <div class="w1w2-tag" style="display:${isW1W2?string('block','none')};">
            <div class="checkbox dottedBox">
              <label for="w1w2-tag-input">
                [#if editable]
                <input type="checkbox" name="fundingSource.fundingSourceInfo.w1w2" value="true" id="w1w2-tag-input" [#if w1w2TagValue]checked[/#if]/>
                [#else]
                   <img src="${baseUrlCdn}/global/images/checked-${w1w2TagValue?string}.png" /> 
                [/#if]
                <small>[@customForm.text name="fundingSource.w1w2Tag" readText=!editable /]</small></label>
            </div>
          </div>
        [/#if]
      </div>
      
      [#-- Agreement status --]
      <div class="col-md-6 metadataElement-agreementStatus">
        [@customForm.select name="fundingSource.fundingSourceInfo.status" i18nkey="projectCofunded.agreementStatus" className="agreementStatus metadataValue"  listName="status" keyFieldName=""  displayFieldName="" disabled=isSynced header=false editable=(editable || editStatus) /] 
        [#if isSynced && (editable || editStatus)]<input type="hidden" class="selectHiddenInput" name="fundingSource.fundingSourceInfo.status" value="${(fundingSource.fundingSourceInfo.status)!}" />[/#if]
      </div>
    </div>
  
    [#-- Finance code --]
    <div class="url-field">
      <label for="fundingSource.financeCode" class="editable">[@s.text name="projectCofunded.financeCode"/]:<span class="red requiredTag" style="display:${requiredCode?string('inline', 'none')};">*</span></label>
      <div class="input-group">
        [#if editable]
          [#-- Finance Channel --]
          <div class="input-group-btn financeChannel">
            <button type="button" class="btn btn-default btn-sm dropdown-toggle ${isSynced?string('disabled', '')}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
              <span class="partnerLeadSelectedName"> ${(fundingSource.fundingSourceInfo.leadCenter.acronym)!"Select a partner lead..."} </span>  <span class="caret"></span>
              <input type="hidden" class="partnerLeadInput" name="fundingSource.fundingSourceInfo.leadCenter.id" value="${(fundingSource.fundingSourceInfo.leadCenter.id)!}" />
            </button>
            <ul class="dropdown-menu">
              [#list (fundingSource.institutions)![] as partnerInstitution]
                <li class="setPartnerLead value-${(partnerInstitution.institution.id)!}"><a href="">${(partnerInstitution.institution.acronym)!partnerInstitution.institution.name}</a></li>
              [/#list]
            </ul>
          </div><!-- /btn-group -->
          [#-- Finance Input --]
          <input type="text" name="fundingSource.fundingSourceInfo.financeCode" value="${(fundingSource.fundingSourceInfo.financeCode)!}" class="form-control input-sm financeCode optional" [#if isSynced]readonly="readonly"[/#if] placeholder="e.g. OCS Code">
        [#else]
          <small>${(fundingSource.fundingSourceInfo.leadCenter.acronym)!}: </small> ${(fundingSource.fundingSourceInfo.financeCode)!}
          <input type="hidden" name="fundingSource.fundingSourceInfo.leadCenter.id" class="partnerLeadInput"  value="${(fundingSource.fundingSourceInfo.leadCenter.id )!}"/>
          <input type="hidden" name="fundingSource.fundingSourceInfo.financeCode"   class="financeCode"       value="${(fundingSource.fundingSourceInfo.financeCode)!}"/>
        [/#if]
        <input type="hidden" name="fundingSource.fundingSourceInfo.leadCenter.acronym" value="${(fundingSource.fundingSourceInfo.leadCenter.acronym)!}"/>
      </div>
      <span class="financeCode-message"></span>
    </div>
    [#assign hasCIAT = (fundingSource.fundingSourceInfo.leadCenter.id == 46)!false /]
    <div class="buttons-field" style="display:${hasCIAT?string('block', 'none')}">
      <input type="hidden" id="isSynced" name="fundingSource.fundingSourceInfo.synced" value="${isSynced?string}" />
      [#if editable]
        <div id="fillMetadata">
          [#-- Sync Button --]
          <div class="checkButton" style="display:${isSynced?string('none','block')};">[@s.text name="project.deliverable.dissemination.sync" /]</div>
          <div class="unSyncBlock" style="display:${isSynced?string('block','none')};">
            [#-- Update Button --]
            <div class="updateButton">[@s.text name="project.deliverable.dissemination.update" /]</div>
            [#-- Unsync Button --]
            <div class="uncheckButton">[@s.text name="project.deliverable.dissemination.unsync" /]</div>
          </div>
        </div>
      [/#if]
    </div>
    
    [#-- Sync Message --]
    <div id="metadata-output row">
      <p class="lastDaySync" style="display:${(!isSynced)?string('none', 'block')}">Last sync was made on <span>${(fundingSource.fundingSourceInfo.syncedDate?date)!}</span></p>
      <input type="hidden" class="fundingSourceSyncedDate" name="fundingSource.fundingSourceInfo.syncedDate" value="${(fundingSource.syncedDate?string["yyyy-MM-dd"])!'2017-06-30'}" />
    </div>
    
    [#--  Funding Sources found List --]
    <div id="vueApp" class="resultList simpleBox" v-if="allFundingSources.length" style="display:none">
      <p>This finance code is also used by:</p>
      <div v-for="item in crpList" v-if="item.fundingSources.length">
        <strong>{{ item.name }}</strong> 
        <p v-for="fs in item.fundingSources">
          <span class="pull-right label label-info">{{ fs.type }}</span>
          <a target="_blank" v-if="item.name=='${crpSession}'" v-bind:href="'${baseUrl}/fundingSources/${crpSession}/fundingSource.do?fundingSourceID='+ fs.id +'&edit=true&phaseID=${(actualPhase.id)!}'">
           <small><strong> FS{{ fs.id }}</strong> - {{ fs.financeCode }} | {{ fs.name }}</small>
          </a>
          
          <small v-else><strong> FS{{ fs.id }}</strong> - {{ fs.financeCode }} | {{ fs.name }}</small>
        </p>
      </div>
    </div>

  </div>
</div>
[/#macro]

[#macro generalInformation]
<div class="">
  [#-- Project title --]
  <div class="form-group metadataElement-description">
    [@customForm.textArea name="fundingSource.fundingSourceInfo.title" i18nkey="projectCofunded.title" className="fundingSource-title limitWords-40 metadataValue" required=true readOnly=isSynced editable=editable /] 
  </div>
  [#-- Project summary --]
  <div class="form-group metadataElement-objectives">
    [@customForm.textArea name="fundingSource.fundingSourceInfo.description" i18nkey="projectCofunded.description" className="limitWords-150 metadataValue" required=false readOnly=isSynced editable=editable /]
  </div>
  
  [#-- Start date, End date and finance code --]
  <ul class="dateErrorBox form-group"></ul> 
  
  <div class="form-group row">
    [#-- Start Date --]
    <div class="col-md-4 metadataElement-startDate">
      <label for="fundingSource.startDate">[@s.text name="fundingSource.startDate" /]:[@customForm.req required=editable && action.canEditFundingSourceBudget()  /]</label>
      [#if editable]
        <input id="fundingSource.fundingSourceInfo.startDate" type="hidden" name="fundingSource.fundingSourceInfo.startDate" value="${(fundingSource.fundingSourceInfo.startDate?string["yyyy-MM-dd"])!}" class="form-control input-sm metadataValue startDateInput">
        <p class="dateLabel btn btn-default ${isSynced?string('disabled','')}">${(fundingSource.fundingSourceInfo.startDate?string["MMMM yyyy"])!}</p>
      [#else]
        <input type="hidden" class="startDateInput" name="fundingSource.fundingSourceInfo.startDate" value="${(fundingSource.fundingSourceInfo.startDate?string["yyyy-MM-dd"])!}" />
        <div class="input"><p>${(fundingSource.fundingSourceInfo.startDate?string["MMMM yyyy"])!}</p></div>
      [/#if]
    </div>
    
    [#-- End Date --]
    <div class="col-md-4 endDateBlock metadataElement-endDate" >
      <label for="fundingSource.endDate">[@s.text name="fundingSource.endDate" /]:[@customForm.req required=editable && action.canEditFundingSourceBudget()  /]</label>
      [#if editable]
        <input id="fundingSource.fundingSourceInfo.endDate" type="hidden" name="fundingSource.fundingSourceInfo.endDate" value="${(fundingSource.fundingSourceInfo.endDate?string["yyyy-MM-dd"])!}" class="form-control input-sm metadataValue endDateInput">
        <p class="dateLabel btn btn-default ${(isSynced || showEXtDate)?string('disabled','')}">${(fundingSource.fundingSourceInfo.endDate?string["MMMM yyyy"])!}</p>
      [#else]
        <input type="hidden" class="endDateInput" name="fundingSource.fundingSourceInfo.endDate" value="${(fundingSource.fundingSourceInfo.endDate?string["yyyy-MM-dd"])!}" />
        <div class="input"><p>${(fundingSource.fundingSourceInfo.endDate?string["MMMM yyyy"])!}</p></div>
      [/#if]
    </div>
    
    [#-- Extension Date --]
    <div class="col-md-4 extensionDateBlock metadataElement-extensionDate" style="display:${showEXtDate?string('block', 'none')}">
      <label for="fundingSource.extensionDate">[@s.text name="fundingSource.extensionDate" /]:[@customForm.req required=true  /]</label> 
      [#if showEXtDate]
        [#assign extensionValue = (fundingSource.fundingSourceInfo.extensionDate?string["yyyy-MM-dd"])!'' /]
      [#else]
        [#assign extensionValue = '' /]
      [/#if]
      [#if (editable || editStatus)]
        <input id="fundingSource.fundingSourceInfo.extensionDate" type="hidden" name="fundingSource.fundingSourceInfo.extensionDate" value="${extensionValue}" class="form-control input-sm metadataValue extensionDateInput">
        <p class="dateLabel btn btn-default ${isSynced?string('disabled','')}">${(fundingSource.fundingSourceInfo.extensionDate?string["MMMM yyyy"])!}</p>    
        <small class="pull-right clearDate syncVisibles" style="display:${isSynced?string('none', 'block')}"> <span class="glyphicon glyphicon-remove"></span> Clear</small>
      [#else]
        <input type="hidden" class="extensionDateInput" name="fundingSource.fundingSourceInfo.extensionDate" value="${(fundingSource.fundingSourceInfo.extensionDate?string["yyyy-MM-dd"])!}" />
        <div class="input"><p>${(fundingSource.fundingSourceInfo.extensionDate?string["MMMM yyyy"])!}</p></div>
      [/#if]
    </div>
  </div>
  
  <hr />
  
  [#--  Does this study involve research with human subjects? --]
  [#if action.hasSpecificities('crp_has_research_human')]
  <div class="form-group" style="position:relative" listname="fundingSource.fundingSourceInfo.hasFileResearch">
    [#if (fundingSource.fundingSourceInfo.hasFileResearch??)!false]
      [#assign hasHumanSubjects = fundingSource.fundingSourceInfo.hasFileResearch /]
    [/#if]
    
    <label>[@s.text name="fundingSource.doesResearchHumanSubjects" /] [@customForm.req required=editable  /]</label>
    [#if editable]
      [@customForm.radioFlat id="humanSubjects-yes" name="fundingSource.fundingSourceInfo.hasFileResearch" label="Yes" value="true" checked=((hasHumanSubjects)!false) cssClass="humanSubjects-yes humanSubjectsRadio" cssClassLabel="radio-label-yes"/]
      [@customForm.radioFlat id="humanSubjects-no" name="fundingSource.fundingSourceInfo.hasFileResearch" label="No" value="false" checked=((!hasHumanSubjects)!false) cssClass="humanSubjects-no humanSubjectsRadio" cssClassLabel="radio-label-no"/]
    [#else]
      ${(hasHumanSubjects?string('Yes', 'No'))!}
    [/#if]
    [#-- Upload File (Human subjects research) fileResearch --]
    <div class="form-group humanSubjectsBlock" style="display:${((hasHumanSubjects)!false)?string('block', 'none')}; position:relative" listname="fundingSource.fundingSourceInfo.fileResearch">
      [@customForm.fileUploadAjax 
        fileDB=(fundingSource.fundingSourceInfo.fileResearch)!{} 
        name="fundingSource.fundingSourceInfo.fileResearch.id" 
        label="fundingSource.uploadHumanSubjects" 
        dataUrl="${baseUrl}/uploadFundingSourceResearch.do" 
        path="${action.getPath((fundingSource.fundingSourceInfo.id?string)!-1)}"
        isEditable=editable
        required=true
      /]
    </div>
  </div>
  <hr />
  [/#if]
  
  <div class="form-group">
    <div class="row">
      [#-- Upload bilateral contract --]
      <div class="col-md-6">
        [@customForm.fileUploadAjax 
          fileDB=(fundingSource.fundingSourceInfo.file)!{}
          name="fundingSource.fundingSourceInfo.file.id" 
          label="fundingSource.uploadContract" 
          dataUrl="${baseUrl}/uploadFundingSource.do" 
          isEditable=editable
        /]
      </div>
    </div>
  </div>
 
  [#-- Contact person name and email --]
  [#assign canSeePIEmail = action.hasSpecificities('crp_email_funding_source')]
  <div class="form-group row">
    <div class="col-md-6 metadataElement-pInvestigator">[@customForm.input name="fundingSource.fundingSourceInfo.contactPersonName" help="projectCofunded.contactName.help" i18nkey="projectCofunded.contactName" className="contactName metadataValue" required=true readOnly=isSynced editable=editable /]</div>
    <div class="col-md-6 metadataElement-pInvestigatorEmail" style="display:${canSeePIEmail?string('block','none')}">[@customForm.input name="fundingSource.fundingSourceInfo.contactPersonEmail" i18nkey="projectCofunded.contactEmail" className="contactEmail metadataValue" required=true readOnly=isSynced editable=editable /]</div>
  </div>
  
  <hr />
    
  <div class="form-group-donor">
    [#-- Direct Donor --]
    <div class="form-group row">
      <div class="col-md-12 metadataElement-directDonorName">
        <label for="">[@s.text name="projectCofunded.directDonor" /]:[@customForm.req required=editable /]  [@customForm.helpLabel name="fundingSource.directDonor.help" paramText="fundingSource.directDonor.help" showIcon=true editable=editable/]</label>
        <span class="description"><i>([@s.text name="projectCofunded.directDonor.helpText" /])</i></span>
          [#if editable]
            [@customForm.select name="fundingSource.fundingSourceInfo.directDonor.id" i18nkey="projectCofunded.directDonor" className="donor" showTitle=false listName="institutionsDonors" keyFieldName="id"  displayFieldName="composedNameLoc" disabled=(isW1W2 && (!centerGlobalUnit)) editable=editable /]
          [#else]
            <p class="input">${(fundingSource.fundingSourceInfo.directDonor.composedName)!}</p>
            <input  type="hidden" name="fundingSource.fundingSourceInfo.directDonor.id" value="${(fundingSource.fundingSourceInfo.directDonor.id)!-1}" />
          [/#if]
        <span class="text-warning metadataSuggested"></span> 
      </div>
    </div>

    [#-- Original Donor --]
    <div class="form-group row">
      <div class="col-md-12 metadataElement-originalDonorName">
        <label for="">[@s.text name="projectCofunded.donor" /]:</label>
        <span class="description"><i>([@s.text name="projectCofunded.donor.helpText" /])</i></span>
          [#if editable]
            [@customForm.select name="fundingSource.fundingSourceInfo.originalDonor.id" i18nkey="projectCofunded.donor" className="donor" showTitle=false  listName="institutionsDonors" keyFieldName="id"  displayFieldName="composedNameLoc" editable=editable /]
          [#else]
            <p class="input">${(fundingSource.fundingSourceInfo.originalDonor.composedName)!}</p>
            <input  type="hidden" name="fundingSource.fundingSourceInfo.originalDonor.id" value="${(fundingSource.fundingSourceInfo.originalDonor.id)!-1}" />
          [/#if]
        <span class="text-warning metadataSuggested"></span> 
      </div>
    </div>

    [#-- Request partner adition --]
    [#if editable]
    <p id="addPartnerText" class="helpMessage">
      [@s.text name="fundingSource.addDonorMessage.first" /]
      <a class="popup" href="[@s.url action='${crpSession}/partnerSave' namespace="/projects"][@s.param name='fundingSourceID']${fundingSource.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
        [@s.text name="projectPartners.addPartnerMessage.second" /]
      </a>
    </p> 
    [/#if]
    
  </div>
</div>
[/#macro]

[#macro locationInformation]
  [#-- Loading --]
  <div class="loading locationBlock" style="display:none"></div>

  [#-- GLOBAL DIMENSION --]
  [#if editable]
    <div class="form-group row ">
      <div class="col-md-6">[@customForm.yesNoInput  label="projectCofunded.globalDimensionQuestion" name="fundingSource.fundingSourceInfo.global"  editable=editable inverse=false  cssClass="" /] </div>
    </div>
    <hr />
    <div class="form-group row">
      <div class="col-md-6">[@customForm.yesNoInput  label="projectCofunded.regionalDimensionQuestion" name="region"  editable=editable inverse=false  cssClass="isRegional" /] </div>
    </div>
  [#else]
    <div class="form-group row ">
      <div class="col-md-12">
        [#if fundingSource.fundingSourceInfo.global]
          <label for="">[@s.text name="projectCofunded.globalDimensionYes" /]</label>
        [#else]
          <label for="">[@s.text name="projectCofunded.globalDimensionNo" /]</label>
        [/#if]
      </div>
    </div>
    <hr />
    <div class="form-group row ">
      <div class="col-md-12">
        [#if region]
          <label for="">[@s.text name="projectCofunded.regionallDimensionYes" /]</label>
        [#else]
          <label for="">[@s.text name="projectCofunded.regionallDimensionNo" /]</label>
        [/#if]
      </div>
    </div>
  [/#if]
      
      [#-- REGIONAL SELECT --]
      <div class="regionsBox form-group row" style="display:${region?string('block','none')}">
        <div class="panel tertiary col-md-12">
         <div class="panel-head">
           <label for=""> [@customForm.text name="projectCofunded.selectRegions" readText=!editable /]:[@customForm.req required=editable /]</label>
           <br />
           <small style="color: #337ab7;">([@s.text name="projectLocations.standardLocations" /])</small>
         </div>
         
          <div id="regionList" class="panel-body" listname="fundingSource.fundingRegions">
            <ul class="list">
            [#if fundingSource.fundingRegions?has_content]
              [#list fundingSource.fundingRegions as region]
                  <li id="" class="region clearfix col-md-3">
                  [#if editable ]
                    <div class="removeRegion removeIcon" title="Remove region"></div>
                  [/#if]
                    <input class="id" type="hidden" name="fundingSource.fundingRegions[${region_index}].id" value="${region.id}" />
                    <input class="rId" type="hidden" name="fundingSource.fundingRegions[${region_index}].locElement.id" value="${(region.locElement.id)!}" />
                    <input class="regionScope" type="hidden" name="fundingSource.fundingRegions[${region_index}].scope" value="${(region.scope?c)!}" />
                    <span class="name">${(region.locElement.name)!}</span>
                    <div class="clearfix"></div>
                  </li>
              [/#list]
              [#else]
              <p class="emptyText"> [@s.text name="No regions added yet." /]</p> 
            [/#if]
            </ul>
            [#if editable ]
              <select name="" id="regionSelect" class="regionsSelect">
                <option value="-1">Select an option...</option>
                [#if scopeRegionLists?has_content]
                  <optgroup label="${(loggedCrp.acronym?upper_case)!} regions">
                  [#list scopeRegionLists as region]
                  <option value="${(region.id)!}-${(region.scope?c)!}">${(region.name)!}</option>
                  [/#list]
                  </optgroup>
                [/#if]
                [#if regionLists?has_content]
                <optgroup label="World Bank classifies regions">
                  [#list regionLists as region]
                  <option value="${(region.id)!}-${(region.locElementType.scope?c)!}">${(region.name)!}</option>
                  [/#list]
                  </optgroup>
                [/#if]
              </select>
            [/#if] 
          </div>
        </div>
      </div>
      
  [#-- SELECT COUNTRIES --]
  <div class="form-group row">
    <div class="panel tertiary col-md-12">
     <div class="panel-head"><label for=""> [@customForm.text name="projectCofunded.listCountries" readText=!editable /]:</label></div>
      <div id="countryList" class="panel-body" listname="fundingSource.fundingCountry"> 
        <ul class="list">
        [#if fundingSource.fundingCountry?has_content]
          [#list fundingSource.fundingCountry as country]
              <li id="" class="country clearfix col-md-3">
              [#if editable ]
                <div class="removeCountry syncVisibles removeIcon" style="display:${isSynced?string('none', 'block')}" title="Remove country"></div>
              [/#if]
                <input class="id" type="hidden" name="fundingSource.fundingCountry[${country_index}].id" value="${(country.id)!-1}" />
                <input class="cId" type="hidden" name="fundingSource.fundingCountry[${country_index}].locElement.isoAlpha2" value="${(country.locElement.isoAlpha2)!}" />
                <input class="cPercentage" type="hidden" name="fundingSource.fundingCountry[${country_index}].percentage" value="${(country.percentage)!}" />
                
                <span class="name"><span> <i class="flag-sm flag-sm-${(country.locElement.isoAlpha2)!}"></i> [@utils.wordCutter string=(country.locElement.name)!'' maxPos=15 /] </span></span>
                <div class="clearfix"></div>
              </li>
          [/#list]
          [#else]
          <p class="emptyText"> [@s.text name="No countries added yet." /]</p> 
        [/#if]
        </ul>
        [#if editable ]
          <div class="syncVisibles" style="display:${isSynced?string('none', 'block')}">
            [@customForm.select name="" label=""  showTitle=false  i18nkey="" listName="countryLists" keyFieldName="isoAlpha2"  displayFieldName="name"  multiple=false required=true  className="countriesSelect" editable=editable /]
          </div>
        [/#if] 
      </div>
    </div>
  </div>
[/#macro]

[#macro fundingSourceBudget ]
  [#-- Grant total amount --]
  <div id="grantTotalAmount" class="metadataElement-grantAmount" style="display:${isSynced?string('block', 'none')}">
    <p><strong>Total Grant Amount:</strong> US$ <span class="amount">${((fundingSource.fundingSourceInfo.grantAmount)!0)?number?string(",##0.00")}</span></p>
    [#-- Remainig budget --]
    <small class="grayLabel"> <i>Total remaining budget: US$ <span class="remaining">0.00</span> </i></small>

    <input type="hidden" class="metadataValue" name="fundingSource.fundingSourceInfo.grantAmount" value="${(fundingSource.fundingSourceInfo.grantAmount)!0}" />
  </div>
  <div class="loadingBlock"></div>
  <div class="contributionWrapper budgetByYears" style="display: none;">
    [#assign projectBudgetsList = (fundingSource.projectBudgetsList)![] /]
    [#assign projectBudgetsListOtherCrps = (fundingSourceShow.projectBudgetsList)![] /]
    
    [#-- Year Tabs --]
    <ul class="nav nav-tabs budget-tabs" role="tablist">
      [#list fundingSourceYears as year]
        <li class="[#if year == currentCycleYear]active[/#if]"><a href="#fundingYear-${year}" role="tab" data-toggle="tab">${year} </a></li>
      [/#list]
    </ul>
    [#-- Years Content --]
    <div class="tab-content contributionContent">
      
      [#list fundingSourceYears as year]
        <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="fundingYear-${year}">
        
        [#attempt]
          [#assign budget = (action.getBudget(year))!{} /]
          [#assign budgetIndex = (action.getIndexBugets(year))!'-1' /]
        [#recover]
          [#assign budget = {} /]
          [#assign budgetIndex = '-1' /]
        [/#attempt]
        
        <h5 class="sectionSubTitle">Budget Amount</h5>
       
        <div class="budgetsYear" >
          <div class="col-md-4">
            <input type="hidden" name="fundingSource.budgets[${budgetIndex}].year" value="${year}"/>
            <input type="hidden" name="fundingSource.budgets[${budgetIndex}].id" value="${(budget.id)!}"/>
            [#if editable   ]
              [@customForm.input name="fundingSource.budgets[${budgetIndex}].budget" i18nkey="projectCofunded.budgetYear" paramText="${year}" className="currencyInput year-${year}" required=true editable=editable /]
            [#else]
            <div class="input">
              <p>US$ <span>${((budget.budget)!0)?number?string(",##0.00")}</span></p>
               <input type="hidden" name="fundingSource.budgets[${budgetIndex}].budget" value="${(budget.budget)!0}"/>
            </div>
            [/#if]
          </div>
          <div class="clearfix"></div>
        </div>
        <br />
        
        [#-- Remainig budget (Calculated with Javascript)--]
        <small class="grayLabel pull-right"> (${year} Remaining budget: US$ <span class="remainingAmount">0.00</span>) </small>

        [#-- Projects that this funding source is assigned to --]
        <h5 class="sectionSubTitle">[@s.text name="fundingSource.projectsAssigned" /]:</h5>
        
        <table class="table tableProjectBudgets-${year}">
          <thead>
           <tr>
            <th>[@s.text name="fundingSource.projectsAssigned.projectID" /]</th>
            <th>[@s.text name="fundingSource.projectsAssigned.projectTitle" /]</th>
            <th>Rationale</th>
            <th>Lead partner</th>
            <th>Budget amount</th>
            [#if editable]
              <th></th>
            [/#if]
           </tr>
          </thead>
          <tbody>
          [#list projectBudgetsList as projectBudget]
            [#assign projectBudgetURL][@s.url action="${crpSession}/budgetByPartners" namespace="/projects"] [@s.param name="projectID" value="${(projectBudget.project.id)!}"/] [#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign]
            [#assign projectBudgetDeliverables = (action.getDeliverableRelationsProject(projectBudget.id, projectBudget.class.name, projectBudget.project.id))! /]
            [#assign hasDeliverables = (projectBudgetDeliverables?size > 0) ]
            [#if projectBudget.year == year]
             <tr class="projectBudgetItem projectBudget-${projectBudget.id}">
              <td><a href="${projectBudgetURL}">C${(projectBudget.project.id)!}</a></td>
              <td class="col-md-5"><a href="${projectBudgetURL}">${(projectBudget.project.projectInfo.title)!}</a> </td>
              <td> ${(projectBudget.rationale)!} </td>
              <td> ${(projectBudget.institution.acronymName)!(projectBudget.institution.name)}</td>
              <td>US$ <span class="pbAmount">${((projectBudget.amount)!0)?number?string(",##0.00")}</span></td>
              [#if editable]
                <td><span class="trashIcon ${hasDeliverables?string('icon-disabled','removeProjectBudget') }" [#if hasDeliverables]title="Can not be removed due the P${(projectBudget.project.id)!} has deliverables(${projectBudgetDeliverables?size}) attached to this funding source"[/#if]></span> </td>
              [/#if]
             </tr>
            [/#if]
          [/#list]
          </tbody>
        </table>
        
        [#-- Button to map Funding Source to a project --  && action.canMapProjects(year) --]
        [#if editable && (year == currentCycleYear)]
          <button type="button" class="btn btn-primary pull-right year-" data-year="${year}" data-toggle="modal" data-target="#mapFundingToProject">Map Funding Source to a Cluster</button>
          <div class="clearfix"></div>
        [/#if]
        
        [#if (projectBudgetsListOtherCrps?has_content)!false]
        <hr />
        <h5 class="sectionSubTitle">[@s.text name="fundingSource.projectsAssignedCRP" /]:</h5>
        <table id="" class="table">
          <thead>
           <tr>
            <th>[@s.text name="fundingSource.projectsAssigned.projectID" /]</th>
            <th>CRP</th>
            <th>[@s.text name="fundingSource.projectsAssigned.projectTitle" /]</th>
            <th>Rationale</th>
            <th>Lead partner</th>
            <th>Budget amount</th>
           </tr>
          </thead>
          <tbody>
          [#list projectBudgetsListOtherCrps as projectBudget]
            [#assign projectBudgetURL][@s.url action="${crpSession}/budgetByPartners" namespace="/projects"] [@s.param name="projectID" value="${(projectBudget.project.id)!}"/] [#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign]
            [#if projectBudget.year == year]
             <tr class="projectBudgetItem">
              <td><a href="${projectBudgetURL}">P${(projectBudget.project.id)!}</a></td>
              <td><a href="${projectBudgetURL}">${(projectBudget.fundingSource.crp.acronym)!}</a></td>
              <td class="col-md-5"><a href="${projectBudgetURL}">${(projectBudget.project.projectInfo.title)!}</a></td>
              <td class="col-md-5"><a href="${projectBudgetURL}">${(projectBudget.rationale)!}</a></td>
              <td> ${(projectBudget.fundingSource.fundingSourceInfo.leadCenter.acronymName)!(projectBudget.fundingSource.fundingSourceInfo.leadCenter.name)!} </td>
              <td>US$ <span class="pbAmount">${((projectBudget.amount)!0)?number?string(",##0.00")}</span></td>
             </tr>
            [/#if]
          [/#list]
          </tbody>
        </table>
        [/#if]
        
        </div>
      [/#list] 
    </div>
  </div>
[/#macro]