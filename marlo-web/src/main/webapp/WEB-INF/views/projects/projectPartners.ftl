[#ftl]
[#assign title = "Project Partners" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectPartners.js"] /] [#-- "${baseUrl}/js/global/autoSave.js" --]
[#assign currentSection = "projects" /]
[#assign currentStage = "partners" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectPartners", "nameSpace":"/projects", "action":""}
] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container">
  <div class="helpMessage"><img src="${baseUrl}/images/global/icon-help.png" /><p> [@s.text name="projectPartners.help" /] </p></div> 
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
           
          <h3 class="headTitle">[@s.text name="projectPartners.title" /]</h3>  
          <div id="" class="borderBox">

          </div> 
           
          
          [#-- Project identifier --]
          <input name="projectID" type="hidden" value="${project.id}" />
          <input type="hidden"  name="className" value="${(project.class.name)!}"/>
          <input type="hidden"  name="id" value="${(project.id)!}"/>
          <input type="hidden"  name="modifiedBy.id" value="${(currentUser.id)!}"/>
          <input type="hidden"  name="actionName" value="${(actionName)!}"/>  
          
          [#-- Section Buttons--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
             
         
          [/@s.form] 
      </div>
    </div>  
</section>
  
[#include "/WEB-INF/global/pages/footer.ftl"]
