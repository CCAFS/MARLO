[#ftl]
[#assign title = "Project Budget By Cluster of activities" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectBudgetByCoAs.js", "${baseUrl}/js/global/autoSave.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectBudgetByCoAs.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "budgetByCoAs" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectBudgetByCoAs", "nameSpace":"/projects", "action":""}
] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign startYear = (project.startDate?string.yyyy)?number /]
[#assign endYear = (project.endDate?string.yyyy)?number /]
[#if currentCycleYear gt endYear][#assign selectedYear = endYear /][#else][#assign selectedYear = currentCycleYear /][/#if]
[#assign budgetCounter = 0 /]
[#assign type = { 
  'w1w2': 'w1w2',
  'w3': '2',
  'bilateral': '3',
  'centerFunds': 'centerFunds'
} /]

<div class="container">
  <div class="helpMessage"><img src="${baseUrl}/images/global/icon-help.png" /><p> [@s.text name="projectBudgetByCoAs.help" /] </p></div> 
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
          <h3 class="headTitle">[@s.text name="projectBudgetByCoAs.title" /]</h3>
          
          [#if project.crpActivities?has_content && project.crpActivities?size gt 1]
            [#-- Year Tabs --]
            <ul class="nav nav-tabs budget-tabs" role="tablist">
              [#list startYear .. endYear as year]
                <li class="[#if year == selectedYear]active[/#if]"><a href="#year-${year}" role="tab" data-toggle="tab">${year} [@customForm.req required=isYearRequired(year) /] </a></li>
              [/#list]
            </ul>
            [#-- Years Content --]
            <div class="tab-content budget-content">
              [#list startYear .. endYear as year]
                <div role="tabpanel" class="tab-pane [#if year == selectedYear]active[/#if]" id="year-${year}">
                  <div class="overallYearBudget fieldset clearfix">
                    <h5 class="title">Remaining ${year} total budget amount</h5>
                    <div class="row">
                      [#-- W1/W2 --]
                      [#if !project.bilateralProject]
                      <div class="col-md-3"><h5 class="subTitle">W1/W2 <small><span class="totalByYear-${type.w1w2}">100%</span></small></h5></div>
                      [/#if]
                      [#-- W3 --]
                      <div class="col-md-3"><h5 class="subTitle">W3 <small> <span class="totalByYear-${type.w3}">100%</span></small></h5></div>
                      [#-- Bilateral  --]
                      <div class="col-md-3"><h5 class="subTitle">Bilateral <small> <span class="totalByYear-${type.bilateral}">100%</span></small></h5></div>
                      [#-- Center Funds --]
                      [#if !project.bilateralProject]
                      <div class="col-md-3"><h5 class="subTitle">Center Funds <small> <span class="totalByYear-${type.centerFunds}">100%</span></small></h5></div>
                      [/#if]
                    </div>
                    
                    <h5 class="title">Remaining ${year} gender amount</h5>
                    <div class="row">
                      [#-- W1/W2 --]
                      [#if !project.bilateralProject]
                      <div class="col-md-3"><h5 class="subTitle">W1/W2 <small><span class="totalByYear-${type.w1w2}">100%</span></small></h5></div>
                      [/#if]
                      [#-- W3 --]
                      <div class="col-md-3"><h5 class="subTitle">W3 <small> <span class="totalByYear-${type.w3}">100%</span></small></h5></div>
                      [#-- Bilateral  --]
                      <div class="col-md-3"><h5 class="subTitle">Bilateral <small> <span class="totalByYear-${type.bilateral}">100%</span></small></h5></div>
                      [#-- Center Funds --]
                      [#if !project.bilateralProject]
                      <div class="col-md-3"><h5 class="subTitle">Center Funds <small> <span class="totalByYear-${type.centerFunds}">100%</span></small></h5></div>
                      [/#if]
                    </div>
                  </div>
                
                  
                  [#list project.crpActivities as coa]
                
                    [@projectCoAMacro element=coa name="" index=-1 selectedYear=year /]
                  [/#list]
                  
                </div>
              [/#list]  
            </div>
            
            [#-- Section Buttons & hidden inputs--]
            [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
          
          [#else]
            <div class="emptyMessage simpleBox text-center">This section is unavailable for this project</div>
          [/#if]
         
        [/@s.form] 
      </div>
    </div>  
</section>


[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro projectCoAMacro element name index=-1 selectedYear=0 isTemplate=false] 
  
  <div id="projectPartner-${isTemplate?string('template',(element.id)!)}" class="projectPartner expandableBlock borderBox ${(isLeader?string('leader',''))!} ${(isCoordinator?string('coordinator',''))!}" style="display:${isTemplate?string('none','block')}">
    [#-- Partner Title --]
    <div class="blockTitle opened">
      [#-- Title --] 
      <span class="partnerTitle">${(element.description)!''}</span> 
       
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
          [#-- Percentage of Amount --]
          <tr>
            <td class="amountType"> % of total</td>
        
            [#if !project.bilateralProject]
               [#assign budgetCounterW1=action.getIndexBudget(element.id,selectedYear,1)/]
             
              <td class="budgetColumn">
               [#assign budgetW1=action.getBudget(element.id,selectedYear,1)/]
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW1}].id" value="${(budgetW1.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW1}].crpClusterOfActivity.id" value="${(element.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW1}].budgetType.id" value="1"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW1}].year" value="${(selectedYear)!}"/>
        
              [#if editable]
             
               
                [@customForm.input name="project.budgetsCluserActvities[${budgetCounterW1}].amount" i18nkey="budget.amount" showTitle=false className="percentageInput type-${type.w1w2}" required=true  /]
              [#else]
                <div class="input"><p><span class="percentageInput totalByPartner-${type.w1w2}">${((budgetW1.amount)!0)?number?string(",##0.00")}</span></p></div>
              [/#if]
            </td>
            [/#if]
            [#-- W3 --]
            <td class="budgetColumn">
                [#assign budgetCounterW3=action.getIndexBudget(element.id,selectedYear,2)/]
                [#assign budgetW3=action.getBudget(element.id,selectedYear,2)/]
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW3}].id" value="${(budgetW3.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW3}].crpClusterOfActivity.id" value="${(element.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW3}].budgetType.id" value="2"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW3}].year" value="${(selectedYear)!}"/>
              [#if editable && action.hasBudgets(2,selectedYear)]
                  
                [@customForm.input name="project.budgetsCluserActvities[${budgetCounterW3}].amount" i18nkey="budget.amount" showTitle=false className="percentageInput type-${type.w3}" required=true   /]
              [#else]
                <div class="input"><p><span class="percentageInput totalByPartner-${type.w3}">${((budgetW3.amount)!0)?number?string(",##0.00")}</span></p></div>
              [/#if]
            </td>
            [#-- Bilateral  --]
            <td class="budgetColumn">
                [#assign budgetCounterBilateral=action.getIndexBudget(element.id,selectedYear,3)/]
                [#assign budgetBilateral=action.getBudget(element.id,selectedYear,3)/]
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterBilateral}].id" value="${(budgetBilateral.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterBilateral}].crpClusterOfActivity.id" value="${(element.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterBilateral}].budgetType.id" value="3"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterBilateral}].year" value="${(selectedYear)!}"/>

              [#if editable && action.hasBudgets(3,selectedYear)]
                [@customForm.input name="project.budgetsCluserActvities[${budgetCounterBilateral}].amount" i18nkey="budget.amount" showTitle=false className="percentageInput type-${type.bilateral}" required=true   /]
              [#else]
                <div class="input"><p><span class="percentageInput totalByPartner-${type.bilateral}">${((budgetBilateral.amount)!0)?number?string(",##0.00")}</span></p></div>
              [/#if]
            </td>
            [#-- Center Funds --]
            [#if !project.bilateralProject]
            <td class="budgetColumn">
                [#assign budgetCounterCenter=action.getIndexBudget(element.id,selectedYear,4)/]
                [#assign budgetCenter=action.getBudget(element.id,selectedYear,4)/]
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterCenter}].id" value="${(budgetCenter.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterCenter}].crpClusterOfActivity.id" value="${(element.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterCenter}].budgetType.id" value="4"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterCenter}].year" value="${(selectedYear)!}"/>

              [#if editable]
                [@customForm.input name="project.budgetsCluserActvities[${budgetCounterCenter}].amount" i18nkey="budget.amount" showTitle=false className="percentageInput type-${type.centerFunds}" required=true /]
              [#else]
                <div class="input"><p><span class="percentageInput totalByPartner-${type.centerFunds}">${((budgetCenter.amount)!0)?number?string(",##0.00")}</span></p></div>
              [/#if]
            </td>
            [/#if]
          </tr>
          
          [#-- Percentage of Amount --]
          <tr>
            <td class="amountType"> % of gender</td>
            [#-- W1/W2 --]
            [#if !project.bilateralProject]
            <td class="budgetColumn">
              [#if editable]

                [@customForm.input name="project.budgetsCluserActvities[${budgetCounterW1}].genderPercentage" i18nkey="budget.amount" showTitle=false className="percentageInput type-${type.w1w2}" required=true  /]
              [#else]
                <div class="input"><p><span class="percentageInput totalByPartner-${type.w1w2}">${((budgetW1.genderPercentage)!0)?number?string(",##0.00")}</span></p></div>
              [/#if]
            </td>
            [/#if]
            [#-- W3 --]
            <td class="budgetColumn">
              [#if editable && action.hasBudgets(2,selectedYear)]
                
                [@customForm.input name="project.budgetsCluserActvities[${budgetCounterW3}].genderPercentage" i18nkey="budget.amount" showTitle=false className="percentageInput type-${type.w3}" required=true   /]
              [#else]
                <div class="input"><p><span class="percentageInput totalByPartner-${type.w3}">${((budgetW3.genderPercentage)!0)?number?string(",##0.00")}</span></p></div>
              [/#if]
            </td>
            [#-- Bilateral  --]
            <td class="budgetColumn">
              [#if editable && action.hasBudgets(3,selectedYear)]
                 
                [@customForm.input name="project.budgetsCluserActvities[${budgetCounterBilateral}].genderPercentage" i18nkey="budget.amount" showTitle=false className="percentageInput type-${type.bilateral}" required=true   /]
              [#else]
                <div class="input"><p><span class="percentageInput totalByPartner-${type.bilateral}">${((budgetBilateral.genderPercentage)!0)?number?string(",##0.00")}</span></p></div>
              [/#if]
            </td>
            [#-- Center Funds --]
            [#if !project.bilateralProject]
            <td class="budgetColumn">
              [#if editable]

                [@customForm.input name="project.budgetsCluserActvities[${budgetCounterCenter}].genderPercentage" i18nkey="budget.amount" showTitle=false className="percentageInput type-${type.centerFunds}" required=true /]
              [#else]
                <div class="input"><p><span class="percentageInput totalByPartner-${type.centerFunds}">${((budgetCenter.genderPercentage)!0)?number?string(",##0.00")}</span></p></div>
              [/#if]
            </td>
            [/#if]
          </tr>
        </tbody>
      </table>

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