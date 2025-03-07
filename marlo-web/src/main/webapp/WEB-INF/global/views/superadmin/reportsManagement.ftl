[#ftl]
[#assign title = "Reports Management" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "trumbowyg"] /]
[#assign customJS = [ "${baseUrlCdn}/global/js/superadmin/reportsManagement.js?20240305",  "${baseUrlCdn}/global/js/fieldsValidation.js"
 ] /]
[#assign customCSS = [ "${baseUrlCdn}/global/css/superadmin/superadmin.css", "${baseUrlCdn}/global/css/superadmin/reportsManagement.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "tipManagement" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"reportsManagement", "nameSpace":"", "action":""}
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
      [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
    </div>
    <div class="col-md-9">
      [@s.form action=actionName enctype="multipart/form-data" ]
      
      [#-- System Level Outcomes --]
      <h4 class="sectionTitle">[@s.text name="reportsManagement.title" /]</h4>

      <div id="tipManagement" class="borderBox">
        <div class="form-row">
          <div class="form-group">
            [@customForm.textArea name="oicrTemplate" i18nkey="reportsManagement.oicr.template" className="description" required=true /]
          </div>
        </div>
    
      [#-- Section Buttons--]
      <div class="buttons">
        <div class="buttons-content">
          [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
        </div>
      </div>
      
      [/@s.form]
      
    </div>
  </div>
</div>
</section>


[#include "/WEB-INF/global/pages/footer.ftl" /]