[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "blueimp-file-upload" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/powb/powb_financialPlan.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/powb/powbGlobal.css" ] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "financialPlan" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"financialPlan", "nameSpace":"powb", "action":"${crpSession}/financialPlan"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/views/powb/macros-powb.ftl" as powbMacros /]

[#-- Helptext --]
[@utilities.helpBox name="financialPlan.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/powb/submenu-powb.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/powb/menu-powb.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/powb/messages-powb.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="financialPlan.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h3>
        <div class="borderBox">
          
          [#-- Briefly highlight any important issues regarding the financial plan and highlight  --] 
          <div class="form-group">
          <input type="hidden" name="powbSynthesis.financialPlan.id" value="${(powbSynthesis.financialPlan.id)!}" />
            [@customForm.textArea  name="powbSynthesis.financialPlan.financialPlanIssues" i18nkey="powbSynthesis.financialPlan.highlight" help="powbSynthesis.financialPlan.highlight.help"  helpIcon=false
             paramText="${actualPhase.year}" required=true className="" editable=editable && PMU powbInclude=true/]
          </div>
          <br />
          
          [#-- Table E: CRP Planned Budget   --]
          <div class="form-group">
            <h4 class="subTitle headTitle powb-table">[@s.text name="financialPlan.tableE.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            <span class="powb-doc badge label-powb-table" title="[@s.text name="powb.includedField.title" /]">[@s.text name="powb.includedField" /] <span class="glyphicon glyphicon-save-file"></span></span>
            [@tableE /]
          </div>
          
          [#-- Table F: Main Areas of W1/2 Expenditure   --]
          <div class="form-group">
            <h4 class="subTitle headTitle powb-table">[@s.text name="financialPlan.tableF.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            <span class="powb-doc badge label-powb-table" title="[@s.text name="powb.includedField.title" /]">[@s.text name="powb.includedField" /] <span class="glyphicon glyphicon-save-file"></span></span>
            [@tableF /]
          </div>
          
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#if PMU]
          [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        [/#if]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]

[#---------------------------------------------- MACROS ----------------------------------------------]

[#macro tableE ]
  [#assign commentsBelow = true /]
  <div class="">
    <table id="tableE" class="table table-bordered">
      <thead>
        <tr>
          <th rowspan="2"></th>
          <th colspan="5" class="text-center">[@s.text name="financialPlan.tableE.plannedBudget"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</th>
          <th rowspan="2">[@s.text name="financialPlan.tableE.comments" /][@customForm.req required=editable && PMU /]</th>
        </tr>
        <tr>
          <th class="text-center col-md-2"> [@s.text name="financialPlan.tableE.carryOver"][@s.param]${(actualPhase.year - 1)!}[/@s.param][/@s.text] </th>
          <th class="text-center col-md-2">[@s.text name="financialPlan.tableE.w1w2" /]</th>
          <th class="text-center col-md-2">[@s.text name="financialPlan.tableE.w3bilateral" /]</th>
          <th class="text-center col-md-2">[@s.text name="financialPlan.tableE.centerFunds" /]</th>
          <th class="text-center">[@s.text name="financialPlan.tableE.total" /]</th>
        </tr>
      </thead>
      <tbody>
      [#assign plannedBudgetIndex = 0 /]
      [#if flagships??]
        [#list flagships  as area]
          [#assign element = (action.getPowbFinancialPlanBudget(area.id, true))! /]
          [@powbExpenditureArea area=area element=element index=plannedBudgetIndex isLiaison=true /]
          [#assign plannedBudgetIndex = plannedBudgetIndex +1 /]
        [/#list]
      [/#if]
      [#if plannedBudgetAreas??]
        [#list plannedBudgetAreas  as area]
          [#assign element = (action.getPowbFinancialPlanBudget(area.id, false))! /]
          [@powbExpenditureArea area=area element=element index=plannedBudgetIndex isLiaison=false/]
          [#assign plannedBudgetIndex = plannedBudgetIndex +1 /]
        [/#list]
      [/#if]
      <tr>
        <th>CRP Total</th>
        <th class="text-right"> <nobr>US$ <span class="label-totalByType type-carry">0.00</span></nobr> </th>
        <th class="text-right"> <nobr>US$ <span class="label-totalByType type-w1w2">0.00</span></nobr> </th>
        <th class="text-right"> <nobr>US$ <span class="label-totalByType type-w3bilateral">0.00</span></nobr> </th>
        <th class="text-right"> <nobr>US$ <span class="label-totalByType type-centerFunds">0.00</span></nobr> </th>
        <th class="text-right"> <nobr>US$ <span class="label-grandTotal">0.00</span></nobr> </th>
        <th></th>
      </tr>
      </tbody>
    </table>
  </div>
[/#macro]

[#macro powbExpenditureArea area element index isLiaison]
  [#local customName = "powbSynthesis.powbFinancialPlannedBudgetList[${index}]" /]
  <tr>
    [#-- FLAGSHIP/OTHER/PMU --]
    <td class="col-md-2">
      <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
      [#if isLiaison]
        <span class="programTag" style="border-color:${(area.crpProgram.color)!'#fff'}" title="${area.crpProgram.composedName}">${area.crpProgram.acronym}</span></td>   
        <input type="hidden" name="${customName}.liaisonInstitution.id" value="${(area.id)!}" />
      [#else]
        <span> ${(area.crpProgram.composedName)!((area.expenditureArea)!'null')}</span>
        <input type="hidden" name="${customName}.powbExpenditureArea.id" value="${(area.id)!}" />
      [/#if]
    </td>
    [#-- CARRY OVER--]
    <td class="text-right">
      [#if editable && PMU  ]
        [@customForm.input name="${customName}.carry" value="${(element.carry)!'0.00'}" i18nkey="" showTitle=false className="currencyInput text-right type-carry category-${index}" required=true /]
      [#else]
        <input type="hidden" name="${customName}.carry" value="${(element.carry)!'0'}" class="currencyInput type-carry category-${index}"/>
        <nobr>US$ ${((element.carry)!'0')?number?string(",##0.00")}</nobr>
      [/#if]
    </td>
    [#-- W1/W2 --]
    <td class="text-right">
      [#if editable && PMU && element.editBudgets  ]
        [@customForm.input name="${customName}.w1w2" value="${(element.w1w2)!'0.00'}" i18nkey="" showTitle=false className="currencyInput text-right type-w1w2 category-${index}" required=true /]
      [#else]
        <input type="hidden" name="${customName}.w1w2" value="${(element.w1w2)!'0'}" class="currencyInput type-w1w2 category-${index}"/>
        [#if (area.crpProgram??)!false]
          [#-- Flagship --]
          [@powbMacros.projectBudgetsByFlagshipMacro element=area.crpProgram totalValue=element.w1w2 type="W1W2" popupEnabled=true/]
        [#elseif area.id == 2]
          [#-- PMU --]
          [@powbMacros.projectBudgetsByFlagshipMacro element=element totalValue=(element.w1w2)! type="W1W2" popupEnabled=true isAreaPMU=true/]
        [#else]
          [#-- Other --]
          <nobr>US$ ${((element.w1w2)!'0')?number?string(",##0.00")}</nobr>
        [/#if]
      [/#if]
    </td>
    [#-- W3/BILATERAL --]
    <td class="text-right">
      [#if editable && PMU && element.editBudgets ]
        [@customForm.input name="${customName}.w3Bilateral" value="${(element.w3Bilateral)!'0.00'}" i18nkey="" showTitle=false className="currencyInput text-right type-w3bilateral category-${index}"  required=true /]
      [#else]
        <input type="hidden" name="${customName}.w3Bilateral" value="${(element.w3Bilateral)!'0'}" class="currencyInput type-w3bilateral category-${index}"/>
        [#if (area.crpProgram??)!false]
          [#-- Flagship --]
          [@powbMacros.projectBudgetsByFlagshipMacro element=area.crpProgram totalValue=(element.w3Bilateral)!0 type="W3BILATERAL" popupEnabled=true/]
        [#elseif area.id == 2]
          [#-- PMU --]
          [@powbMacros.projectBudgetsByFlagshipMacro element=element totalValue=(element.w3Bilateral)! type="W3BILATERAL" popupEnabled=true isAreaPMU=true/]
        [#else]
          [#-- Other --]
          <nobr>US$ ${((element.w3Bilateral)!'0')?number?string(",##0.00")}</nobr>
        [/#if]
      [/#if]
    </td>
    [#-- CENTER FUNDS--]
    <td class="text-right">
      [#if editable && PMU && element.editBudgets ]
        [@customForm.input name="${customName}.centerFunds" value="${(element.centerFunds)!'0.00'}" i18nkey="" showTitle=false className="currencyInput text-right type-centerFunds category-${index}"  required=true /]
      [#else]
        <input type="hidden" name="${customName}.centerFunds" value="${(element.centerFunds)!'0'}" class="currencyInput type-centerFunds category-${index}"/>
        [#if (area.crpProgram??)!false]
          [#-- Flagship --]
          [@powbMacros.projectBudgetsByFlagshipMacro element=area.crpProgram totalValue=(element.centerFunds)!0 type="CENTERFUNDS" popupEnabled=true/]
        [#elseif area.id == 2]
          [#-- PMU --]
          [@powbMacros.projectBudgetsByFlagshipMacro element=element totalValue=(element.centerFunds)! type="CENTERFUNDS" popupEnabled=true isAreaPMU=true/]
        [#else]
          [#-- Other --]
          <nobr>US$ ${((element.centerFunds)!'0')?number?string(",##0.00")}</nobr>
        [/#if]
      [/#if]
    </td>
    [#-- TOTAL --]
    <td class="text-right"> <nobr>US$ <span class="text-right label-total category-${index}">0.00</span></nobr> </td>
    [#-- COMMENTS--]
    <td class="col-md-4">[@customForm.textArea  name="${customName}.comments" value="${(element.comments)!}" i18nkey="" fieldEmptyText="global.prefilledByPmu" showTitle=false className="" editable=editable && PMU/]</td>
  </tr>
[/#macro]

[#macro tableF ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th>[@s.text name="financialPlan.tableF.expenditureArea" /]</th>
          <th>
            [@s.text name="financialPlan.tableF.estimatedPercentage"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]**
            <img title="[@s.text name="financialPlan.tableF.estimatedPercentage.help" /]" src="${baseUrl}/global/images/icon-help2.png" alt="" />
          </th>
          <th>
            [@s.text name="financialPlan.tableF.comments" /][@customForm.req required=editable && PMU /] <br />
            <small>([@s.text name="financialPlan.tableF.notApplicable" /])</small>
          </th>
        </tr>
      </thead>
      <tbody>
      [#if expenditureAreas??]
        [#list expenditureAreas  as expenditureArea]      
          [#assign customName = "powbSynthesis.powbFinancialExpendituresList[${expenditureArea_index}]" /]
          [#assign element = (action.getPowbFinancialExpenditurebyExpenditureArea(expenditureArea.id))!{} /]
          <tr>
            <td> 
              <span>${expenditureArea.expenditureArea} </span>
              
              [#local expenditureHelp][@s.text name="financialPlan.tableF.expenditureHelp.${expenditureArea.id}" /][/#local]
              [#if expenditureHelp?has_content]<img title="${expenditureHelp}" src="${baseUrl}/global/images/icon-help2.png" alt="" />[/#if]
              
              <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
              <input type="hidden" name="${customName}.powbExpenditureArea.id" value="${(expenditureArea.id)!}" />
            </td>
            <td class="text-center"> 
              [#if editable && PMU ]
                [@customForm.input name="${customName}.w1w2Percentage" value="${(element.w1w2Percentage)!'0'}" i18nkey="" showTitle=false className="percentageInput text-center type-percentage category-${expenditureArea_index}" required=true /]
              [#else]
                <input type="hidden" name="${customName}.w1w2Percentage" value="${(element.w1w2Percentage)!'0'}" class="percentageInput type-percentage category-${expenditureArea_index}"/>
                <nobr>${(element.w1w2Percentage)!0}%</nobr>
              [/#if]
            </td>
            <td class="col-md-6">
              [@customForm.textArea  name="${customName}.comments" value="${(element.comments)!}" fieldEmptyText="global.prefilledByPmu" placeholder="" i18nkey="" showTitle=false className="" editable=editable && PMU/] 
            </td>
          </tr>
        [/#list]
      [/#if]
      <tr>
        <th>Total Funding (Amount)</th>
        <th class="text-right"> <nobr>US$ <span class="label-expenditureTotal">0.00</span></nobr> </th>
        <th class="text-right"> </th>
      </tr>
      </tbody>
    </table>
    <i class="hidden">[@s.text name="financialPlan.tableF.expenditureArea.help" /] </i> <br />
    <i>[@s.text name="financialPlan.tableF.estimatedPercentage.help" /] </i>
  </div>
[/#macro]


