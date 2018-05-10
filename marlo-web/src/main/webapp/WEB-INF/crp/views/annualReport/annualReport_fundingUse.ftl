[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${powbSynthesisID}" /]
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
        
          [#-- Summarize the main areas of expenditure of W1/2 --]
          <div class="form-group margin-panel">
            [@customForm.textArea name="${customName}.summarize" i18nkey="${customLabel}.summarize" help="${customLabel}.summarize.help" className="" helpIcon=false required=true editable=editable && PMU /]
          </div>
          
          [#-- Table F: Main Areas of W1/2 Expenditure --]
          <div class="form-group margin-panel">
            <div class="evidence-plannedStudies-header">
              <h4 class="subTitle headTitle">[@s.text name="${customLabel}.tableF.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            </div>
            <hr />
            [@tableFMacro list=[{},{},{},{}] /]
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

[#macro tableFMacro list]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th width="25%">[@s.text name="${customLabel}.tableF.expenditure" /]</th>
          <th width="25%">
            [@s.text name="${customLabel}.tableF.percentage"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]**
            <img title="[@s.text name="${customLabel}.tableF.percentage.help" /]" src="${baseUrl}/global/images/icon-help2.png" alt="" />
          </th>
          <th width="50%">
            [@s.text name="${customLabel}.tableF.comments" /] [@customForm.req required=editable && PMU /]<br />
            <small>([@s.text name="${customLabel}.tableF.notApplicable" /])</small>
          </th>
        </tr>
      </thead>
      <tbody>
      [#if list??]
        [#list list  as expenditureArea]
          [#assign localCustomName = "${customName}[${expenditureArea_index}]" /]
          [#assign element = (action.getPowbFinancialExpenditurebyExpenditureArea(expenditureArea.id))!{} /]
          <tr>
            [#-- Expenditure area --]
            <td> 
              <span> ${expenditureArea.expenditureArea!''} </span>
              
              [#local expenditureHelp][@s.text name="${customLabel}.tableF.expenditureHelp.${expenditureArea.id!''}" /][/#local]
              [#if expenditureHelp?has_content]<img title="${expenditureHelp}" src="${baseUrl}/global/images/icon-help2.png" alt="" />[/#if]
              
              <input type="hidden" name="${localCustomName}.id" value="${(element.id)!}" />
              <input type="hidden" name="${localCustomName}.powbExpenditureArea.id" value="${(expenditureArea.id)!}" />
            </td>
            [#-- Estimated percentage of total W1/W2 --]
            <td class="text-center">
              [#if editable && PMU ]
                [@customForm.input name="${localCustomName}.w1w2Percentage" value="${(element.w1w2Percentage)!'0'}" i18nkey="" showTitle=false className="percentageInput text-center type-percentage category-${expenditureArea_index}" required=true /]
              [#else]
                <input type="hidden" name="${localCustomName}.w1w2Percentage" value="${(element.w1w2Percentage)!'0'}" class="percentageInput type-percentage category-${expenditureArea_index}"/>
                <nobr>${(element.w1w2Percentage)!0}%</nobr>
              [/#if]
            </td>
            [#-- Comments --]
            <td class="col-md-6">
              [@customForm.textArea  name="${localCustomName}.comments" value="${(element.comments)!}" fieldEmptyText="global.prefilledByPmu" placeholder="" i18nkey="" showTitle=false className="" editable=editable && PMU/] 
            </td>
          </tr>
        [/#list]
      [/#if]
      <tr>
        <th>[@s.text name="${customLabel}.tableF.total" /]</th>
        <th class="text-right"> <nobr>US$ <span class="label-expenditureTotal">0.00</span></nobr> </th>
        <th class="text-right"> </th>
      </tr>
      </tbody>
    </table>
    <i>**[@s.text name="${customLabel}.tableF.help" /]</i>
  </div>
[/#macro]