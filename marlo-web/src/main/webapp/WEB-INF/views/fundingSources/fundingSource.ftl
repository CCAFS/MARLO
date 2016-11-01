[#ftl]
[#assign title = "MARLO Funding Sources" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-${fundingSource.id}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/fundingSources/fundingSource.js", "${baseUrl}/js/global/autoSave.js" ] /]
[#assign customCSS = ["${baseUrl}/css/fundingSources/fundingSource.css"] /]
[#assign currentSection = "fundingSources" /]

[#assign editable = true /]

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
  <h4 class="headTitle">General information</h4> 
    <div class="borderBox informationWrapper">
      [#-- Participating Center, CRP Lead Center --]
      <div class="pull-right">
        [#if editable]
        <label for="cofundedMode-1"><input type="radio" name="fundingSource.centerType" id="cofundedMode-1" value="1" [#if fundingSource.centerType?? && fundingSource.centerType == 1]checked="checked"[/#if] /> [@s.text name="projectCofunded.participatingCenter" /] </label><br />
        <label for="cofundedMode-2"><input type="radio" name="fundingSource.centerType" id="cofundedMode-2" value="2" [#if fundingSource.centerType?? && fundingSource.centerType == 2]checked="checked"[/#if] /> [@s.text name="projectCofunded.crpLeadCenter" /] </label>
        [#else]
          [#if fundingSource.centerType?? && fundingSource.centerType == 1][@s.text name="projectCofunded.participatingCenter" /][/#if]
          [#if fundingSource.centerType?? && fundingSource.centerType == 2][@s.text name="projectCofunded.crpLeadCenter" /][/#if]
        [/#if]
      </div>
      [#-- Project title --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">[@customForm.textArea name="fundingSource.description" i18nkey="projectCofunded.title" required=true editable=editable /] </div>
        </div>
      </div>
      [#-- start date, end date and finance code --]
      <div class="form-group">
        <div class="row">
           <div class="col-md-4">[@customForm.input name="fundingSource.startDate" i18nkey="projectCofunded.startDate" required=true editable=editable /] </div>
           <div class="col-md-4">[@customForm.input name="fundingSource.endDate" i18nkey="projectCofunded.endDate" required=true editable=editable/] </div>
           <div class="col-md-4">[@customForm.input name="fundingSource.financeCode"  i18nkey="projectCofunded.financeCode" placeholder="projectCofunded.financeCode.placeholder" editable=editable/] </div>
        </div>
      </div>
      [#-- Agreement status and total budget --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-6">[@customForm.select name="fundingSource.status" i18nkey="projectCofunded.agreementStatus"  listName="status" keyFieldName=""  displayFieldName="" header=false editable=editable /] </div>
          <div class="col-md-6">[@customForm.select name="fundingSource.budgetType.id"   i18nkey="projectCofunded.type" className="type" listName="budgetTypes" header=false required=true editable=editable /]</div>
        </div>
      </div>
      [#-- CGIAR lead center --]
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">
            [@customForm.select name="fundingSource.liaisonInstitution.id" i18nkey="CGIAR lead center"  listName="liaisonInstitutions" keyFieldName="id"  displayFieldName="composedName" required=true editable=editable /]
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
            [@customForm.select name="fundingSource.institution.id" i18nkey="projectCofunded.donor"  listName="institutions" keyFieldName="id"  displayFieldName="ComposedName" required=true editable=editable /]
          </div>
        </div>
      </div>
      <div class="note">
        [@s.text name="projectCofunded.donor.disclaimer" /]
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
          
          <h5 class="sectionSubTitle">Budget Amount</h5>
          <div class="budgetsYear">
            <div class="col-md-4">
              <input type="hidden" name="fundingSource.budgets[${budgetIndex}].year" value="${year}"/>
              <input type="hidden" name="fundingSource.budgets[${budgetIndex}].id" value="${(budget.id)!}"/>
              [@customForm.input name="fundingSource.budgets[${budgetIndex}].budget" i18nkey="projectCofunded.budgetYear" paramText="${year}" className="currencyInput" required=true editable=editable /]
            </div>
            <div class="clearfix"></div>
          </div>
          <br />
          <h5 class="sectionSubTitle">Projects</h5>
          [#list fundingSource.projectBudgetsList as projectBudget]
            [#if projectBudget.year == year]
            <div class="grayBox col-md-12 borderBox">
              <div class="col-md-12 pContributionTitle form-group">${(projectBudget.project.composedName)!'null'}</div>
              <div class="col-md-5 form-group">
                <span class="col-md-2"><b>Type:</b></span>
                <span class="col-md-3">${projectBudget.budgetType.name}</span>
              </div>
              <div class="col-md-7">
                <span class="col-md-2"><b>Amount:</b></span>
                <span class="col-md-5 currencyInput">US$ <span>${((projectBudget.amount)!0)?number?string(",##0.00")} </span>
              </div>
            </div>
            [/#if]
          [/#list] 
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
