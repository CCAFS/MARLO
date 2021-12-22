[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "trumbowyg" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js?20211222A" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20190621"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis.reportSynthesisFinancialSummary" /]
[#assign customLabel= "annualReport2018.${currentStage}" /]
[#assign isSelectedPhaseAR2021 = action.isSelectedPhaseAR2021()!false]

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
          <span id="actualPhase" style="display: none;">${(action.isSelectedPhaseAR2021()!false)?c}</span>
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
            [#-- Narrative --]
            <div class="form-group">
              [#if PMU]
                [#-- Word Document Tag --]
                [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]

                [#if isSelectedPhaseAR2021]    
                  [@customForm.textArea name="reportSynthesis.reportSynthesisCrpFinancialReport.financialStatusNarrative" i18nkey="${customLabel}.financialStatus" help="${customLabel}.financialStatus.help" helpIcon=false required=true editable=editable allowTextEditor=true /]
                [#else]
                  [@customForm.textArea name="${customName}.narrative" i18nkey="${customLabel}.financialStatus" help="${customLabel}.financialStatus.help" helpIcon=false required=true editable=editable allowTextEditor=true /]
                [/#if]
              [#else]
                <div class="textArea">
                  <label for="">[@customForm.text name="${customLabel}.financialStatus" readText=true /]</label>:
                  <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
                </div>
              [/#if]
            </div>

            [#-- Table 13: CRP Financial Report --]
            [#if PMU]
            <div class="form-group margin-panel">
              <br />
              [#-- Word Document Tag --]
              [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
              
              <h4 class="subTitle headTitle">[@s.text name="${customLabel}.table12.title" /]</h4>

              [#if !isSelectedPhaseAR2021]
                [#list (reportSynthesis.reportSynthesisFinancialSummary.budgets)![] as item] 
                  [@financialReport name="${customName}.budgets" element=item element_index=item_index editable=editable && PMU /]
                [/#list]
              [#else]
                [@financialReportAR2021 name="${customName}.budgets" editable=editable && PMU /]
              [/#if]
            </div>
            [/#if]
            
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

[#macro financialReportAR2021 name  editable]
  <div class="blockContent" style="display:block">
      <hr/>
      <table class="financial-report">
        <thead>
            <tr>
              <th></th>
              <th class="text-center">[@s.text name="${customLabel}.table12.2020Forecast" /]</th>
              <th class="text-center">[@s.text name="${customLabel}.table12.2021Budget" /]</th>
              <th class="text-center">[@s.text name="${customLabel}.table12.commentsChanges" /]<span class="red requiredTag">*</span></th>
            </tr>
        </thead>
        <tbody>
          <tr>
            [#-- Personnel  --]
            [#-- Title --]
            <td class="row-title"><b>[@customForm.text name="${customLabel}.table12.personnel" /]:</b></td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.personnel2020Forecast" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.personnel2020Forecast)!0}" className="currencyInputAR2021 element-forecast text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.personnel2020Forecast" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.personnel2020Forecast)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.personnel2020Forecast)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.personnel2021Budget" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.personnel2021Budget)!0}" className="currencyInputAR2021 element-budget text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.personnel2021Budget" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.personnel2021Budget)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.personnel2021Budget)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            [#-- Comments --]
            <td>
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.personnelComments" showTitle=false  required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.personnelComments" value=""/>
                <nobr>${(reportSynthesis.reportSynthesisCrpFinancialReport.personnelComments)!''}</nobr>
              [/#if]
            </td>
          </tr>
          <tr>
            [#-- Consultancy  --]
            [#-- Title --]
            <td class="row-title"><b>[@customForm.text name="${customLabel}.table12.consultancy" /]:</b></td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.consultancy2020Forecast" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.consultancy2020Forecast)!0}" className="currencyInputAR2021 element-forecast text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.consultancy2020Forecast" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.consultancy2020Forecast)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.consultancy2020Forecast)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.consultancy2021Budget" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.consultancy2021Budget)!0}" className="currencyInputAR2021 element-budget text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.consultancy2021Budget" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.consultancy2021Budget)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.consultancy2021Budget)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            [#-- Comments --]
            <td>
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.consultancyComments" showTitle=false  required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.consultancyComments" value=""/>
                <nobr>${(reportSynthesis.reportSynthesisCrpFinancialReport.consultancyComments)!''}</nobr>
              [/#if]
            </td>
          </tr>
          <tr>
            [#-- Travel  --]
            [#-- Title --]
            <td class="row-title"><b>[@customForm.text name="${customLabel}.table12.travel" /]:</b></td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.travel2020Forecast" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.travel2020Forecast)!0}" className="currencyInputAR2021 element-forecast text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.travel2020Forecast" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.travel2020Forecast)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.travel2020Forecast)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.travel2021Budget" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.travel2021Budget)!0}" className="currencyInputAR2021 element-budget text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.travel2021Budget" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.travel2021Budget)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.travel2021Budget)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            [#-- Comments --]
            <td>
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.travelComments" showTitle=false  required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.travelComments" value=""/>
                <nobr>${(reportSynthesis.reportSynthesisCrpFinancialReport.travelComments)!''}</nobr>
              [/#if]
            </td>
          </tr>
          <tr>
            [#-- Operational expenses  --]
            [#-- Title --]
            <td class="row-title"><b>[@customForm.text name="${customLabel}.table12.expenses" /]:</b></td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.operation2020Forecast" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.operation2020Forecast)!0}" className="currencyInputAR2021 element-forecast text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.operation2020Forecast" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.operation2020Forecast)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.operation2020Forecast)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.operation2021Budget" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.operation2021Budget)!0}" className="currencyInputAR2021 element-budget text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.operation2021Budget" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.operation2021Budget)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.operation2021Budget)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            [#-- Comments --]
            <td>
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.operationComments" showTitle=false  required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.operationComments" value=""/>
                <nobr>${(reportSynthesis.reportSynthesisCrpFinancialReport.operationComments)!''}</nobr>
              [/#if]
            </td>
          </tr>
          <tr>
            [#-- Collaborators and partnerships  --]
            [#-- Title --]
            <td class="row-title"><b>[@customForm.text name="${customLabel}.table12.collaborators" /]:</b></td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnerships2020Forecast" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnerships2020Forecast)!0}" className="currencyInputAR2021 element-forecast text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnerships2020Forecast" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnerships2020Forecast)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnerships2020Forecast)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnerships2021Budget" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnerships2021Budget)!0}" className="currencyInputAR2021 element-budget text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnerships2021Budget" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnerships2021Budget)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnerships2021Budget)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            [#-- Comments --]
            <td>
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnershipsComments" showTitle=false  required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnershipsComments" value=""/>
                <nobr>${(reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnershipsComments)!''}</nobr>
              [/#if]
            </td>
          </tr>
          <tr>
            [#-- Capital and equipment  --]
            [#-- Title --]
            <td class="row-title"><b>[@customForm.text name="${customLabel}.table12.capital" /]:</b></td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipment2020Forecast" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipment2020Forecast)!0}" className="currencyInputAR2021 element-forecast text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipment2020Forecast" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipment2020Forecast)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipment2020Forecast)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipment2021Budget" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipment2021Budget)!0}" className="currencyInputAR2021 element-budget text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipment2021Budget" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipment2021Budget)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipment2021Budget)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            [#-- Comments --]
            <td>
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipmentComments" showTitle=false  required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipmentComments" value=""/>
                <nobr>${(reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipmentComments)!''}</nobr>
              [/#if]
            </td>
          </tr>
          <tr>
            [#-- Closeout cost  --]
            [#-- Title --]
            <td class="row-title"><b>[@customForm.text name="${customLabel}.table12.closeout" /]:</b></td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.closeout2020Forecast" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.closeout2020Forecast)!0}" className="currencyInputAR2021 element-forecast text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.closeout2020Forecast" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.closeout2020Forecast)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.closeout2020Forecast)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            <td class="text-center">
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.closeout2021Budget" showTitle=false value="${(reportSynthesis.reportSynthesisCrpFinancialReport.closeout2021Budget)!0}" className="currencyInputAR2021 element-budget text-center " required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.closeout2021Budget" value="${(reportSynthesis.reportSynthesisCrpFinancialReport.closeout2021Budget)!0}"/>
                <nobr>US$ ${((reportSynthesis.reportSynthesisCrpFinancialReport.closeout2021Budget)!'0')?number?string(",##0.00")}</nobr>
              [/#if]
            </td>
            [#-- Comments --]
            <td>
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.closeoutComments" showTitle=false  required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.closeoutComments" value=""/>
                <nobr>${(reportSynthesis.reportSynthesisCrpFinancialReport.closeoutComments)!''}</nobr>
              [/#if]
            </td>
          </tr>
          <tr>
            [#-- CRP total budget  --]
            [#-- Title --]
            <td class="row-title"><b>[@customForm.text name="${customLabel}.table12.totalBudget" /]:</b></td>
            <td class="text-center totalForecast"><b>US$ <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.crpTotal2020Forecast" value=""/><span>${((reportSynthesis.reportSynthesisCrpFinancialReport.crpTotal2020Forecast)!'0')?number?string(",##0.00")}</span></b></td>
            <td class="text-center totalBudget"><b>US$ <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.crpTotal2021Budget" value=""/><span>${((reportSynthesis.reportSynthesisCrpFinancialReport.crpTotal2021Budget)!'0')?number?string(",##0.00")}</span></b></td>
            [#-- Comments --]
            <td>
              [#if editable]
                [@customForm.input name="reportSynthesis.reportSynthesisCrpFinancialReport.crpTotalComments" showTitle=false  required=true /]
              [#else]
                <input type="hidden" name="reportSynthesis.reportSynthesisCrpFinancialReport.crpTotalComments" value=""/>
                <nobr>${(reportSynthesis.reportSynthesisCrpFinancialReport.crpTotalComments)!''}</nobr>
              [/#if]
            </td>
          </tr>
        </tbody>
      </table>
[/#macro]

[#macro financialReport name element element_index editable]
  [#local customName = "${name}[${element_index}]"]
    
  <div id="flagship-${element_index}" class="flagship expandableBlock borderBox">
    <div class="blockTitle opened">
      [#-- Title --] 
      <span>${(element.liaisonInstitution.crpProgram.composedName)!(element.expenditureArea.expenditureArea)!''}</span>
      
      [#-- Hidden Inputs --]
      <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
      <input type="hidden" name="${customName}.liaisonInstitution.id" value="${(element.liaisonInstitution.id)!}" />
      <input type="hidden" name="${customName}.expenditureArea.id" value="${(element.expenditureArea.id)!}" />
    </div>
    <div class="blockContent" style="display:block">
      <hr />
      <table class="financial-report">
            [#assign budgetTypesList=[
              {"id":"1", "name":"W1/W2",        "p": "w1Planned",         "r": "w1Actual"   },
              {"id":"2", "name":"W3/Bilateral", "p": "w3Planned",         "r": "w3Actual"    }
              [#--  {"id":"3", "name":"Bilateral",    "p": "bilateralPlanned",  "r": "bilateralActual" } --]
            ] /]
        
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
      
      [#-- Comments --]
      <br />
      <div class="form-group">      
        [@customForm.textArea name="${customName}.comments" i18nkey="${customLabel}.comments" className="" required=true editable=editable allowTextEditor=true /]
      </div>

    </div>
  </div>
[/#macro]