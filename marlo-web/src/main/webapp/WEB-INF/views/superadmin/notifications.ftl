[#ftl]
[#assign title = "MARLO Admin" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrl}/js/superadmin/notifications.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/superadmin/notifications.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "notifications" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"notifications", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
      
        <h4 class="sectionTitle">System Reset</h4>
        <div class="borderBox ">
         <div class="form-group">
          [@customForm.textArea name="" i18nkey="notifications.systemReset.message" placeholder="notifications.systemReset.message.placeholder" required=true className="systemReset-message" /]
         </div>
         <div class="form-group">
          [@customForm.input name="" i18nkey="notifications.systemReset.diffTime" placeholder="notifications.systemReset.diffTime.placeholder" required=true className="systemReset-diffTime" /]
         </div>
         <div class="form-group">
          
         </div>
        </div>

      </div>
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]

