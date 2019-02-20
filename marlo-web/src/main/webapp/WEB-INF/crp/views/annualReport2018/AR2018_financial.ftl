[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis" /]
[#assign customLabel= "annualReport2018.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="${customLabel}.help" /]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
  [#else]
    [#-- Program (Flagships and PMU) --]
    [#include "/WEB-INF/crp/views/annualReport2018/submenu-AR2018.ftl" /]
    
    <div class="row">
      [#-- POWB Menu --]
      <div class="col-md-3">[#include "/WEB-INF/crp/views/annualReport2018/menu-AR2018.ftl" /]</div>
      [#-- POWB Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/annualReport2018/messages-AR2018.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
            [#-- Narrative --]
            <div class="form-group">
              [#if PMU]
                [@customForm.textArea name="${customName}.financialStatus" i18nkey="${customLabel}.financialStatus" help="${customLabel}.financialStatus.help" helpIcon=false required=true editable=editable /]
              [#else]
                <div class="textArea">
                  <label for="">[@customForm.text name="${customLabel}.financialStatus" readText=true /]</label>:
                  <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
                </div>
              [/#if]
            </div>
            
            
            [#assign financialList = [
                { 
                  "a": "FP1",
                  "b": "expenditureArea 1",
                  "status": "Status"
                },
                { 
                  "a": "FP2",
                  "b": "expenditureArea 2",
                  "status": "Status 1"
                }
              ] /]
            
            [#-- Table 12: CRP Financial Report --]
            <div class="form-group margin-panel">
              <div class="evidence-plannedStudies-header">
                <h4 class="subTitle headTitle">[@s.text name="${customLabel}.table12.title" /]</h4>
              </div>
              <hr />
              [#-- [#list (reportSynthesis.reportSynthesisFinancialSummary.budgets)![] as item] --]
              [#list financialList as item]
                [@financialReport name="${customName}.budgets" element=item element_index=item_index editable=editable && PMU /]
              [/#list]
            </div>
            
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport2018/buttons-AR2018.ftl" /]
        [/@s.form] 
      </div> 
    </div>
  [/#if] 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]


[#---------------------------------------------------- MACROS ----------------------------------------------------]


[#macro financialReport name element element_index editable]
  
  [#local customName = "${name}[${element_index}]"]
  [#assign budgetTypesList=[
    {"id":"1", "name":"W1/W2",      "p": "w1Planned",         "r": "w1Actual"   },
    {"id":"2", "name":"W3",         "p": "w3Planned",         "r": "w3Actual"    },
    {"id":"3", "name":"Bilateral",  "p": "bilateralPlanned",  "r": "bilateralActual" }
  ] /]
  
  <div id="flagship-${element_index}" class="flagship expandableBlock borderBox">
    <div class="blockTitle opened">
      [#-- Title --] 
      <span>${(element.liaisonInstitution)!(element.expenditureArea)!''}</span>
      
      [#-- Hidden Inputs --]
      <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
      <input type="hidden" name="${customName}.liaisonInstitution.id" value="${(element.liaisonInstitution.id)!}" />
      <input type="hidden" name="${customName}.expenditureArea.id" value="${(element.expenditureArea.id)!}" />
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
            <th class="amountType text-center" width="20%"><u>[@s.text name="${customLabel}.table12.total" /]</u></th>
          </tr>
        </thead>
        <tbody>
          [#-- Planned Budget  --]
          <tr>
            [#-- Title --]
            <td class="row-title"><b> [@customForm.text name="${customLabel}.table12.budget" param="${actualPhase.year}" /]: </b></td>
            [#-- Amount --]
            [#list budgetTypesList as budgetType]
              <td class="text-center">
                [#if editable]
                  [@customForm.input name="${customName}.${budgetType.p}" showTitle=false value="${(element[budgetType.p])!0}" className="currencyInput text-center type-${budgetType.id} element-${element_index} category-planned" required=true /]
                [#else]
                  <input type="hidden" name="${customName}.amount" value="${(element[budgetType.p])!0}"/>
                  <nobr>US$ ${((element[budgetType.p])!'0')?number?string(",##0.00")}</nobr>
                [/#if]
              </td>
            [/#list]
            [#-- Total --]
            <td class="text-center">
              <nobr class="totalCategory element-${element_index} category-planned">US$ <span>${((budgetObject.total)!'0')?number?string(",##0.00")}</span></nobr>
            </td>
          </tr>
          [#-- Actual expenditure --]
          <tr>
            <td class="row-title"><b> [@s.text name="${customLabel}.table12.expenditure" /]: </b></td>
            [#list budgetTypesList as budgetType]
              <td class="text-center">
                [#if editable]
                  [@customForm.input name="${customName}.${budgetType.r}" showTitle=false value="${(element[budgetType.r])!0}" className="currencyInput text-center type-${budgetType.id} element-${element_index} category-actualExpenditure" required=true  /]
                [#else]
                  <input type="hidden" name="${customName}.amount" value="${(element[budgetType.r])!0}"/>
                  <nobr>US$ ${((element[budgetType.r])!'0')?number?string(",##0.00")}</nobr>
                [/#if]
              </td>
            [/#list]
            <td class="text-center">
              <nobr class="totalCategory element-${element_index} category-actualExpenditure">US$ <span>0.00</span></nobr>
            </td>
          </tr>
          [#-- Difference --]
          <tr>
            <td class="row-title"><b> [@s.text name="${customLabel}.table12.difference" /]: </b></td>
            [#list budgetTypesList as budgetType]
              <td class="text-center">
                <nobr class="totalDiff element-${element_index} type-${budgetType.id} category-difference">US$ <span>${((budgetObject.difference)!'0')?number?string(",##0.00")}</span></nobr>
              </td>
            [/#list]
            <td class="text-center">
              <nobr class="totalCategory element-${element_index} category-difference"> <strong> US$ <span>${((budgetObject.total)!'0')?number?string(",##0.00")}</span> </strong></nobr>
            </td>
          </tr>
        </tbody>
      </table>

    </div>
  </div>
[/#macro]