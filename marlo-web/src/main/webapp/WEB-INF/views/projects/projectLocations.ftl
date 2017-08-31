[#ftl]
[#assign title = "Project Locations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrlMedia}/js/projects/projectLocations.js", "${baseUrlMedia}/js/global/autoSave.js","${baseUrlMedia}/js/global/fieldsValidation.js"] /] [#-- "${baseUrl}/js/global/autoSave.js" --]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectLocations.css" ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "locations" /]
[#assign hideJustification = false /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectLocations", "nameSpace":"/projects", "action":""}
] /]

[#assign locationLevelName = "project.locationsData"/]
[#assign locationName = "locElements"/]
[#assign countID = 0/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

<div class="container helpText viewMore-block">
  <div  class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlMedia}/images/global/icon-help.jpg" />
    <p class="col-md-10">[#if reportingActive] [@s.text name="projectLocations.help2" /] [#else] [@s.text name="projectLocations.help1" /] [/#if] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
                        <input class="projectInfo" type="hidden" name="project.projectInfo.id" value="${project.projectInfo.id}" />
           
          <p class="bg-primary" style="padding: 18px; display:none;">
            <span class="glyphicon glyphicon-flash"></span>
              We are re-vamping this section in order to make it more user-friendly. 
              Please apologies if something is not properly working. It would be great if you can inform us about any issue.
          </p>
          
          <div class="row">
          <h3 class="headTitle col-md-7">[@s.text name="projectLocations.title" /]</h3>  
          </div>
          <div id="" class="borderBox projectLocationsWrapper">
            [#-- Content--]
              <div class="row">
                <div class="col-md-12">
                <b>NOTE: </b>
                <br />
                <div class="note left">
                  <div id="popup" class="helpMessage3">
                  </div>
                  <p><small>[@s.text name="projectLocations.note" /] </small></p>
                </div>
                [#-- Hide map hint depending on 'crp_other_locations' parameter --]
                [#if action.hasSpecificities('crp_other_locations')]
                <span><span><img style="width: 3%;" src="${baseUrlMedia}/images/global/left-click.jpg" alt="" /></span>Left click to get detailed information of a specific location.</span>
                [/#if]
                <br />
                <br />
                [#-- <span><span><img style="width: 3%;" src="${baseUrlMedia}/images/global/right-click.jpg" alt="" /></span>Right click in the map to add a new location.</span> --]
                </div>
                [#-- Hide map depending on 'crp_other_locations' parameter --]
                [#if action.hasSpecificities('crp_other_locations')]
                <div  class="col-md-12 map">
                  <div id="map" class="col-md-12"></div>
                </div>
                [/#if]
                
                [#-- GLOBAL DIMENSION --]
                <div class="form-group  col-md-12">
                  [@customForm.yesNoInput  label="projectLocations.globalDimension" name="project.locationGlobal"  editable=editable && action.hasSpecificities("crp_other_locations") inverse=false  cssClass="" /] 
                </div>
                <br />
                <div class="form-group col-md-12 ">
                  <hr />
                </div>
                <div class="form-group col-md-12">
                  [@customForm.yesNoInput  label="projectLocations.regionalDimension" name="project.locationRegional"   editable=editable && action.hasSpecificities("crp_other_locations") inverse=false  cssClass="isRegional" /]
                  [#if editable && action.hasSpecificities("crp_other_locations")]
                    <small style="color: #337ab7;">[@s.text name="projectLocations.regionsNote" /] </small>
                  [/#if]
                </div>
                 [#-- RECOMMENDED LOCATIONS --]
                 <div class="col-md-12">
                  <label for="">[@s.text name="projectLocations.locationsBelow" /]:</label>
                  <div class="simpleBox col-md-12">
                  <div class="row recommendedList">
                    [#-- RECOMMENDED REGIONS LIST --]
                    [#if project.regionFS?has_content]
                    <div class="regionsContent" style="display:${(project.locationRegional?string("block","none"))!"none"};">
                      <div class="col-md-12" >
                        <h5 class="sectionSubTitle">Suggested Regions:</h5>
                      </div>
                      [#list project.regionFS as location]
                        [@recommendedLocation element=location name="project.regionFS" index=location_index template=false /]
                      [/#list]
                    </div>
                    [#else]
                      [#assign recommendedRegions=0]
                    [/#if]
                    [#-- RECOMMENDED COUNTRIES LIST --]
                    [#if project.countryFS?has_content]
                      <div class="col-md-12">
                        <h5 class="sectionSubTitle">Suggested Countries:</h5>
                      </div>
                      [#list project.countryFS as location]
                        [@recommendedLocation element=location name="project.countryFS" index=location_index template=false /]
                      [/#list]
                    [#else]
                      [#assign recommendedCountries=0]
                    [/#if]
                    [#if recommendedCountries?? && recommendedCountries==0 && recommendedRegions?? && recommendedRegions==0]
                      <p class="text-center inf">There is not locations recommended</p>
                    [/#if]
                  </div>
                  </div>
                 </div>
                [#-- OTHER LOCATIONS LABEL --]   
                [#if action.hasSpecificities('crp_other_locations')]
                  <div class="col-md-12">
                  <h5 class="sectionSubTitle">[@s.text name="projectLocations.otherLocations" /]</h5>
                  </div>    
                      
                  [#-- REGIONS SELECT --]
                  <div class="row">
                  <div class="regionsBox form-group col-md-12" style="display:${(project.locationRegional?string("block","none"))!"none"};">
                    <div class="panel tertiary col-md-12">
                     <div class="panel-head">
                       <label for=""> [@customForm.text name="projectCofunded.selectRegions" readText=!editable /]:[@customForm.req required=editable /]</label>
                       <br />
                       <small style="color: #337ab7;">([@s.text name="projectLocations.standardLocations" /])</small>
                     </div>
                     
                      <div id="regionList" class="panel-body" listname="project.projectRegions"> 
                        <ul class="list">
                        [#if project.projectRegions?has_content]
                          [#list project.projectRegions as region]
                              <li id="" class="region clearfix col-md-3">
                              [#if editable ]
                                <div class="removeRegion removeIcon" title="Remove region"></div>
                              [/#if]
                                <input class="id" type="hidden" name="project.projectRegions[${region_index}].id" value="${region.id}" />
                               [#if region.locElement?has_content ]
                               <span class="name" title="${(region.locElement.name)!}">[@utilities.wordCutter string=(region.locElement.name)!'No name' maxPos=20 /]</span>
                                <input class="regionScope" type="hidden" name="project.projectRegions[${region_index}].scope" value="${(region.locElement.locElementType.scope?c)!}" />
                                <input class="rId" type="hidden" name="project.projectRegions[${region_index}].locElement.id" value="${(region.locElement.id)!}" />
                                  [#else]
                                   <span class="name" title="${(region.locElementType.name)!}">[@utilities.wordCutter string=(region.locElementType.name)!'No name' maxPos=20 /]</span>
                                <input class="regionScope" type="hidden" name="project.projectRegions[${region_index}].scope" value="${(region.locElementType.scope?c)!}" />
                              <input class="rId" type="hidden" name="project.projectRegions[${region_index}].locElementType.id" value="${(region.locElementType.id)!}" />
                              [/#if]
                             
                                
                                <div class="clearfix"></div>
                              </li>
                          [/#list]
                          [#else]
                          <p class="emptyText"> [@s.text name="No regions added yet." /]</p> 
                        [/#if]
                        </ul>
                        [#if editable ]
                          <select name="" id="regionSelect" class="regionsSelect">
                            <option value="-1">[@s.text name="form.select.placeholder" /]</option>
                            [#if scopeRegionLists?has_content]
                              <optgroup label="${(loggedCrp.acronym?upper_case)!} regions">
                              [#list scopeRegionLists as region]
                              <option value="${(region.id)!}-${(region.scope?c)!}">${(region.name)!}</option>
                              [/#list]
                              </optgroup>
                            [/#if]
                            [#if regionLists?has_content]
                            <optgroup label="[@s.text name="projectLocations.unStandard" /]">
                              [#list regionLists as region]
                              <option value="${(region.id)!}-${(region.locElementType.scope?c)!}">${(region.name)!}</option>
                              [/#list]
                              </optgroup>
                            [/#if]
                          </select>
                        [/#if] 
                      </div>
                    </div>
                  </div>
                  </div>
                  [#-- LOCATION LIST --]
                  <div class="col-md-12">
                  <label for="">Locations list</label>
                  <div id="selectsContent" class="col-md-12 simpleBox " listname="project.locationsData">
                    [#-- Content collapsible--]
                    <div class="selectWrapper row">
                      [#if project.locationsData?has_content]
                        [#list project.locationsData as locationLevels]
                          [@locationLevel element=locationLevels name="${locationLevelName}" index=locationLevels_index list=locationLevels.list?? && locationLevels.list /]
                        [/#list]
                      [#else]
                      <p class="text-center borderBox inf">No locations has been added, please use the map to add new locations.</p>
                      [/#if]
                    </div>
                  </div>
                  </div>
                [/#if]
              </div>
              
            [#--[#if editable]
             locations level Select 
            <label for="locLevelSelect">Select to add a location level:</label>
            <select name="" id="locLevelSelect" class="selectLocationLevel select " >
            <option value="-1" >Select an option...</option>
              [#list locationsLevels as locLevels]
                [#list locLevels.locations as locations]
                  <option value="${locations.id}-${locations.list?string}-${locations.name}" >${locations.name}</option>
                [/#list]
              [/#list]
            </select>
            [/#if]
            --]
          </div> 
          
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
         
          [/@s.form] 
      </div>
    </div>  
</section>

[#--<script src="https://maps.googleapis.com/maps/api/js?key=${config.googleApiKey}&callback=initMap"></script>--]

[#-- Section hidden inputs--]
[@locationLevel element={} name="${locationLevelName}" index=-1 template=true /]

[@locationMacro element={} name="${locationLevelName}[-1].${locationName}" index=-1 template=true /]

[@recommendedLocation element={} name="${locationLevelName}.${locationName}" index=-1 template=true /]

<input type="hidden" id="locationLevelName" value="${locationLevelName}" />
<input type="hidden" id="locationName" value="${locationName}" />

[#-- INFO WINDOW FORM - TEMPLATE --]
<div id="infoWrapper" class="infoWrapper" style="display:none;">
<h4 class="sectionSubTitle" style="text-align:center; width:450px;">Adding a new location</h4>
<br />
<div class="form-group">
<label for="locLevelSelect" style="display:block;">Select a location level:</label>
  <select name="" id="locLevelSelect"  class="selectLocationLevel select " >
    <option value="-1" >Select an option...</option>
    [#list locationsLevels as locLevels]
      [#list locLevels.locations as locations]
        <option value="${locations.id}-${locations.list?string}-${locations.name}" >${locations.name}</option>
      [/#list]
    [/#list]
  </select>
  </div>
  <div class="selectLocations" >
  <label for="">Select location(s)</label>
  <select name="" data-placeholder="Click here to drop down the options" id="countriesCmvs" multiple="true"></select>
  </div>
  [#-- Form 2 --]
  <div id="inputFormWrapper" style="display:none; width: 450px;">
    <div class="nameWrapper"><label for="">Location name:</label><input placeholder="name (Required)" class="name form-control" type="text" /></div>
    <div class="latitudeWrapper"><label for="">Latitude:</label><input placeholder="Latitude" class="latitude form-control" type="text" value="" /></div>
    <div class="longitudeWrapper"><label for="">Longitude:</label><input placeholder="Longitude" class="longitude form-control " type="text"  value=""/></div>
  </div>
  [#-- Button --]
  <div style="margin-left:10px; float:right;">
    <span id="cancelButton" class=" cancelButton pull-right" style="display:block; margin-top:10px; border-radius:8px;">[@s.text name="Cancel" /]</span>
  </div>
  <div>
    <span id="addLocationButton" class=" addButton pull-right" style="display:none; margin-top:10px; border-radius:8px;">[@s.text name="Drop pin" /]</span>
  </div>
</div>

[#-- INFO WINDOW Information editable- TEMPLATE --]
<div id="informationWrapper" class="infoWrapper" style="display:none;">
<h4 class="sectionSubTitle" style="text-align:center; width:450px;">Location Information</h4>
<br />
  [#-- Form to editable location --]
  <div class="editableLoc" >
    <div class="form-group">
    <label for="">Location level:</label> 
    <br />
    <span class="infoLocName">{test}</span>
    </div>    
    <div id="inputFormWrapper" style="width: 450px;">
      <div class="nameWrapper"><label for="">Change name:</label><input placeholder="name" class="nameMap form-control" type="text" value=""/></div>
      <div class="latitudeWrapper"><label for="">Latitude:</label><input  placeholder="Latitude" class="latMap form-control" type="text" value="" /></div>
      <div class="longitudeWrapper"><label for="">Longitude:</label><input placeholder="Longitude" class="lngMap form-control " type="text" value="" /></div>
    </div>
  </div>
  [#-- Button --]
  <div>
    <span id="changeLocation" class=" addButton pull-right" style="margin-top:15px; border-radius:8px;">[@s.text name="Ok" /]</span>
  </div>
</div>

[#-- INFO WINDOW Information dont editable- TEMPLATE --]
<div id="notEditableInfoWrapper" class="infoWrapper" style="display:none;">
<h4 class="sectionSubTitle" style="text-align:center; width:450px;">Location Information</h4>
<br />
[#-- Form to dont editable location --]
  <div class="dontEditableLoc" >
    <div class="form-group">
    <label for="">Location level:</label> 
    <br />
    <span class="infoLocName">{test}</span>
    </div>    
    <div id="inputFormWrapper" style="width: 450px;">
      <div class="nameWrapper"><label for="">Location name:</label><span class="nameMap">LocName</span></div>
      <div class="latitudeWrapper"><label for="">Latitude:</label><br /><span class="latMap">Lat</span></div>
      <div class="longitudeWrapper"><label for="">Longitude:</label><br /><span class="lngMap">long</span></div>
    </div>
  </div>
  [#-- Button --]
  <div>
    <span id="okInfo" class=" addButton pull-right" style="margin-top:15px; border-radius:8px;">[@s.text name="Ok" /]</span>
  </div>
</div>

[#-- Region element template --]
<ul style="display:none">
  <li id="regionTemplate" class="region clearfix col-md-3">
      <div class="removeRegion removeIcon" title="Remove region"></div>
      <input class="id" type="hidden" name="project.projectRegions[-1].id" value="" />
      <input class="rId" type="hidden" name="project.projectRegions[-1].locElement.id" value="" />
      <input class="regionScope" type="hidden" name="project.projectRegions[-1].scope" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>

<span class="hidden has_otherLoc">${action.hasSpecificities('crp_other_locations')?string("true","false")!}</span>

[#-- Country and CMVS templates --]
<span class="hidden qCountry">[@s.text name="projectLocations.selectAllCountries" /]</span>
<span class="hidden qCmvSites">[@s.text name="projectLocations.selectAllCmvs" /]</span>
  
[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro locationLevel element  name index template=false list=false]
  [#local customName = "${name}[${index}]" /]
  [#local countryQuestion = '[@s.text name="projectLocations.selectAllCountries" /]'/]
  [#local cmvsQuestion = '[@s.text name="projectLocations.selectAllCmvs" /]'/]
  [#-- Content collapsible--]
  <div id="locationLevel-${template?string('template',index)}" class="locationLevel col-md-12" style="display:${template?string('none','block')}">
    [#-- header element --]
    <h5 class="sectionSubTitle">
    <span class="locLevelName">
      ${(element.name)!}:
    </span> 
    [#-- Remove --]
   [#if editable]<span class="listButton remove removeLocationLevel pull-right" style="padding: 0 5px 3px 5px;">[@s.text name="form.buttons.remove" /] location level</span>[/#if]
    </h5>
    <div class=" locationLevel-optionContent " listname="${customName}.locElements">
      <span class="allCountriesQuestion" style="display:none">
        <span class="question">[@s.text name="projectLocations.selectAllCmvs" /]</span>
        [@customForm.yesNoInput name="${customName}.allCountries"  editable=editable inverse=false  cssClass="allCountries text-center" /]
        <hr />
      </span>
      [#-- Content of locations--]
      <div class="optionSelect-content row">
        [#if element.locElements?has_content]
          [#list element.locElements as location]
            [@locationMacro element=location name="${customName}.${locationName}" index=location_index isList=list template=element.allCountries /]
          [/#list]
        [/#if]
      </div>
    </div>
    [#-- TEST FORM BY LOCATION LEVEL --]
    [#if editable]
    <div class="col-md-12" style="">
      <span class="pull-right glyphicon glyphicon-plus addLoc-locLevel"><b> [@s.text name="Add new location" /]</b></span>
    </div>
    [/#if]
    <input class="locationLevelId" type="hidden" name="${locationLevelName}[${index}].id" value="${(element.id)!}"/>
    <input class="locationLevelName" type="hidden" name="${locationLevelName}[${index}].name" value="${(element.name)!}"/>
    <input type="hidden" class="isList" name="${customName}.isList"  value="${(list)?string}"/>
  </div>
[/#macro]

[#macro locationMacro element  name index template=false isList=false ]
  [#local customName = "${name}[${index}]" /]
  [#assign countID = countID+1/]
  [#-- Content collapsible--]
  <div id="location-${template?string('template',countID)}" class="col-md-4 locElement" style="display:${template?string('none','block')}">
    <div class="locations col-md-12">
      <div class="locationName"><span class="lName">${(element.name)!}</span> [#if element.locGeoposition?? && element.locGeoposition.latitude?? && element.locGeoposition.longitude?? && element.locGeoposition.latitude!=0 && element.locGeoposition.longitude!=0] <span class="lPos">[#if isList!=true](${(element.locGeoposition.latitude)!}, ${(element.locGeoposition.longitude)!})[/#if]</span> [/#if] </div>
      [#if editable]
      <div class="removeLocation removeIcon" title="Remove Location"></div>
      [/#if]
    </div>
    [#-- Hidden inputs --]
    <input type="hidden" class="locElementId" name="${customName}.id" value="${(element.id)!}"/>
    <input type="hidden" class="locElementName" name="${customName}.name" value="${(element.name)!}" />
    <input type="hidden" class="locElementCountry" name="${customName}.locElement.isoAlpha2" value="${(element.isoAlpha2)!}" />
    <input type="hidden" class="geoId" name="${customName}.locGeoposition.id"  value="${(element.locGeoposition.id)!}" />
    
    <input type="hidden" class="geoLatitude" name="${customName}.locGeoposition.latitude"  value="${(element.locGeoposition?? && element.locGeoposition.latitude?? && element.locGeoposition.latitude!=0)?string((element.locGeoposition.latitude?c)!,'')}" /> 
    <input type="hidden" class="geoLongitude" name="${customName}.locGeoposition.longitude"  value="${(element.locGeoposition?? && element.locGeoposition.longitude?? && element.locGeoposition.longitude!=0)?string((element.locGeoposition.longitude?c)!,'')}" />
  </div>
[/#macro]

[#macro recommendedLocation element  name index template=false ]
  [#local customName = "${name}[${index}]" /]
  [#-- Content collapsible--]
  <div id="recommendedLocation-${template?string('template',index)}" class="col-md-4 recommended locElement [#if !editable]${((element.selected)?string('', 'hidden'))!}[/#if]" style="display:${template?string('none','block')}">
    <div class="locations col-md-12">
      [#-- Location Name --]
      <div class="recommendedLocName pull-left">
        [#if element.locElement??]
          <input type="hidden" class="elementID" name="${customName}.locElement.id" value="${(element.locElement.id)!}"/>
          <input type="hidden" class="locScope" name="${customName}.scope" value="${(element.locElement.locElementType.scope?c)!}"/>
          <span class="lName">
          [#assign miniName= (element.locElement.name)!""]
          <b title="${(element.locElement.name)!}">[#if miniName?length < 23]${miniName}[#else]${miniName?substring(0,22)} ...[/#if]</b>
          </span> 
        [#else]
          <input type="hidden" class="elementID" name="${customName}.locElementType.id" value="${(element.locElementType.id)!}"/>
          <input type="hidden" class="locScope" name="${customName}.scope" value="${(element.locElementType.scope?c)!}"/>
          <span class="lName">
          [#assign miniName= (element.locElementType.name)!""]
          <b title="${(element.locElementType.name)!}">[#if miniName?length < 23]${miniName}[#else]${miniName?substring(0,22)}...[/#if]</b>
          </span> 
        [/#if]
      </div>  
       
      [#-- Check Icon --]
      [#if element.locElement??]
        [#if editable]
          <input type="checkbox" class="recommendedSelected pull-right" name="" [#if element.selected]checked[/#if]/>
        [#else]
          [#if element.selected]<span class="glyphicon glyphicon-ok text-success pull-right"></span>[/#if]
        [/#if]
        <input type="hidden" name="${customName}.selected" value="${element.selected?string}" />
        [#if element.locElement.locElementType.id==2 ]
          <span class="hidden isoAlpha">${(element.locElement.isoAlpha2)!}</span>
        [/#if]
      [/#if]
      [#if element.locElementType??]
        [#if editable]
        <input type="checkbox" class="recommendedSelected pull-right" name="" [#if element.selected]checked[/#if]/>
        [#else]
          [#if element.selected]<span class="glyphicon glyphicon-ok text-success pull-right"></span>[/#if]
        [/#if]
        <input type="hidden" name="${customName}.selected" value="${element.selected?string}" />
      [/#if]
    </div>
    
    <div class="col-md-12 fundingContent">
      [#if element.fundingSources?has_content]
        [#list element.fundingSources as fs]
          [#if action.hasSpecificities('crp_fs_w1w2_cofinancing')] ${(fs.w1w2?string('<small class="text-primary">(Co-Financing)</small>',''))!} [/#if]
          <span style="font-size:0.7em;">${fs.composedName}</span><br />
        [/#list]
      [/#if]
    </div>
     
  </div>
[/#macro]
