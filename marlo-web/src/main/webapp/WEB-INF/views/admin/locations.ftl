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
          [#-- Locations Levels List --]
          <div class="locations-list">
            [#assign locationsLevels= [
                {'hasCoordinates': false}, 
                {'hasCoordinates': true, 'locElements':[{},{}] }
              ] 
            /]
            [#list locationsLevels as level]
              [@locationLevelMacro locLevel=level name="locationsLevels" index=level_index  /]
            [/#list]
          </div>
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


[#-- Location Level Template --]
[@locationLevelMacro locLevel={} name="" index=0 isTemplate=true /]

<ul style="display:none">
  [#-- Location Element Template --]
  [@locElementMacro element={} name="" index=0 isTemplate=true /]
</ul>

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro locationLevelMacro locLevel name index isTemplate=false ]
  <div id="locationLevel-${isTemplate?string('template', index)}" class="locationLevel borderBox" style="display:${isTemplate?string('none','block')}">
    [#assign customName = "${name}[${index}]" ]
    [#-- Index Button --]
    <div class="leftHead sm">
      <span class="index">${index+1}</span>
    </div>
    [#-- Remove Button --]
    <div class="removeLocationLevel removeElement" title="Remove Location Level"></div>
    [#-- Location Level ID Hidden parameter --]
    <input type="hidden" class="locationLevelId" name="${customName}.id" value="${(locLevel.id)!}"/>
    [#-- Location level name --]
    <div class="form-group">
      [@customForm.input name="${customName}.name" type="text"  i18nkey="Location Level Name" placeholder="Name" className="locationName" required=true editable=true /]
    </div>
    <div class="form-group">
      [#-- Does this location level have specific coordinates?   --]
      [@customForm.yesNoInput name="${customName}.hasCoordinates" label="Does this location level have specific coordinates? " editable=true inverse=false value="${((locLevel.hasCoordinates)!false)?string}" cssClass="text-left" /]
      [#-- Locations List --]
      <div class="aditional-hasCoordinates" style="display:${((locLevel.hasCoordinates)!false)?string('block','none')}">
        <div class="items-list simpleBox">
          <ul class="">
            [#if locLevel.locElements?has_content]
              [#list locLevel.locElements as locElement]
                [@locElementMacro element=locElement name="${customName}.locElements" index=locElement_index /]
              [/#list]
            [#else] 
              <p class="message text-center">There is not specific coordinates yet.</p>
            [/#if]
          </ul>
          [#-- Add Location Element --]
          <hr />
          <div class="form-group">
            <div class="latitudeBlock">[@customForm.input name="" type="text"  placeholder="Latitude" showTitle=false className="locationLatitude-input" /]</div>
            <div class="longitudeBlock">[@customForm.input name="" type="text"  placeholder="Longitude" showTitle=false className="locationLongitude-input" /]</div>
            <div class="nameBlock">[@customForm.input name="" type="text"  placeholder="Location Name" showTitle=false className="locationName-input" /]</div>
            <div class="buttonBlock text-right"><div class="addLocElement button-blue"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add Location</div></div>
            <div class="clearfix"></div>
          </div>
        </div>
      </div>
    </div> 
  </div>
[/#macro]

[#macro locElementMacro element name index isTemplate=false]
  <li id="locElement-${isTemplate?string('template', index)}" class="locElement userItem" style="display:${isTemplate?string('none','block')}">
    [#assign locElementName = "${name}[${index}]" ]
    [#-- Location Name --]
    <span class="glyphicon glyphicon-map-marker" aria-hidden="true"></span> <span class="name">${(element.name)!'{name}'}</span>
    <input type="hidden" class="locElementId" name="${locElementName}.id" value="${(element.id)!}"/>
    <input type="hidden" class="locElementName" name="${locElementName}.name" value="${(element.name)!}" />
    <input type="hidden" class="GeoId" name="${locElementName}.locGeoposition.id"  value="${(element.locGeoposition.id)!}" />
    <input type="hidden" class="GeoLat" name="${locElementName}.locGeoposition.latitude"  value="${(element.locGeoposition.latitude)!}" />
    <input type="hidden" class="GeoLng" name="${locElementName}.locGeoposition.longitude"  value="${(element.locGeoposition.longitude)!}" />
  </li>
[/#macro]

