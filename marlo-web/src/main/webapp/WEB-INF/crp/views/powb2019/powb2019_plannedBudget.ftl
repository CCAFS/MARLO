[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${powbSynthesisID}" /]
[#assign pageLibs = [ "trumbowyg"] /]
[#assign customJS = [ "${baseUrlMedia}/js/powb2019/powb2019_plannedBudget.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "plannedBudget" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb2019", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"plannedBudget", "nameSpace":"powb2019", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "powbSynthesis.plannedBudget" /]
[#assign customLabel= "powbSynthesis.${currentStage}" /]


[#-- Helptext --]
[@utilities.helpBox name="${customLabel}.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/powb2019/submenu-powb2019.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/powb2019/menu-powb2019.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/powb2019/messages-powb2019.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
        <div class="borderBox">
          <div class="form-group margin-panel">
            [#-- Provide a short narrative of expected highlights of the CRP/PTF in the coming year --]
            <div class="form-group">
              [#if PMU][@utilities.tag label="powb.docBadge" tooltip="powb.docBadge.tooltip"/][/#if]
              [@customForm.textArea name="${customName}.narrative" i18nkey="${customLabel}.narrative" help="${customLabel}.narrative.help" helpIcon=false required=true className="limitWords-${calculateLimitWords(500)}" editable=editable allowTextEditor=true   /]
            </div>
            
            [#if PMU]
            <div class="form-group">
              [@tableFlagshipSynthesis tableName="narrativeFlagshipsTable" list=tocList columns=["narrative"] /]
            </div>
            [/#if]
          </div>
          
          <hr />
          
          [#-- Table 3: Planned Budget --]
          <h4 class="sectionSubTitle">[@s.text name="${customLabel}.table3PlannedBudget.title${PMU?string('','FP')}" /]</h4>
          <div class="form-group">
            <div class="expenditures-list">
              [#assign expenditureAreas = ((flagships)![]) + ((plannedBudgetAreas)![]) + ((otherPlannedBudgets)![]) ]
              [#list expenditureAreas  as area]
                [#assign isLiaison = (area.class.name?contains("LiaisonInstitution"))!false]
                [#assign element = (action.getPowbFinancialPlanBudget(area.id, isLiaison))! /]
                [#if PMU || ((element.crpProgram.id == liaisonInstitutionID)!false)]
                  [@powbExpenditureArea area=area element=element index=area_index isLiaison=isLiaison /]
                [/#if]
              [/#list]
            </div>
            [#-- Add other main program planned budget outside FPs  --]
            [#if editable && PMU]
              <div class="addExpenditureArea bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addExpenditureArea"/]</div>
            [/#if]
          </div>
          
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>

[#-- Templates --]
[@powbExpenditureArea area={} element={} index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#----------------------------------------------------- MACROS --------------------------------------------------------]

[#macro tableFlagshipSynthesis tableName="tableName" list=[] columns=[] ]
  <div class="form-group">
    <h4 class="simpleTitle">[@s.text name="${customLabel}.${tableName}.title" /]</h4>
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="col-md-1 text-center"> FP </th>
          [#list columns as column]<th> [@s.text name="${customLabel}.${tableName}.column${column_index}" /] </th>[/#list]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#local crpProgram = (item.liaisonInstitution.crpProgram)!{} ]
            <tr>
              <td>
                <span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}">${(crpProgram.acronym)!}</span>
              </td>
              [#list columns as column]
                <td>
                  [#if (item[column]?has_content)!false] 
                    ${item[column]?replace('\n', '<br>')} 
                  [#else]
                    <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                  [/#if]
                </td>
              [/#list]
            </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="${columns?size + 1}"><i>No flagships loaded...</i></td>
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro powbExpenditureArea area element index isLiaison=false isTemplate=false ]
  [#local customName = "powbSynthesis.powbFinancialPlannedBudgetList[${index}]"]
  [#local areaType = (area.class.name?split('.')[area.class.name?split('.')?size-1])!]
  [#local isCustomExpenditure =  !(((area.name?has_content)!false) || ((area.expenditureArea?has_content)!false))]
  [#local isAreaPMU = (area.id == 2)!false ]
  <div id="expenditureBlock-${isTemplate?string('template', index)}" class="expenditureBlock simpleBox form-group" style="display:${isTemplate?string('none','block')}">
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    [#if !isCustomExpenditure ]
      <input type="hidden" name="${customName}.${isLiaison?string('liaisonInstitution','powbExpenditureArea')}.id" value="${(area.id)!}" />
    [/#if]
    
    [#-- Remove Button --]
    [#if editable && isCustomExpenditure]<div class="removeElement removeIcon sm removeExpendutureArea" title="Remove"></div>[/#if]
    
    [#-- Title --]
    [#if !isCustomExpenditure ]
      [#if isLiaison || isAreaPMU]
      <div class="pull-right">
         
      </div>
      [/#if]
      
      [#if isLiaison]<span class="programTag" style="border-color:${(area.crpProgram.color)!'#fff'};margin-right: 1em;  margin-top: 6px;" >${area.crpProgram.acronym}</span></td>[/#if]      
      <h4 class="subTitle headTitle"> ${(area.name)!(area.expenditureArea)!'null'} </h4>
      
      <hr />
    [#else]
      <div class="form-group">
        [@customForm.input name="${customName}.title" value="${(area.title)!}" i18nkey="${customLabel}.areaTitle" help="${customLabel}.areaTitle.help" helpIcon=false className="" required=true editable=editable && PMU /]
      </div>
    [/#if]
    
    
    <div class="form-group">
      <table class="noFormatTable">
        <thead>
          <tr>
            <th></th>
            <th class="text-center">W1/W2</th>
            <th class="text-center">W3/Bilateral</th>
            <th class="text-center">Center own funds</th>
            <th class="text-center">Total</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="amountType"> <nobr>Planned Budget:</nobr> </td>
            [#-- W1/W2 --]
            <td class="text-center">
              [#if isLiaison || isAreaPMU]
                [@projectBudgetsByFlagshipMacro element=(area.crpProgram)!area totalValue=element.w1w2 type="W1W2" popupEnabled=true isAreaPMU=isAreaPMU/]
                <input type="hidden" name="${customName}.w1w2" value="${(element.w1w2)!'0'}" class="currencyInput type-w1w2 category-${index}"/>
              [#else]
                [@customForm.input name="${customName}.w1w2" value="${(element.w1w2)!'0.00'}" i18nkey="" showTitle=false className="currencyInput text-right type-w1w2 category-${index}" required=true editable=editable && PMU /]
              [/#if]
            </td>
            [#-- W3/Bilateral --]
            <td class="text-center">
              [#if isLiaison || isAreaPMU]
                [@projectBudgetsByFlagshipMacro element=(area.crpProgram)!area totalValue=element.w3Bilateral type="W3BILATERAL" popupEnabled=true isAreaPMU=isAreaPMU/]
                <input type="hidden" name="${customName}.w3Bilateral" value="${(element.w3Bilateral)!'0'}" class="currencyInput type-w3Bilateral category-${index}"/>
              [#else]
                [@customForm.input name="${customName}.w3Bilateral" value="${(element.w3Bilateral)!'0.00'}" i18nkey="" showTitle=false className="currencyInput text-right type-w3Bilateral category-${index}" required=true editable=editable && PMU /]
              [/#if]
            </td>
            [#-- Center own funds --]
            <td class="text-center">
              [#if isLiaison || isAreaPMU]
                [@projectBudgetsByFlagshipMacro element=(area.crpProgram)!area totalValue=element.centerFunds type="CENTERFUNDS" popupEnabled=true isAreaPMU=isAreaPMU/]
                <input type="hidden" name="${customName}.centerFunds" value="${(element.centerFunds)!'0'}" class="currencyInput type-centerFunds category-${index}"/>
              [#else]
                [@customForm.input name="${customName}.centerFunds" value="${(element.centerFunds)!'0.00'}" i18nkey="" showTitle=false className="currencyInput text-right type-centerFunds category-${index}" required=true editable=editable && PMU /]
              [/#if]
            </td>
            [#-- Total --]
            <td class="text-right">
              <nobr>US$ <span class="text-right label-total category-${index}">0.00</span></nobr>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    [#-- Comments --]
    <div class="form-group">
      [@customForm.textArea  name="${customName}.comments" value="${(element.comments)!}" i18nkey="${customLabel}.table3PlannedBudget.comments" fieldEmptyText="global.prefilledByPmu" className="" editable=editable && PMU/]
    </div>
  </div>
[/#macro]

[#macro projectBudgetsByFlagshipMacro element type totalValue=0 tiny=false popupEnabled=true isAreaPMU=false]
  
  [#if !popupEnabled]
    <nobr>US$ <span >${((totalValue)!0)?number?string(",##0.00")}</span></nobr>
  [#else]
    [#if isAreaPMU]
      [#local projects = (action.loadPMUProjects())![] ]
    [#else]
      [#local projects = (action.loadFlagShipBudgetInfoProgram(element.id))![] ]
    [/#if]
    [#if projects?size > 0]
    <a class=" btn btn-default btn-xs" data-toggle="modal" style="border-color: #00BCD4;color: #057584;" data-target="#projectBudgets-${type}-${(element.id)!}">
       <span class="glyphicon glyphicon-fullscreen" style="color:#b3b3b3"></span>  
       <nobr>US$ <span >${((totalValue)!0)?number?string(",##0.00")}</span></nobr>
    </a>
    
    <!-- Modal -->
    <div class="modal fade" id="projectBudgets-${type}-${(element.id)!}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="myModalLabel">
              [#if type == "W1W2"]
                [@s.text name="expectedProgress.projectBudgetsW1w2" /]
              [#elseif type == "W3BILATERAL"]
                [@s.text name="expectedProgress.projectBudgetsW3Bilateral" /]
              [#elseif type == "CENTERFUNDS"]
                [@s.text name="expectedProgress.projectBudgetsCenterFunds" /]
              [/#if]
            </h4>
            <span class="programTag" style="border-color:${(element.color)!'#fff'}">${(element.composedName)!}</span> 
          </div>
          <div class="modal-body">
            <div class="">
              <table class="table table-bordered">
                <thead>
                  <tr>
                    <th rowspan="2">[@s.text name="project.title" /]</th>
                    [#if type == "W1W2"] <th colspan="2" class="text-center">[@s.text name="project.coreBudget" /]</th>[/#if]
                    [#if type == "W3BILATERAL"] <th colspan="2" class="text-center">[@s.text name="project.w3Budget" /]</th>[/#if]
                    [#if type == "W3BILATERAL"] <th colspan="2" class="text-center">[@s.text name="project.bilateralBudget" /]</th>[/#if]
                    [#if type == "CENTERFUNDS"] <th colspan="2" class="text-center">[@s.text name="project.centerFundsBudget" /]</th>[/#if]
                    
                  </tr>
                  <tr>
                    [#if type == "W1W2"]
                      <th class="text-center"> Total project [@s.text name="project.coreBudget" /] Amount</th> 
                      <th class="text-center">  ${(element.acronym)!} % Contribution and amount </th>
                    [/#if]
                    [#if type == "W3BILATERAL"]
                      <th class="text-center"> Total project [@s.text name="project.w3Budget" /] Amount</th>
                      <th class="text-center"> ${(element.acronym)!} % Contribution and amount</th>
                    [/#if]
                    [#if type == "W3BILATERAL"]
                      <th class="text-center"> Total project [@s.text name="project.bilateralBudget" /] Amount</th>
                      <th class="text-center"> ${(element.acronym)!} % Contribution and amount</th>
                    [/#if]
                    [#if type == "CENTERFUNDS"]
                      <th class="text-center"> Total project [@s.text name="project.centerFundsBudget" /] Amount</th>
                      <th class="text-center"> ${(element.acronym)!} % Contribution and amount</th>
                    [/#if]
                  </tr>
                </thead>
                <tbody>
                  [#local totalProjectsW1W2 = 0 /]
                  [#local totalContributionW1W2 = 0 /]
                  
                  [#local totalProjectsW3 = 0 /]
                  [#local totalContributionW3 = 0 /]
                  
                  [#local totalProjectsBilateral = 0 /]
                  [#local totalContributionBilateral = 0 /]
                  
                  [#local totalProjectsCenterFunds = 0 /]
                  [#local totalContributionCenterFunds = 0 /]
     
                  [#list projects as project]
                    [#local pURL][@s.url namespace="/projects" action="${(crpSession)!}/budgetByPartners"][@s.param name='projectID']${project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    <tr>
                      <td class="col-md-6"> <a href="${pURL}" target="_blank">${(project.composedName)!}</a></td>
                      [#if type == "W1W2"]
                        <td class="text-right"> <nobr>US$ ${(project.coreBudget?number?string(",##0.00"))!}</nobr></td> 
                        <td class="text-right"> <nobr> <span class="text-primary"> ${(project.percentageW1)!}% </span> (US$ ${(project.totalW1?number?string(",##0.00"))!})</nobr></td>
                        [#local totalProjectsW1W2 = totalProjectsW1W2 + (project.coreBudget)!0 /]
                        [#local totalContributionW1W2 = totalContributionW1W2 + (project.totalW1)!0 /]
                      [/#if]
                      [#if type == "W3BILATERAL"]
                        <td class="text-right"> <nobr>US$ ${(project.w3Budget?number?string(",##0.00"))!}</nobr></td>
                        <td class="text-right"> <nobr> <span class="text-primary">${(project.percentageW3)!}%</span> (US$ ${(project.totalW3?number?string(",##0.00"))!})</nobr></td>
                        [#local totalProjectsW3 = totalProjectsW3 + (project.w3Budget)!0 /]
                        [#local totalContributionW3 = totalContributionW3 + (project.totalW3)!0 /]
                      [/#if]
                      [#if type == "W3BILATERAL"]
                        <td class="text-right"> <nobr>US$ ${(project.bilateralBudget?number?string(",##0.00"))!}</nobr></td>
                        <td class="text-right"> <nobr> <span class="text-primary">${(project.percentageBilateral)!}%</span>  (US$ ${(project.totalBilateral?number?string(",##0.00"))!})</nobr></td>
                        [#local totalProjectsBilateral = totalProjectsBilateral + (project.bilateralBudget)!0 /]
                        [#local totalContributionBilateral = totalContributionBilateral + (project.totalBilateral)!0 /]
                      [/#if]
                      [#if type == "CENTERFUNDS"]
                        <td class="text-right"> <nobr>US$ ${(project.centenFundsBudget?number?string(",##0.00"))!}</nobr></td>
                        <td class="text-right"> <nobr> <span class="text-primary">${(project.percentageFundsBudget)!}%</span>  (US$ ${(project.totalCenterFunds?number?string(",##0.00"))!})</nobr></td>
                        [#local totalProjectsCenterFunds = totalProjectsCenterFunds + (project.centenFundsBudget)!0 /]
                        [#local totalContributionCenterFunds = totalContributionCenterFunds + (project.totalCenterFunds)!0 /]
                      [/#if]
                    </tr>
                  [/#list]
                </tbody>
                <tfoot>
                  <tr>
                    <td class="col-md-6"> <strong>Total</strong> </td>
                    [#if type == "W1W2"]
                      [#if totalProjectsW1W2 != 0]
                        [#local percentageContributionW1W2 = (totalContributionW1W2/ totalProjectsW1W2) * 100 ]
                      [/#if]
                      <td class="text-right"> <nobr>US$ ${(totalProjectsW1W2?number?string(",##0.00"))!}</nobr></td>
                      <td class="text-right"> <nobr> <span class="text-primary">${((percentageContributionW1W2)!0)?number?string(",##0.00")}%</span> (US$ ${(totalContributionW1W2?number?string(",##0.00"))!}) </nobr></td>
                    [/#if]
                    [#if type == "W3BILATERAL"]
                      [#if totalProjectsW3 != 0]
                        [#local percentageContributionW3 = (totalContributionW3/ totalProjectsW3) * 100 ]
                      [/#if]
                      <td class="text-right"> <nobr>US$ ${(totalProjectsW3?number?string(",##0.00"))!}</nobr></td>
                      <td class="text-right"> <nobr> <span class="text-primary">${((percentageContributionW3)!0)?number?string(",##0.00")}%</span> (US$ ${(totalContributionW3?number?string(",##0.00"))!}) </nobr> </td>
                    [/#if]
                    [#if type == "W3BILATERAL"]
                      [#if totalProjectsBilateral != 0]
                        [#local percentageContributionBilateral = (totalContributionBilateral/ totalProjectsBilateral) * 100 ]
                      [/#if]
                      <td class="text-right"> <nobr>US$ ${(totalProjectsBilateral?number?string(",##0.00"))!} </nobr> </td>
                      <td class="text-right"> <nobr> <span class="text-primary"> ${((percentageContributionBilateral)!0)?number?string(",##0.00")}% </span> (US$ ${(totalContributionBilateral?number?string(",##0.00"))!}) </nobr> </td>
                    [/#if]
                    [#if type == "CENTERFUNDS"]
                      [#if totalProjectsCenterFunds != 0]
                        [#local percentageContributionBilateral = (totalContributionCenterFunds/ totalProjectsCenterFunds) * 100 ]
                      [/#if]
                      <td class="text-right"> <nobr>US$ ${(totalProjectsCenterFunds?number?string(",##0.00"))!} </nobr> </td>
                      <td class="text-right"> <nobr> <span class="text-primary"> ${((percentageContributionBilateral)!0)?number?string(",##0.00")}% </span> (US$ ${(totalContributionCenterFunds?number?string(",##0.00"))!}) </nobr> </td>
                    [/#if]
                  </tr>
                </tfoot>
              </table>
              <div class="text-right">
                
                <strong>
                  [#if type == "W1W2"]Total [@s.text name="project.coreBudget" /][/#if]
                  [#if type == "W3BILATERAL"]Total [@s.text name="project.w3Budget" /]/[@s.text name="project.bilateralBudget" /] [/#if]
                  [#if type == "CENTERFUNDS"]Total [@s.text name="project.centerFundsBudget" /][/#if]
                  : US$ <span >${((totalValue)!0)?number?string(",##0.00")}</span>
                </strong>
              </div>
            </div>
            
          </div>
          <div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button></div>
        </div>
      </div>
    </div>
    [#else]
      No Projects
    [/#if]
  [/#if]
[/#macro]