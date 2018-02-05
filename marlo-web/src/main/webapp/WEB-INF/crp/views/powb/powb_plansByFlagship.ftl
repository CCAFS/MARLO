[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "blueimp-file-upload" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/powb/powb_plansByFlagship.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/powb/powbGlobal.css" ] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "plansByFlagship" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"plansByFlagship", "nameSpace":"powb", "action":"${crpSession}/plansByFlagship"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="plansByFlagship.help" /]
    
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
        <h3 class="headTitle">[@s.text name="plansByFlagship.title" /]</h3>
        <div class="borderBox">
          
          [#-- Summarize the plans for each flagship in 2018 --] 
          <div class="form-group">
            [@customForm.textArea  name="powbSynthesis.powbFlagshipPlans.planSummary" i18nkey="liaisonInstitution.powb.planSummary" help="liaisonInstitution.powb.planSummary.help" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- If major changes have been made to your flagship since the CRP proposal was published, please annex a brief summary of the current flagship program with the updated theory of change. --]
          <div class="form-group" style="position:relative" listname="">
            [@customForm.fileUploadAjax 
              fileDB=(flagshipProgramFile)!{} 
              name="flagshipProgramFile.id" 
              label="liaisonInstitution.powb.flagshipProgramFile" 
              dataUrl="${baseUrl}/UPLOAD_SERVICE_HERE.do" 
              path="${(action.getPath(liaisonInstitutionID))!''}"
              isEditable=editable
            /]
          </div>
        
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]