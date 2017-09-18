[#ftl]
[#assign title = "Welcome to MARLO" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["jQuery-Timelinr","cytoscape","cytoscape-panzoom","cytoscape-qtip","qtip2","datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrlMedia}/js/home/dashboard.js","${baseUrlMedia}/js/global/impactGraphic.js"] /]
[#assign customCSS = ["${baseUrlMedia}/css/home/dashboard.css","${baseUrlMedia}/css/global/customDataTable.css","${baseUrlMedia}/css/global/impactGraphic.css"] /]
[#assign currentSection = "home" /]
[#assign breadCrumb = [
  {"label":"home", "nameSpace":"", "action":""}
]/]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/projectsListTemplate.ftl" as projectList /]

[#assign timeline = [
  {"id":"1", "startDate":"11/28/2016", "endDate":"11/30/2016","what":"MARLO opens for Impact Pathway","who":"Flagship Leaders"},
  {"id":"2", "startDate":"12/01/2016", "endDate":"12/02/2016","what":"Create new projects according to new budget distribution; Assign W1/W2 budget to all projects.","who":"Finance Manager"},
  {"id":"3", "startDate":"12/01/2016 ", "endDate":"12/06/2016","what":"Pre-set projects portfolio","who":"Flagship Leaders and Regional Program Leaders"},
  {"id":"4", "startDate":"12/07/2016 ", "endDate":"01/16/2017","what":"MARLO opens for planning (Project Leaders) ","who":"Project Leaders"},
  {"id":"5", "startDate":"12/19/2016", "endDate":"01/10/2017","what":"Management liaison to review the plan, liaise with the PL and approve/make recommendations for project submission","who":"Flagship Leaders and Regional Program Leaders"},
  {"id":"6", "startDate":"01/11/2017", "endDate":"01/13/2017","what":"PLs to make changes accordingly and submit the project","who":"Project Leaders"},
  {"id":"5", "startDate":"01/16/2017", "endDate":"","what":"MARLO closes planning stage","who":"KDS Team"},
  {"id":"7", "startDate":"02/01/2017", "endDate":"02/17/2017","what":"Project Leaders and Contact Pounts will be responsible to input detailed information regarding their projects for 2016.","who":""},
  {"id":"8", "startDate":"02/20/2017", "endDate":"02/24/2017","what":"<small>Contact Points will be responsible to report on the CRP indicators and on any publications that are not directly linked to a particular project. <br>Regional Program Leaders will be responsible to complete the synthesis by MOG and by CCAFS Outcome, based on the information reported by Project Leaders.</small>","who":""},
  {"id":"9", "startDate":"02/27/2017", "endDate":"03/03/2017","what":"Flagship Program Leaders will be responsible to report on the CRP indicators, synthesis by MOG and synthesis by CCAFS Outcome based on the information reported by project leaders and Regional Program leaders.","who":""}
]/]

[#if switchSession]
  <script type="text/javascript">
        location.reload();
  </script>
[/#if]

<section class="marlo-content">
  <div class="container">
    [#-- What do you want to do --]
    <div class="homeTitle"><b>[@s.text name="dashboard.decisionTree.title" /]</b></div>
    <div id="decisionTree" class="borderBox">
      [#-- Add new Project --]
      [#if !crpClosed && !reportingActive]<a href="[@s.url namespace="/projects" action='${crpSession}/addNewCoreProject'/]">[/#if]
        <div id="newProject" class="option ${(crpClosed)?string('disabled','')}" ><p>[@s.text name="dashboard.decisionTree.newProject" /]</p></div>
      [#if !crpClosed && !reportingActive]</a>[/#if]
      
      [#-- Update an ongoing Project --]
      [#if !crpClosed]<a href="[@s.url namespace="/projects" action='${crpSession}/projectsList'/]"> [/#if]
        <div id="updatePlanning" class="option ${crpClosed?string('disabled','')}" ><p>[@s.text name="dashboard.decisionTree.updateProject" /]</p></div>
      [#if !crpClosed]</a>[/#if]
      
      [#-- Evaluate Project --]
      <div id="reportProject" class="option disabled" title="This link is disabled"><p>[@s.text name="dashboard.decisionTree.evaluateProject" /]</p></div>
      <div class="clearfix"></div>
    </div>
    
    
    [#-- Shorcuts --]    
    <div id="shorcuts"  class="col-md-5">
    [#if crpSession?contains("CCAFS")]
      <div class="homeTitle"><strong>Timeline</strong></div>   
      <div class="borderBox col-md-12">
        <div id="timeline">
        <span class="timelineControl leftControl glyphicon glyphicon-chevron-left"></span>
        <span class="timelineControl rigthControl control glyphicon glyphicon-chevron-right"></span>
          <ul id="dates">
          [#list timeline as time]
            <li><a href="#${time.id}">[#if time.startDate?has_content]${(time.startDate)?date("MM/dd/yyyy")}[/#if]</a></li>
          [/#list]
          </ul>
          
          <div class="borderBox">
            <ul id="issues">
            [#list timeline as time]
              <li class="infoActions" id="${time.id}">
                <span class="startDate hidden">${time.startDate}</span>
                <span class="endDate hidden">${time.endDate}</span>
                <h1>[#if time.startDate?has_content]${((time.startDate)?date("MM/dd/yyyy"))?split(",")[0]}[/#if] [#if time.endDate?has_content]- ${((time.endDate)?date("MM/dd/yyyy"))?split(",")[0]}[/#if]</h1>
                <hr />
                <label for="">What happen?</label>
                <p> ${time.what}</p>
                [#if (time.who?has_content)]
                <hr />
                <label for="">Who?</label>
                <p>${time.who}</p>
                [/#if]
              </li>
            [/#list]
            </ul> 
          </div>
        </div>
      </div>
      [/#if]
    </div>     
    
    [#-- Dashboard --]   
    <div id="dashboardContent" class="col-md-7">
      <div class="homeTitle col-md-12"><strong>Dashboard</strong></div>
      <div class=" col-md-12">
        <ul class="nav nav-tabs" role="tablist">
          <li role="presentation" class="active"><a  id="projects" href="#myProjects" aria-controls="myProjects" role="tab" data-toggle="tab">My projects</a></li>
          <li role="presentation" style="display:none;"><a id="impact" href="#impactP" aria-controls="impactP" role="tab" data-toggle="tab">Impact pathway</a></li>
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
              [#-- Download button--]
             [#--  <span id="buttonShowAll"><span class="glyphicon glyphicon-download-alt"></span></span>--]
              <div id="impactGraphic" ></div>
              [#-- Download button--]
              <span title="View full graph" id="fullscreen" class="glyphicon glyphicon-fullscreen"></span>
            </div>
          </div>
        </div>  
      </div>    
    </div>
    
    
    <div id="impactGraphic-content"  style="display:none;" >
  
  [#-- Information panel --]
  <div id="infoRelation" class="panel panel-default">
    <div class="panel-heading"><strong>Relations</strong></div>
    <div id="infoContent" class="panel-body">
     <ul></ul>
    </div>
  </div>
  
  [#-- Controls --]
  <div id="controls" class="">
    <span id="zoomIn" class="glyphicon glyphicon-zoom-in tool"></span>
    <span id="zoomOut" class="glyphicon glyphicon-zoom-out tool "></span>
    <span id="panRight" class="glyphicon glyphicon-arrow-right tool "></span>
    <span id="panDown" class="glyphicon glyphicon-arrow-down tool "></span>
    <span id="panLeft" class="glyphicon glyphicon-arrow-left tool "></span>
    <span id="panUp" class="glyphicon glyphicon-arrow-up tool "></span>
    <span id="resize" class="glyphicon glyphicon-resize-full  tool"></span>
  </div>
  
  [#-- Download button--]
  <a class="download" href=""><span title="download" id="buttonDownload"><span class="glyphicon glyphicon-download-alt"></span></span></a>
  
  <div id="impactGraphic-fullscreen"></div>
</div>
  </div>
 
 
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]