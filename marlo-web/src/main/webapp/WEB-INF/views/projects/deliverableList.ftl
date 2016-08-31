[#ftl]
[#assign title = "Project Deliverables" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrl}/js/projects/deliverables/deliverableList.js"] /] [#-- "${baseUrl}/js/global/autoSave.js" --]
[#assign customCSS = ["${baseUrl}/css/global/customDataTable.css","${baseUrl}/css/projects/projectDeliverable.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "deliverableList" /]

[#assign breadCrumb = [
  {"label":"deliverableList", "nameSpace":"/projects", "action":""}
]/]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/deliverableListTemplate.ftl" as deliverableList /]

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
           
          <h3 class="headTitle">[@s.text name="Project Deliverables" /]</h3>  
           
           <div style="">[@deliverableList.deliverablesList deliverables=deliverables canValidate=true namespace="/projects" defaultAction="${(crpSession)!}/deliverable"/]</div> 
                     
          <div class="text-right">
            <div class="addDeliverable button-blue"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addDeliverable" /]</div>
          </div>
             
         
          [/@s.form] 
      </div>
    </div>  
</section>

  
[#include "/WEB-INF/global/pages/footer.ftl"]
