[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "adaptativeManagement" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/summaryHighlight"},
  {"label":"adaptativeManagement", "nameSpace":"powb", "action":"${crpSession}/adaptativeManagement"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="adaptativeManagement.help" /]
    
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
        <h3 class="headTitle">[@s.text name="adaptativeManagement.title" /]</h3>
        <div class="borderBox">
        
          [#-- CRP Management and governance (up to 1/4 page) --] 
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.managementGovernance" help="liaisonInstitution.powb.managementGovernance.help" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- Monitoring, Evaluation, Impact Assessment and Learning (up to ½ page) --] 
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.melInitiatives" help="liaisonInstitution.powb.melInitiatives.help" required=true className="limitWords-300" editable=editable /]
          </div>
          
          [#-- Adjustments/ changes to your Theories of Change (up to ½ page) --] 
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.plannedAdjusments" help="liaisonInstitution.powb.plannedAdjusments.help" required=true className="limitWords-300" editable=editable /]
          </div>
          
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]