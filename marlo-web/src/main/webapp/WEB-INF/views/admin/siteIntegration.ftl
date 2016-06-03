[#ftl]
[#assign title = "Site Integration" /]
[#assign pageLibs = [] /]
[#assign customJS = ["${baseUrl}/js/admin/siteIntegration.js" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "siteIntegration" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"siteIntegration", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]  
        
        <h4 class="sectionTitle">Site Integration</h4>
        [#assign crpCountries = [{'name': 'CO'},{'name': 'CN'}] /]
        [#if crpCountries?has_content]
          [#list crpCountries as crpCountry]
            <div class="borderBox">
              ${crpCountry.name}
            </div>
          [/#list]
        [#else]
          <p class="text-center">There are not countries added yet.</p>
        [/#if] 
        
        <div class="buttons">
          [@s.submit type="button" name="save" cssClass=""][@s.text name="form.buttons.save" /][/@s.submit]
        </div>
        
        [/@s.form]
      </div>
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]