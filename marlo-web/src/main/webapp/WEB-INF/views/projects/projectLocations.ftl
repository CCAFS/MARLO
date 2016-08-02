[#ftl]
[#assign title = "Project Locations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectId}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectsLocation.js"] /] [#-- "${baseUrl}/js/global/autoSave.js" --]
[#assign customCSS = ["${baseUrl}/css/projects/projectLocations.css" ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "locations" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectLocations", "nameSpace":"/projects", "action":""}
] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container">
  <div class="helpMessage"><img src="${baseUrl}/images/global/icon-help.png" /><p> [@s.text name="projectLocations.help" /] </p></div> 
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
           
          <h3 class="headTitle">[@s.text name="projectLocations.title" /]</h3>  
          <div id="" class="borderBox">
            <div >
              [#-- yes or not option--]
              <div class="borderBox col-md-6"> 
                <div id="globalProject"><span>Is the project global?</span></div>
                [@customForm.yesNoInput name="changeGraphic" label="" inverse=false value="" yesLabel="Yes" noLabel="No"cssClass="" /]
              </div> 
            </div>
            
            [#-- Content--]
            <div id="content" class="col-md-12">
              <div class="text-center col-md-12  alert alert-info"><span> Select the points where the focus activity is being carried out </span></div>
              <div id="selectsContent" class="col-md-7">
                <div class="selectWrapper row">
                  [#-- Content collapsible--]                  
                </div>
              </div>
              
              <div  class="col-md-5 map">
                <div id="map" class="col-md-12"></div>
              </div>
                
            </div>
            <select name="" id="" class="selectpicker" placeholder="select an option...">
              <option>Select an option...</option>
              <option>SADC-Southern African Development Community</option>
              <option value="">CCAFS sites</option>
              <option value="">Districts</option>
            </select>
          </div> 
           
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
             
         
          [/@s.form] 
      </div>
    </div>  
</section>


[@locationLevel element={} name="" index=0 template=true /]

[@location element={} name="" index=0 template=true /]
  
[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro locationLevel element  name index template=false]
  [#assign customName = "${name}[${index}]" /]
  [#-- Content collapsible--]
  <div id="locationLevel-${template?string('template',index)}" class="locationLevel col-md-12" style="display:${template?string('none','block')}">
    <div class="col-md-12 locationName-content borderBox">
      <div class=" locationLevel-option "></div> 
      <div class="glyphicon glyphicon-chevron-down collapsible" ></div>   
    </div>
    <div class="col-md-12 locationLevel-optionContent borderBox">
      <div class="col-md-12 checkBox">
        <span class="col-md-10">Are you working in all countries on this region?</span>
        <input class="col-md-1" type="checkbox" />
      </div>
      <div class="optionSelect-content row">
      
      </div>
      <select name="" id="" class="selectLocation col-md-12" placeholder="select an option...">
        <option>Select an option...</option>
        <option>Angola</option>
        <option value="">Nyando</option>
      </select>
    </div>
  </div>
[/#macro]

[#macro location element  name index template=false]
  [#assign customName = "${name}[${index}]" /]
  [#-- Content collapsible--]
  <div id="location-${template?string('template',index)}" class="col-md-12 locElement" style="display:${template?string('none','block')}">
    <div class="locations col-md-12">
      <div class="locationName"> </div>
      <div class="removeLocation removeIcon" title="Remove Location"></div>
    </div>
  </div>
[/#macro]
