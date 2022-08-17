[#ftl]
[#assign title = "MARLO Projects" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs","font-awesome"] /]
[#assign customJS = [
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlMedia}/js/projects/projectsList.js"
  ]
/]
[#assign customCSS = [
  "${baseUrlCdn}/global/css/customDataTable.css",
  "${baseUrlMedia}/css/projects/projectsList.css"]
/]

[#if !action.isAiccra()]
  [#assign currentSection = "projects" /]
[#else]
  [#assign currentSection = "clusters" /]
[/#if]

[#assign currentStage = (filterBy)!"all" /]


[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"${currentSection}", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/projectsListTemplate.ftl" as projectList /]
<!--
<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectsList.help"][@s.param]${currentCycle}[/@s.param][/@s.text] </p>
  </div>
  <div style="display:none" class="viewMore closed"></div>
</div>
-->
<div class="animated flipInX container  viewMore-block containerAlertMargin">
  <div class=" containerAlert alert-leftovers alertColorBackgroundInfo ">
    <div class="containerLine alertColorInfo"></div>
    <div class="containerIcon">
      <div class="containerIcon alertColorInfo">
        <img src="${baseUrlCdn}/global/images/icon-question.png" />        
      </div>
    </div>
    <div class="containerText col-md-12">
      <p class="alertText">
        [@s.text name="projectsList.help"][@s.param]${currentCycle}[/@s.param][/@s.text] 
      </p>
    </div>
  </div>
</div>

<section class="container">
  <article class="row" id="mainInformation">
    <div class="col-md-12">
      <div class="loadingBlock"></div>
      <div style="display:none" >
        <!-- Nav tabs -->
        <ul class="nav nav-tabs" role="tablist">
          <li role="presentation" class="active">
            <a href="#active-tab" aria-controls="home" role="tab" data-toggle="tab">
              <strong><span class="glyphicon glyphicon-flag"></span>  [@s.text name="projectsList.active"/] </strong> <br /><small>([@s.text name="projectsList.active${reportingActive?string('Reporting', 'Planning')}.help"/])</small>
            </a>
          </li>
          [#if !reportingActive]
          <li role="presentation">
            <a href="#archived-tab" aria-controls="profile" role="tab" data-toggle="tab">
              <strong><span class="glyphicon glyphicon-inbox"></span> [@s.text name="projectsList.archived"/] </strong> <br /><small>([@s.text name="projectsList.archived.help"/])</small>
            </a>
          </li>
          [/#if]
        </ul>

        <!-- Tab panes -->
        <div class="tab-content">
          <div role="tabpanel" class="tab-pane active" id="active-tab">
            [#-- Projects List (My Projects) --]
            <h3 class="headTitle text-center">[@s.text name="projectsList.yourProjects"/]</h3>
            [@projectList.projectsList projects=myProjects canValidate=true canEdit=true namespace="/${currentSection}" defaultAction="${(crpSession)!}/description" /]
            <hr/>
            [#-- Projects List (Other Projects) --]
            <h3 class="headTitle text-center">[@s.text name="projectsList.otherProjects" /] <br /> <small>[@s.text name="projectsList.otherProjects.help" /]</small></h3>
            [@projectList.projectsList projects=allProjects canValidate=true namespace="/${currentSection}" defaultAction="${(crpSession)!}/description"/]
          </div>
          [#if !reportingActive]
          <div role="tabpanel" class="tab-pane" id="archived-tab">
            [#-- Archived Projects List (My Projects) --]
            <h3 class="headTitle text-center">[@s.text name="projectsList.archivedProjects"/]</h3>
            [@projectList.projectsListArchived projects=(closedProjects)! canValidate=false canEdit=false namespace="/${currentSection}" defaultAction="${(crpSession)!}/description" /]
          </div>
          [/#if]
        </div>
      </div>
      [#-- Section Buttons --]
      [#if (action.canAddCoreProject() || action.canAddBilateralProject()) && (!crpClosed) && !reportingActive && action.getActualPhase().editable]
      <div class="buttons">
        <div class="buttons-content">
          <a class="addButton" href="[@s.url action='${crpSession}/addNewCoreProject'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">[@s.text name="projectsList.addResearchProject" /]</a>
          <a class="addButton" href="[@s.url action='${crpSession}/addNewAdminProject'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">[@s.text name="projectsList.addManagementProject" /]</a>
          <div class="clearfix"></div>
        </div>
      </div>
      <div class="clearfix"></div>
      [/#if]
    </div>

  </article>
</section>
[@customForm.confirmJustification action="deleteProject.do" namespace="/${currentSection}" title="Remove Project" /]


[#include "/WEB-INF/global/pages/footer.ftl"]
