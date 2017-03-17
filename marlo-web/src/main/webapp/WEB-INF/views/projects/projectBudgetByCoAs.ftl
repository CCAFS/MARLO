[#ftl]
[#assign title = "Project Budget By Cluster of activities" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectBudgetByCoAs.js", "${baseUrl}/js/global/autoSave.js","${baseUrl}/js/global/fieldsValidation.js"] /]
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

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
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
                  [#assign hasW1w2Budgets = action.hasBudgets(1,year) /]
                  [#assign hasW3Budgets = action.hasBudgets(2,year) /]
                  [#assign hasBilateralBudgets = action.hasBudgets(3,year) /]
                  [#assign hasCenterBudgets = action.hasBudgets(4,year) /]
                  
                  [#-- Budgest cannot be editable message --]
                  [#if !isYearEditable(year)]<div class="note">Percentages for ${year} cannot be editable.</div>[/#if]
                  
                  [#if hasW1w2Budgets || hasW3Budgets || hasBilateralBudgets || hasCenterBudgets]
                  
                    <div class="overallYearBudget fieldset clearfix">
                      <h5 class="title">Remaining ${year} total budget amount</h5>
                      <div class="row">
                        [#-- W1/W2 --]
                        [#if !project.bilateralProject && hasW1w2Budgets ]
                        <div class="col-md-3">
                          <p class="subTitle"><strong>W1/W2</strong> <small> <span class="context-total totalByYear-${type.w1w2}">${(action.getRemaining(1,year))!}%</span> </small></p>
                        </div>
                        [/#if]
                        [#-- W3 --]
                        [#if hasW3Budgets ]
                        <div class="col-md-3">
                          <p class="subTitle"><strong>W3</strong> <small> <span class="context-total totalByYear-${type.w3}">${(action.getRemaining(2,year))!}%</span> </small></p>
                        </div>
                        [/#if]
                        [#-- Bilateral  --]
                        [#if hasBilateralBudgets ]
                        <div class="col-md-3">
                          <p class="subTitle"><strong>Bilateral</strong> <small> <span class="context-total totalByYear-${type.bilateral}">${(action.getRemaining(3,year))!}%</span> </small></p>
                        </div>
                        [/#if]
                        [#-- Center Funds --]
                        [#if !project.bilateralProject && hasCenterBudgets ]
                        <div class="col-md-3">
                          <p class="subTitle"><strong>Center Funds</strong> <small> <span class="context-total totalByYear-${type.centerFunds}">${(action.getRemaining(4,year))!}%</span> </small></p>
                        </div>
                        [/#if]
                      </div>
                      
                      [#if  action.hasSpecificities('crp_budget_gender')]
                      <h5 class="title">Remaining ${year} gender amount</h5>
                      <div class="row">
                        [#-- W1/W2 --]
                        [#if !project.bilateralProject && hasW1w2Budgets]
                        <div class="col-md-3">
                          <p class="subTitle"><strong>W1/W2</strong>  <small><span class="context-gender totalByYear-${type.w1w2}">${(action.getRemainingGender(1,year))!}%</span></small></p>
                        </div>
                        [/#if]
                        [#-- W3 --]
                        [#if hasW3Budgets ]
                        <div class="col-md-3">
                          <p class="subTitle"><strong>W3</strong> <small> <span class="context-gender totalByYear-${type.w3}">${(action.getRemainingGender(2,year))!}%</span></small></p>
                        </div>
                        [/#if]
                        [#-- Bilateral  --]
                        [#if hasBilateralBudgets ]
                        <div class="col-md-3">
                          <p class="subTitle"><strong>Bilateral</strong> <small> <span class="context-gender totalByYear-${type.bilateral}">${(action.getRemainingGender(3,year))!}%</span></small></p>
                          </div>
                        [/#if]
                        [#-- Center Funds --]
                        [#if !project.bilateralProject && hasCenterBudgets]
                        <div class="col-md-3">
                          <p class="subTitle"><strong>Center Funds</strong> <small> <span class="context-gender totalByYear-${type.centerFunds}">${(action.getRemainingGender(4,year))!}%</span></small></p>
                        </div>
                        [/#if]
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
            [#-- W1/W2 --]
            [#if !project.bilateralProject && action.hasBudgets(1,selectedYear)]
            <th class="text-center">W1/W2</th>
            [/#if]
            [#-- W3 --]
            [#if action.hasBudgets(2,selectedYear)]
            <th class="text-center">W3</td>
            [/#if]
            [#-- Bilateral  --]
            [#if action.hasBudgets(3,selectedYear)]
            <th class="text-center">Bilateral</th>
            [/#if]
            [#-- Center Funds --]
            [#if !project.bilateralProject && action.hasBudgets(4,selectedYear)]
            <th class="text-center">Center Funds</th>
            [/#if]
          </tr>
        </thead>
        <tbody>
          [#-- Percentage of Amount --]
          <tr>
            <td class="amountType"> % of total</td>
            [#-- W1W2 --]
            [#if !project.bilateralProject && action.hasBudgets(1,selectedYear)]
              <td class="budgetColumn">
                [#assign budgetCounterW1=action.getIndexBudget(element.id,selectedYear,1)/]
                [#assign budgetW1=action.getBudget(element.id,selectedYear,1)/]
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW1}].id" value="${(budgetW1.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW1}].crpClusterOfActivity.id" value="${(element.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW1}].budgetType.id" value="1"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW1}].year" value="${(selectedYear)!}"/>
                [#if editable && isYearEditable(selectedYear)]
                  [@customForm.input name="project.budgetsCluserActvities[${budgetCounterW1}].amount" i18nkey="budget.amount" showTitle=false className="percentageInput context-total  type-${type.w1w2}" required=true  /]
                [#else]
                  <div class="input"><p><span class="percentageInput totalByPartner-${type.w1w2}">${((budgetW1.amount)!0)}%</span></p></div>
                  <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW1}].amount" value="${(budgetW1.amount)!0}"/>
                [/#if]
            </td>
            [/#if]
            [#-- W3 --]
            [#if action.hasBudgets(2,selectedYear)]
            <td class="budgetColumn">
                [#assign budgetCounterW3=action.getIndexBudget(element.id,selectedYear,2)/]
                [#assign budgetW3=action.getBudget(element.id,selectedYear,2)/]
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW3}].id" value="${(budgetW3.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW3}].crpClusterOfActivity.id" value="${(element.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW3}].budgetType.id" value="2"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW3}].year" value="${(selectedYear)!}"/>
                [#if editable && isYearEditable(selectedYear)]
                  [@customForm.input name="project.budgetsCluserActvities[${budgetCounterW3}].amount" i18nkey="budget.amount" showTitle=false className="percentageInput context-total type-${type.w3}" required=true   /]
                [#else]
                  <div class="input"><p><span class="percentageInput totalByPartner-${type.w3}">${((budgetW3.amount)!0)}%</span></p></div>
                  <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW3}].amount" value="${(budgetW3.amount)!0}"/>
                [/#if]
            </td>
            [/#if]
            [#-- Bilateral  --]
            [#if action.hasBudgets(3,selectedYear)]
            <td class="budgetColumn">
                [#assign budgetCounterBilateral=action.getIndexBudget(element.id,selectedYear,3)/]
                [#assign budgetBilateral=action.getBudget(element.id,selectedYear,3)/]
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterBilateral}].id" value="${(budgetBilateral.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterBilateral}].crpClusterOfActivity.id" value="${(element.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterBilateral}].budgetType.id" value="3"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterBilateral}].year" value="${(selectedYear)!}"/>
                [#if editable && isYearEditable(selectedYear)]
                  [@customForm.input name="project.budgetsCluserActvities[${budgetCounterBilateral}].amount" i18nkey="budget.amount" showTitle=false className="percentageInput context-total type-${type.bilateral}" required=true   /]
                [#else]
                  <div class="input"><p><span class="percentageInput totalByPartner-${type.bilateral}">${((budgetBilateral.amount)!0)}%</span></p></div>
                  <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterBilateral}].amount" value="${(budgetBilateral.amount)!0}"/>
                [/#if]
            </td>
            [/#if]
            [#-- Center Funds --]
            [#if !project.bilateralProject && action.hasBudgets(4,selectedYear)]
            <td class="budgetColumn">
                [#assign budgetCounterCenter=action.getIndexBudget(element.id,selectedYear,4)/]
                [#assign budgetCenter=action.getBudget(element.id,selectedYear,4)/]
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterCenter}].id" value="${(budgetCenter.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterCenter}].crpClusterOfActivity.id" value="${(element.id)!}"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterCenter}].budgetType.id" value="4"/>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterCenter}].year" value="${(selectedYear)!}"/>
                [#if editable && isYearEditable(selectedYear)]
                  [@customForm.input name="project.budgetsCluserActvities[${budgetCounterCenter}].amount" i18nkey="budget.amount" showTitle=false className="percentageInput context-total type-${type.centerFunds}" required=true /]
                [#else]
                  <div class="input"><p><span class="percentageInput totalByPartner-${type.centerFunds}">${((budgetCenter.amount)!0)}%</span></p></div>
                  <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterCenter}].amount" value="${(budgetCenter.amount)!0}"/>
                [/#if]
            </td>
            [/#if]
          </tr>
          
          [#-- Percentage of Gender Amount --]
          [#if  action.hasSpecificities('crp_budget_gender')]
          <tr>
            <td class="amountType"> % of gender</td>
            [#-- W1/W2 --]
            [#if !project.bilateralProject && action.hasBudgets(1,selectedYear)]
            <td class="budgetColumn">
              [#if editable && isYearEditable(selectedYear)]
                [@customForm.input name="project.budgetsCluserActvities[${budgetCounterW1}].genderPercentage" i18nkey="budget.amount" showTitle=false className="percentageInput context-gender type-${type.w1w2}" required=true  /]
              [#else]
                <div class="input"><p><span class="percentageInput totalByPartner-${type.w1w2}">${((budgetW1.genderPercentage)!0)}%</span></p></div>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW1}].genderPercentage" value="${(budgetW1.genderPercentage)!0}"/>
              [/#if]
            </td>
            [/#if]
            [#-- W3 --]
            [#if action.hasBudgets(2,selectedYear)]
            <td class="budgetColumn">
              [#if editable && isYearEditable(selectedYear)]
                [@customForm.input name="project.budgetsCluserActvities[${budgetCounterW3}].genderPercentage" i18nkey="budget.amount" showTitle=false className="percentageInput context-gender type-${type.w3}" required=true   /]
              [#else]
                <div class="input"><p><span class="percentageInput totalByPartner-${type.w3}">${((budgetW3.genderPercentage)!0)}%</span></p></div>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterW3}].genderPercentage" value="${(budgetW3.genderPercentage)!0}"/>
              [/#if]
            </td>
            [/#if]
            [#-- Bilateral  --]
            [#if action.hasBudgets(3,selectedYear)]
            <td class="budgetColumn">
              [#if editable && isYearEditable(selectedYear)]
                [@customForm.input name="project.budgetsCluserActvities[${budgetCounterBilateral}].genderPercentage" i18nkey="budget.amount" showTitle=false className="percentageInput context-gender type-${type.bilateral}" required=true   /]
              [#else]
                <div class="input"><p><span class="percentageInput totalByPartner-${type.bilateral}">${((budgetBilateral.genderPercentage)!0)}%</span></p></div>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterBilateral}].genderPercentage" value="${(budgetBilateral.genderPercentage)!0}"/>
              [/#if]
            </td>
            [/#if]
            [#-- Center Funds --]
            [#if !project.bilateralProject && action.hasBudgets(4,selectedYear)]
            <td class="budgetColumn">
              [#if editable && isYearEditable(selectedYear)]
                [@customForm.input name="project.budgetsCluserActvities[${budgetCounterCenter}].genderPercentage" i18nkey="budget.amount" showTitle=false className="percentageInput context-gender type-${type.centerFunds}" required=true /]
              [#else]
                <div class="input"><p><span class="percentageInput totalByPartner-${type.centerFunds}">${((budgetCenter.genderPercentage)!0)}%</span></p></div>
                <input type="hidden" name="project.budgetsCluserActvities[${budgetCounterCenter}].genderPercentage" value="${(budgetCenter.genderPercentage)!0}"/>
              [/#if]
            </td>
            [/#if]
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