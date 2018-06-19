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

[#assign customName= "reportSynthesis.reportSynthesisFundingUseSummary" /]
[#assign customLabel= "annualReport.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="annualReport.${currentStage}.help" /]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
  [#else]
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
          
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
          
            [#-- Summarize the main areas of expenditure of W1/2 --]
            <div class="form-group margin-panel">
              [#if PMU]
                [@customForm.textArea name="${customName}.mainArea" i18nkey="${customLabel}.summarize" help="${customLabel}.summarize.help" className="" helpIcon=false required=true editable=editable && PMU /]
              [#else]
                <div class="textArea">
                  <label for="">[@customForm.text name="${customLabel}.summarize" readText=true /]:</label>
                  <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
                </div>
              [/#if]
            </div>
            
            [#-- Table F: Main Areas of W1/2 Expenditure --]
            <div class="form-group margin-panel">
              <div class="">
                <h4 class="subTitle headTitle">[@s.text name="${customLabel}.tableF.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
              </div>
              <hr />
              [@tableFMacro name="${customName}.expenditureAreas" list=(reportSynthesis.reportSynthesisFundingUseSummary.expenditureAreas)![] /]
            </div>
          
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#if PMU]
            [#include "/WEB-INF/crp/views/annualReport/buttons-annualReport.ftl" /]
          [/#if]
          
        [/@s.form] 
      </div> 
    </div>
  [/#if]
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro tableFMacro name="" list=[] ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th width="25%">[@s.text name="${customLabel}.tableF.expenditure" /]</th>
          <th width="25%">
            [@s.text name="${customLabel}.tableF.percentage"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]**
          </th>
          <th width="50%">
            [@s.text name="${customLabel}.tableF.comments" /] [@customForm.req required=editable && PMU /]<br />
            <small>([@s.text name="${customLabel}.tableF.notApplicable" /])</small>
          </th>
        </tr>
      </thead>
      <tbody>
      [#if list??]
        [#list list  as synthesisExpArea]
          [#local customName = "${name}[${synthesisExpArea_index}]" /]
          <tr>
            [#-- Expenditure area --]
            <td>
              <span> ${(synthesisExpArea.expenditureArea.expenditureArea)!''} </span>
              [#local expenditureHelp][@s.text name="${customLabel}.expenditureHelp.${(synthesisExpArea.expenditureArea.id)!''}" /][/#local]
              [#if expenditureHelp?has_content]<img title="${expenditureHelp}" src="${baseUrl}/global/images/icon-help2.png" alt="" />[/#if]
              <input type="hidden" name="${customName}.id" value="${(synthesisExpArea.id)!}" />
              <input type="hidden" name="${customName}.expenditureArea.id" value="${(synthesisExpArea.expenditureArea.id)!}" />
            </td>
            [#-- Estimated percentage of total W1/W2 --]
            <td class="text-center">
              [#if editable && PMU ]
                [@customForm.input name="${customName}.w1w2Percentage" value="${(synthesisExpArea.w1w2Percentage)!'0'}" i18nkey="" showTitle=false className="percentageInput text-center type-percentage category-${synthesisExpArea_index}" required=true /]
              [#else]
                <input type="hidden" name="${customName}.w1w2Percentage" value="${(synthesisExpArea.w1w2Percentage)!'0'}" class="percentageInput type-percentage category-${synthesisExpArea_index}"/>
                <nobr>${(synthesisExpArea.w1w2Percentage)!0}%</nobr>
              [/#if]
            </td>
            [#-- Comments --]
            <td class="col-md-6">
              [@customForm.textArea  name="${customName}.comments" value="${(synthesisExpArea.comments)!}" fieldEmptyText="global.prefilledByPmu" placeholder="" i18nkey="" showTitle=false className="" editable=editable && PMU/] 
            </td>
          </tr>
        [/#list]
      [/#if]
      [#--  
      <tr>
        <th>[@s.text name="${customLabel}.tableF.total" /]</th>
        <th class="text-right"> <nobr>US$ <span class="label-expenditureTotal">${(totalFunding?number?string(",##0.00"))!}</span></nobr> </th>
        <th class="text-right"> </th>
      </tr>
      --]
      </tbody>
    </table>
    
    <i>**[@s.text name="${customLabel}.tableF.help" /]</i>
  </div>
[/#macro]