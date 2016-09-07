[#ftl]
[#assign title = "Project Budget By Partners" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectBudgetByPartners.js", "${baseUrl}/js/global/autoSave.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectBudgetByPartners.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "budgetByPartners" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectBudgetByPartners", "nameSpace":"/projects", "action":""}
] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign startYear = (project.startDate?string.yyyy)?number /]
[#assign endYear = (project.endDate?string.yyyy)?number /]
[#assign type = { 
  'w1w2': 'w1w2',
  'w3': '2',
  'bilateral': '3',
  'centerFunds': 'centerFunds'
} /]

<div class="container">
  <div class="helpMessage"><img src="${baseUrl}/images/global/icon-help.png" /><p> [@s.text name="projectBudgetByPartners.help" /] </p></div> 
</div>
    
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
          [#-- Year Tabs --]
          <ul class="nav nav-tabs budget-tabs" role="tablist">
            [#list startYear .. endYear as year]
              <li class="[#if year == currentCycleYear]active[/#if]"><a href="#year-${year}" role="tab" data-toggle="tab">${year} [@customForm.req required=isYearRequired(year) /] </a></li>
            [/#list]
          </ul>
          
          
          [#-- Years Content --]
          <div class="tab-content budget-content">
            [#list startYear .. endYear as year]
              <div role="tabpanel" class="tab-pane [#if year == currentCycleYear]active[/#if]" id="year-${year}">
                <div class="overallYearBudget fieldset clearfix">
                  <h5 class="title">Overall ${year} budget</h5>
                  <div class="row">
                    [#-- W1/W2 --]
                    [#if !project.bilateralProject]
                    <div class="col-md-3"><h5 class="subTitle">W1/W2 <small>US$ <span class="totalByYear-${type.w1w2}">${action.getTotalYear(year,1)?number?string(",##0.00")}</span></small></h5></div>
                    [/#if]
                    [#-- W3 --]
                    <div class="col-md-3"><h5 class="subTitle">W3 <small>US$ <span class="totalByYear-${type.w3}">${action.getTotalYear(year,2)?number?string(",##0.00")}</span></small></h5></div>
                    [#-- Bilateral  --]
                    <div class="col-md-3"><h5 class="subTitle">Bilateral <small>US$ <span class="totalByYear-${type.bilateral}">${action.getTotalYear(year,3)?number?string(",##0.00")}</span></small></h5></div>
                    [#-- Center Funds --]
                    [#if !project.bilateralProject]
                    <div class="col-md-3"><h5 class="subTitle">Center Funds <small>US$ <span class="totalByYear-${type.centerFunds}">${action.getTotalYear(year,4)?number?string(",##0.00")}</span></small></h5></div>
                    [/#if]
                  </div>
                </div>
              
                [#if projectPPAPartners?has_content]
                  [#list projectPPAPartners as projectPartner]
                    [@projectPartnerMacro element=projectPartner name="project.partners[${projectPartner_index}]" index=projectPartner_index selectedYear=year/]
                  [/#list]
                [/#if]
              </div>
            [/#list]  
          </div>
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
          
         
        [/@s.form] 
      </div>
    </div>  
</section>

[#-- Bilateral Co-Funded Project Popup --]
[#include "/WEB-INF/global/macros/bilateralCoFundedPopup.ftl"]
  
[#include "/WEB-INF/global/pages/footer.ftl"]


[#macro projectPartnerMacro element name index=-1 selectedYear=0 isTemplate=false]
  [#local isLeader = (element.leader)!false/]
  [#local isCoordinator = (element.coordinator)!false/]
  [#local isPPA = (action.isPPA(element.institution))!false /]
  
  <div id="projectPartner-${isTemplate?string('template',(element.id)!)}" class="projectPartner expandableBlock borderBox ${(isLeader?string('leader',''))!} ${(isCoordinator?string('coordinator',''))!}" style="display:${isTemplate?string('none','block')}">
    [#-- Partner Title --]
    <div class="blockTitle opened">
      [#-- Title --]
      <span><span class="partnerTitle"></span>${(element.institution.composedName)!'New Project Partner'}</span>

      [#-- Tags --]
      <div class="partnerTags pull-right">
        <span class="label label-success type-leader" style="display:${(isLeader?string('inline','none'))!'none'}">Leader</span>
        <span class="label label-default type-coordinator" style="display:${(isCoordinator?string('inline','none'))!'none'}">Coordinator</span>
        <span class="index ${isPPA?string('ppa','')}">${isPPA?string('PPA Partner','Partner')}</span>
      </div>
      
      <div class="clearfix"></div>
    </div>
    
    <div class="blockContent" style="display:block">
      <hr />
      
      <table class="table">
        <thead>
          <tr>
            <th class="amountType"> </th>
            [#-- W1/W2 --]
            [#if !project.bilateralProject]
            <th class="text-center">W1/W2</th>
            [/#if]
            [#-- W3 --]
            <th class="text-center">W3</td>
            [#-- Bilateral  --]
            <th class="text-center">Bilateral</th>
            [#-- Center Funds --]
            [#if !project.bilateralProject]
            <th class="text-center">Center Funds</th>
            [/#if]
          </tr>
        </thead>
        <tbody>
          [#-- Budget Amount --]
          <tr>
            <td class="amountType"> Budget: </td>
            [#-- W1/W2 --]
            [#if !project.bilateralProject]
            <td class="budgetColumn">
              [#local indexBudgetW1W2 =action.getIndexBudget(element.institution.id,selectedYear,1) ]
              [#local budgetW1W2 = action.getBudget(element.institution.id,selectedYear,1) ]
              <input type="hidden" name="project.budgets[${indexBudgetW1W2}].id" value="${(budgetW1W2.id)!}"/>
              <input type="hidden" name="project.budgets[${indexBudgetW1W2}].institution.id" value="${(element.institution.id)!}"/>
              <input type="hidden" name="project.budgets[${indexBudgetW1W2}].budgetType.id" value="1"/>
              <input type="hidden" name="project.budgets[${indexBudgetW1W2}].year" value="${(selectedYear)!}"/>
              [#if editable]
                [@customForm.input name="project.budgets[${indexBudgetW1W2}].amount" showTitle=false className="currencyInput type-${type.w1w2}" required=true  /]
              [#else]
                <div class="input"><p>US$ <span class="currencyInput totalByPartner-${type.w1w2}">${((budgetW1W2.amount)!0)?number?string(",##0.00")}</span></p></div>
              [/#if]
            </td>
            [/#if]
            [#-- W3 --]
            <td class="budgetColumn">
              [#local indexBudgetW3=action.getIndexBudget(element.institution.id,selectedYear,2) ]
              [#local budgetW3 = action.getBudget(element.institution.id,selectedYear,2) ]
              <input type="hidden" name="project.budgets[${indexBudgetW3}].id" value="${(budgetW3.id)!}"/>
              <input type="hidden" name="project.budgets[${indexBudgetW3}].institution.id" value="${(element.institution.id)!}"/>
              <input type="hidden" name="project.budgets[${indexBudgetW3}].budgetType.id" value="2"/>
              <input type="hidden" name="project.budgets[${indexBudgetW3}].year" value="${(selectedYear)!}"/>
              [#if editable && project.bilateralProject]
                [@customForm.input name="project.budgets[${indexBudgetW3}].amount" showTitle=false className="currencyInput type-${type.w3}" required=true   /]
              [#else]
                <div class="input"><p>US$ <span class="currencyInput totalByPartner-${type.w3}">${((budgetW3.amount)!0)?number?string(",##0.00")}</span></p></div>
              [/#if]
            </td>
            [#-- Bilateral  --]
            <td class="budgetColumn">
              [#local indexBudgetBilateral=action.getIndexBudget(element.institution.id,selectedYear,3) ]
              [#local budgetBilateral = action.getBudget(element.institution.id,selectedYear,3) ]
              <input type="hidden" name="project.budgets[${indexBudgetBilateral}].id" value="${(budgetBilateral.id)!}"/>
              <input type="hidden" name="project.budgets[${indexBudgetBilateral}].institution.id" value="${(element.institution.id)!}"/>
              <input type="hidden" name="project.budgets[${indexBudgetBilateral}].budgetType.id" value="3"/>
              <input type="hidden" name="project.budgets[${indexBudgetBilateral}].year" value="${(selectedYear)!}"/>
              [#if editable && project.bilateralProject]
                [@customForm.input name="project.budgets[${indexBudgetBilateral}].amount" showTitle=false className="currencyInput type-${type.bilateral}" required=true   /]
              [#else]
                <div class="input"><p>US$ <span class="currencyInput totalByPartner-${type.bilateral}">${((budgetBilateral.amount)!0)?number?string(",##0.00")}</span></p></div>
              [/#if]
            </td>
            [#-- Center Funds --]
            [#if !project.bilateralProject]
            <td class="budgetColumn">
              [#local indexBudgetCenterFunds=action.getIndexBudget(element.institution.id,selectedYear,4) ]
              [#local budgetCenterFunds = action.getBudget(element.institution.id,selectedYear,4) ]
              <input type="hidden" name="project.budgets[${indexBudgetCenterFunds}].id" value="${(budgetCenterFunds.id)!}"/>
              <input type="hidden" name="project.budgets[${indexBudgetCenterFunds}].institution.id" value="${(element.institution.id)!}"/>
              <input type="hidden" name="project.budgets[${indexBudgetCenterFunds}].budgetType.id" value="4"/>
              <input type="hidden" name="project.budgets[${indexBudgetCenterFunds}].year" value="${(selectedYear)!}"/>
              [#if editable]
                [@customForm.input name="project.budgets[${indexBudgetCenterFunds}].amount" showTitle=false className="currencyInput type-${type.centerFunds}" required=true /]
              [#else]
                <div class="input"><p>US$ <span class="currencyInput totalByPartner-${type.centerFunds}">${((budgetCenterFunds.amount)!0)?number?string(",##0.00")}</span></p></div>
              [/#if]
            </td>
            [/#if]
          </tr>
          [#-- Budget Percentage --]
          [#if project.projectEditLeader]
          <tr>
            <td class="amountType"> Gender %:</td>
            [#-- W1/W2 --]
            [#if !project.bilateralProject]
            <td class="budgetColumn">
              [#if editable]
                [@customForm.input name="project.budgets[${indexBudgetW1W2}].genderPercentage" showTitle=false className="percentageInput type-${type.w1w2}" required=true  /]
              [#else]
                <div class="input"><p><span class="percentageInput type-${type.w1w2}">${((budgetW1W2.genderPercentage)!0)}%</span></p></div>
              [/#if]
              <div class="percentageAmount type-w1w2 text-center">
                <small>US$ <span class="percentageInput totalByPartner-${type.w1w2}">${(((budgetW1W2.amount/100)*budgetW1W2.genderPercentage)!0)?string(",##0.00")}</span></small>
              </div>
            </td>
            [/#if]
            [#-- W3 --]
            <td class="budgetColumn">
              [#if editable && project.bilateralProject]
                [@customForm.input name="project.budgets[${indexBudgetW3}].genderPercentage" showTitle=false className="percentageInput type-${type.w3}" required=true /]
              [#else]
                <div class="input"><p><span class="percentageInput type-${type.w3}">${((budgetW3.genderPercentage)!0)}%</span></p></div>
              [/#if]
              <div class="percentageAmount type-${type.w3} text-center">
                <small>US$ <span>${(((budgetW3.amount/100)*budgetW3.genderPercentage)!0)?string(",##0.00")}</span></small>
              </div>
            </td>
            [#-- Bilateral  --]
            <td class="budgetColumn">
              [#if editable && project.bilateralProject]
                [@customForm.input name="project.budgets[${indexBudgetBilateral}].genderPercentage" showTitle=false className="percentageInput type-${type.bilateral}" required=true /]
              [#else]
                <div class="input"><p><span class="percentageInput type-${type.bilateral}">${((budgetBilateral.genderPercentage)!0)}%</span></p></div>
              [/#if]
              <div class="percentageAmount type-${type.bilateral} text-center">
                <small>US$ <span>${(((budgetBilateral.amount/100)*budgetBilateral.genderPercentage)!0)?string(",##0.00")}</span></small>
              </div>
            </td>
            [#-- Center Funds --]
            [#if !project.bilateralProject]
            <td class="budgetColumn">
              [#if editable]
                [@customForm.input name="project.budgets[${indexBudgetCenterFunds}].genderPercentage" showTitle=false className="percentageInput type-${type.centerFunds}" required=true /]
              [#else]
                <div class="input"><p><span class="percentageInput type-${type.centerFunds}">${((budgetCenterFunds.genderPercentage)!0)}%</span></p></div>
              [/#if]
              <div class="row percentageAmount type-${type.centerFunds} text-center">
                <small>US$ <span>${(((budgetCenterFunds.amount/100)*budgetCenterFunds.genderPercentage)!0)?string(",##0.00")}</span></small>
              </div>
            </td>
            [/#if]
          </tr>
          [/#if]
        </thead>
      </table>
      
      [#if project.projectEditLeader && !project.bilateralProject]
      <h5 class="sectionSubTitle">W3 Funds & Bilateral:</h5>
      <div class="projectW3bilateralFund-block">
        <div class="projectW3bilateralFund-list simpleBox">
          [#list project.budgetsCofinancing as found]
          
          [#if found.year=selectedYear && element.institution.id=found.institution.id] 
           [#local indexBudgetBilateral=action.getIndexBudgetCofinancing(found.institution.id,found.projectBilateralCofinancing.id,selectedYear,found.budgetType.id) ]
            [@w3bilateralFundMacro element=found name="project.budgetsCofinancing" selectedYear=selectedYear  index=indexBudgetBilateral /]
          [/#if]
          [/#list]
        </div>
        <div class="text-right">
          <div class="searchProject button-blue"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> [@s.text name="form.buttons.selectProject" /]</div>
        </div>
      </div>
      [/#if]
    </div>
  </div>
[/#macro]

[#macro w3bilateralFundMacro element name selectedYear index=-1  isTemplate=false]
  <div id="projectW3bilateralFund-${isTemplate?string('template', index )}" class="projectW3bilateralFund expandableBlock grayBox" style="display:${isTemplate?string('none','block')}">
    [#-- remove --]
    [#if editable]<div class="removeIcon removeW3bilateralFund" title="Remove"></div>[/#if]
    [#-- Project Title --]
    <p class="title">P${element.projectBilateralCofinancing.id} ${element.projectBilateralCofinancing.title}</p>

    <input type="hidden" name="project.budgetsCofinancing[${index}].id" value="${(element.id)!}"/>
    <input type="hidden" name="project.budgetsCofinancing[${index}].institution.id" value="${(element.institution.id)!}"/>

    <input type="hidden" name="project.budgetsCofinancing[${index}].year" value="${(selectedYear)!}"/>
    <input type="hidden" name="project.budgetsCofinancing[${index}].projectBilateralCofinancing.id" value="${(element.projectBilateralCofinancing.id)!}"/>
             
    [#-- Project Fund --]
    <div class="row w3bilateralFund">
      <div class="col-md-5">
        <div class="row col-md-5"><strong>Type:</strong>  </div>
        ${(project.budgetsCofinancing[index].budgetType.id)!}
        <div class="row col-md-9">[@customForm.select name="project.budgetsCofinancing[${index}].budgetType.id"  value="${project.budgetsCofinancing[index].budgetType.id}" showTitle=false  disabled=!editable  listName="w3bilateralBudgetTypes" required=true editable=editable /]</div>
      </div>
      <div class="col-md-4">
        <div class="row col-md-6"><strong>Amount:</strong>  </div>
        <div class="row col-md-7">[@customForm.input name="project.budgetsCofinancing[${index}].amount" showTitle=false className="currencyInput type-${(element.budgetType.id)!'none'}" required=true editable=editable /]</div>
      </div>
      <div class="col-md-3">
        <div class="row col-md-8"><strong>Gender %:</strong>  </div>
        <div class="row col-md-7">[@customForm.input name="project.budgetsCofinancing[${index}].genderPercentage" showTitle=false className="percentageInput type-${(element.budgetType.id)!'none'}" required=true editable=editable /]</div>
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