[#ftl]
[#assign title = "MARLO Funding Sources" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-${fundingSource.id}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload"] /]
[#assign customJS = ["${baseUrl}/js/global/fieldsValidation.js","${baseUrl}/js/fundingSources/fundingSource.js", "${baseUrl}/js/global/autoSave.js" ] /]
[#assign customCSS = ["${baseUrl}/css/fundingSources/fundingSource.css"] /]
[#assign currentSection = "fundingSources" /]

[#assign breadCrumb = [
  {"label":"fundingSourcesList", "nameSpace":"/fundingSources", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign startYear = ((fundingSource.startDate?string.yyyy)?number)!2014 /]
[#assign endYear = ((fundingSource.endDate?string.yyyy)?number)!2017 /]
    
<section class="container">
  <article class="fullBlock col-md-12" id="mainInformation">
  
  [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
  
  <div class="col-md-12">
  
  [#-- Messages --]
  [#include "/WEB-INF/views/fundingSources/messages-fundingSource.ftl" /]
  
  <h4 class="headTitle">General information</h4> 
    <div class="borderBox informationWrapper">
      [#-- Participating Center, CRP Lead Center --]
      <div class="pull-right">
        [#if editable]
        <label for="cofundedMode-1"><input type="radio" name="fundingSource.centerType" id="cofundedMode-1" value="1" [#if fundingSource.centerType?? && fundingSource.centerType == 1]checked="checked"[/#if] /> [@s.text name="projectCofunded.participatingCenter" /] </label><br />
        <label for="cofundedMode-2"><input type="radio" name="fundingSource.centerType" id="cofundedMode-2" value="2" [#if fundingSource.centerType?? && fundingSource.centerType == 2]checked="checked"[/#if] [#if !action.canEditCenterType()]disabled="disabled"[/#if]/> [@s.text name="projectCofunded.crpLeadCenter" /] </label>
        [#else]
          [#if fundingSource.centerType?? && fundingSource.centerType == 1][@s.text name="projectCofunded.participatingCenter" /][/#if]
          [#if fundingSource.centerType?? && fundingSource.centerType == 2][@s.text name="projectCofunded.crpLeadCenter" /][/#if]
        [/#if]
      </div>
      [#-- Project title --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">[@customForm.input name="fundingSource.title" i18nkey="projectCofunded.title" required=true editable=editable /] </div>
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
        <div class="row">
           <div class="col-md-4">[@customForm.input name="fundingSource.startDate" i18nkey="projectCofunded.startDate" required=true  editable=editable /] </div>
           <div class="col-md-4">[@customForm.input name="fundingSource.endDate" i18nkey="projectCofunded.endDate" required=true  editable=editable/] </div>
           <div class="col-md-4">[@customForm.input name="fundingSource.financeCode"  i18nkey="projectCofunded.financeCode" placeholder="projectCofunded.financeCode.placeholder" editable=editable/] </div>
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
          <div class="col-md-6">[@customForm.select name="fundingSource.status" i18nkey="projectCofunded.agreementStatus"  listName="status" keyFieldName=""  displayFieldName="" header=false editable=editable /] </div>
          <div class="col-md-6">[@customForm.select name="fundingSource.budgetType.id"   i18nkey="projectCofunded.type"  className="type" listName="budgetTypes" header=false required=true editable=editable&&action.canEditType() /]</div>
        </div>
      </div>
      [#-- CGIAR lead center --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">
            [@customForm.select name="fundingSource.leader.id" i18nkey="fundingSource.leadPartner" className="institution"  listName="institutions" keyFieldName="id"  displayFieldName="composedName" required=true editable=editable && action.canEditInstitution() /]
          </div>
        </div>
      </div>
      [#-- Contact person name and email --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-6">[@customForm.input name="fundingSource.contactPersonName" i18nkey="projectCofunded.contactName" required=true editable=editable /]</div>
          <div class="col-md-6">[@customForm.input name="fundingSource.contactPersonEmail" i18nkey="projectCofunded.contactEmail" required=true editable=editable /]</div>
        </div>
      </div>
      [#-- Donor --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">
            [@customForm.select name="fundingSource.institution.id" i18nkey="projectCofunded.donor" className="donor"  listName="institutionsDonors" keyFieldName="id"  displayFieldName="ComposedName" required=true editable=editable /]
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
          [#assign budget = action.getBudget(year) /]
          [#assign budgetIndex = action.getIndexBugets(year) /]
          
          <small class="grayLabel pull-right"> (Remaining budget US$ <span class="projectAmount">${((fundingSource.getRemaining(year))!0)?number?string(",##0.00")}</span>) </small>
          
          <h5 class="sectionSubTitle">Budget Amount</h5>
          <div class="budgetsYear">
            <div class="col-md-4">
              <input type="hidden" name="fundingSource.budgets[${budgetIndex}].year" value="${year}"/>
              <input type="hidden" name="fundingSource.budgets[${budgetIndex}].id" value="${(budget.id)!}"/>
              [#if editable]
                [@customForm.input name="fundingSource.budgets[${budgetIndex}].budget" i18nkey="projectCofunded.budgetYear" paramText="${year}" className="currencyInput" required=true editable=editable /]
              [#else]
              <div class="input">
              	<p>US$ <span>${((budget.budget)!0)?number?string(",##0.00")}</p>
              </div>
                
              [/#if]
            </div>
            <div class="clearfix"></div>
          </div>
          <br />
          <h5 class="sectionSubTitle">Projects</h5>
          [#assign counter = 0 /]
          [#list fundingSource.projectBudgetsList as projectBudget]
            [#if projectBudget.year == year]
            <div class="grayBox col-md-12 borderBox">
              <div class="col-md-12 pContributionTitle form-group">${(projectBudget.project.composedName)!'null'}</div>
              <div class="col-md-5 form-group">
                <span class="col-md-2"><b>Type:</b></span>
                <span class="col-md-5">${projectBudget.budgetType.name}</span>
              </div>
              <div class="col-md-7">
                <span class="col-md-2"><b>Amount:</b></span>
                <span class="col-md-5 currencyInput">US$ <span>${((projectBudget.amount)!0)?number?string(",##0.00")} </span>
              </div>
            </div>
            [#assign counter = counter + 1 /]
            [/#if]
          [/#list]
          [#if counter = 0 ]
            <p class="messageText">No projects adopting this funding source for ${year}.</p>
          [/#if] 
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


[#include "/WEB-INF/global/pages/footer.ftl"]
