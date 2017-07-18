[#ftl]
[#assign title = "MARLO Admin" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrlMedia}/js/superadmin/notifications.js" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "notifications" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"notifications", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/center//global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/center//global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/center//global/pages/generalMessages.ftl" /]


<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/center//views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
      
        <h4 class="sectionTitle">System Reset</h4>
        <div id="systemReset" class="borderBox ">
         <div class="form-group">
          [@customForm.textArea name="" i18nkey="notifications.systemReset.message" placeholder="notifications.systemReset.message.placeholder" required=true className="systemReset-message" /]
         </div>
         <div class="form-group">
           <div class="row">
           	<div class="col-md-4">
              [@customForm.input name="" i18nkey="notifications.systemReset.diffTime" placeholder="notifications.systemReset.diffTime.placeholder" required=true className="systemReset-diffTime" /]
           	</div>
           </div>
         </div>
         <br />
         <div class="form-group">
          <button type="button" class="btn btn-primary">[@s.text name="form.buttons.sendMessage" /]</button>
         </div>
        </div>
        
        <h4 class="sectionTitle">Simple Message</h4>
        <div id="simpleNotification" class="borderBox ">
         <div class="form-group">
          [@customForm.textArea name="" i18nkey="notifications.simpleNotification.message" placeholder="notifications.simpleNotification.message.placeholder" required=true className="simpleNotification-message" /]
         </div>
         <br />
         <div class="form-group">
          <button type="button" class="btn btn-primary">[@s.text name="form.buttons.sendMessage" /]</button>
         </div>
        </div>

      </div>
    </div>
  </div>
</section>


[#include "/WEB-INF/center//global/pages/footer.ftl" /]