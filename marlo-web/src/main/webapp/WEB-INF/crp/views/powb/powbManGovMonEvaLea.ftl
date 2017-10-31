[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "manGovMonEvaLea" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/delivery"},
  {"label":"powbManGovMonEvaLea", "nameSpace":"powb", "action":"${crpSession}/manGovMonEvaLea"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="powbManGovMonEvaLea.help" /]
    
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
        <h3 class="headTitle">[@s.text name="powbManGovMonEvaLea.title" /]</h3>
        <div class="borderBox">
        
          [#-- A 3.1 Relevant Changes in Management and Governance--] 
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.relevantChanges" help="liaisonInstitution.powb.relevantChanges.help" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- A 3.2 Monitoring, Evaluation, Impact Assessment and Learning Plans --] 
          <div class="form-group">
            [@customForm.textArea name="liaisonInstitution.powb.MonEvaImpAssLeaPlans" help="liaisonInstitution.powb.MonEvaImpAssLeaPlans.help" required=true className="limitWords-100" editable=editable /]
          </div>
        
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]