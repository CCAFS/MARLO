[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"annualReport", "nameSpace":"annualReport", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}", "nameSpace":"annualReport", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="annualReport.${currentStage}.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/annualReport/submenu-annualReport.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/annualReport/menu-annualReport.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/annualReport/messages-annualReport.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
                  
        [#assign customName= "annualReport.${currentStage}" /]
        [#assign customLabel= "annualReport.${currentStage}" /]
        
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
        <div class="borderBox">
        
          [#-- Please give a narrative summary on the financial status and health of the CRP --]
          <div class="form-group margin-panel">
            [@customForm.textArea name="${customName}.summary" i18nkey="${customLabel}.summary" help="${customLabel}.summary.help" className="" helpIcon=false required=true editable=editable && PMU /]
          </div>
          
          [#-- Table J: CRP Financial Report --]
          <div class="form-group margin-panel">
            <div class="evidence-plannedStudies-header">
              <h4 class="subTitle headTitle">[@s.text name="${customLabel}.tableJ.title" /]</h4>
            </div>
            <hr />
            
           [#-- REMOVE TEMPORAL LIST ASSIGN --]
           [#assign list=[
              {"composedName":"F1: Priorities and Policies for CSA"},
              {"composedName":"F2: Climate-Smart technologies and Practices"},
              {"composedName":"F3: Priorities and Policies for CSA"},
              {"composedName":"F4: Climate-Smart technologies and Practices"}
            ] /]
            
            [#list list as item]
              [@tableJMacro element=item editable=editable && PMU /]
            [/#list]
          </div>
        
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#if PMU]
          [#include "/WEB-INF/crp/views/annualReport/buttons-annualReport.ftl" /]
        [/#if]
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]


[#macro tableJMacro element editable]
 [#-- REMOVE TEMPORAL LISTS ASSIGN --]
 [#assign budgetTypesList=[{"name":"W1/W2"},{"name":"W3"},{"name":"Bilateral"}] /]
  
  <div id="flagship-${element.id!''}" class="flagship expandableBlock borderBox">
    <div class="blockTitle opened">
      [#-- Title --] 
      <span>${(element.composedName)!''}</span> 
      <div class="clearfix"></div>
    </div>
    
    <div class="blockContent" style="display:block">
      <hr />
      <table class="financial-report">
        <thead>
          <tr>
            <th></th>
            
            [#-- Budget Types--]
            [#list budgetTypesList as budgetType]
              <th class="text-center">${budgetType.name}</th>
            [/#list]
            [#-- Total --]
            <th class="amountType text-center" width="20%"><u>[@s.text name="${customLabel}.tableJ.total" /]</u></th>
          </tr>
        </thead>
        <tbody>
          [#-- Planned Budget row --]
          <tr>
            <td class="row-title"><b> [@s.text name="${customLabel}.tableJ.budget" /]: </b></td>
            [#list budgetTypesList as budgetType]
              <td class="text-center">
                [#if editable]
                  [@customForm.input name="${customName}.amount" showTitle=false value="${(budgetObject.amount)!0}" className="currencyInput text-center" required=true /]
                [#else]
                  <div class="input">US$ ${((budgetObject.amount)!0)}</div>
                  <input type="hidden" name="${customName}.amount" value="${(budgetObject.amount)!0}"/>
                [/#if]
              </td>
            [/#list]
            <td class="text-center">
              <div class="input">US$ ${((budgetObject.total)!0)}</div>
            </td>
          </tr>
          
          [#-- Percentage of Gender Amount --]
          <tr>
            <td class="row-title"><b> [@s.text name="${customLabel}.tableJ.expenditure" /]: </b></td>
            [#list budgetTypesList as budgetType]
              <td class="text-center">
                [#if editable]
                  [@customForm.input name="${customName}.amount" i18nkey="budget.amount" showTitle=false value="${(budgetObject.amount)!0}" className="currencyInput text-center" required=true  /]
                [#else]
                  <div class="input">US$ ${((budgetObject.amount)!0)}</div>
                  <input type="hidden" name="${customName}.amount" value="${(budgetObject.amount)!0}"/>
                [/#if]
              </td>
            [/#list]
            <td class="text-center">
              <div class="input">US$ ${((budgetObject.total)!0)}</div>
            </td>
          </tr>
          [#-- Difference --]
          <tr>
            <td class="row-title"><b> [@s.text name="${customLabel}.tableJ.difference" /]: </b></td>
            [#list budgetTypesList as budgetType]
              <td class="text-center">
                <div class="input">US$ ${((budgetType.difference)!0)}</div>
                <input type="hidden" name="${customName}.amount" value="${(budgetType.difference)!0}"/>
              </td>
            [/#list]
            <td class="text-center">
              <div class="input">US$ ${((budgetObject.total)!0)}</div>
            </td>

        </tbody>
      </table>

    </div>
  </div>
[/#macro]