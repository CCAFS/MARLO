[#ftl]
[#assign title = "MARLO Projects" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrlMedia}/js/monitoring/projects/projectsList.js" ] /]
[#assign customCSS = ["${baseUrl}/global/css/customDataTable.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = (filterBy)!"all" /]


[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/monitoring", "action":"${(centerSession)!}/projectList"}
]/]

[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]
[#import "/WEB-INF/center/macros/projectsListTemplate-center.ftl" as projectList /]
[#-- Help text --]
<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />
    <p class="col-md-10"> [@s.text name="projectList.help"][/@s.text] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

<section class="container">
  <article class="row" id="mainInformation">
    <div class="col-md-12">
      [#include "/WEB-INF/center//views/monitoring/project/submenu-project.ftl" /]
      [#-- Projects List (My Projects) --]
      <h3 class="headTitle text-center">${selectedProgram.name}- Projects</h3>
      <div class="loadingBlock"></div>
      <div style="display:none">[@projectList.projectsList projects=projects canValidate=true canEdit=editable namespace="/monitoring" defaultAction="${(centerSession)!}/projectDescription" /]</div>

      [#-- Section Buttons --]
      [#if canEdit]
      <div class="buttons">
        <div class="buttons-content"> 
          <button type="button" class="addButton" data-toggle="modal" data-target="#addProjectsModal"> [@s.text name="Add project" /]</button>
          [#-- <a class="addButton" href="[@s.url action='${(centerSession)!}/addNewProject'][@s.param name='programID']${selectedProgram.id?c}[/@s.param][/@s.url]">[@s.text name="Add project" /]</a> --]
          <div class="clearfix"></div>
        </div>
      </div>
      [/#if]
      
      <div class="clearfix"></div>
    </div>
    
  </article>
</section>

[#-- Justification of deletion popup --]
[@customForm.confirmJustificationProject action="deleteProject.do" namespace="/${currentSection}" title="Remove Project" /]

[#-- Add Projects Popup --]
[#include "/WEB-INF/center/macros/addProjectsPopup-center.ftl" /]


[#include "/WEB-INF/center/pages/footer.ftl"]
