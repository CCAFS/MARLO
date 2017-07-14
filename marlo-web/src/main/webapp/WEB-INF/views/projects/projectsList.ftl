[#ftl]
[#assign title = "MARLO Projects" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs","font-awesome"] /]
[#assign customJS = ["${baseUrlMedia}/js/projects/projectsList.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/global/customDataTable.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = (filterBy)!"all" /]


[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#import "/WEB-INF/global/macros/projectsListTemplate.ftl" as projectList /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlMedia}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectsList.help"][@s.param]${currentCycle}[/@s.param][/@s.text] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

<section class="container">
  <article class="row" id="mainInformation">
    <div class="col-md-12">
    
      [#-- Projects List (My Projects) --]
      <h3 class="headTitle text-center">[@s.text name="projectsList.yourProjects"/]</h3>
      <div class="loadingBlock"></div>
      <div style="display:none">[@projectList.projectsList projects=myProjects canValidate=true canEdit=true namespace="/projects" defaultAction="${(crpSession)!}/description" /]</div>
  
      [#-- Section Buttons --]
      [#if (action.canAddCoreProject() || action.canAddBilateralProject()) && (!crpClosed) && !reportingActive]
      <div class="buttons">
        <div class="buttons-content">
        
          <a class="addButton" href="[@s.url action='${crpSession}/addNewCoreProject'/]">[@s.text name="projectsList.addResearchProject" /]</a>
          <a class="addButton" href="[@s.url action='${crpSession}/addNewAdminProject'/]">[@s.text name="projectsList.addManagementProject" /]</a>
          <div class="clearfix"></div>
        </div>
      </div>
      [/#if]

      
      <div class="clearfix"></div>
      <hr/>
      
      [#-- Projects List (Other Projects) --]
      <h3 class="headTitle text-center">[@s.text name="projectsList.otherProjects" /] <br /> <small>[@s.text name="projectsList.otherProjects.help" /]</small></h3>
      <div class="loadingBlock"></div>
      <div style="display:none">[@projectList.projectsList projects=allProjects canValidate=true namespace="/projects" defaultAction="${(crpSession)!}/description"/]</div>
    </div>
    
  </article>
</section>
[@customForm.confirmJustification action="deleteProject.do" namespace="/${currentSection}" title="Remove Project" /]


[#include "/WEB-INF/global/pages/footer.ftl"]
