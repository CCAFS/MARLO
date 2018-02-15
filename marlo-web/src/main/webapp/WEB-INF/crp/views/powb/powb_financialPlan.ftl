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
       [#if powbExpenditureAreas??]
        [#list powbExpenditureAreas  as category]
       [#assign customName = "powbSynthesis.plannedBudget[${category_index}]" /]
          [#assign element = {} /]
          <tr>
            <td>
              <span>CRP Management & Support Cost ${category}</span>
              <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
              <input type="hidden" name="${customName}.CATEGORY.id" value="${(category)!}" />
            </td>
            <td>[@customForm.input name="${customName}.w1w2" value="${(element.w1w2)!}" i18nkey="" showTitle=false className="currencyInput text-center type-w1w2 category-${category_index}" required=true editable=editable && PMU /]</td>
            <td>[@customForm.input name="${customName}.w3bilateral" value="${(element.w3bilateral)!}" i18nkey="" showTitle=false className="currencyInput text-center type-w3bilateral category-${category_index}" required=true editable=editable && PMU /]</td>
            <td> US$ <span class="label-total category-${category_index}">0.00</span> </td>
            <td>[@customForm.textArea  name="${customName}.comments" i18nkey="" showTitle=false className="" editable=editable && PMU/]</td>
          </tr>
        [/#list]
         [/#if]
          
      </tbody>
    </table>
  </div>
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
        [#list 0 .. 12 as category]
          [#assign customName = "powbSynthesis.expenditures[${category_index}]" /]
          [#assign element = {} /]
          <tr>
            <td> 
              <span>Start-up or maintenance of partnerships (internal or external) ${category} </span>
              <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
              <input type="hidden" name="${customName}.CATEGORY.id" value="${(category)!}" />
            </td>
            <td> [@customForm.input name="${customName}.percentage" value="${(element.percentage)!}" i18nkey="" showTitle=false className="percentageInput text-center type-percentage category-${category_index}" required=true editable=editable && PMU /]</td>
            <td class="col-md-7"> [@customForm.textArea  name="${customName}.comments" i18nkey="" showTitle=false className="" editable=editable && PMU/] </td>
          </tr>
        [/#list]
      </tbody>
    </table>
  </div>
[/#macro]