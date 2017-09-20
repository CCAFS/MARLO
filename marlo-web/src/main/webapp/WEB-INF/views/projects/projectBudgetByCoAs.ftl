[#ftl]
[#assign title = "Project Budget By Cluster of activities" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectBudgetByCoAs.js", 
  "${baseUrl}/global/js/autoSave.js",
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectBudgetByCoAs.css"] /]
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

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectBudgetByCoAs.help" /]</p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
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
                  
                  [#-- Budgest cannot be editable message --]
                  [#if !isYearEditable(year)]<div class="note">Percentages for ${year} cannot be editable.</div>[/#if]
                  
                  [#if action.hasBudgets(1,year) || action.hasBudgets(2,year) || action.hasBudgets(3,year) || action.hasBudgets(4,year) || action.hasBudgets(5,year)]
                  
                    <div class="overallYearBudget fieldset clearfix">
                      <h5 class="title">Remaining ${year} total budget amount</h5>
                      <div class="row">
                        [#list budgetTypesList as budgetType]
                          [#-- Budget Type--]
                          [#if action.hasBudgets(budgetType.id, year) ]
                            <div class="col-md-3">
                              <p class="subTitle"><strong>${budgetType.name}</strong> <br /> <small> <span class="context-total totalByYear-${budgetType.id}">${(action.getRemaining(budgetType.id,year))!}%</span> </small></p>
                            </div>
                          [/#if]
                        [/#list]
                      </div>
                      
                      [#if  action.hasSpecificities('crp_budget_gender')]
                      <h5 class="title">Remaining ${year} gender amount</h5>
                      <div class="row">
                        [#list budgetTypesList as budgetType]
                          [#-- Budget Type--]
                          [#if action.hasBudgets(budgetType.id, year) ]
                            <div class="col-md-3">
                              <p class="subTitle"><strong>${budgetType.name}</strong> <br /> <small><span class="context-gender totalByYear-${budgetType.id}">${(action.getRemainingGender(budgetType.id,year))!}%</span></small></p>
                            </div>
                          [/#if]
                        [/#list]
                      </div>
                      [/#if]
                    
                    </div>
                    
                    [#-- Project Cluster of activities --]
                    [#list project.crpActivities as coa]
                      [@projectCoAMacro element=coa name="" index=-1 selectedYear=year /]
                    [/#list]
                  
                  [#else]
                    <p class="emptyMessage text-center">[@s.text name="projectBudgetByCoAs.notBudgetSaved" /]</p>
                    
                  [/#if]
                  
                </div>
              [/#list]  
            </div>
            
            [#-- Section Buttons & hidden inputs--]
            [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
          
          [#else]
            <div class="emptyMessage simpleBox text-center">Not Available</div>
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
      <span class="partnerTitle">${(element.composedName)!''}</span> 
       
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
              [#if action.hasBudgets(budgetType.id, selectedYear)]
                <th class="text-center">${budgetType.name}</th>
              [/#if]
            [/#list]
          </tr>
        </thead>
        <tbody>
          [#-- Percentage of Amount --]
          <tr>
            <td class="amountType"> % of total</td>
            [#list budgetTypesList as budgetType]
              [#-- Budget Type--]
              [#if action.hasBudgets(budgetType.id, selectedYear)]
                <td class="budgetColumn">
                  [#assign budgetIndex= action.getIndexBudget(element.id,selectedYear, budgetType.id) /]
                  [#assign budgetObject= action.getBudget(element.id,selectedYear, budgetType.id) /]
                  [#assign customName = "project.budgetsCluserActvities[${budgetIndex}]" /]
                  <input type="hidden" name="${customName}.id" value="${(budgetObject.id)!}"/>
                  <input type="hidden" name="${customName}.crpClusterOfActivity.id" value="${(element.id)!}"/>
                  <input type="hidden" name="${customName}.budgetType.id" value="${budgetType.id}"/>
                  <input type="hidden" name="${customName}.year" value="${(selectedYear)!}"/>
                  [#if editable && isYearEditable(selectedYear)]
                    [@customForm.input name="${customName}.amount" i18nkey="budget.amount" showTitle=false className="percentageInput context-total  type-${budgetType.id}" required=true  /]
                  [#else]
                    <div class="input"><p><span class="percentageInput totalByPartner-${budgetType.id}">${((budgetObject.amount)!0)}%</span></p></div>
                    <input type="hidden" name="${customName}.amount" value="${(budgetObject.amount)!0}"/>
                  [/#if]
                </td>
              [/#if]
            [/#list]
          </tr>
          
          [#-- Percentage of Gender Amount --]
          [#if  action.hasSpecificities('crp_budget_gender')]
          <tr>
            <td class="amountType"> % of gender</td>
            [#list budgetTypesList as budgetType]
              [#-- Budget Type--]
              [#if action.hasBudgets(budgetType.id, selectedYear)]
              <td class="budgetColumn">
                [#assign budgetIndex= action.getIndexBudget(element.id,selectedYear, budgetType.id) /]
                [#assign budgetObject= action.getBudget(element.id,selectedYear, budgetType.id) /]
                [#assign customName = "project.budgetsCluserActvities[${budgetIndex}]" /]
                [#if editable && isYearEditable(selectedYear)]
                  [@customForm.input name="${customName}.genderPercentage" i18nkey="budget.amount" showTitle=false className="percentageInput context-gender type-${budgetType.id}" required=true  /]
                [#else]
                  <div class="input"><p><span class="percentageInput totalByPartner-${budgetType.id}">${((budgetObject.genderPercentage)!0)}%</span></p></div>
                  <input type="hidden" name="${customName}.genderPercentage" value="${(budgetObject.genderPercentage)!0}"/>
                [/#if]
              </td>
              [/#if]
            [/#list]
          </tr>
          [/#if]
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