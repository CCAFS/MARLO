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
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="${customName}.title" /]</h3>
        <div class="borderBox">
          [#assign customName= "annualReport.${currentStage}" /]
          [#assign customLabel= "annualReport.${currentStage}" /]
        
          [#-- Summarize highlights, value added and points to improve/learning points from this year on external partnerships --]
          <div class="form-group margin-panel">
            [@customForm.textArea name="${customName}.summarizeHighlights" i18nkey="${customLabel}.summarizeHighlights" help="${customLabel}.summarizeHighlights.help" className="" helpIcon=false required=true editable=editable /]
          </div>
          
          [#-- Flagships - External Partnerships Synthesis --]
          [#if PMU]
            [@tableFlagshipsMacro /]
          [/#if]
          
          <hr />
        
        </div>
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/annualReport/buttons-annualReport.ftl" /]
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro tableFlagshipsMacro ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th> [@s.text name="annualReport.externalPartnerships.table.flagship" /] </th>
          <th> [@s.text name="annualReport.externalPartnerships.table.externalPartnerships" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if false]
          [#list flagships as liaisonInstitution]
            <tr>
              <td>[#-- <span class="programTag" style="border-color:${(liaisonInstitution.crpProgram.color)!'#fff'}">${liaisonInstitution.crpProgram.acronym}</span> --]</td>
              <td>[#if false] ${liaisonInstitution.externalPartnerships} [#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
            </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="3"><i>[@s.text name="annualReport.externalPartnerships.table.void" /]</i></td>
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]