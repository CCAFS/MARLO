[#ftl]
[#assign title = "Project Budget By Partners" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2", "dropzone", "blueimp-file-upload"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectBudgetByPartners.js", "${baseUrl}/js/global/autoSave.js","${baseUrl}/js/global/fieldsValidation.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectBudgetByPartners.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "budgetByPartners" /]
[#assign editable = !(reportingActive) /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectBudgetByPartners", "nameSpace":"/projects", "action":""}
] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#if !reportingActive]
<div class="container helpText viewMore-block">
  <div style="display:none" class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10">
      [#if project.projectEditLeader] [@s.text name="projectBudgets.help2" /] [#else] [@s.text name="projectBudgets.help1" /] [/#if]
    </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
[/#if]
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          [#-- Section Title --]
          <h3 class="headTitle">[@s.text name="projectBudgetByPartners.title" /]</h3>
          
          [#if project.startDate?? && project.endDate??]
          
            [#assign startYear = (project.startDate?string.yyyy)?number /]
            [#assign endYear = (project.endDate?string.yyyy)?number /]
            
            [#if currentCycleYear gt endYear]
              [#assign selectedYear = endYear /]
            [#elseif currentCycleYear lt startYear]
              [#assign selectedYear = startYear /]
            [#else]
              [#assign selectedYear = currentCycleYear /]
            [/#if]

            [#-- Year Tabs --]
            <ul class="nav nav-tabs budget-tabs" role="tablist">
              [#list startYear .. endYear as year]
                <li class="[#if year == selectedYear]active[/#if]"><a href="#year-${year}" role="tab" data-toggle="tab">${year} [@customForm.req required=isYearRequired(year) && editable && !reportingActive /] </a></li>
              [/#list]
            </ul>
            
            [#-- Years Content --]
            <div class="tab-content budget-content">
              [#list startYear .. endYear as year]
                <div role="tabpanel" class="tab-pane [#if year == selectedYear]active[/#if]" id="year-${year}">
                  [#-- No Budget available for this year --]
                  [#if reportingActive && (year  lte currentCycleYear)]
                      <div class="note">No budget available for this year</div>
                  [#else]
                  
                    [#-- Budgest cannot be editable message --]
                    [#if !isYearEditable(year) && editable]<div class="note">Budgets for ${year} cannot be editable.</div>[/#if]
                  
                    <div class="overallYearBudget fieldset clearfix">
                      <h5 class="title">Overall ${year} budget</h5>
                      <div class="row">
                      <table class="text-center">
                        <tr>
                        [#list budgetTypesList as budgetType]
                          [#-- Budget Type--]
                          <td class=""><h5 class="subTitle"> ${budgetType.name} <br /> <small>US$ <span class="totalByYear-${budgetType.id}">${action.getTotalYear(year,budgetType.id)?number?string(",##0.00")}</span></small></h5></td>
                        [/#list]
                        </tr>
                      </table>
                      </div>
                    </div>
                    
                    [#if projectPPAPartners?has_content]
                      [#list projectPPAPartners as projectPartner]
                        [@projectPartnerMacro element=projectPartner name="project.partners[${projectPartner_index}]" index=projectPartner_index selectedYear=year/]
                      [/#list]
                    [#else]
                      <div class="simpleBox emptyMessage text-center">Before entering budget information, you need to add project partner in <a href="[@s.url action="${crpSession}/partners"][@s.param name="projectID" value=projectID /][@s.param name="edit" value=true /][/@s.url]">partners section</a></div>
                    [/#if]

                  [/#if]
                </div>
              [/#list]  
            </div>
            
            [#if !reportingActive]
            [#-- Section Buttons & hidden inputs--]
            [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
            [/#if]
          [#else]
            <div class="simpleBox emptyMessage text-center">Before entering this section, please fill project start & end date <a href="[@s.url action="${crpSession}/description"][@s.param name="projectID" value=projectID /][@s.param name="edit" value=true /][/@s.url]">description section </a>and click <span class="label label-success">save</span></div>  
          [/#if]
         
        [/@s.form] 
      </div>
    </div>  
</section>

<span id="budgetIndex" style="display:none">${budgetIndex+1}</span>

[#-- Bilateral Co-Funded Project Popup --]
[#include "/WEB-INF/global/macros/fundingSourcesPopup.ftl"]

[#-- W3/bilaterl Fund Template --]
[@fundingSourceMacro element={} name="project.budgets" selectedYear=-1 index=-1  isTemplate=true /]


[#include "/WEB-INF/global/pages/footer.ftl"]


[#macro projectPartnerMacro element name index=-1 selectedYear=0 isTemplate=false]
  [#local isLeader = (element.leader)!false/]
  [#local isCoordinator = (element.coordinator)!false/]
  [#local isPPA = (action.isPPA(element.institution))!false /]
  
  <div id="projectPartner-${isTemplate?string('template',(element.id)!)}" class="projectPartner expandableBlock borderBox ${(isLeader?string('leader',''))!} ${(isCoordinator?string('coordinator',''))!}" style="display:${isTemplate?string('none','block')}">
    [#-- Partner Title --]
    <div class="blockTitle opened">
      [#-- Title --] 
      <span class="partnerTitle">${(element.institution.composedName)!''}</span>
      <span class="partnerInstitutionId" style="display:none">${(element.institution.id)!}</span>
      [#-- Tags --]
      <div class="partnerTags pull-right">
        <span class="label label-success type-leader" style="display:${(isLeader?string('inline','none'))!'none'}">Leader</span>
        <span class="label label-default type-coordinator" style="display:${(isCoordinator?string('inline','none'))!'none'}">Coordinator</span>
        <span class="index ${isPPA?string('ppa','')}">${isPPA?string('Managing / PPA Partner','Partner')}</span>
      </div>
      <div class="clearfix"></div>
    </div>
    
    <div class="blockContent" style="display:block">
      <hr />
      <table class="table">
        <thead>
          <tr>
            <th class="amountType"> </th>
            [#list budgetTypesList as budgetType]
              [#-- Budget Type--]
              <th class="text-center">${budgetType.name}</th>
            [/#list]
          </tr>
        </thead>
        <tbody>
          [#-- Budget Amount --]
          <tr>
            <td class="amountType"> Budget:</td>
            [#list budgetTypesList as budgetType]
              [#-- Budget Type--]
              <td class="budgetColumn">
                <div class="input"><p>US$ <span class="currencyInput totalByPartner-${budgetType.id}">${((action.getTotalAmount(element.institution.id, selectedYear, budgetType.id))!0)?number?string(",##0.00")}</span></p></div>
              </td>
            [/#list]
          </tr>
          [#-- Gender Budget Percentage --]
          [#if project.projectEditLeader && action.hasSpecificities('crp_budget_gender')]
          <tr>
            <td class="amountType"> Gender %:</td>
            [#list budgetTypesList as budgetType]
              [#-- Budget Type--]
              <td class="budgetColumn">
                <div class="input"><p><span class="percentageLabel type-${budgetType.id}">${((action.getTotalGenderPer(element.institution.id, selectedYear, budgetType.id))!0)}%</span></p></div>
                <div class="row percentageAmount type-${budgetType.id} text-center">
                  <small>US$ <span>${((action.getTotalGender(element.institution.id, selectedYear, budgetType.id))!0)?number?string(",##0.00")}</span></small>
                </div>
              </td>
            [/#list]
          </tr>
          [/#if]
        </tbody>
      </table>
      
      <h5 class="sectionSubTitle">Funding Sources:</h5>
      <div class="projectW3bilateralFund-block">
        [#-- Funding sources --]
        [#assign fundingSources = 0 /]
        <div class="projectW3bilateralFund-list simpleBox">
          [#attempt]
            [#list action.getBudgetsByPartner(element.institution.id,selectedYear) as budget ]
                [#assign fundingSources++ /]
                [#local indexBudgetfundingSource=action.getIndexBudget(element.institution.id,selectedYear,budget.fundingSource.budgetType.id,budget.fundingSource.id) ]
                [@fundingSourceMacro element=budget name="project.budgets" selectedYear=selectedYear  index=indexBudgetfundingSource /]
            [/#list]
          [#recover]
            ERROR LOADING FUNDING SOURCES
          [/#attempt]
          
          [#if fundingSources == 0]
            [#if editable && isYearEditable(selectedYear) && action.canSearchFunding(element.institution.id)]
              <p class="emptyMessage text-center">Add a new one clicking on "[@s.text name="form.buttons.selectProject" /]" button below.</p>
            [#else]
              <p class="emptyMessage text-center">Theres is not a funding source added.</p>
            [/#if]
          [/#if]
        </div>
        [#-- Search project bilateral co-funded --]
        [#if editable  && isYearEditable(selectedYear) && action.canSearchFunding(element.institution.id)]
        <div class="text-right">
          <div class="searchProject button-blue ${action.canAddFunding(element.institution.id)?string('canAddFunding','')}"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> [@s.text name="form.buttons.selectProject" /]</div>
        </div>
        [/#if]
      </div>
      
    </div>
  </div>
[/#macro]

[#macro fundingSourceMacro element name selectedYear index=-1  isTemplate=false]
  <div id="projectW3bilateralFund-${isTemplate?string('template', index )}" class="projectW3bilateralFund expandableBlock grayBox" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]" /]
    [#-- Remove --]
    [#if (editable && isYearEditable(selectedYear) && action.canEditFunding(((element.fundingSource.budgetType.id)!-1),(element.institution.id)!-1) ) || isTemplate]<div class="removeIcon removeW3bilateralFund" title="Remove"></div>[/#if]
    
    [#-- Project Title --]
    <p class="checked">
      [#assign fsRemaining = ((element.fundingSource.getRemaining(selectedYear))!0)?number /]
      <small>Funding source #<span class="titleId">${(element.fundingSource.id)!}</span></small> -
      <small class="grayLabel [#if fsRemaining lt 0]fieldError[/#if]"> (Remaining budget US$ <span class="projectAmount">${fsRemaining?string(",##0.00")}</span>) </small>
    </p> 
    
    [#if !isTemplate]
    <a href="[@s.url namespace="/fundingSources" action="${crpSession}/fundingSource"][@s.param name="fundingSourceID" value="${(element.fundingSource.id)!}" /][@s.param name="edit" value="true" /][/@s.url]" class="" target="_BLANK"> 
    [/#if]
      <p> <span class="title">${(element.fundingSource.title)!}</span> </p>
    [#if !isTemplate]
    </a>
    [/#if]
    
    <input type="hidden" class="id " name="${customName}.id" value="${(element.id)!}"/>
    <input type="hidden" class="institutionId" name="${customName}.institution.id" value="${(element.institution.id)!}"/>
    <input type="hidden" class="selectedYear" name="${customName}.year" value="${(selectedYear)!}"/>
    <input type="hidden" class="projectId institution-${(element.institution.id)!} year-${(selectedYear)!}" name="${customName}.fundingSource.id" value="${(element.fundingSource.id)!}"/>
    
    [#-- Project Fund --]
    <div class="row w3bilateralFund">
      <div class="col-md-4">
        <div class="row col-md-6"> <strong>Type:</strong> </div>
        <div class="row col-md-8">
          <span class="budgetTypeName">${(element.fundingSource.budgetType.name)!}</span> 
          <input type="hidden" class="budgetTypeId" name="${customName}.budgetType.id" value="${(element.fundingSource.budgetType.id)!}" />
        </div>
      </div>
      <div class="col-md-4">
        <div class="row col-md-5">
          <div class="row"><strong>Amount:</strong></div>
        </div>
        <div class="row col-md-9">
        [#if (editable && isYearEditable(selectedYear) && action.canEditFunding(((element.fundingSource.budgetType.id)!-1),(element.institution.id)!-1) )|| isTemplate]
          [@customForm.input name="${customName}.amount" i18nkey="budget.amount" showTitle=false className="currencyInput fundInput type-${(element.fundingSource.budgetType.id)!'none'}" required=true /]
        [#else]
          <div class="input"><p>US$ <span>${((element.amount)!0)?number?string(",##0.00")}</span></p></div>
          <input type="hidden" name="${customName}.amount" value="${(element.amount)!0}" />
        [/#if]
        </div>
      </div>
      <div class="col-md-4">
        [#if project.projectEditLeader && action.hasSpecificities('crp_budget_gender')]
          <div class="row col-md-6"> <div class="row"><strong>Gender %:</strong></div> </div>
          <div class="row col-md-7">
          [#if (editable && isYearEditable(selectedYear)) || isTemplate]
            [@customForm.input name="${customName}.genderPercentage" i18nkey="budget.genderPercentage" showTitle=false className="percentageInput type-${(element.fundingSource.budgetType.id)!'none'}" required=true   /]
          [#else]  
            <div class="input"><p><span>${((element.genderPercentage)!0)}%</span></p></div>
            <input type="hidden" name="${customName}.genderPercentage" value="${(element.genderPercentage)!0}" />
          [/#if]
          </div>
        [/#if]
      </div>
    </div>
  </div>
[/#macro]


[#-- Get if the year is required--]
[#function isYearRequired year]
  [#if project.endDate??]
    [#assign endDate = (project.endDate?string.yyyy)?number]
    [#if reportingActive]
      [#return  (year == currentCycleYear)  && (endDate gte year) ]
    [#else]
      [#return  (year == currentCycleYear) && (endDate gte year) ]
    [/#if]
  [#else]
    [#return false]
  [/#if]
[/#function]

[#-- Get if the year is editable--]
[#function isYearEditable year]
  [#if project.endDate??]
    [#assign endDate = (project.endDate?string.yyyy)?number]
    [#if reportingActive]
      [#return  (year gte currentCycleYear) ]
    [#else]
      [#return  (year gte currentCycleYear) ]
    [/#if]
  [#else]
    [#return false]
  [/#if]
[/#function]