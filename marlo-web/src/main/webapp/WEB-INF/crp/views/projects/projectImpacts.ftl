[#ftl]
[#assign title = "Project Description" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectDescription.js", 
  "${baseUrlCdn}/global/js/autoSave.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectDescription.css"
  ] 
/]
[#assign currentSection = "projects" /]
[#assign currentStage = "description" /]
[#assign hideJustification = true /]
[#assign isCrpProject = (action.isProjectCrpOrPlatform(project.id))!false ]
[#assign isCenterProject = (action.isProjectCenter(project.id))!false ]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        <div class="col-md-9">
          <h3 class="headTitle">[@s.text name="projects.impacts.covid19Title" /]</h3>
          <div id="projectImpactCovid19" class="borderBox">
            <div class="form-group">
              [@customForm.textArea name="actualProjectImpact.answer" i18nkey="projects.impacts.covid19Title" required=true className="project-title limitWords-100" editable=editable && action.hasPermission("title") /]
            </div>
          </div>  
        </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
      [/@s.form]
      
    </div>
</section>

