[#ftl]
[#assign title = "Locations" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrl}/js/admin/locations.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/admin/locations.css" ] /]
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
        [#assign locationsLevels= [{'hasCoordinates': false}, {'hasCoordinates': true}] /]
        [#list locationsLevels as level]
          [@locationLevelMacro locLevel=level name="locationsLevels" index=level_index  /]
        [/#list]
        
        [#-- Add Location Level Button --]
        <div class="addLocationLevel bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add a Location Level</div>
        
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

[#macro locationLevelMacro locLevel name index isTemplate=false ]
  <div id="locationLevel-${index}" class="locationLevel borderBox">
    [#assign customName = "${name}[${index}]" ]
    [#-- Index Button --]
    <div class="leftHead sm">
      <span class="index">${index+1}</span>
    </div>
    [#-- Remove Button --]
    <div class="removeOutcome removeElement" title="Remove Outcome"></div>
    [#-- Location level name --]
    <div class="form-group">
      [@customForm.input name="${customName}.name" type="text"  i18nkey="Location Level Name" placeholder="Name" className="locationName" required=true editable=true /]
    </div>
    <div class="form-group">
      [#-- Does this location level have specific coordinates?   --]
      [@customForm.yesNoInput name="${customName}.hasCoordinates" label="Does this location level have specific coordinates? " editable=true inverse=false value="${((locLevel.hasCoordinates)!false)?string}" cssClass="text-left" /]
      [#-- Locations List --]
      <div class="aditional-hasCoordinates  simpleBox" style="display:${((locLevel.hasCoordinates)!false)?string('block','none')}">
        <div class="items-list">
          <ul class="">
            <li class="userItem">
              <span class="glyphicon glyphicon-map-marker" aria-hidden="true"></span>
              Nyando
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
[/#macro]

