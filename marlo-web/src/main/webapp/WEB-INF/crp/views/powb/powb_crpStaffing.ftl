[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "blueimp-file-upload" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/powb/powb_crpStaffing.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/powb/powbGlobal.css" ] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "crpStaffing" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"crpStaffing", "nameSpace":"powb", "action":"${crpSession}/crpStaffing"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="crpStaffing.help" /]
    
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
        <h3 class="headTitle">[@s.text name="crpStaffing.title" /]</h3>
        <div class="borderBox">
          
          [#-- Briefly summarize any staffing issues or constraints relevant to CRP capacity --] 
          <div class="form-group">
            [@customForm.textArea  name="powbSynthesis.powbFlagshipPlans.planSummary" i18nkey="powbSynthesis.crpStaffing.staffingIssues" help="powbSynthesis.crpStaffing.staffingIssues.help" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- Table D: CRP Staffing (OPTIONAL IN POWB 2018)  --]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="crpStaffing.tableD.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            [@tableD /]
          </div>
        
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#if flagship]
          [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        [/#if]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]

[#---------------------------------------------- MACROS ----------------------------------------------]

[#macro tableD ]

  [#assign categories = [
    "Program director & flagship leaders",
    "Principal Investigators",
    "Other Senior Scientists (not PIs)",
    "Post-docs / junior scientists",
    "Research fellows",
    "Other science support staff",
    "TOTAL CRP"
    ] 
  /]

  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th>[@s.text name="crpStaffing.tableD.category" /]</th>
          <th class="col-md-2">[@s.text name="crpStaffing.tableD.female" /]</th>
          <th class="col-md-2">[@s.text name="crpStaffing.tableD.male" /]</th>
          <th>[@s.text name="crpStaffing.tableD.total" /]</th>
          <th>[@s.text name="crpStaffing.tableD.percFemale" /]</th>
        </tr>
      </thead>
      <tbody>
        [#if categories??]
          [#list categories as category]
            <tr>
              <td>${category}</td>
              <td> [@customForm.input name="powbSynthesis.crpStaffing.female" i18nkey="" showTitle=false className="currencyInput" required=true /]  </td>
              <td> [@customForm.input name="powbSynthesis.crpStaffing.male" i18nkey="" showTitle=false className="currencyInput" required=true /] </td>
              <td></td>
              <td></td>
            </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]