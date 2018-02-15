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
            [@customForm.textArea  name="powbSynthesis.financialPlan.financialPlanIssues" i18nkey="powbSynthesis.financialPlan.highlight" help="powbSynthesis.financialPlan.highlight.help" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable /]
          </div>
          <br />
          
          [#-- Table E: CRP Planned Budget   --]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="financialPlan.tableE.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            [@tableE /]
          </div>
          
          [#-- Table F: Main Areas of W1/2 Expenditure   --]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="financialPlan.tableF.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
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
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th rowspan="2"></th>
          <th colspan="3" class="text-center">[@s.text name="financialPlan.tableE.plannedBudget"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</th>
          <th rowspan="2">[@s.text name="financialPlan.tableE.comments" /]</th>
        </tr>
        <tr>
          <th class="text-center col-md-2">[@s.text name="financialPlan.tableE.w1w2" /]</th>
          <th class="text-center col-md-2">[@s.text name="financialPlan.tableE.w3bilateral" /]</th>
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
      </tbody>
    </table>
  </div>
[/#macro]

[#macro powbExpenditureArea area element index isLiaison]
  [#local customName = "powbSynthesis.powbFinancialPlannedBudgetList[${index}]" /]
  <tr>
    <td>
      <span> ${(area.composedName)!((area.expenditureArea)!'null')}</span>
      <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
      [#if isLiaison]
        <input type="hidden" name="${customName}.liaisonInstitution.id" value="${(area.id)!}" />
      [#else]
        <input type="hidden" name="${customName}.powbExpenditureArea.id" value="${(area.id)!}" />
      [/#if]
    </td>
    <td>[@customForm.input name="${customName}.w1w2" value="${(element.w1w2)!}" i18nkey="" showTitle=false className="currencyInput text-center type-w1w2 category-${index}" required=true editable=editable && PMU /]</td>
    <td>[@customForm.input name="${customName}.w3bilateral" value="${(element.w3Bilateral)!}" i18nkey="" showTitle=false className="currencyInput text-center type-w3bilateral category-${index}" required=true editable=editable && PMU /]</td>
    <td> US$ <span class="label-total category-${index}">0.00</span> </td>
    <td>[@customForm.textArea  name="${customName}.comments" i18nkey="" showTitle=false className="" editable=editable && PMU/]</td>
  </tr>
[/#macro]

[#macro tableF ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th>[@s.text name="financialPlan.tableF.expenditureArea" /]</th>
          <th>[@s.text name="financialPlan.tableF.estimatedPercentage"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</th>
          <th>[@s.text name="financialPlan.tableF.comments" /]</th>
        </tr>
      </thead>
      <tbody>
      [#if expenditureAreas??]
        [#list expenditureAreas  as area]      
          [#assign customName = "powbSynthesis.powbFinancialExpendituresList[${area_index}]" /]
          [#assign element = {} /]
          <tr>
            <td> 
              <span>${area.expenditureArea} </span>
              <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
              <input type="hidden" name="${customName}.powbExpenditureArea.id" value="${(area.id)!}" />
            </td>
            <td> [@customForm.input name="${customName}.percentage" value="${(element.percentage)!}" i18nkey="" showTitle=false className="percentageInput text-center type-percentage category-${area_index}" required=true editable=editable && PMU /]</td>
            <td class="col-md-7"> [@customForm.textArea  name="${customName}.comments" i18nkey="" showTitle=false className="" editable=editable && PMU/] </td>
          </tr>
        [/#list]
      [/#if]
      </tbody>
    </table>
  </div>
[/#macro]