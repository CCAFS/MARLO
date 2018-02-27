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
            [@customForm.textArea  name="powbSynthesis.financialPlan.financialPlanIssues" i18nkey="powbSynthesis.financialPlan.highlight" help="powbSynthesis.financialPlan.highlight.help" fieldEmptyText="global.prefilledByPmu" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable && PMU /]
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
          <th rowspan="2">[@s.text name="financialPlan.tableE.comments" /][@customForm.req required=editable && PMU /]</th>
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
      <tr>
        <th>CRP Total</th>
        <th class="text-right"> <nobr>US$ <span class="label-totalByType type-w1w2">0.00</span></nobr> </th>
        <th class="text-right"> <nobr>US$ <span class="label-totalByType type-w3bilateral">0.00</span></nobr> </th>
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
    <td>
      <span> ${(area.crpProgram.composedName)!((area.expenditureArea)!'null')}</span>
      <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
      [#if isLiaison]
        <span class="programTag" style="border-color:${(liaisonInstitution.crpProgram.color)!'#fff'}">${liaisonInstitution.crpProgram.acronym}</span></td>   
        <input type="hidden" name="${customName}.liaisonInstitution.id" value="${(area.id)!}" />
      [#else]
        <span> ${(area.crpProgram.composedName)!((area.expenditureArea)!'null')}</span>
        <input type="hidden" name="${customName}.powbExpenditureArea.id" value="${(area.id)!}" />
      [/#if]
    </td>
    <td class="text-right">
      [#if editable && PMU && element.editBudgets  ]
        [@customForm.input name="${customName}.w1w2" value="${(element.w1w2)!'0.00'}" i18nkey="" showTitle=false className="currencyInput text-right type-w1w2 category-${index}" required=true /]
      [#else]
        <input type="hidden" name="${customName}.w1w2" value="${(element.w1w2)!'0'}" class="currencyInput type-w1w2 category-${index}"/>
        <nobr>US$ ${((element.w1w2)!'0')?number?string(",##0.00")}</nobr>
        [#if (area.crpProgram??)!false]
        [@projectBudgetsByFlagshipMacro element=area.crpProgram type="W1W2" popupEnabled=true/]
        [/#if]
      [/#if]
    </td>
    <td class="text-right">
      [#if editable && PMU && element.editBudgets ]
        [@customForm.input name="${customName}.w3Bilateral" value="${(element.w3Bilateral)!'0.00'}" i18nkey="" showTitle=false className="currencyInput text-right type-w3bilateral category-${index}"  required=true /]
      [#else]
        <input type="hidden" name="${customName}.w3Bilateral" value="${(element.w3Bilateral)!'0'}" class="currencyInput type-w3bilateral category-${index}"/>
        <nobr>US$ ${((element.w3Bilateral)!'0')?number?string(",##0.00")}</nobr>
        [#if (area.crpProgram??)!false]
        [@projectBudgetsByFlagshipMacro element=area.crpProgram type="W3BILATERAL" popupEnabled=true/]
        [/#if]
      [/#if]
    </td>
    <td class="text-right"> <nobr>US$ <span class="text-right label-total category-${index}">0.00</span></nobr> </td>
    <td class="col-md-3">[@customForm.textArea  name="${customName}.comments" value="${(element.comments)!}" i18nkey="" fieldEmptyText="global.prefilledByPmu" showTitle=false className="" editable=editable && PMU/]</td>
  </tr>
[/#macro]

[#macro tableF ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th>[@s.text name="financialPlan.tableF.expenditureArea" /]</th>
          <th>[@s.text name="financialPlan.tableF.estimatedPercentage"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</th>
          <th>[@s.text name="financialPlan.tableF.comments" /][@customForm.req required=editable && PMU /]</th>
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
            <td class="col-md-7"> [@customForm.textArea  name="${customName}.comments" value="${(element.comments)!}" fieldEmptyText="global.prefilledByPmu" i18nkey="" showTitle=false className="" editable=editable && PMU/] </td>
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
  </div>
[/#macro]


[#macro projectBudgetsByFlagshipMacro element type tiny=false popupEnabled=true]
  
  [#if !popupEnabled]
    <nobr>US$ <span >[#if type == "W1W2"]${element.w1?number?string(",##0.00")}[#elseif type == "W3BILATERAL"]${element.w3?number?string(",##0.00")}[/#if]</span></nobr>
  [#else]
    [#local projects = (action.loadFlagShipBudgetInfoProgram(element.id))![] ]
    [#if projects?size > 0]
    <a class=" btn btn-default btn-xs" data-toggle="modal" data-target="#projectBudgets-${type}-${element.id}">
       US$ <span >[#if type == "W1W2"]${element.w1?number?string(",##0.00")}[#elseif type == "W3BILATERAL"]${element.w3?number?string(",##0.00")}[/#if]</span>
    </a>
    
    <!-- Modal -->
    <div class="modal fade" id="projectBudgets-${type}-${element.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="myModalLabel">
              [#if type == "W1W2"]
                [@s.text name="expectedProgress.projectBudgetsW1w2" /]
              [#elseif type == "W3BILATERAL"]
                [@s.text name="expectedProgress.projectBudgetsW3Bilateral" /]
              [/#if]
            </h4>
            <span class="programTag" style="border-color:${(element.color)!'#fff'}">${element.composedName}</span> 
          </div>
          <div class="modal-body">
            <div class="">
              <table class="table table-bordered">
                <thead>
                  <tr>
                    <th rowspan="2">[@s.text name="project.title" /]</th>
                    [#if type == "W1W2"] <th colspan="2" class="text-center">[@s.text name="project.coreBudget" /]</th>[/#if]
                    [#if type == "W3BILATERAL"] <th colspan="2" class="text-center">W3</th>[/#if]
                    [#if type == "W3BILATERAL"] <th colspan="2" class="text-center">Bilateral</th>[/#if]
                  </tr>
                  <tr>
                    [#if type == "W1W2"]<th class="text-center"> Total [@s.text name="project.coreBudget" /] Amount</th>[/#if]
                    [#if type == "W1W2"]<th class="text-center">  ${element.acronym} % (Amount) </th>[/#if]
                    [#if type == "W3BILATERAL"]<th class="text-center"> Total [@s.text name="project.w3Budget" /] Amount</th>[/#if]
                    [#if type == "W3BILATERAL"]<th class="text-center"> ${element.acronym} % (Amount)</th>[/#if]
                    [#if type == "W3BILATERAL"]<th class="text-center"> Total [@s.text name="project.bilateralBudget" /] Amount</th>[/#if]
                    [#if type == "W3BILATERAL"]<th class="text-center"> ${element.acronym} % (Amount)</th>[/#if]
                  </tr>
                </thead>
                <tbody>
                  [#list projects as project]
                    [#local pURL][@s.url namespace="/projects" action="${(crpSession)!}/budgetByPartners"][@s.param name='projectID']${project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    <tr>
                      <td class="col-md-6"> <a href="${pURL}" target="_blank">${(project.composedName)!}</a></td>
                      [#if type == "W1W2"]<td class="text-right"> <nobr>US$ ${(project.coreBudget?number?string(",##0.00"))!}</nobr></td>[/#if]
                      [#if type == "W1W2"]<td class="text-right"> <nobr>${(project.percentageW1)!}% (US$ ${(project.totalW1?number?string(",##0.00"))!})</nobr></td>[/#if]
                      [#if type == "W3BILATERAL"]<td class="text-right"> <nobr>US$ ${(project.w3Budget?number?string(",##0.00"))!}</nobr></td>[/#if]
                      [#if type == "W3BILATERAL"]<td class="text-right"> <nobr>${(project.percentageW3)!}% (US$ ${(project.totalW3?number?string(",##0.00"))!})</nobr></td>[/#if]
                      [#if type == "W3BILATERAL"]<td class="text-right"> <nobr>US$ ${(project.bilateralBudget?number?string(",##0.00"))!}</nobr></td>[/#if]
                      [#if type == "W3BILATERAL"]<td class="text-right"> <nobr>${(project.percentageBilateral)!}%  (US$ ${(project.totalBilateral?number?string(",##0.00"))!})</nobr></td>[/#if]
                    </tr>
                  [/#list]
                </tbody>
              </table>
              <div class="text-right">Total: US$ US$ <span >[#if type == "W1W2"]${element.w1?number?string(",##0.00")}[#elseif type == "W3BILATERAL"]${element.w3?number?string(",##0.00")}[/#if]</span></div>
            </div>
            
          </div>
          <div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button></div>
        </div>
      </div>
    </div>
    [/#if]
  [/#if]
[/#macro]