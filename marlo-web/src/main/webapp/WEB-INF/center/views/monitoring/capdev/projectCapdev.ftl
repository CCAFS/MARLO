[#ftl]
[#assign title = "Project CapDev" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "${baseUrl}/global/js/usersManagement.js", 
  "${baseUrlMedia}/js/impactPathway/output.js", 
  "${baseUrl}/global/js/fieldsValidation.js", 
  "${baseUrl}/global/js/autoSave.js",
  "${baseUrlMedia}/js/capDev/capdevList.js"
  ] 
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/impactPathway/outputList.css",
  "${baseUrl}/global/css/customDataTable.css", 
  "${baseUrlMedia}/css/capDev/capacityDevelopment.css"
  ] 
/]
[#assign currentSection = "projects" /]
[#assign currentStage = "projectCapdev" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/monitoring", "action":"${(centerSession)!}/projectList"},
  {"label":"projectCapdev", "nameSpace":"/monitoring", "action":""}]/]
  
[#assign leadersName = "leaders"/]
[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#import "/WEB-INF/center/macros/capdevListTemplate.ftl" as capdevList /]
[#-- Help text --]
<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />
    <p class="col-md-10">  [@s.text name="capdev.help.list"][/@s.text]  </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

[#--  marlo cluster of activities--]
<section class="marlo-content">
  <div class="container"> 
    
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/center/views/monitoring/project/menu-projects.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/center/views/monitoring/project/messages-projects.ftl" /]
        <br />

        <div class="clearfix"></div>
        

        [#-- Back --]
        <div class="pull-right">
          <a href="[@s.url action='${centerSession}/projectList'][@s.param name="programID" value=programID /][@s.param name="projectID" value=projectID /][@s.param name="edit" value=true /][/@s.url]">
            <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the project list
          </a>
        </div>
          
        <div>[@capdevList.capdevList capdevs=capdevs canValidate=true canEdit=editable namespace="/capdev" defaultAction="${(centerSession)!}/detailCapdev" /]</div>
        
      </div>
    </div>
    
  </div>
</section>



[#include "/WEB-INF/center/pages/footer.ftl" /]