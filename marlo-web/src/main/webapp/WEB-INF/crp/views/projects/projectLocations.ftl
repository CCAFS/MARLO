[#ftl]
[#assign title = "Project Locations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectLocations.js?20181029", 
  [#--  "${baseUrlCdn}/global/js/autoSave.js?20210616",  --]
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ] 
/] 
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectLocations.css?20230106" ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "locations" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"C${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"projectLocations", "nameSpace":"/projects", "action":""}
] /]

[#assign locationLevelName = "project.locationsData"/]
[#assign locationName = "locElements"/]
[#assign countID = 0/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

<!--
<div class="container helpText viewMore-block">
  <div  class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10">[#if reportingActive] [@s.text name="projectLocations.help2" /] [#else] [@s.text name="projectLocations.help1" /] [/#if] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
-->
<div class="animated flipInX container  viewMore-block containerAlertMargin">
  <div class="containerAlert alert-leftovers alertColorBackgroundInfo " id="containerAlert">
    <div class="containerLine alertColorInfo"></div>
    <div class="containerIcon">
      <div class="containerIcon alertColorInfo">
        <img src="${baseUrlCdn}/global/images/icon-question.png" />      
      </div>
    </div>
    <div class="containerText col-md-12">
      <p class="alertText">
        [#if reportingActive] [@s.text name="projectLocations.help2" /] [#else] [@s.text name="projectLocations.help1" /] [/#if] 
      </p>
    </div>
    <div  class="viewMoreCollapse closed"></div>
  </div>
</div>

[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          <input class="projectInfo" type="hidden" name="project.id" value="${project.id}" />
          
          <h3 class="headTitle ">[@s.text name="projectLocations.title" /]</h3>  
          <div id="" class="borderBox projectLocationsWrapper">
            [#-- Content--]
              <div class="row">
                [#-- GLOBAL DIMENSION --]
                <div class="form-group col-md-12">
                  [@customForm.yesNoInput  label="projectLocations.globalDimension" name="project.projectInfo.locationGlobal"  editable=editable && action.hasSpecificities("crp_other_locations") inverse=false  cssClass="" /] 
                </div>
                <br />
                <div class="form-group col-md-12 ">
                  <hr />
                </div>
                [#-- REGIONAL DIMENSION --]
                <div class="form-group col-md-12">
                  [@customForm.yesNoInput  label="projectLocations.regionalDimension" name="project.projectInfo.locationRegional"   editable=editable && action.hasSpecificities("crp_other_locations") inverse=false  cssClass="isRegional" /]
                  [#if editable && action.hasSpecificities("crp_other_locations")]
                    <small style="color: #337ab7;">[@s.text name="projectLocations.regionsNote" /]</small>
                  [/#if]
                </div>
                
                  [#-- REGIONS SELECT --]
                  <div class="">
                    [#if action.hasSpecificities('crp_other_locations')]
                    <div class="regionsBox form-group col-md-12" style="display:${(project.projectInfo.locationRegional?string("block","none"))!"none"};">
                      <div class="panel tertiary col-md-12">
                        <div class="panel-head">
                          <label for=""> [@customForm.text name="projectCofunded.selectRegions" readText=!editable /]:[@customForm.req required=editable /]</label><br />
                          <small style="color: #a2a2a2;">([@s.text name="projectLocations.standardLocations" /])</small>
                        </div>
                        
                        <div id="regionList" class="panel-body" listname="project.projectRegions"> 
                          <ul class="list">
                          [#if project.projectRegions?has_content]
                            [#list project.projectRegions as region]
                              <li id="" class="region clearfix col-md-3">
                                [#if editable ]
                                  <div class="removeRegion removeIcon" title="[@s.text name="projectLocations.removeRegion" /]"></div>
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
                    [/#if]
                  </div>
                </div>
                <hr />
                
                [#if reportingActive && action.hasSpecificities("crp_location_csv_activities")]
                [#-- AR4D activities related to the evaluation of CSA options --]
                <div class="form-group">
                  [@customForm.yesNoInput  label="projectLocations.activitiesCSV" name="project.projectInfo.activitiesCSV"  editable=editable inverse=false  cssClass="isCSV" /] 
                </div>
                <div class="clear-fix"></div>
                
                <div class="csvActivitiesBox simpleBox" style="display:${(project.projectInfo.activitiesCSV?string("block","none"))!"none"};">
                  <div class="row">
                    <div class="col-md-9">
                      <label for="">[@s.text name="projectLocations.activitiesCSV.instructions" /]:</label>
                    </div>
                    <div class="col-md-3 right">
                      <a href="${baseUrlCdn}/global/documents/Guidelines_CSA_evlautions_template _Reporting_2020.pdf" target="__BLANK">
                        <img src="${baseUrlCdn}/global/images/pdf.png" height="20" />
                        [[@s.text name="projectLocations.activitiesCSV.guideline" /]]
                      </a>
                    </div>
                  </div>
                  
                  <div class="row justify-content-md-center">
                    
                    [#-- Download Template --]
                    <div class="col-md-2"></div>
                    <div class="col-md-4">
                       <label for="">[@s.text name="projectLocations.activitiesCSV.download" /]:</label>
                       <div class="form-group">
                         <a href="${baseUrlCdn}/global/documents/CSA_Evaluation_template_2020.xlsm" download><img src="${baseUrlCdn}/global/images/download-excel.png" height="70" /></a>
                       </div>
                    </div>
                    
                    [#-- Upload Template --]
                    <div class="col-md-5 " style="position:relative" listname="project.projectInfo.activitiesCSVFile">
                      [@customForm.fileUploadAjax 
                        fileDB=(project.projectInfo.activitiesCSVFile)!{}  
                        name="project.projectInfo.activitiesCSVFile.id" 
                        label="projectLocations.activitiesCSV.upload"
                        image=true
                        imgUrl="upload-excel.png"
                        imgClass="csvUpload"
                        dataUrl="${baseUrl}/uploadProjectLocationActivitiesCSV.do"  
                        path="${(action.getPath())!}"
                        isEditable=editable
                        labelClass=""
                        required=true
                      /]
                    </div>
                  </div>
                  
                </div>
                [/#if]
                
              </div>
              
              
              [#-- SUGGESTED LOCATIONS --]
              [#if project.regionFS?has_content ||  project.countryFS?has_content] 
              <div class="borderBox">
                <div class="">
                  <h5 class="sectionSubTitle">[@s.text name="projectLocations.suggestedLocations" /]:</h5>
                  <label for="">[@s.text name="projectLocations.locationsBelow" /]:</label>
                  <div class="simpleBox">
                    <div class="recommendedList">
                      [#-- SUGGESTED REGIONS LIST --]
                      [#if project.regionFS?has_content]
                        <h5 class="sectionSubTitle text-gray">[@s.text name="projectLocations.suggestedRegions" /]:</h5>
                        <div class="row regionsContent" style="display:${(project.projectInfo.locationRegional?string("block","none"))!"none"};">
                          [#list project.regionFS as location]
                            [@recommendedLocation element=location name="project.regionFS" index=location_index template=false /]
                          [/#list]
                        </div>
                      [#else]
                        [#assign recommendedRegions=0]
                      [/#if]
                      [#-- SUGGESTED COUNTRIES LIST --]
                      [#if project.countryFS?has_content]
                        <h5 class="sectionSubTitle text-gray">[@s.text name="projectLocations.suggestedCountries" /]:</h5>
                        <div class="row">
                          [#list project.countryFS as location]
                            [@recommendedLocation element=location name="project.countryFS" index=location_index template=false /]
                          [/#list]
                        </div>
                      [#else]
                        [#assign recommendedCountries=0]
                      [/#if]
                      [#if recommendedCountries?? && recommendedCountries==0 && recommendedRegions?? && recommendedRegions==0]
                        <p class="text-center inf">[@s.text name="projectLocations.noLocations" /]</p>
                      [/#if]
                    </div>
                  </div>
                </div>
              </div>
              [/#if]
              
              [#-- OTHER LOCATIONS (MAP) --]   
              <div class="borderBox" style="display:${action.hasSpecificities('crp_other_locations')?string('block', 'none')}">
                <div class="allLocationsButton btn btn-default pull-right" data-toggle="modal" data-target=".allLocationsModal">
                  <img src="${baseUrlCdn}/global/images/map.png" alt="" /> <span>[@s.text name="projectLocations.allLocationsMap" /]</span>
                </div>
                <h5 id="locations-list-title" class="sectionSubTitle">[@s.text name="projectLocations.locationsList" /]:</h5>
                [#-- LOCATION LIST --]
                <div class="">
                  [#-- Add new location (Modal) --]
                  [@addNewLocationModal /]
                  [#-- All locations map (Modal) --]
                  [@allLocationsMapModal /]
                  [#-- Locations list table --]
                  <div id="selectsContent" class="col-md-12" listname="project.locationsData">
                    <div class="row">
                      <table class="locationsDataTable">
                        <tbody>
                        [#if project.locationsData?has_content]
                          [#list project.locationsData as locationLevels]
                              [@locationsTableMacro element=locationLevels name="${locationLevelName}" index=locationLevels_index list=locationLevels.list?? && locationLevels.list/]
                          [/#list]
                        [#else]
                          <p id="noLocationsAdded">[@s.text name="projectLocations.notLocationsAdded" /].</p>
                        [/#if]
                        </tbody>
                      </table>
                    </div>
                  </div>
                  [#-- Add new location button --]
                  [#if editable && action.hasSpecificities('crp_other_locations')]
                    <div class="clearfix"></div>
                    <div id="addNewLocation-button" class="bigAddButton text-center loc-button" data-toggle="modal" data-target=".addLocationModal"> 
                      <span class="glyphicon glyphicon-plus"></span>[@s.text name="Add new location" /] 
                    </div>
                  [/#if]
                </div>
              </div>
              
              [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
            </div>
            
          </div> 
        [/@s.form] 
      </div>
    </div>  
</section>
[/#if]


[#-- Section hidden inputs--]
[@locationsTableMacro element={} name="${locationLevelName}" index=-1 template=true /]

[@locationMacro element={} name="${locationLevelName}[-1].${locationName}" index=-1 template=true /]

[@recommendedLocation element={} name="${locationLevelName}.${locationName}" index=-1 template=true /]

<input type="hidden" id="locationLevelName" value="${locationLevelName}" />
<input type="hidden" id="locationName" value="${locationName}" />

[#macro addNewLocationModal]
[#-- START Add new location (Modal) --]
<div id="addLocationModal" class="modal fade addLocationModal" tabindex="-1" role="dialog" aria-labelledby="addNewLocation" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <button id="close-modal-button" type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
      
      <div class="locationForm-container">
        <h3 class="title">[@s.text name="projectLocations.addingNewLocation" /]</h3>
        <hr />
        <div class="form-group col-md-7">
          <div class="locLevelSelect-container">
            <label for="locLevelSelect" style="display:block;">[@s.text name="projectLocations.selectLocationLevel" /]:</label>
            <select name="" id="locLevelSelect"  class="selectLocationLevel select " >
              <option value="-1" >[@s.text name="projectLocations.selectOption" /]...</option>
              [#list locationsLevels as locLevels]
                [#list locLevels.locations as locations]
                  <option value="${locations.id}-${locations.list?string}-${locations.name}" >${locations.name}</option>
                [/#list]
              [/#list]
            </select>
          </div>
          [#-- Select location(s) Form --]
          <div class="selectLocations" style="display:none;">
            <label for="">[@s.text name="projectLocations.selectLocations" /]</label>
            <select name="" data-placeholder="[@s.text name="projectLocations.selectPlaceholder" /]" id="countriesCmvs" multiple="true"></select>
          </div>
          [#-- Location name -Latitude -Longitude --]
          <div id="inputFormWrapper" class="inputFormCoordinates-container" style="display:none;">
            <div class="nameWrapper"><label for="">[@s.text name="projectLocations.locationName" /]:</label><input placeholder="Name (Required)" class="name form-control" type="text" /></div>
            <div class="latitudeWrapper"><label for="">[@s.text name="projectLocations.latitude" /]:</label><input placeholder="[@s.text name="projectLocations.latitude" /]" class="latitude form-control" type="text" value="" /></div>
            <div class="longitudeWrapper"><label for="">[@s.text name="projectLocations.longitude" /]:</label><input placeholder="[@s.text name="projectLocations.longitude" /]" class="longitude form-control " type="text"  value=""/></div>
          </div>
          [#-- add Location Button --]
          <div class="addLocationButton-container">
            <span id="addLocationButton" class=" addButton pull-right" style="display:none; margin-top:10px; border-radius:8px;">[@s.text name="projectLocations.addLocations" /]</span>
          </div>
          [#-- Successfully added alert --]
          <div>
            <div id="alert-succesfully-added" class="alert alert-success" role="alert" style="display:none">[@s.text name="projectLocations.successfullyAdded" /]</div>
          </div>
        </div>
      </div>
      
      [#-- Add location MAP --]
      <div class="map-container col-md-5">
        <div id="add-location-map" class="col-md-12 map">
          [#-- <input id="pac-input" class="controls" type="text" placeholder="Search Box" style="display:none"> --]
          <div id="map" class="col-md-12"></div>
        </div>
      </div>
      
    </div>
  </div>
</div>
[#-- END Add new location (Modal) --]
[/#macro]

[#macro allLocationsMapModal]
[#-- START All locations map (Modal) --]
<div id="allLocationsModal" class="modal fade allLocationsModal" tabindex="-1" role="dialog" aria-labelledby="allLocations" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <button id="close-modal-button" type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
      [#-- All locations list --]
      <div class="allLocations-container">
        <h3 class="title">[@s.text name="projectLocations.allLocations" /]</h3>
        <hr />
        
        <div class="form-group col-md-3 list-container">
        [#if project.locationsData?has_content]
          [#list project.locationsData as locationLevels]
            [@allLocationsListMacro element=locationLevels list=locationLevels.list?? && locationLevels.list/]
          [/#list]
        [/#if]
        </div>
      </div>
      [#-- All locations MAP --]
      <div class="map-container col-md-9">
        <div id="all-locations-map" class="col-md-12 map">
        </div>
      </div>
      
    </div>
  </div>
</div>
[#-- END All locations map (Modal) --]
[/#macro]

[#macro allLocationsListMacro element list]
<div>
  <h4 name="${element.name!}" class="loc-level">${element.name!}</h4>
  <ul id="${element.id}" name="${element.name!}">
    [#if element.locElements?has_content]
      [#list element.locElements as location]
        <li id="${location.id!}" name="${location.name!}" class="marker-map">
          <span class="glyphicon glyphicon-map-marker"></span>
          <span class="item-name">${location.name!}</span>
          <br />
          [#if list!=true]<span class="coordinates" data-lat="${(location.locGeoposition.latitude)!}" data-lon="${(location.locGeoposition.longitude)!}">(${(location.locGeoposition.latitude)!}, ${(location.locGeoposition.longitude)!})</span>[/#if]
        </li>
      [/#list]
    [/#if]
    [#if element.name?has_content]
      [#if element.name == 'Country']
        [#if project.countryFS?has_content]
          [#list project.countryFS as suggestedCountry]
            [#if suggestedCountry.selected]
              <li id="${suggestedCountry.locElement.id!}" name="${suggestedCountry.locElement.name!}" class="marker-map">
                <span class="glyphicon glyphicon-map-marker"></span>
                <span class="item-name">${(suggestedCountry.locElement.name)!}</span>
                <br />
              </li>
            [/#if]
          [/#list]
        [/#if]
      [/#if]
    [/#if]
  </ul>
</div>
[/#macro]

[#macro locationsTableMacro element name index template=false list=false]
  [#if template]<table>[/#if]
      [#local customName = "${name}[${index}]" /]
      <tr id="locationLevel-${template?string('template',index)}" class="locationLevel" style="display:${template?string('none','')}" data-name="${(element.name)!''}">
        <th class="locLevelName" width="20%">${(element.name)!''}</th>
        <td width="80%">
          <div class=" locationLevel-optionContent " listname="${customName}.locElements">
            [#-- Content of locations--]
            <div class="optionSelect-content row">
              [#-- Other countries --]
              <div class="row countriesList">
                <div class="col-sm-12">
                [#if element.locElements?has_content]
                  [#list element.locElements as location]
                    [@locationMacro element=location name="${customName}.${locationName}" index=location_index isList=list template=element.allCountries /]
                  [/#list]
                [/#if]
                </div>
              </div>
              [#-- Countries from suggested locations list --]
              <div class="row suggestedCountriesList" style="display:none">
                <div class="col-sm-12">
                [#if template]
                  <hr class="suggestedLocations-separator" />
                  <div class="row suggestedLocations-separator">
                    <div class="col-sm-4 col-md-4">
                      <div class="suggestedLocations-label">
                        [@s.text name="projectLocations.suggestedCountriesSeparator" /]:
                      </div>
                    </div>
                  </div>
                [/#if]
                [#if element.name?has_content]
                  [#if element.name == 'Country']
                    [#if project.countryFS?has_content]
                      <hr class="suggestedLocations-separator" />
                      <div class="row suggestedLocations-separator">
                        <div class="col-sm-4 col-md-4">
                          <div class="suggestedLocations-label">
                            [@s.text name="projectLocations.suggestedCountriesSeparator" /]:
                          </div>
                        </div>
                      </div>
                      [#list project.countryFS as suggestedCountry]
                        [#if suggestedCountry.selected]
                          <div class="col-md-4">
                            <div class="locations col-md-12">
                              <div id="${suggestedCountry.locElement.isoAlpha2!}" class="locationName">
                                <span class="lName">${(suggestedCountry.locElement.name)!}</span>
                              </div>
                              [#if editable]
                              <div class="removeIcon removeDisabled" title="[@s.text name="projectLocations.suggestedCountryRemove" /]"></div>
                              [/#if]
                            </div>
                          </div>
                        [/#if]
                      [/#list]
                    [/#if]
                  [/#if]
                [/#if]
                </div>
              </div>
            </div>
          </div>
        </td>
        <input class="locationLevelId" type="hidden" name="${locationLevelName}[${index}].id" value="${(element.id)!}"/>
        <input class="locationLevelName" type="hidden" name="${locationLevelName}[${index}].name" value="${(element.name)!}"/>
        <input type="hidden" class="isList" name="${customName}.isList"  value="${(list)?string}"/>
      </tr>
  [#if template]</table>[/#if]
[/#macro]

[#macro locationMacro element  name index template=false isList=false ]
  [#local customName = "${name}[${index}]" /]
  [#assign countID = countID+1/]
  [#-- Content collapsible--]
  <div id="location-${template?string('template',countID)}" class="col-md-4 locElement" style="display:${template?string('none','block')}" data-locId="${element.id!}">
    <div class="locations col-md-12">
      <div class="locationName">
        <span class="lName">${(element.name)!}</span> 
        [#if element.locGeoposition?? && element.locGeoposition.latitude?? && element.locGeoposition.longitude?? && element.locGeoposition.latitude!=0 && element.locGeoposition.longitude!=0]
          <br />
          <span class="lPos">[#if isList!=true](${(element.locGeoposition.latitude)!}, ${(element.locGeoposition.longitude)!})[/#if]</span> 
        [/#if] 
      </div>
      [#if editable]
      <div class="removeLocation removeIcon" title="[@s.text name="projectLocations.removeLocation" /]"></div>
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

[#-- Region element template --]
<ul style="display:none">
  <li id="regionTemplate" class="region clearfix col-md-3">
      <div class="removeRegion removeIcon" title="[@s.text name="projectLocations.removeRegion" /]"></div>
      <input class="id" type="hidden" name="project.projectRegions[-1].id" value="" />
      <input class="rId" type="hidden" name="project.projectRegions[-1].locElement.id" value="" />
      <input class="regionScope" type="hidden" name="project.projectRegions[-1].scope" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>

[#-- location element template (table) --]
<div id="suggestedLocation-template" class="col-md-4" style="display:none">
  <div class="locations col-md-12">
    <div id="" class="locationName">
      <span class="lName"></span>
    </div>
    [#if editable]
    <div class="removeIcon removeDisabled" title="[@s.text name="projectLocations.suggestedCountryRemove" /]"></div>
    [/#if]
  </div>
</div>

[#-- list element template (all locations modal) --]
<div id="itemLoc-template" style="display:none">
  <h4 name="" class="loc-level"></h4>
  <ul id="" name="">
    <li id="itemList-template" name="" class="marker-map">
      <span class="glyphicon glyphicon-map-marker"></span>
      <span class="item-name"></span>
      <br />
      <span class="coordinates" data-lat="" data-lon="" style="display:none"></span>
    </li>
  </ul>
</div>

[#include "/WEB-INF/global/pages/footer.ftl"]

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
          <span class="lName" data-name="${(element.locElement.name)!}">
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
        <input class="recommended-location" type="hidden" name="${customName}.selected" value="${element.selected?string}" />
        [#if element.locElement.locElementType.id==2 ]
          <span class="hidden isoAlpha" data-isoAlpha="${(element.locElement.isoAlpha2)!}">${(element.locElement.isoAlpha2)!}</span>
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
          <a target="_blank" href="[@s.url namespace="/fundingSources" action="${(crpSession)!}/fundingSource"] [@s.param name='fundingSourceID']${fs.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
            <span style="font-size:0.7em;">
              [#-- Title --]
              [#-- Finance Code --]
              [#if (fs.fundingSourceInfo.financeCode?has_content)!false] <strong>${fs.fundingSourceInfo.financeCode}</strong>  |  [/#if] 
              <strong> FS${fs.id} </strong> - ${(fs.fundingSourceInfo.title)!'null'}
            </span>
          </a>
          <br />
        [/#list]
      [/#if]
    </div>
     
  </div>
[/#macro]