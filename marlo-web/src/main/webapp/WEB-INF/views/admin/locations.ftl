[#ftl]
[#assign title = "Locations" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrl}/js/admin/locations.js" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "locations" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"locations", "nameSpace":"", "action":""}
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
        [@s.form action=actionName enctype="multipart/form-data"]
        
        <h4 class="sectionTitle">Locations</h4>
        <div class="locationsBlock">
        [#assign locationsLevels= [{}, {}] /]
        
        [#list locationsLevels as level]
          [#assign customName = "locationsLevels[${level_index}]" ]
          <div id="locationLevel-${level_index}" class="locationLevel borderBox">
            <div class="form-group">
              [@customForm.input name="${customName}.name" type="text"  i18nkey="Location Level Name" placeholder="Name" className="locationName" required=true editable=true /]
            </div>
            <div class="form-group">
              [#-- Does this location level have specific coordinates?   --]
              [@customForm.yesNoInput name="${customName}.hasCoordinates" label="Does this location level have specific coordinates? " editable=true inverse=false value="${(level.hasCoordinates?string)!'false'}" cssClass="text-left" /]
            </div>
            <div class="aditional-hasCoordinates simpleBox">
              asd
            </div>
          </div>
        [/#list]
          
        </div>
        
        [#-- Save Button --]
        <div class="buttons">
          [@s.submit type="button" name="save" cssClass=""][@s.text name="form.buttons.save" /][/@s.submit]
        </div>
        
        [/@s.form]
      </div>
    </div>
  </div>
</section>


[#include "/WEB-INF/global/pages/footer.ftl" /]
