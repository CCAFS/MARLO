[#ftl]
[#assign title = "Locations" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = [ "select2", "flat-flags" ] /]
[#assign customJS = [ "${baseUrl}/js/admin/locations.js","${baseUrl}/js/global/fieldsValidation.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/admin/locations.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "locations" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"locations", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="crpLocations.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data"]
        
        <h4 class="sectionTitle">[@s.text name="crpLocations.title" /]</h4>
        [#-- Default locations --]
        <div class="defaultLocations simpleBox">
          [#list defaultLocationTypes as elementType]
            [#-- <p><span class="glyphicon glyphicon-ok-circle"></span> ${elementType.name}</p> --]
            <p><span class="glyphicon glyphicon-ok-circle"></span> ${elementType.name}</p>
          [/#list]
          <div class="clearfix"></div>
        </div>
        
        <h4 class="sectionTitle">[@s.text name="crpLocations.customize"] [@s.param]${(crpSession?upper_case)!}[/@s.param] [/@s.text]</h4>
        <div class="locationsBlock" listname="loggedCrp.locationElementTypes">
          [#-- Locations Levels List --]
          <div class="locations-list">
          [#if loggedCrp.locationElementTypes??]
            [#list loggedCrp.locationElementTypes as level]
              [@locationLevelMacro locLevel=level name="loggedCrp.locationElementTypes" index=level_index locationType=true  /]
            [/#list]
          [/#if]
          </div>
          [#-- Add Location Level Button --]
          [#if editable]
            <div class="addLocationLevel type-location bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addLocationLevel"/]</div>
          [/#if]
        </div>
        
        <h4 class="sectionTitle">[@s.text name="crpLocations.customizeScopes"] [@s.param]${(crpSession?upper_case)!}[/@s.param] [/@s.text]</h4>
        <div class="scopesBlock" listname="loggedCrp.locationElementTypes">
          [#-- Scopes/Regions List --]
          <div class="scopes-list">
          [#if loggedCrp.locationCustomElementTypes??]
            [#list loggedCrp.locationCustomElementTypes as level]
              [@locationLevelMacro locLevel=level name="loggedCrp.locationCustomElementTypes" index=level_index locationType=false  /]
            [/#list]
          [/#if]
          </div>
          [#-- Add Location Level Button --]
          [#if editable]
            <div class="addLocationLevel type-scope bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addScopeLevel"/]</div>
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
[@locationLevelMacro locLevel={} name="loggedCrp.locationElementTypes" index=-1 isTemplate=true /]

[#-- Scope/Region Level Template --]
[@locationLevelMacro locLevel={} name="loggedCrp.locationCustomElementTypes" index=-1 isTemplate=true locationType=false /]

<ul style="display:none">
  [#-- Location Element Template --]
  [@locElementMacro element={} name="loggedCrp.locationElementTypes[-1].locationElements" index=-1 isTemplate=true /]
  [#-- Country Element Template --]
  [@locElementMacro element={} name="loggedCrp.locationCustomElementTypes[-1].locationElements" index=-1 isTemplate=true locationType=false /]
</ul>

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro locationLevelMacro locLevel name index isTemplate=false locationType=true ]
  <div id="locationLevel-${locationType?string('location', 'scope')}-${isTemplate?string('template', index)}" class="locationLevel borderBox" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]" ]
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
      [@customForm.input name="${customName}.name" value="${(locLevel.name)!}" type="text"  i18nkey="location.levelName${locationType?string('','Scope')}" placeholder="location.inputName.placeholder" className="locationName" required=true editable=editable /]
    </div>
    <div class="form-group">
      [#if locationType]
        [#-- Does this location level have specific coordinates?   --]
        [@customForm.yesNoInput name="${customName}.hasCoordinates" label="location.question" editable=editable inverse=false value="${((locLevel.hasCoordinates)!false)?string}" cssClass="text-left" /]
        <div class="infoContent" >
          <div class="info-icon"><span class="glyphicon glyphicon-info-sign"></span></div>
          <div class="info-text"><span>If so, the locations added below will prefill a dropdown menu for Project Leader to pick from. Otherwise, Project Leaders will define by themselves.</span></div>
        </div>
      [#else]
        <input type="hidden" name="${customName}.hasCoordinates" value="false" />
      [/#if]
      <div class="clearfix"></div>
      [#-- confirm popup --]
      <div id="dialog-confirm"  style="display:none;">
        <p><span class="glyphicon glyphicon-warning-sign" style="float:left; margin:0 7px 20px 0;"></span> If you want to proceed with this action, <span class="locElements"></span> locations elements will be removed by the system.  </p>
      </div>
      [#-- Locations List --]
      <div class="aditional-hasCoordinates" style="display:${(((locLevel.hasCoordinates)!false) || !locationType)?string('block','none')}">
        <div class="items-list simpleBox" listname="${customName}.locationElements">
          <ul class="">
            [#if locLevel.locationElements?has_content]
              [#list locLevel.locationElements as locElement]
                [@locElementMacro element=(locElement)!{} name="${customName}.locationElements" index=locElement_index locationType=locationType /]
              [/#list]
            [#else] 
              <p class="message text-center">[@s.text name="location.notSpecificCoordinates${locationType?string('','Scope')}"/]</p>
            [/#if]
          </ul>
          <div class="clearfix"></div> 
          [#-- Add Location Element --]
          [#if editable]
            <hr />
            <div class="form-group">
            [#if locationType]
              <div class="latitudeBlock">[@customForm.input name="" type="text"  placeholder="location.inputLatitude.placeholder" showTitle=false className="locationLatitude-input" /]</div>
              <div class="longitudeBlock">[@customForm.input name="" type="text"  placeholder="location.inputLongitude.placeholder" showTitle=false className="locationLongitude-input" /]</div>
              <div class="nameBlock">[@customForm.input name="" type="text"  placeholder="location.inputLocationName.placeholder" showTitle=false className="locationName-input" /]</div>
              <div class="buttonBlock text-right"><div class="addLocElement button-blue"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addLocation"/]</div></div>
              <div class="clearfix"></div>
            [#else]
              [@customForm.select name=""  i18nkey="location.select.country" listName="countriesList" header=true keyFieldName="isoAlpha2" displayFieldName="name" value="id" className="countriesList"/]
            [/#if]
            </div>
          [/#if]
        </div>
      </div>
    </div> 
  </div>
[/#macro]

[#macro locElementMacro element name index isTemplate=false locationType=true]
  <li id="locElement-${locationType?string('location', 'scope')}-${isTemplate?string('template', index)}" class="locElement userItem" style="display:${isTemplate?string('none','block')}">
    [#assign locElementName = "${name}[${index}]" ]
    [#-- Remove Button --]
    [#if editable]
      <div class="removeLocElement removeIcon" title="Remove Location"></div>
    [/#if] 
    
    [#-- Location Name --]
    [#if locationType]
      <span class="glyphicon glyphicon-map-marker" aria-hidden="true"></span> <span class="name">${(element.name)!'{name}'}</span><br />
      <span class="coordinates" title="${(element.locElement.name)!'Undefined'}"> [@utilities.wordCutter string=(element.locElement.name)!'Undefined' maxPos=15 /] (${(element.locGeoposition.latitude)!}, ${(element.locGeoposition.longitude)!})</span>
    [#else]
      <span class="flag-icon"><i class="flag-sm flag-sm-${(element.locElement.isoAlpha2?upper_case)!}"></i></span> <span class="name">${(element.name)!'{name}'}</span><br />
    [/#if]
    [#-- Hidden inputs --]
    <input type="hidden" class="locElementId" name="${locElementName}.id" value="${(element.id)!}"/>
    <input type="hidden" class="locElementName" name="${locElementName}.name" value="${(element.name)!}" />
    <input type="hidden" class="locElementCountry" name="${locElementName}.locElement.isoAlpha2" value="${(element.locElement.isoAlpha2)!}" />
    <input type="hidden" class="geoId" name="${locElementName}.locGeoposition.id"  value="${(element.locGeoposition.id)!}" />
    <input type="hidden" class="geoLat" name="${locElementName}.locGeoposition.latitude"  value="${(element.locGeoposition.latitude)!}" />
    <input type="hidden" class="geoLng" name="${locElementName}.locGeoposition.longitude"  value="${(element.locGeoposition.longitude)!}" />
  </li>
[/#macro]

