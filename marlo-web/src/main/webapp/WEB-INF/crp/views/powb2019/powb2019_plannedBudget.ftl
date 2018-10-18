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
          
          [#if PMU]
            <h4 class="sectionSubTitle">[@s.text name="${customLabel}.table3PlannedBudget.title" /]</h4>
            <div class="form-group">
              <div class="expenditures-list">
                [#assign expenditureAreas = ((flagships)![]) + ((plannedBudgetAreas)![]) ]
                [#list expenditureAreas  as area]
                  [#assign isLiaison = (area.class.name?contains("LiaisonInstitution"))!false]
                  [#assign element = (action.getPowbFinancialPlanBudget(area.id, isLiaison))! /]
                  [@powbExpenditureArea area=area element=element index=area_index isLiaison=isLiaison /]
                [/#list]
              </div>
              [#-- Add other main program planned budget outside FPs  --]
              [#if editable]
                <div class="addExpenditureArea bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addExpenditureArea"/]</div>
              [/#if]
            </div>
          [/#if]
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
  <div id="expenditureBlock-${isTemplate?string('template', index)}" class="expenditureBlock simpleBox form-group" style="display:${isTemplate?string('none','block')}">
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    [#if !isCustomExpenditure ]
      <input type="hidden" name="${customName}.${isLiaison?string('liaisonInstitution','powbExpenditureArea')}.id" value="${(area.id)!}" />
    [/#if]
    
    [#-- Remove Button --]
    [#if editable && isCustomExpenditure]<div class="removeElement removeIcon sm removeExpendutureArea" title="Remove"></div>[/#if]
    
    [#-- Title --]
    [#if !isCustomExpenditure ]
      [#if isLiaison]<span class="programTag" style="border-color:${(area.crpProgram.color)!'#fff'};margin-right: 1em;  margin-top: 6px;" >${area.crpProgram.acronym}</span></td>[/#if]      
      <h4 class="subTitle headTitle"> ${(area.name)!(area.expenditureArea)!'null'} </h4>
      <hr />
    [#else]
      <div class="form-group">
        [@customForm.input name="${customName}.title" i18nkey="${customLabel}.areaTitle" help="${customLabel}.areaTitle.help" helpIcon=false className="" required=true editable=editable /]
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
            <td class="text-right">
              [@customForm.input name="${customName}.w1w2" value="${(element.w1w2)!'0.00'}" i18nkey="" showTitle=false className="currencyInput text-right type-w1w2 category-${index}" required=true editable=editable /]
            </td>
            [#-- W3/Bilateral --]
            <td class="text-right">
              [@customForm.input name="${customName}.w3Bilateral" value="${(element.w3Bilateral)!'0.00'}" i18nkey="" showTitle=false className="currencyInput text-right type-w3Bilateral category-${index}" required=true editable=editable /]
            </td>
            [#-- Center own funds --]
            <td class="text-right">
              [@customForm.input name="${customName}.centerFunds" value="${(element.centerFunds)!'0.00'}" i18nkey="" showTitle=false className="currencyInput text-right type-centerFunds category-${index}" required=true editable=editable /]
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