[#ftl]
[#assign title = "Project Budget By Partners" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "dropzone", "blueimp-file-upload"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectBudgetByPartners.js?20190403",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectBudgetByPartners.css?20230206"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "budgetByPartners" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"P${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"projectBudgetByPartners", "nameSpace":"/projects", "action":""}
] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/relationsPopupMacro.ftl" as popUps /]



[#assign budgetExpenditureIndex = 0 /]

[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
[#if !reportingActive]
<!-- 
<div class="container helpText viewMore-block">
  <div style="display:none" class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10">
      [#if project.projectInfo.projectEditLeader] [@s.text name="projectBudgets.help2" /] [#else] [@s.text name="projectBudgets.help1" /] [/#if]
    </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
-->
<div class="animated flipInX container  viewMore-block containerAlertMargin">
  <div class=" containerAlert alert-leftovers alertColorBackgroundInfo " id="containerAlert">
    <div class="containerLine alertColorInfo"></div>
    <div class="containerIcon">
      <div class="containerIcon alertColorInfo">
        <img src="${baseUrlCdn}/global/images/icon-question.png" />        
      </div>
    </div>
    <div class="containerText col-md-12">
      <p class="alertText">
        [#if project.projectInfo.projectEditLeader] [@s.text name="projectBudgets.help2" /] [#else] [@s.text name="projectBudgets.help1" /] [/#if]
      </p>
    </div>
    <div  class="viewMoreCollapse closed"></div>
  </div>
</div>
[/#if]
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          [#-- Section Title --]
          <h3 class="headTitle">[@s.text name="projectBudgetByPartners.title" /]</h3>
          
          [#if project.projectInfo.startDate?? && project.projectInfo.endDate??]
          
            [#assign startYear = (project.projectInfo.startDate?string.yyyy)?number /]
            [#assign endYear = (project.projectInfo.endDate?string.yyyy)?number /]
            
            [#if currentCycleYear gt endYear]
              [#assign selectedYear = endYear /]
            [#elseif currentCycleYear lt startYear]
              [#assign selectedYear = startYear /]
            [#else]
              [#assign selectedYear = currentCycleYear /]
            [/#if]

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
                  [#if !isYearEditable(year) && editable]<div class="note"> ${year} budgets for cannot be editable.</div>[/#if]
                  <div class="overallYearBudget clearfix">
                    <div class="row fieldset" listname="project.budgets">
                      <table>
                        [#-- Window Type --]
                        <tr>
                          <td></td>
                          [#list budgetTypesList as budgetType]
                            <td class="text-right"><h5 class="subTitle"> ${budgetType.name} <img title="${budgetType.description}" src="${baseUrlCdn}/global/images/icon-help2.png" alt="" /></h5></td>
                          [/#list]
                          <td class="text-right"><h5 class="title">Overall ${year}</h5></td>
                        </tr>
                        [#-- Planning --]
                        <tr>
                          <td class="amountType"><small> Planned Budget </small></td>
                          [#list budgetTypesList as budgetType]
                            <td class="text-right"><small>US$ <span class="totalByYear cycle-planning year-${year} totalByYear-${budgetType.id}">${action.getTotalYear(year,budgetType.id)?number?string(",##0.00")}</span></small></td>
                          [/#list]
                          <td class="text-right"><strong><small>US$ <span class="overallAmount cycle-planning year-${year}">0.00</span></small></strong></td>
                        </tr>
                        [#-- Reporting --]
                        [#if (reportingActive)  && action.hasSpecificities(crpEnableBudgetExecution)]
                          <tr>
                            <td class="amountType"> <small>Actual Expenditure</small> </td>
                            [#list budgetTypesList as budgetType]
                              <td class="text-right"><small>US$ <span class="totalByYear cycle-reporting year-${year} totalByYear-${budgetType.id}">${((action.getTotalProjectBudgetExecution(year, budgetType.id)?number)!0)?string(",##0.00")}</span></small></td>
                            [/#list]
                            <td class="text-right"><strong><small>US$ <span class="overallAmount cycle-reporting year-${year}"> ${((action.getTotalProjectBudgetExecution(year))!0)?string(",##0.00")}</span></small></strong></td>
                          </tr>
                        [/#if]
                      </table>
                    </div>
                  </div>
                  [#if projectPPAPartners?has_content]
                    [#list projectPPAPartners as projectPartner]
                      [#if action.existOnYear(projectPartner.id,year)]
                        [@projectPartnerMacro element=projectPartner name="project.partners[${projectPartner_index}]" index=projectPartner_index selectedYear=year/]
                      [/#if]
                    [/#list]
                  [#else]
                    <div class="simpleBox emptyMessage text-center">[@s.text name="projectBudgetByPartners.beforeEnteringBudgetInformation" /] <a href="[@s.url action="${crpSession}/partners"][@s.param name="projectID" value=projectID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">partners section</a></div>
                  [/#if]
                </div>
              [/#list]  
            </div>
            
            [#if true || !reportingActive]
            [#-- Section Buttons & hidden inputs--]
            [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
            [/#if]
          [#else]
            <div class="simpleBox emptyMessage text-center">
              [@s.text name="projectBudgetByPartners.beforeFillingSections"]
                [@s.param]<a href="[@s.url action="${crpSession}/description"][@s.param name="projectID" value=projectID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">description section </a>[/@s.param]
                [@s.param]<span class="label label-success">save</span>[/@s.param]
              [/@s.text]
            </div>  
          [/#if]
         
        [/@s.form] 
      </div>
    </div>  
</section>
[/#if]

[#-- Budget tab index --]
<span id="budgetIndex" style="display:none">${budgetIndex+1}</span>

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


[#-- Bilateral Co-Funded Project Popup --]
[#include "/WEB-INF/global/macros/fundingSourcesPopup.ftl"]

[#-- W3/bilaterl Fund Template --]
[@projectFundingBudget element={} name="project.budgets" selectedYear=-1 index=-1  isTemplate=true /]


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
        <span class="index ${isPPA?string('ppa','')}">${isPPA?string('Managing Partner','Partner')}</span>
      </div>
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
              <th class="text-center">${budgetType.name}</th>
            [/#list]
            
          </tr>
        </thead>
        <tbody>
          [#-- Budget Amount --]
          <tr>
            <td class="amountType"> Budget:</td>
            [#list budgetTypesList as budgetType]
              [#-- Budget--]
              <td class="budgetColumn">
                <div class="input"><p>US$ <span class="currencyInput cycle-planning totalByPartner-${budgetType.id}">${((action.getTotalAmount(element.institution.id, selectedYear, budgetType.id,1))!0)?number?string(",##0.00")}</span></p></div>
              </td>
            [/#list]
          </tr>
          [#-- Actual Expenditure--]
          [#if (reportingActive)  && action.hasSpecificities(crpEnableBudgetExecution)]
            <tr>
              <td class="amountType"> Actual expenditure:</td>
              [#list budgetTypesList as budgetType]
                <td class="budgetColumn">
                  <div class="budgetExecutionAmount type-${budgetType.id} text-center">
                    [#-- Budget Expenditure --]
                    [#local budgetExecution = (action.getProjectBudgetExecution (element.institution.id, selectedYear, budgetType.id))!{} /]
                    [#local budgetExecutionName = "project.budgetExecutions[${budgetExpenditureIndex}]" /]
                    <input type="hidden" name="${budgetExecutionName}.id" value="${(budgetExecution.id)!}" />
                    <input type="hidden" name="${budgetExecutionName}.institution.id" value="${(element.institution.id)!}" />
                    <input type="hidden" name="${budgetExecutionName}.budgetType.id" value="${(budgetType.id)!}" />
                    <input type="hidden" name="${budgetExecutionName}.phase.id" value="${(actualPhase.id)!}" />
                    <input type="hidden" name="${budgetExecutionName}.year" value="${(selectedYear)!}" />
                    [@customForm.input name="${budgetExecutionName}.actualExpenditure" value="${(budgetExecution.actualExpenditure)!0}" i18nkey="budget.amount" showTitle=false className="currencyInput cycle-reporting year-${selectedYear} type-${budgetType.id}" required=true editable=editable && action.canEditProjectExecution(budgetType.id,project.id,element.institution.id) && isYearEditable(selectedYear) && !(transaction??) /]
                    [#-- Index --]
                    [#assign budgetExpenditureIndex = budgetExpenditureIndex + 1 /]
                  </div>
                </td>
              [/#list]
            </tr>
          [/#if]
          [#-- Gender Budget Percentage --]
          [#if project.projectInfo.projectEditLeader && action.hasSpecificities('crp_budget_gender')]
          <tr>
            <td class="amountType"> Gender %:</td>
            [#list budgetTypesList as budgetType]
              [#-- Budget Type--]
              <td class="budgetColumn">
                <div class="input"><p><span class="percentageLabel cycle-planning type-${budgetType.id}">${((action.getTotalGenderPer(element.institution.id, selectedYear, budgetType.id,1))!0)}%</span></p></div>
                <div class="row percentageAmount cycle-planning type-${budgetType.id} text-center">
                  <small>US$ <span>${((action.getTotalGender(element.institution.id, selectedYear, budgetType.id,1))!0)?number?string(",##0.00")}</span></small>
                </div>
              </td>
            [/#list]
          </tr>
          [/#if]
        </tbody>
      </table>
      [#attempt]
        [#assign projectFundingSources = (action.getBudgetsByPartner(element.institution.id,selectedYear))![] /]
      [#recover]
        [#assign projectFundingSources = [] /]
        ERROR LOADING FUNDING SOURCES
      [/#attempt]
      
      [#assign expandedProjectFundingSource = !(projectFundingSources?size > 0) ]
      [#if projectFundingSources?size > 0]
      <a class="btn btn-default btn-xs pull-right toggleProjectFundingSource"> 
        <span class="project-fs-expandible-true" style="display:${expandedProjectFundingSource?string('none', 'block')}"><span class="glyphicon glyphicon-resize-full"></span> Expand</span>
        <span class="project-fs-expandible-false" style="display:${expandedProjectFundingSource?string('block', 'none')}"><span class="glyphicon glyphicon-resize-small"></span> Collapse all</span>
      </a>
      [/#if]
      <h5 class="sectionSubTitle">Funding Sources: </h5>
      <div class="projectW3bilateralFund-block">
        [#-- Funding sources --]
        <div class="projectW3bilateralFund-list simpleBox project-fs-expandible-true" style="display:${expandedProjectFundingSource?string('block', 'none')}">
          [#list projectFundingSources as budget ] 
            [#local indexBudgetfundingSource=(action.getIndexBudget(element.institution.id,selectedYear,budget.fundingSource.fundingSourceInfo.budgetType.id,budget.fundingSource.id))!"" ]
            [@projectFundingBudget element=budget name="project.budgets" selectedYear=selectedYear  index=indexBudgetfundingSource /]
          [#else]
            [#if editable && isYearEditable(selectedYear) && action.canSearchFunding(element.institution.id)]
              <p class="emptyMessage text-center">[@s.text name="projectBudgetByPartners.assginFundingSourceClicking" /] "[@s.text name="form.buttons.selectProject" /]".</p>
            [#else]
              <p class="emptyMessage text-center">[@s.text name="projectBudgetByPartners.noFundingSourcesAdded" /].</p>
            [/#if]
          [/#list]
        </div>
        <div class="simpleBox project-fs-expandible-false" style="display:${expandedProjectFundingSource?string('none', 'block')}">
          <p class="emptyMessage text-center"> <span class="fsCounter">${projectFundingSources?size}</span> Funding source(s), please click in the "Expand" button above to show them.</p>
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



[#macro fundingSourceRowMacro element name selectedYear index=-1  isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  <tr id="projectW3bilateralFund-${isTemplate?string('template', index )}" class="projectW3bilateralFund  " style="display:${isTemplate?string('none','table-row')}">
    
    [#-- Title --]
    <td>
      [#-- Relations --]
      [#if !isTemplate]
        <div class="pull-right">[@popUps.relationsMacro element=element labelText=false /] </div>
      [/#if]
    
      [#-- Funding Source Title --]
      <p class="">
        [#-- Finance Code --]
        [#if (element.fundingSource.fundingSourceInfo.financeCode?has_content)!false]
          [#assign isSynced = (element.fundingSource.fundingSourceInfo.synced)!false ]
          [#-- Code --]
          <span [#if isSynced]title="Synced on ${(element.fundingSource.fundingSourceInfo.syncedDate)!''}"  style="color: #2aa4c9;"[/#if]>${element.fundingSource.fundingSourceInfo.financeCode}</span> |
        [/#if]
        
        [#local fsURL][@s.url namespace="/fundingSources" action="${crpSession}/fundingSource"][@s.param name="fundingSourceID" value="${(element.fundingSource.id)!}" /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [#if !isTemplate]<a href="${fsURL}" class="" target="_BLANK">[/#if]
          <span class="titleId">FS${(element.fundingSource.id)!}</span>  - <span class="title">${(element.fundingSource.fundingSourceInfo.title)!}</span>
        [#if !isTemplate]</a>[/#if]
      </p>
      
      [#-- Funding Source remaining budget --]
      [#assign fsRemaining = ((element.fundingSource.getRemaining(selectedYear,action.getActualPhase()))!0)?number /]
      [#if (isYearEditable(selectedYear)) || isTemplate]
        <small class="grayLabel [#if fsRemaining lt 0]fieldError[/#if]"> (Remaining budget US$ <span class="projectAmount">${fsRemaining?string(",##0.00")}</span>) </small>
      [/#if]
      
      <input type="hidden" class="id " name="${customName}.id" value="${(element.id)!}"/>
      <input type="hidden" class="institutionId" name="${customName}.institution.id" value="${(element.institution.id)!}"/>
      <input type="hidden" class="selectedYear" name="${customName}.year" value="${(selectedYear)!}"/>
      <input type="hidden" class="projectId institution-${(element.institution.id)!} year-${(selectedYear)!}" name="${customName}.fundingSource.id" value="${(element.fundingSource.id)!}"/>
      <input type="hidden"  name="${customName}.phase.id" value="${(element.phase.id)!}"/>
    </td>
    
    [#-- Funding Type --]
    <td class="col-md-2 text-center">
      <span class="budgetTypeName">${(element.fundingSource.fundingSourceInfo.budgetType.name)!} 
        [#if action.hasSpecificities('crp_fs_w1w2_cofinancing')] <br />${(element.fundingSource.fundingSourceInfo.w1w2?string('<small class="text-primary">(Co-Financing)</small>',''))!} [/#if]
      </span> 
      <input type="hidden" class="budgetTypeId" name="${customName}.budgetType.id" value="${(element.fundingSource.fundingSourceInfo.budgetType.id)!}" />
    </td>
    
    [#-- Amount --]
    <td class="col-md-2">
      [#-- TODO: Allow to add funding sources when there is no aggregate (problem with permissions)  --]
      [#-- Added action.canSearchFunding to allow to modify gender depending on institution  --]
      [#if (editable && isYearEditable(selectedYear) && (action.canEditFunding(((element.fundingSource.fundingSourceInfo.budgetType.id)!-1),(element.institution.id)!-1) ))|| isTemplate]
        [@customForm.input name="${customName}.amount"    i18nkey="budget.amount" showTitle=false className="currencyInput cycle-planning fundInput type-${(element.fundingSource.fundingSourceInfo.budgetType.id)!'none'}" required=true /]
      [#else]
        <div class="${customForm.changedField(customName+'.amount')}">
          <div class="input"><p>US$ <span>${((element.amount)!0)?number?string(",##0.00")}</span></p></div>
          <input type="hidden" name="${customName}.amount"  value="${(element.amount)!0}" />
        </div>
      [/#if]
    </td>
    
    [#-- Gender Percentage --]
    [#if ((project.projectInfo.projectEditLeader)!false) && action.hasSpecificities('crp_budget_gender')]
    <td class="col-md-1">
      [#-- TODO: Allow to add funding sources when there is no aggregate (problem with permissions)  --]
      [#-- Added action.canSearchFunding to allow to modify gender depending on institution  --]
      [#if (editable && isYearEditable(selectedYear) && action.canSearchFunding(element.institution.id) && action.canEditGender()) || isTemplate]
        [@customForm.input name="${customName}.genderPercentage" i18nkey="budget.genderPercentage" showTitle=false className="percentageInput cycle-planning type-${(element.fundingSource.fundingSourceInfo.budgetType.id)!'none'}" required=true   /]
      [#else]  
        <div class="${customForm.changedField(customName+'.genderPercentage')}">
          <div class="input"><p><span>${((element.genderPercentage)!0)}%</span></p></div>
          <input type="hidden" name="${customName}.genderPercentage"  value="${(element.genderPercentage)!0}" />
        </div>
      [/#if]
    </td>
    [/#if]
    
    [#-- Remove --]
    <td>
      [#if (editable && isYearEditable(selectedYear) && action.canEditFunding(((element.fundingSource.fundingSourceInfo.budgetType.id)!-1),(element.institution.id)!-1) ) || isTemplate]
        [#if action.canBeDeleted((element.id)!-1,(element.class.name)!"")]
          <div class="removeW3bilateralFund" title="Remove"> <img src="${baseUrlCdn}/global/images/icon-remove.png" alt="" /> </div>
        [#else]
          <div class="disable text-right" title="Project Budget cannot be deleted"></div>
        [/#if]
      [/#if]
    </td>
      
  </tr>
[/#macro]

[#macro projectFundingBudget element name selectedYear index=-1  isTemplate=false]
  <div id="projectW3bilateralFund-${isTemplate?string('template', index )}" class="projectW3bilateralFund expandableBlock grayBox" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]" /]
    [#-- Remove --]
    [#if (editable && isYearEditable(selectedYear) && action.canEditFunding(((element.fundingSource.fundingSourceInfo.budgetType.id)!-1),(element.institution.id)!-1) ) || isTemplate]
      [#if action.canBeDeleted((element.id)!-1,(element.class.name)!"")]
        <div class="removeIcon removeW3bilateralFund" title="Remove"></div>
      [#else]
        <div class="removeIcon disable text-right" title="Project Budget cannot be deleted"></div>
      [/#if]  
      [#if !isTemplate]
      <div class="pull-right">[@popUps.relationsMacro element=element /] </div>
      [/#if]
    [/#if]
    
    [#-- End Date --]
    [#local fsEndDate]${(element.fundingSource.fundingSourceInfo.endDate)!}[/#local]
    [#if (element.fundingSource.fundingSourceInfo.status == 4)!true]
      [#local fsEndDate][#if (element.fundingSource.fundingSourceInfo.extensionDate??)!false]${(element.fundingSource.fundingSourceInfo.extensionDate)!}[#else]${(element.fundingSource.fundingSourceInfo.endDate)!}[/#if][/#local]
    [/#if]
    
    <p class="checked">
      [#assign fsRemaining = ((element.fundingSource.getRemaining(selectedYear,action.getActualPhase()))!0)?number /]
      [#-- Finance Code --]
      [#if (element.fundingSource.fundingSourceInfo.financeCode?has_content)!false]
        [#assign isSynced = (element.fundingSource.fundingSourceInfo.synced)!false ]
        [#-- Code --]
        <span [#if isSynced]title="Synced on ${(element.fundingSource.fundingSourceInfo.syncedDate)!''}" style="color: #2aa4c9;"[/#if]>${element.fundingSource.fundingSourceInfo.financeCode}</span> |
      [/#if]
      [#-- Funding Source Title --]
      <small>Funding source #<span class="titleId">${(element.fundingSource.id)!}</span>  </small>
      [#-- Funding Source remaining budget --]
      [#if (isYearEditable(selectedYear)) || isTemplate]
        - <small class="grayLabel [#if fsRemaining lt 0]fieldError[/#if]"> (Remaining budget US$ <span class="projectAmount">${fsRemaining?string(",##0.00")}</span>) </small>
      [/#if]
    </p> 
    
    [#if !isTemplate]
    <a href="[@s.url namespace="/fundingSources" action="${crpSession}/fundingSource"][@s.param name="fundingSourceID" value="${(element.fundingSource.id)!}" /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" class="" target="_BLANK"> 
    [/#if]
      <p class=""> <span class="title">${(element.fundingSource.fundingSourceInfo.title)!}</span> </p>
    [#if !isTemplate]
    </a>
    [/#if]

    <input type="hidden" class="id " name="${customName}.id" value="${(element.id)!}"/>
    <input type="hidden" class="institutionId" name="${customName}.institution.id" value="${(element.institution.id)!}"/>
    <input type="hidden" class="selectedYear" name="${customName}.year" value="${(selectedYear)!}"/>
    <input type="hidden" class="projectId institution-${(element.institution.id)!} year-${(selectedYear)!}" name="${customName}.fundingSource.id" value="${(element.fundingSource.id)!}"/>
    <input type="hidden"  name="${customName}.phase.id" value="${(element.phase.id)!}"/>
    
    [#-- Project Fund --]
    <div class="row form-group w3bilateralFund">
      <div class="col-md-4">
        <div class="row col-md-6"> <strong>Type:</strong> </div>
        <div class="row col-md-8">
          <span class="budgetTypeName">${(element.fundingSource.fundingSourceInfo.budgetType.name)!} 
            [#if action.hasSpecificities('crp_fs_w1w2_cofinancing')] ${(element.fundingSource.fundingSourceInfo.w1w2?string('<small class="text-primary">(Co-Financing)</small>',''))!} [/#if]
          </span> 
          <input type="hidden" class="budgetTypeId" name="${customName}.budgetType.id" value="${(element.fundingSource.fundingSourceInfo.budgetType.id)!}" />
        </div>
      </div>
      <div class="col-md-4">
        <div class="row col-md-5">
          <div class="row"><strong>Amount:</strong></div>
        </div>
        <div class="row col-md-9">
          [#-- TODO: Allow to add funding sources when there is no aggregate (problem with permissions)  --]
          [#-- Added action.canSearchFunding to allow to modify gender depending on institution  --]
          [#if (editable && isYearEditable(selectedYear) && (action.canEditFunding(((element.fundingSource.fundingSourceInfo.budgetType.id)!-1),(element.institution.id)!-1) ))|| isTemplate]
            [@customForm.input name="${customName}.amount"    i18nkey="budget.amount" showTitle=false className="currencyInput cycle-planning fundInput type-${(element.fundingSource.fundingSourceInfo.budgetType.id)!'none'}" required=true /]
          [#else]
            <div class="${customForm.changedField(customName + '.amount')}">
              <div class="input"><p>US$ <span>${((element.amount)!0)?number?string(",##0.00")}</span></p></div>
              <input type="hidden" name="${customName}.amount"  value="${(element.amount)!0}" />
            </div>
          [/#if]
        </div>
      </div>
      <div class="col-md-4">
        [#if ((project.projectInfo.projectEditLeader)!false) && action.hasSpecificities('crp_budget_gender')]
          <div class="row col-md-6"> <div class="row"><strong>Gender %:</strong></div> </div>
          <div class="row col-md-7">
            [#-- TODO: Allow to add funding sources when there is no aggregate (problem with permissions)  --]
            [#-- Added action.canSearchFunding to allow to modify gender depending on institution  --]
            [#if (editable && isYearEditable(selectedYear) && action.canSearchFunding(element.institution.id) && action.canEditGender()) || isTemplate]
              [@customForm.input name="${customName}.genderPercentage" i18nkey="budget.genderPercentage" showTitle=false className="percentageInput cycle-planning type-${(element.fundingSource.fundingSourceInfo.budgetType.id)!'none'}" required=true   /]
            [#else]  
              <div class="${customForm.changedField(customName+'.genderPercentage')}">
                <div class="input"><p><span>${((element.genderPercentage)!0)}%</span></p></div>
                <input type="hidden" name="${customName}.genderPercentage"  value="${(element.genderPercentage)!0}" />
              </div>
            [/#if]
          </div>
        [/#if]
      </div>
    </div>
    
    <div class="form-group">
      [@customForm.textArea name="${customName}.rationale"  value="${(element.rationale)!}" i18nkey="mapFunding.justification" help="mapFunding.justification.help" helpIcon=true className="" required=true editable=editable && (isYearEditable(selectedYear) || isTemplate) /]
    </div>
    
    <div class="">
      <small class="pull-right">
        [#if fsEndDate?has_content]
          [#local fsYear = fsEndDate?date?string('yyyy')?number ]
          [#local validDate = (fsYear >= actualPhase.year)!false ]
          <nobr><p class="${(!validDate)?string('fieldError', '')}">End date: ${fsEndDate}</p></nobr>
        [#else]
          <p style="opacity:0.5">Not defined</p>
        [/#if]
      </small>
      <div class="clearfix"></div>
    </div>
  </div>
[/#macro]


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