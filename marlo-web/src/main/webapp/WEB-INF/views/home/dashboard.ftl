[#ftl]
[#assign title = "Welcome to MARLO" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["jQuery-Timelinr","cytoscape","cytoscape-panzoom","cytoscape-qtip","qtip2","datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrl}/js/home/dashboard.js","${baseUrl}/js/global/impactGraphic.js"] /]
[#assign customCSS = ["${baseUrl}/css/home/dashboard.css","${baseUrl}/css/global/customDataTable.css"] /]
[#assign currentSection = "home" /]
[#assign breadCrumb = [
  {"label":"home", "nameSpace":"", "action":""}
]/]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/projectsListTemplate.ftl" as projectList /]

<section class="marlo-content">
  <div class="container">
    [#-- What do you want to do --]
    <div class="homeTitle"><b>[@s.text name="dashboard.decisionTree.title" /]</b></div>
    <div id="decisionTree" class="borderBox">
      <div id="newProject" class="option"><p>[@s.text name="dashboard.decisionTree.newProject" /]</p></div>
      <div id="updatePlanning" class="option disabled" title="This link is disabled"><p>[@s.text name="dashboard.decisionTree.updateProject" /]</p></div>
      <div id="reportProject" class="option disabled" title="This link is disabled"><p>[@s.text name="dashboard.decisionTree.evaluateProject" /]</p></div>
      <div class="clearfix"></div>
      <div class="addProjectButtons clearfix" style="display:none">
        <p class="title">[@s.text name="dashboard.decisionTree.typeProjectQuestion" /]</p>
        [#if action.hasPermission("addCoreProject") || action.hasPermission("addBilateralProject")]
          [#if action.hasPermission("addCoreProject")]
            <a href="[@s.url namespace="/projects" action='${crpSession}/addNewCoreProject'/]"><div class="addProject"><p>[@s.text name="dashboard.decisionTree.coreProject" /]</p></div></a>
          [/#if]
          [#if action.hasPermission("addBilateralProject")]
            <a href="[@s.url namespace="/projects" action='${crpSession}/addNewBilateralProject'/]"><div class="addProject"><p>[@s.text name="dashboard.decisionTree.bilateralProject" /]</p></div></a>
          [/#if]
        [#else]
          <p>[@s.text name="dashboard.decisionTree.notPermissions" /]</p>
        [/#if]
      </div>
    </div>
    
    [#-- Shorcuts --]    
    <div id="shorcuts" class="col-md-5">
      <div class="homeTitle"><strong>Timeline</strong></div>   
      <div class="borderBox col-md-12">
        <div id="timeline">
          <ul id="dates">
            <li><a href="#1900">Fri Mar 18th</a></li>
            <li><a href="#1700">Mon Mar 21st</a></li>
            <li><a href="#1600">Open 4/2017</a></li>
            <li><a href="#1400">Close 5/2017</a></li>
          </ul>
          
          <div class="borderBox">
            <ul id="issues">
              <li id="1900">
                <h1>18/3/2017</h1>
                <p>Regional Program Leaders will have until Friday, March 18th to complete the Outcome and MOG synthesis</p>
              </li>
              <li id="1700">
                <h1>2/2017</h1>
                <p>Flagship Leaders are expected to start from March 21st and complete by April 1st, 2016; and</p>
              </li>
              <li id="1600">
                <h1>3/2017</h1>
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Repudiandae maxime autem expedita laborum aliquid quibusdam deleniti in quisquam assumenda est officia modi ipsam laboriosam possimus blanditiis reprehenderit adipisci mollitia officiis.</p>
              </li>
              <li id="1400">
                <h1>4/2017</h1>
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Repudiandae maxime autem expedita laborum aliquid quibusdam deleniti in quisquam assumenda est officia modi ipsam laboriosam possimus blanditiis reprehenderit adipisci mollitia officiis.</p>
              </li>
            </ul> 
          </div>
        </div>
      </div>
    </div>     
    
    [#-- Dashboard --]   
    <div id="dashboardContent" class="col-md-7">
      <div class="homeTitle col-md-12"><strong>Dashboard</strong></div>
      <div class=" col-md-12">
        <ul class="nav nav-tabs" role="tablist">
          <li role="presentation" class="active"><a  id="projects" href="#myProjects" aria-controls="myProjects" role="tab" data-toggle="tab">My projects</a></li>
          <li role="presentation"><a id="impact" href="#impactP" aria-controls="impactP" role="tab" data-toggle="tab">Impact pathway</a></li>
        </ul>
        
        <div class="tab-content">
          <div role="tabpanel" class="tab-pane fade in active" id="myProjects">
            [@projectList.dashboardProjectsList projects=myProjects canValidate=true canEdit=true namespace="/projects" defaultAction="${(crpSession)!}/description" /]
          </div>
          
          <div role="tabpanel" class="tab-pane fade" id="impactP">
            <div id="infoRelations" class="panel panel-default">
              <div class="panel-heading"><strong>Relations</strong></div>
              <div id="infoContent" class="panel-body">
               <ul></ul>
              </div>
            </div>
            <div id="contentGraph">
              <div id="impactGraphic" ></div>
            </div>
          </div>
        </div>  
      </div>    
    </div>
  </div>
 
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]