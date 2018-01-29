[#ftl]
[#assign title = "Project Budget By Flagships" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectBudgetByFlagships.js", 
  "${baseUrl}/global/js/autoSave.js",
  "${baseUrl}/global/js/fieldsValidation.js"
  ]
/]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectBudgetByFlagships.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "budgetByFlagships" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectBudgetByFlagships", "nameSpace":"/projects", "action":""}
] /]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectBudgetByFlagships.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

[#assign budgetCounter = 0 /]
[#assign type = { 
  'w1w2': 'w1w2',
  'w3': '2',
  'bilateral': '3',
  'centerFunds': 'centerFunds'
} /]

<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Budget By Flagships Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          [#-- Section Title --]
          <h3 class="headTitle">[@s.text name="projectBudgetByFlagships.title" /]</h3>
          
          [#if project.projectInfo.startDate?? && project.projectInfo.endDate??]
          
            [#assign startYear = (project.projectInfo.startDate?string.yyyy)?number /]
            [#assign endYear = (project.projectInfo.endDate?string.yyyy)?number /]
            [#if currentCycleYear gt endYear][#assign selectedYear = endYear /][#else][#assign selectedYear = currentCycleYear /][/#if]
          
            [#-- Year Tabs --]
            <ul class="nav nav-tabs budget-tabs" role="tablist">
              [#list startYear .. selectedYear as year]
                <li class="[#if year == selectedYear]active[/#if]"><a href="#year-${year}" role="tab" data-toggle="tab">${year} [@customForm.req required=isYearRequired(year) && editable && !reportingActive /] </a></li>
              [/#list]
            </ul>
            
            [#-- Years Content --]
            <div class="tab-content budget-content">
              [#list startYear .. selectedYear as year]
                <div role="tabpanel" class="tab-pane [#if year == selectedYear]active[/#if]" id="year-${year}">
                  [#-- Budgest cannot be editable message --]
                  [#if !isYearEditable(year)]<div class="note">Percentages for ${year} cannot be editable.</div>[/#if]
                  
                [#if action.hasBudgets(1,year) || action.hasBudgets(2,year) || action.hasBudgets(3,year) || action.hasBudgets(4,year) || action.hasBudgets(5,year)]
                  [#if project.flagships?has_content]
                    [#list project.flagships as budgetFlagship]
                      [@BudgetByFlagshipsMacro element=budgetFlagship name="project.flagships" index=flagships_index selectedYear=year/]
                    [/#list]
                    
                  [#else]
                    <div class="simpleBox emptyMessage text-center"> [@s.text name="projectBudgetByFlagships.beforeEnteringBudgetInformation" /] <a href="[@s.url action="${crpSession}/description"][@s.param name="projectID" value=projectID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">description section</a></div>
                  [/#if]
                [#else]
                  <p class="emptyMessage text-center">[@s.text name="projectBudgetByFlagships.notBudgetSaved" /]</p>
                [/#if]
                </div>
              [/#list]  
            </div>
            [#-- Section Buttons & hidden inputs--]
            [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
            <div class="clearfix"></div>
          [#else]
            <div class="simpleBox emptyMessage text-center">
              [@s.text name="projectBudgetByFlagships.beforeFillingSections"]
                [@s.param]<a href="[@s.url action="${crpSession}/description"][@s.param name="projectID" value=projectID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">description section </a>[/@s.param]
                [@s.param]<span class="label label-success">save</span>[/@s.param]
              [/@s.text]
            </div>  
          [/#if]
          
        [/@s.form] 
      </div>
    </div>  
</section>

[#-- Budget types JSON --]
<span id="budgetTypeJson" style="display:none">

[
[#list budgetTypesList as budgetType]
 {
   "id": ${budgetType.id},
   "name": "${budgetType.name}",
   "description" : "${budgetType.description}"
 }[#if budgetType_has_next],[/#if]
[/#list]
]
</span>

[#include "/WEB-INF/crp/pages/footer.ftl"]

[#macro BudgetByFlagshipsMacro element name index=-1 selectedYear=0 isTemplate=false]
  <div id="projectFlagship-${isTemplate?string('template',(element.id)!)}" class="projectFlagship expandableBlock borderBox ${(isLeader?string('leader',''))!} ${(isCoordinator?string('coordinator',''))!}" style="display:${isTemplate?string('none','block')}">
    [#-- Partner Title --]
    <div class="blockTitle opened">
      [#-- Title --] 
      <span class="partnerTitle">${(element.composedName)!''}</span>
      <div class="clearfix"></div>
    </div>
    
    <div class="blockContent" style="display:block">
      <hr />
      <table class="table" >
        <thead>
          <tr>
            <th class="amountType"> </th>
            [#list budgetTypesList as budgetType]
              [#-- Budget Type--]
              [#if action.hasBudgets(budgetType.id, selectedYear)]
              <th class="text-center">${budgetType.name}[@customForm.req /]</th>
              [/#if]
            [/#list]
          </tr>
        </thead>
        <tbody>
          [#-- Budget Amount --]
          <tr>
            <td class="amountType"> % of total:</td>
            [#list budgetTypesList as budgetType]
              [#-- Budget Type--]
              [#if action.hasBudgets(budgetType.id, selectedYear)]
                <td class="budgetColumn">
                  [#assign budgetIndex= action.getIndexBudget(element.id,selectedYear, budgetType.id) /]
                  [#assign budgetObject= action.getBudget(element.id,selectedYear, budgetType.id) /]
                  [#assign customName = "project.budgetsFlagship[${budgetIndex}]" /]
                  <input type="hidden" name="${customName}.id" value="${(budgetObject.id)!}"/>
                  <input type="hidden" name="${customName}.crpProgram.id" value="${(element.id)!}"/>
                  <input type="hidden" name="${customName}.budgetType.id" value="${budgetType.id}"/>
                  <input type="hidden" name="${customName}.year" value="${(selectedYear)!}"/>
                  [#assign budgetTypePermission= BudgetTypePermission(budgetType.name) /]
                  [#if editable && isYearEditable(selectedYear) && action.hasPermission(budgetTypePermission)]
                    [@customForm.input name="${customName}.amount" value="${((budgetObject.amount)!0)}" i18nkey="budget.amount" showTitle=false className="percentageInput context-total  type-${budgetType.id}" required=true /]
                  [#else]
                    <div class="input"><p><span class="percentageInput totalByPartner-${budgetType.id}">${((budgetObject.amount)!0)}%</span></p></div>
                    <input type="hidden" name="${customName}.amount" value="${(budgetObject.amount)!0}"/>
                  [/#if]
                </td>
              [/#if]
            [/#list]
          </tr>
        </tbody>
      </table>
    </div>
  </div>
[/#macro]

[#-- Change BudgetType.name to budgetTypePermission format--]
[#function BudgetTypePermission budgetTypeName]
  [#switch budgetTypeName]
    [#case "W1/W2"]
      [#return 'w1w2' /]
      [#break]
    [#case "W3"]
      [#return 'w3' /]
      [#break]
    [#case "Bilateral"]
      [#return 'bilateral' /]
      [#break]
    [#case "Center Funds"]
      [#return 'center' /]
      [#break]
  [/#switch]
[/#function]

[#-- Get if the year is required--]
[#function isYearRequired year]
  [#if (project.projectInfo.endDate??)!false]
    [#assign endDate = (project.projectInfo.endDate?string.yyyy)?number]
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
  [#if (project.projectInfo.endDate??)!false]
    [#assign endDate = (project.projectInfo.endDate?string.yyyy)?number]
    [#if reportingActive]
      [#return  (year gte currentCycleYear) ]
    [#else]
      [#return  (year gte currentCycleYear) ]
    [/#if]
  [#else]
    [#return false]
  [/#if]
[/#function]