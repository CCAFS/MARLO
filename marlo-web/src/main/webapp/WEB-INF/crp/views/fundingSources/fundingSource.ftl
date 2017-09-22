[#ftl]
[#assign title = "MARLO Funding Sources" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-${fundingSource.id}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload", "datatables.net", "datatables.net-bs","flat-flags"] /]
[#assign customJS = [
  "${baseUrl}/global/js/fieldsValidation.js",
  "${baseUrlMedia}/js/fundingSources/fundingSource.js", 
  "${baseUrl}/global/js/autoSave.js" 
  ] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/fundingSources/fundingSource.css"] /]
[#assign currentSection = "fundingSources" /]

[#assign breadCrumb = [
  {"label":"fundingSourcesList", "nameSpace":"/fundingSources", "action":""}
]/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#assign startYear = ((fundingSource.startDate?string.yyyy)?number)!currentCycleYear /]
[#assign endYear = ((fundingSource.endDate?string.yyyy)?number)!startYear /]
    
<section class="container">
  <article class="fullBlock col-md-12" id="mainInformation">
  
  [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
  
  <div class="col-md-12">
  
  [#-- Messages --]
  [#include "/WEB-INF/crp/views/fundingSources/messages-fundingSource.ftl" /]
  
  <h4 class="headTitle">General information</h4> 
    <div class="borderBox informationWrapper">
      [#-- Loading --]
      <div class="loading" style="display:none"></div>
    
      
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
          <div class="col-md-6">[@customForm.select name="fundingSource.status" i18nkey="projectCofunded.agreementStatus" className="agreementStatus"  listName="status" keyFieldName=""  displayFieldName="" header=false editable=editable /] </div>
          <div class="col-md-6">
            [@customForm.select name="fundingSource.budgetType.id" i18nkey="projectCofunded.type" className="type" listName="budgetTypes" header=false required=true editable=editable && action.canEditType() /]
            [#-- W1W2 Tag --]
            [#if action.hasSpecificities('crp_fs_w1w2_cofinancing')]
              [#assign isW1W2 = (fundingSource.budgetType.id == 1)!false /]
              [#assign w1w2TagValue = (fundingSource.w1w2)!false /]
              <div class="w1w2-tag" style="display:${isW1W2?string('block','none')};">
                <div class="checkbox dottedBox">
                  <label for="w1w2-tag-input">
                    [#if editable]
                    <input type="checkbox" name="fundingSource.w1w2" value="true" id="w1w2-tag-input" [#if w1w2TagValue]checked[/#if]/>
                    [#else]
                       <img src="${baseUrl}/global/images/checked-${w1w2TagValue?string}.png" /> 
                    [/#if]
                    <small>[@customForm.text name="fundingSource.w1w2Tag" readText=!editable /]</small></label>
                </div>
              </div>
            [/#if]
          </div>
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
    
    <h4 class="headTitle">Location information</h4> 
    <div class="borderBox informationWrapper">
    [#-- GLOBAL DIMENSION --]
    [#if editable]
      <div class="form-group row ">
        <div class="col-md-6">[@customForm.yesNoInput  label="projectCofunded.globalDimensionQuestion" name="fundingSource.global"  editable=editable inverse=false  cssClass="" /] </div>
      </div>
      <hr />
      <div class="form-group row">
        <div class="col-md-6">[@customForm.yesNoInput  label="projectCofunded.regionalDimensionQuestion" name="region"  editable=editable inverse=false  cssClass="isRegional" /] </div>
      </div>
      [#else]
      <div class="form-group row ">
        <div class="col-md-12">
      [#if fundingSource.global]
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
                  <li id="" class="country clearfix col-md-2">
                  [#if editable ]
                    <div class="removeCountry removeIcon" title="Remove country"></div>
                  [/#if]
                    <input class="id" type="hidden" name="fundingSource.fundingCountry[${country_index}].id" value="${(country.id)!-1}" />
                    <input class="cId" type="hidden" name="fundingSource.fundingCountry[${country_index}].locElement.isoAlpha2" value="${(country.locElement.isoAlpha2)!}" />
                    <span class="name"><span> <i class="flag-sm flag-sm-${(country.locElement.isoAlpha2)!}"></i> ${(country.locElement.name)!} </span></span>
                    <div class="clearfix"></div>
                  </li>
              [/#list]
              [#else]
              <p class="emptyText"> [@s.text name="No countries added yet." /]</p> 
            [/#if]
            </ul>
            [#if editable ]
              [@customForm.select name="" label=""  showTitle=false  i18nkey="" listName="countryLists" keyFieldName="isoAlpha2"  displayFieldName="name"  multiple=false required=true  className="countriesSelect" editable=editable /]
            [/#if] 
          </div>
        </div>
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
          <h5 class="sectionSubTitle">[@s.text name="fundingSource.projectsAssigned" /]:</h5>
          
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
    [#include "/WEB-INF/crp/views/fundingSources/buttons-fundingSources.ftl" /]
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
  <li id="countryTemplate" class="country clearfix col-md-2">
      <div class="removeCountry removeIcon" title="Remove country"></div>
      <input class="id" type="hidden" name="fundingSource.fundingCountry[-1].id" value="" />
      <input class="cId" type="hidden" name="fundingSource.fundingCountry[-1].locElement.isoAlpha2" value="" />
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

[#include "/WEB-INF/crp/pages/footer.ftl"]
