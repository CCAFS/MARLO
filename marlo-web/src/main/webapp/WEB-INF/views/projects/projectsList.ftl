[#ftl]
[#assign title = "MARLO Projects" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectsList.js" ] /]
[#assign customCSS = ["${baseUrl}/css/global/customDataTable.css"] /]
[#assign currentSection = "projects" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#import "/WEB-INF/global/macros/projectsListTemplate.ftl" as projectList /]
    
<section class="container">
  <article class="fullBlock" id="mainInformation">
    [#-- Projects List (My Projects) --]
    <h3 class="headTitle text-center">[@s.text name="projectsList.yourProjects"/]</h3>
    <div class="loadingBlock"></div>
    <div style="display:none">[@projectList.projectsList projects=myProjects canValidate=true canEdit=true namespace="/projects" defaultAction="${(crpSession)!}/description" /]</div>

   
    [#-- Section Buttons--]
    <div class="buttons">
      <div class="buttons-content">
        <a class="addButton" href="[@s.url namespace="/${currentSection}" action='addNewCoreProject'/]">[@s.text name="projectsList.addCoreProject" /]</a>
        <a class="addButton" href="[@s.url namespace="/${currentSection}" action='addNewBilateralProject'/]">[@s.text name="projectsList.addBilateralProject" /]</a>
      </div>
    </div>

    <div class="clearfix"></div>
    <hr/>
    
    [#-- Projects List (Other Projects) --]
    <h3 class="headTitle text-center">[@s.text name="projectsList.otherProjects" /]</h3>
    <div class="loadingBlock"></div>
    <div style="display:none">[@projectList.projectsList projects=allProjects canValidate=true namespace="/projects" defaultAction="${(crpSession)!}/description"/]</div>
    
  </article>
</section>
[@customForm.confirmJustification action="deleteProject" namespace="/${currentSection}" title="Remove Project" /]


[#include "/WEB-INF/global/pages/footer.ftl"]
