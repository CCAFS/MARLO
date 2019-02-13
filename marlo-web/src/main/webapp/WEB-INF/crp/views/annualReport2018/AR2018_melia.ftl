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
          
          [#-- Short narrative to introduce the table 9 --]
          [#if PMU]
            <div class="form-group">
                [@customForm.textArea name="${customName}.narrative" i18nkey="${customLabel}.narrative" className="" helpIcon=false required=false editable=editable /]
            </div>
          [#else]
            <div class="textArea">
                <label for="">[@customForm.text name="${customLabel}.narrative" readText=true /]:</label>
                <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
            </div>
          [/#if]
          
          [#-- Table 9: MELIA --]
          [#assign meliaList = [
                { 
                  "studies": "Studies",
                  "status": "Status",
                  "type": "Type",
                  "comments": "Comments"
                },
                { 
                  "studies": "Studies 1",
                  "status": "Status 1",
                  "type": "Type 1",
                  "comments": "Comments 1"
                }
              ] /]
          [@meliaTable name="table9" list=meliaList /]
          
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

[#macro meliaTable name list=[]]

  <div class="form-group">
    <h4 class="subTitle headTitle annualReport-table">[@s.text name="${customLabel}.${name}.title" /]</h4>
    [@customForm.helpLabel name="${customLabel}.${name}.help" showIcon=false editable=editable/]
    
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.studies" /] </th>
          <th class="text-center col-md-2"> [@s.text name="${customLabel}.${name}.status" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.type" /] </th>
          <th class="text-center col-md-4"> [@s.text name="${customLabel}.${name}.comments" /] </th>
          <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.includeAR" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
          <tr>
            <td>${item.studies}</td>
            <td>${item.status}</td>
            <td>${item.type}</td>
            <td>${item.comments}</td>
            <td class="text-center">
              [@customForm.checkmark id="" name="" label="" value="" editable=editable checked=false cssClass="" /]
            </td>
          </tr>
          [/#list]
          [#else]
          <tr>
            <td class="text-center" colspan="6"><i>No entries added yet.</i></td>
          </tr>
        [/#if]
        <tr>
        </tr>
      </tbody>
    </table>
    
  </div>

[/#macro]