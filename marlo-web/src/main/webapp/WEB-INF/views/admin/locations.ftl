[#ftl]
[#assign title = "Locations" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
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
    <div class="helpMessage"><img src="${baseUrl}/images/global/icon-help.png" /><p>[@s.text name="crpLocations.help" /] </p></div> 
  </div>

  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data"]
        
        <h4 class="sectionTitle">[@s.text name="crpLocations.title" /]</h4>
        <div class="defaultLocations simpleBox">
          [#list defaultLocationTypes as elementType]
            <p><span class="glyphicon glyphicon-ok-circle"></span> ${elementType.name}</p>
          [/#list]
          <div class="clearfix"></div>
        </div>
        
        <h4 class="sectionTitle">[@s.text name="crpLocations.customize"] [@s.param]${(crpSession?upper_case)!}[/@s.param] [/@s.text]</h4>
        <div class="locationsBlock">
          [#-- Locations Levels List --]
          <div class="locations-list">            
            [#list loggedCrp.locationElementTypes as level]
              [@locationLevelMacro locLevel=level name="loggedCrp.locationElementTypes" index=level_index  /]
            [/#list]
          </div>
          [#-- Add Location Level Button --]
          [#if editable]
            <div class="addLocationLevel bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addLocationLevel"/]</div>
          [/#if]
        </div>
        
        [#-- Section Buttons--]
        <div class="buttons">
          <div class="buttons-content">
          [#if editable]
            <a href="[@s.url][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> [@s.text name="form.buttons.back" /]</a>
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
          [#else]
            [#if canEdit]
              <a href="[@s.url][@s.param name="edit" value="true"/][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
            [/#if]
          [/#if]
          </div>
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
    [#if editable]
    <div class="removeLocationLevel removeElement" title="Remove Location Level"></div>
    [/#if]
    [#-- Location Level ID Hidden parameter --]
    <input type="hidden" class="locationLevelId" name="${customName}.id" value="${(locLevel.id)!}"/>
    [#-- Location level name --]
    <div class="form-group">
      [@customForm.input name="${customName}.name" type="text"  i18nkey="location.levelName" placeholder="location.inputName.placeholder" className="locationName" required=true editable=editable /]
    </div>
    <div class="form-group">
      [#-- Does this location level have specific coordinates?   --]
      [@customForm.yesNoInput name="${customName}.hasCoordinates" label="location.question" editable=editable inverse=false value="${((locLevel.hasCoordinates)!false)?string}" cssClass="text-left" /]
      <div class="infoContent" >
        <div class="info-icon"><span class="glyphicon glyphicon-info-sign"></span></div>
        <div class="info-text"><span>If so, the locations added below will prefill a dropdown menu for Project Leader to pick from. Otherwise, Project Leaders will define by themselves.</span></div>
      </div>
      <div class="clearfix"></div>
      [#-- confirm popup --]
      <div id="dialog-confirm"  style="display:none;">
        <p><span class="glyphicon glyphicon-warning-sign" style="float:left; margin:0 7px 20px 0;"></span> If you want to proceed with this action, <span class="locElements"></span> locations elements will be removed by the system.  </p>
      </div>
      [#-- Locations List --]
      <div class="aditional-hasCoordinates" style="display:${((locLevel.hasCoordinates)!false)?string('block','none')}">
        <div class="items-list simpleBox">
          <ul class="">
            [#if locLevel.locationElements?has_content]
              [#list locLevel.locationElements as locElement]
                [@locElementMacro element=locElement name="${customName}.locationElements" index=locElement_index /]
              [/#list]
            [#else] 
              <p class="message text-center">[@s.text name="location.notSpecificCoordinates.span"/]</p>
            [/#if]
          </ul>
          <div class="clearfix"></div> 
          [#-- Add Location Element --]
          [#if editable]
          <hr />
          <div class="form-group">
            <div class="latitudeBlock">[@customForm.input name="" type="text"  placeholder="location.inputLatitude.placeholder" showTitle=false className="locationLatitude-input" /]</div>
            <div class="longitudeBlock">[@customForm.input name="" type="text"  placeholder="location.inputLongitude.placeholder" showTitle=false className="locationLongitude-input" /]</div>
            <div class="nameBlock">[@customForm.input name="" type="text"  placeholder="location.inputLocationName.placeholder" showTitle=false className="locationName-input" /]</div>
            <div class="buttonBlock text-right"><div class="addLocElement button-blue"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addLocation"/]</div></div>
            <div class="clearfix"></div>
          </div>
          [/#if]
        </div>
      </div>
    </div> 
  </div>
[/#macro]

[#macro locElementMacro element name index isTemplate=false]
  <li id="locElement-${isTemplate?string('template', index)}" class="locElement userItem" style="display:${isTemplate?string('none','block')}">
    [#assign locElementName = "${name}[${index}]" ]
    [#-- Remove Button --]
    [#if editable]
      <div class="removeLocElement removeIcon" title="Remove Location"></div>
    [/#if]
    [#-- Location Name --]
    <span class="glyphicon glyphicon-map-marker" aria-hidden="true"></span> <span class="name">${(element.name)!'{name}'}</span><br />
    <span class="coordinates">${(element.locElement.name)!} (${(element.locGeoposition.latitude)!}, ${(element.locGeoposition.longitude)!})</span>
    [#-- Hidden inputs --]
    <input type="hidden" class="locElementId" name="${locElementName}.id" value="${(element.id)!}"/>
    <input type="hidden" class="locElementName" name="${locElementName}.name" value="${(element.name)!}" />
    <input type="hidden" class="locElementCountry" name="${locElementName}.locElement.isoAlpha2" value="${(element.locElement.isoAlpha2)!}" />
    <input type="hidden" class="geoId" name="${locElementName}.locGeoposition.id"  value="${(element.locGeoposition.id)!}" />
    <input type="hidden" class="geoLat" name="${locElementName}.locGeoposition.latitude"  value="${(element.locGeoposition.latitude)!}" />
    <input type="hidden" class="geoLng" name="${locElementName}.locGeoposition.longitude"  value="${(element.locGeoposition.longitude)!}" />
  </li>
[/#macro]

