[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "blueimp-file-upload" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/powb/powb_collaborationIntegration.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/powb/powbGlobal.css" ] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "collaborationIntegration" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"collaborationIntegration", "nameSpace":"powb", "action":"${crpSession}/collaborationIntegration"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="collaborationIntegration.help" /]
    
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
        <h3 class="headTitle">[@s.text name="collaborationIntegration.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h3>
        <div class="borderBox">
          
          [#-- 2.3.1  New Key External Partnerships  --] 
          <div class="form-group">
            [@customForm.textArea  name="powbSynthesis.collaborationIntegration.partnerships" i18nkey="powbSynthesis.collaborationIntegration.partnerships" help="powbSynthesis.collaborationIntegration.partnerships.help" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- 2.3.2  New Contribution to and from Platforms --] 
          <div class="form-group">
            [@customForm.textArea  name="powbSynthesis.collaborationIntegration.platformsContributions" i18nkey="powbSynthesis.collaborationIntegration.platformsContributions" help="powbSynthesis.collaborationIntegration.platformsContributions.help" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- 2.3.3  New Cross-CRP Interactions --] 
          <div class="form-group">
            [@customForm.textArea  name="powbSynthesis.collaborationIntegration.crpInteractions" i18nkey="powbSynthesis.collaborationIntegration.crpInteractions" help="powbSynthesis.collaborationIntegration.crpInteractions.help" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- 2.3.4  Expected Efforts on Country Coordination --] 
          <div class="form-group">
            [@customForm.textArea  name="powbSynthesis.collaborationIntegration.expectedEfforts" i18nkey="powbSynthesis.collaborationIntegration.expectedEfforts" help="powbSynthesis.collaborationIntegration.expectedEfforts.help" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable /]
          </div>
          
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]

[#---------------------------------------------- MACROS ----------------------------------------------]

