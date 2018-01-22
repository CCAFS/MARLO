[#ftl]
[#assign title = "Project Expected Studies" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrl}/global/js/fieldsValidation.js",
  "${baseUrlMedia}/js/projects/projectExpectedStudies.js", 
  "${baseUrl}/global/js/autoSave.js"
  ] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectExpectedStudies.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "expectedStudies" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"expectedStudies", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectExpectedStudies.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>


<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Expected Study Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
           
          <h3 class="headTitle">[@s.text name="projectExpectedStudies.title" /]</h3>
          <div class="simpleBox">
            <div class="expectedStudies-list" listname="">
            [#if project.expectedStudies?has_content]
              [#list project.expectedStudies as expectedStudy]
                [@expectedStudyMacro element=expectedStudy name="project.expectedStudies"  index=expectedStudy_index isEditable=editable  /]
              [/#list]
            [/#if]
            </div>
            [#if canEdit && editable]
            <div class="text-right">
              <div class="addExpectedStudy bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addExpectedStudy"/]</div>
            </div> 
            [/#if]
          </div>
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
          
          <div class="clearfix"></div>
         
          [/@s.form] 
      </div> 
    </div> 
</section>

[#-- Expected Study Template --]
[@expectedStudyMacro element={} name="project.expectedStudies"  index=-1 template=true/]

[#include "/WEB-INF/crp/pages/footer.ftl"]

[#macro expectedStudyMacro element name index template=false isEditable=true]
  [#local customName = "${name}[${index}]" /]
  <div id="expectedStudy-${template?string('template', index)}" class="expectedStudy borderBox form-group" style="position:relative; display:${template?string('none','block')}">
    
    [#-- Index --]
    <div class="leftHead"><span class="index">${index+1}</span></div>
    [#-- Remove Button --]
    [#if isEditable]<div class="removeExpectedStudy removeIcon" title="Remove Expected Study"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
     
  </div>
[/#macro]