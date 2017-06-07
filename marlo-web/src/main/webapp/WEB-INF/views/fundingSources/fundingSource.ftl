[#ftl]
[#assign title = "MARLO Funding Sources" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-${fundingSource.id}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrl}/js/global/fieldsValidation.js","${baseUrl}/js/fundingSources/fundingSource.js", "${baseUrl}/js/global/autoSave.js" ] /]
[#assign customCSS = ["${baseUrl}/css/fundingSources/fundingSource.css"] /]
[#assign currentSection = "fundingSources" /]

[#assign breadCrumb = [
  {"label":"fundingSourcesList", "nameSpace":"/fundingSources", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign startYear = ((fundingSource.startDate?string.yyyy)?number)!currentCycleYear /]
[#assign endYear = ((fundingSource.endDate?string.yyyy)?number)!startYear /]
    
<section class="container">
  <article class="fullBlock col-md-12" id="mainInformation">
  
  [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
  
  <div class="col-md-12">
  
  [#-- Messages --]
  [#include "/WEB-INF/views/fundingSources/messages-fundingSource.ftl" /]
  
  <h4 class="headTitle">General information</h4> 
    <div class="borderBox informationWrapper">
      [#-- Finance code --]
      <div class="form-group row">
        <div class="col-md-offset-6 col-md-6">
          <div class="url-field">
            [@customForm.input name="fundingSource.financeCode"  i18nkey="projectCofunded.financeCode" placeholder="projectCofunded.financeCode.placeholder" editable=editable/]
          </div>
          <div class="buttons-field">
            [#if editable]
              [#assign isSynced = false ]
              <div id="fillMetadata">
                <input type="hidden" name="fundingSource.synced" value="${isSynced?string}" />
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
        </div>
        <hr />
      </div>
      
      [#-- Project title --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">[@customForm.input name="fundingSource.title" i18nkey="projectCofunded.title" className="limitWords-40" required=true editable=editable /] </div>
        </div>
      </div>
      [#-- Project summary --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">[@customForm.textArea name="fundingSource.description" i18nkey="projectCofunded.description" className="limitWords-150" required=false editable=editable /] </div>
        </div>
      </div>
      [#-- start date, end date and finance code --]
      <div class="form-group">
        <div class="dateErrorBox"></div>
        <div class="row">
           <div class="col-md-4">[@customForm.input name="fundingSource.startDate" i18nkey="projectCofunded.startDate" required=true  editable=editable && action.canEditFundingSourceBudget() /] </div>
           <div class="col-md-4">[@customForm.input name="fundingSource.endDate" i18nkey="projectCofunded.endDate" required=true  editable=editable && action.canEditFundingSourceBudget() /] </div>
           
        </div>
      </div>
      
      [#-- Upload bilateral contract --]
      <div class="form-group fileUploadContainer">
        <label>[@customForm.text name="fundingSource.uploadContract" readText=!editable /]:</label>
        [#assign hasFile = fundingSource.file?? && fundingSource.file.id?? /]
        <input id="fileID" type="hidden" name="fundingSource.file.id" value="${(fundingSource.file.id)!}" />
        [#-- Input File --]
        [#if editable]
        <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="upload" type="file" name="file" data-url="${baseUrl}/uploadFundingSource.do"></div>
        [/#if]
        [#-- Uploaded File --]
        <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
          <span class="contentResult">[#if fundingSource.file??]${(fundingSource.file.fileName)!('No file name')} [/#if]</span> 
          [#if editable]<span class="removeIcon"> </span> [/#if]
        </p>
      </div>
       
      [#-- Agreement status and total budget --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-6">[@customForm.select name="fundingSource.status" i18nkey="projectCofunded.agreementStatus" className="agreementStatus"  listName="status" keyFieldName=""  displayFieldName="" header=false editable=editable /] </div>
          <div class="col-md-6">[@customForm.select name="fundingSource.budgetType.id" i18nkey="projectCofunded.type" className="type" listName="budgetTypes" header=false required=true editable=editable && action.canEditType() /]</div>
        </div>
      </div>
      
      [#-- CGIAR lead center --]
      [#assign ifpriDivision = false /]
      <div class="form-group row">
        <div class="panel tertiary col-md-12">
         <div class="panel-head"><label for=""> [@customForm.text name="fundingSource.leadPartner" readText=!editable /]:[@customForm.req required=editable /]</label></div>
          <div id="leadPartnerList" class="panel-body" listname="deliverable.fundingSources"> 
            <ul class="list">
            [#if fundingSource.institutions?has_content]
              [#list fundingSource.institutions as institutionLead]
                [#-- Show if is a headquarter institution --]
                [#if !(institutionLead.headquarter??)]
                  <li id="" class="leadPartners clearfix col-md-6">
                  [#if editable ]
                    <div class="removeLeadPartner removeIcon" title="Remove Lead partner"></div>
                  [/#if]
                    <input class="id" type="hidden" name="fundingSource.institutions[${institutionLead_index}].id" value="${institutionLead.id}" />
                    <input class="fId" type="hidden" name="fundingSource.institutions[${institutionLead_index}].institution.id" value="${institutionLead.institution.id}" />
                    <span class="name">${(institutionLead.institution.composedName)!}</span>
                    <div class="clearfix"></div>
                    
                    [#-- Check IFPRI Division --]
                    [#if institutionLead.institution.id == action.getIFPRIId() ] [#assign ifpriDivision = true /] [/#if]
                  </li>
                [/#if]
              [/#list]
              [#else]
              <p class="emptyText"> [@s.text name="No lead partner added yet." /]</p> 
            [/#if]
            </ul>
            [#if editable ]
              [@customForm.select name="fundingSource.leader.id" label=""  showTitle=false  i18nkey="" listName="institutions" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className="institution" editable=editable /]
            [/#if] 
          </div>
        </div>
      </div>
      
      [#-- Division --]
      [#if action.hasSpecificities('crp_division_fs')]
        <div class="form-group row divisionBlock division-${action.getIFPRIId()}"  style="display:${ifpriDivision?string('block','none')}">
          <div class="col-md-7">
            [@customForm.select name="fundingSource.partnerDivision.id" i18nkey="projectCofunded.division" listName="divisions" keyFieldName="id" displayFieldName="composedName" required=true editable=editable /]
          </div>
        </div>
      [/#if]
      
      [#-- Contact person name and email --]
      [#assign canSeePIEmail = action.hasSpecificities('crp_email_funding_source')]
      <div class="form-group row">
          <div class="col-md-6">[@customForm.input name="fundingSource.contactPersonName" i18nkey="projectCofunded.contactName" className="contactName" required=true editable=editable /]</div>
          <div class="col-md-6" style="display:${canSeePIEmail?string('block','none')}">[@customForm.input name="fundingSource.contactPersonEmail" i18nkey="projectCofunded.contactEmail" className="contactEmail" required=true editable=editable /]</div>
      </div>
      
      [#-- Donor --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">
            [@customForm.select name="fundingSource.institution.id" i18nkey="projectCofunded.donor" className="donor"  listName="institutionsDonors" keyFieldName="id"  displayFieldName="composedNameLoc" required=true editable=editable /]
          </div>
        </div>
        
        [#-- Request partner adition --]
        [#if editable]
        <p id="addPartnerText" class="helpMessage">
          [@s.text name="projectPartners.addPartnerMessage.first" /]
          <a class="popup" href="[@s.url action='${crpSession}/partnerSave' namespace="/projects"][@s.param name='fundingSourceID']${fundingSource.id?c}[/@s.param][/@s.url]">
            [@s.text name="projectPartners.addPartnerMessage.second" /]
          </a>
        </p> 
        [/#if]
      </div>
      
    </div>
    
    
    <h4 class="headTitle">Annual funding source contribution</h4>
    <div class="contributionWrapper budgetByYears">
      [#-- Year Tabs --]
      <ul class="nav nav-tabs budget-tabs" role="tablist">
        [#list startYear .. endYear as year]
          <li class="[#if year == currentCycleYear]active[/#if]"><a href="#fundingYear-${year}" role="tab" data-toggle="tab">${year} </a></li>
        [/#list]
      </ul>
      [#-- Years Content --]
      <div class="tab-content col-md-12 contributionContent">
        [#list startYear .. endYear as year]
          <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="fundingYear-${year}">
          
          
          [#attempt]
            [#assign budget = (action.getBudget(year))!{} /]
            [#assign budgetIndex = (action.getIndexBugets(year))!'-1' /]
          [#recover]
            [#assign budget = {} /]
            [#assign budgetIndex = '-1' /]
          [/#attempt]
          
          <small class="grayLabel pull-right"> (Remaining budget US$ <span class="projectAmount">${((fundingSource.getRemaining(year))!0)?number?string(",##0.00")}</span>) </small>
          
          <h5 class="sectionSubTitle">Budget Amount</h5>
          <div class="budgetsYear">
            <div class="col-md-4">
              <input type="hidden" name="fundingSource.budgets[${budgetIndex}].year" value="${year}"/>
              <input type="hidden" name="fundingSource.budgets[${budgetIndex}].id" value="${(budget.id)!}"/>
              [#if editable   ]
                [@customForm.input name="fundingSource.budgets[${budgetIndex}].budget" i18nkey="projectCofunded.budgetYear" paramText="${year}" className="currencyInput" required=true editable=editable /]
              [#else]
              <div class="input">
              	<p>US$ <span>${((budget.budget)!0)?number?string(",##0.00")}</p>
              	 <input type="hidden" name="fundingSource.budgets[${budgetIndex}].budget" value="${(budget.budget)!0}"/>
              </div>
                
              [/#if]
            </div>
            <div class="clearfix"></div>
          </div>
          <br />
          <h5 class="sectionSubTitle">Projects</h5>
          
          <table class="table">
          <thead>
           <tr>
            <th>Project ID</th>
            <th>Project title</th>
            <th>Lead partner</th>
            <th>Budget type</th>
            <th>Budget amount</th>
           </tr>
          </thead>
          
         <tbody>
          
          [#assign counter = 0 /]
          [#list fundingSource.projectBudgetsList as projectBudget]
            [#if projectBudget.year == year]
             <tr class="projectBudgetItem">
              <td>
                <a href="[@s.url action="${crpSession}/budgetByPartners" namespace="/projects"] [@s.param name="projectID" value="${(projectBudget.project.id)!}"/] [@s.param name='edit']true[/@s.param][/@s.url]">
                  P${(projectBudget.project.id)!}              
                </a>
              </td>
              <td class="col-md-5">
                <a href="[@s.url action="${crpSession}/budgetByPartners" namespace="/projects"] [@s.param name="projectID" value="${(projectBudget.project.id)!}"/] [@s.param name='edit']true[/@s.param][/@s.url]">
                  ${(projectBudget.project.title)!}
                </a>
              </td>
              <td> ${(projectBudget.institution.acronym)!(projectBudget.institution.name)} </td>
              <td>${projectBudget.budgetType.name}</td>
              <td>US$ <span>${((projectBudget.amount)!0)?number?string(",##0.00")}</td>
             </tr>
            [#assign counter = counter + 1 /]
            [/#if]
          [/#list]
          
          </tbody>
          
          </table>
          
          </div>
        [/#list] 
      </div>
    </div>
      
    [#-- Section Buttons & hidden inputs--]
    [#include "/WEB-INF/views/fundingSources/buttons-fundingSources.ftl" /]
  </div>
  
  [/@s.form] 
  </article>
</section>

[#-- Funding Source list template --]
<ul style="display:none">
  <li id="leadPartnerTemplate" class="leadPartners clearfix col-md-6" style="display:none;">
    <div class="removeLeadPartner removeIcon" title="Remove Lead partner"></div>
    <input class="id" type="hidden" name="fundingSource.institutions[-1].id" value="" />
    <input class="fId" type="hidden" name="fundingSource.institutions[-1].institution.id" value="" />
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

<span class="hidden cgiarConsortium">${action.getCGIARInsitution()}</span>

[#include "/WEB-INF/global/pages/footer.ftl"]
