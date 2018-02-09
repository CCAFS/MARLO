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
            [@customForm.textArea  name="powbSynthesis.powbFlagshipPlans.planSummary" i18nkey="powbSynthesis.crpStaffing.staffingIssues" help="powbSynthesis.crpStaffing.staffingIssues.help" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable /]
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

