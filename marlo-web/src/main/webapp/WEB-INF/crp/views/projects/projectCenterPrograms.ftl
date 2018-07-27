[#ftl]
[#assign title = "Project Programs" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrl}/global/js/fieldsValidation.js",
  "${baseUrl}/global/js/autoSave.js"
  ] 
/]
[#assign currentSection = "projects" /]
[#assign currentStage = "centerProgram" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"P${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"centerProgram", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Center Programs Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
           
          [#-- Select Project Program--]
          <h3 class="headTitle">[@s.text name="projectPrograms.title" /]</h3>
          <div class="borderBox">
            <div class="form-group">
              <div class="col-md-6">
                ${currentCrp.acronym} Programs
              </div>
              <div class="col-md-6">
                ${currentCrp.acronym} Regions
              </div>
            </div>
            
          </div>
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
          
          [/@s.form] 
      </div> 
    </div> 
</section>
[/#if]



  
[#include "/WEB-INF/global/pages/footer.ftl"]



