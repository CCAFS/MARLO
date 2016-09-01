[#ftl]
[#assign title = "Project Activities" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectActivities.js"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "activities" /]

[#assign breadCrumb = [
  {"label":"activities", "nameSpace":"/projects", "action":"${(crpSession)!}/activities"}]/]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container">
  <div class="helpMessage"><img src="${baseUrl}/images/global/icon-help.png" /><p> </p></div> 
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
           
          
          <h3 class="headTitle">[@s.text name="projectActivities.title" /]</h3>
          
            
    
            
          </div> 
           
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
             
         
          [/@s.form] 
      </div>
    </div>  
</section>
  
[#include "/WEB-INF/global/pages/footer.ftl"]