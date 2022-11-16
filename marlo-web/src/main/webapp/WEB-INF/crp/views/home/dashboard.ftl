[#ftl]
[#assign title = "Welcome to AICCRA" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["jQuery-Timelinr","cytoscape","cytoscape-panzoom","cytoscape-qtip","qtip2","datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/home/dashboard.js?20221116",
  "${baseUrlCdn}/global/js/impactGraphic.js"
  ]
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/home/dashboard.css?20221101B",
  "${baseUrlCdn}/global/css/customDataTable.css",
  "${baseUrlCdn}/global/css/impactGraphic.css",
  "https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"
  ]
/]
[#assign currentSection = "home" /]
[#assign breadCrumb = [
  {"label":"home", "nameSpace":"", "action":""}
]/]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/projectsListTemplate.ftl" as projectList /]
[#import "/WEB-INF/global/macros/homeDashboard.ftl" as indicatorLists /]

[#assign timeline = [
  {"id":"1", "startDate":"11/28/2016", "endDate":"11/30/2016","what":"MARLO opens for Impact Pathway","who":"Flagship Leaders"},
  {"id":"2", "startDate":"12/01/2016", "endDate":"12/02/2016","what":"Create new projects according to new budget distribution; Assign W1/W2 budget to all projects.","who":"Finance Manager"},
  {"id":"3", "startDate":"12/01/2016", "endDate":"12/06/2016","what":"Pre-set projects portfolio","who":"Flagship Leaders and Regional Program Leaders"},
  {"id":"4", "startDate":"12/07/2016", "endDate":"01/16/2017","what":"MARLO opens for planning (Project Leaders) ","who":"Project Leaders"},
  {"id":"5", "startDate":"12/19/2016", "endDate":"01/10/2017","what":"Management liaison to review the plan, liaise with the PL and approve/make recommendations for project submission","who":"Flagship Leaders and Regional Program Leaders"},
  {"id":"6", "startDate":"01/11/2017", "endDate":"01/13/2017","what":"PLs to make changes accordingly and submit the project","who":"Project Leaders"},
  {"id":"5", "startDate":"01/16/2017", "endDate":"",          "what":"MARLO closes planning stage","who":"KDS Team"},
  {"id":"7", "startDate":"02/01/2017", "endDate":"02/17/2017","what":"Project Leaders and Contact Pounts will be responsible to input detailed information regarding their projects for 2016.","who":""},
  {"id":"8", "startDate":"02/20/2017", "endDate":"02/24/2017","what":"<small>Contact Points will be responsible to report on the CRP indicators and on any publications that are not directly linked to a particular project. <br>Regional Program Leaders will be responsible to complete the synthesis by MOG and by CCAFS Outcome, based on the information reported by Project Leaders.</small>","who":""},
  {"id":"9", "startDate":"02/27/2017", "endDate":"03/03/2017","what":"Flagship Program Leaders will be responsible to report on the CRP indicators, synthesis by MOG and synthesis by CCAFS Outcome based on the information reported by project leaders and Regional Program leaders.","who":""}
]/]

[#if switchSession]
  <script type="text/javascript">
    window.location.href = window.location.href;
  </script>
[/#if]
<!--  africa-color.svg  -->



  <div class="container">
    [#-- What do you want to do --]

  <section class="marlo-content">
  <section class="sectionMap">	
  <div class="containerMapsection">
    <div class="containerTextMap">
      <p class="titleMap">What is a Cluster?</p>
      <p class="textMap">A cluster is defined as the group of AICCRA main activities led by each AICCRA Country Leader (Ghana, Mali, Senegal, Ethiopia, Kenya and Zambia), AICCRA Regional Leaders (Western Africa and Eastern & Southern Africa), and  AICCRA Thematic leaders (Theme 1, Theme 2, Theme 3, and Theme 4). In each cluster, participants are involved as leaders, coordinators and collaborators with specific budget allocations for each AICCRA main activity with a set of deliverables and contributions towards our performance indicators.</p>
    </div>
    <div class="containerImgMap">
      <img src="${baseUrlCdn}/global/images/Map_africa.svg">
      <div class="dialogMap">
        <p class="dialogMapTitle">Cluster</p>
        <p class="dialogMapText"></p>
      </div>
      <div class="circleMap" id="cluster1"></div>
      <div class="circleMap" id="cluster2"></div>
      <div class="circleMap" id="cluster3"></div>
      <div class="circleMap" id="cluster4"></div>
      <div class="circleMap" id="cluster5"></div>
      <div class="circleMap" id="cluster6"></div>
      <div class="circleMap" id="cluster7"></div>
      <div class="circleMap" id="cluster8"></div>
      <div class="circleMap" id="cluster9"></div>
      <div class="circleMap" id="cluster10"></div>
      <div class="circleMap" id="cluster11"></div>
      <div class="circleMap" id="cluster12"></div>
    </div>
    </div>
  </section>
  <div class="borderMap"></div>

[#if action.hasSpecificities('homepage_timeline_active') ]
  <section class="timeline">	
    <div class="homeTitleTimeline"><b>Schedule</b></div>
    <div class="timelineAlert">
      <!--  <img src="${baseUrlCdn}/global/images/icon-transparent-warning.png" width="30">  -->
      <p class="timelineAlertText"> days left until the end of the activity</p>
    </div>
		<div class="containerTimeline">
			<!--  <a href="#item1">touch me</a>  -->
			<div class="scroll-x-containerTimeline " id="listItemTimeline">
			</div>
			<div class="sideButtonTimeline buttonLeftTimeline"><p><</p></div>
			<div class="sideButtonTimeline buttonRightTimeline"><p>></p></div>
		</div>
	</section>
[/#if]

  <section class="containerTabletItems">
  <div class="tableItemsBackground"></div>	
    <div class="containerSeccionItems">
      <div class="sectionItems">
        <div class="tableItemsTitle">
          <p>Select a category you want to call in</p>
        </div>
        <div class="containerItems">
          <div class="itemsTablet itemsActive projects" id="projects">
            <img class="itemimgprojects" src="${baseUrlCdn}/global/images/1309-load-balancer-outline.png" width="70">
            <img class="itemgifprojects" src="${baseUrlCdn}/global/images/1309-load-balancer-outline.gif" style="display:none;" width="70">
            <p>Clusters</p>
          </div>
          <div class="itemsTablet" id="deliverables">
            <img class="itemimgdeliverables" src="${baseUrlCdn}/global/images/verification.png" width="70">
            <img class="itemgifdeliverables" src="${baseUrlCdn}/global/images/verification.gif" style="display:none;" width="70">
            <p>Deliverables</p>
          </div>
          <div class="itemsTablet" id="studies">
            <img class="itemimgstudies" src="${baseUrlCdn}/global/images/oicrs_icon.png" width="70">
            <img class="itemgifstudies" src="${baseUrlCdn}/global/images/oicrs_icon.gif" style="display:none;" width="70">
            <p>OICRs & MELIAs</p>
          </div>
          <!--  <div class="itemsTablet" id="innovations">
            <img class="itemimginnovations" src="${baseUrlCdn}/global/images/ocurrencia.png" width="70">
            <img class="itemgifinnovations" src="${baseUrlCdn}/global/images/ocurrencia.gif" style="display:none;" width="70">
            <p>Innovations</p>
          </div>  -->
        </div>
      </div>
      <div id="dashboardContent" class="">
        
        <div class="col-md-12">
        [#if !action.isAiccra()]
          
          <ul class="nav nav-tabs" role="tablist">
            <li role="presentation" class="active"><a  id="projects" href="#myProjects" aria-controls="myProjects" role="tab" data-toggle="tab">[@s.text name="dashboard.myProjects.title" /]</a></li>
            <li role="presentation" style="display:none;"><a id="impact" href="#impactP" aria-controls="impactP" role="tab" data-toggle="tab">Impact pathway</a></li>
          </ul>
        [#else]
        <div class="infTableItems">[@s.text name="dashboard.homepage.description" /]</div>
        <ul class="nav nav-tabs" role="tablist">
            <li role="presentation" class="active"><a  id="projects" href="#myProjects" aria-controls="myProjects" role="tab" data-toggle="tab">[@s.text name="dashboard.myProjects.title" /]</a></li>
            <li role="presentation"><a id="deliverables" href="#myDeliverables" aria-controls="myProjects" role="tab" data-toggle="tab">[@s.text name="dashboard.myDeliverables.title" /]</a></li>
            <li role="presentation"><a id="studies" href="#myStudies" aria-controls="myProjects" role="tab" data-toggle="tab">[@s.text name="dashboard.studies.table.title" /]</a></li>
            <li role="presentation"><a id="innovations" href="#myInnovations" aria-controls="myProjects" role="tab" data-toggle="tab">[@s.text name="dashboard.innovations.table.title" /]</a></li>
            <li role="presentation" style="display:none;"><a id="impact" href="#impactP" aria-controls="impactP" role="tab" data-toggle="tab">Impact pathway</a></li>
          </ul>
        [/#if]

          <div class="tab-content">
            <div role="tabpanel" class="tab-pane fade in active" id="myProjects">
              [#if !action.isAiccra()]
                  [@projectList.dashboardProjectsList projects=myProjects canValidate=true canEdit=true namespace="/projects" defaultAction="${(crpSession)!}/description" /]
              [#else]
                [@projectList.dashboardProjectsList projects=myProjects canValidate=true canEdit=true namespace="/clusters" defaultAction="${(crpSession)!}/description" /]
              [/#if]
            </div>

            <div role="tabpanel" class="tab-pane fade" id="myDeliverables">
              [@indicatorLists.deliverablesHomeList deliverables=myDeliverables canValidate=true canEdit=true namespace="/clusters" defaultAction="${(crpSession)!}/deliverable" /]
            </div>
            
            <div role="tabpanel" class="tab-pane fade" id="myStudies">
              [@indicatorLists.studiesHomeList studies=myStudies canValidate=true canEdit=true namespace="/clusters" defaultAction="${(crpSession)!}/study" /]
            </div>
            
            <div role="tabpanel" class="tab-pane fade" id="myInnovations">
              [@indicatorLists.innovationsHomeList innovations=myInnovations canValidate=true canEdit=true namespace="/clusters" defaultAction="${(crpSession)!}/innovation" /]
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
                <span title="View full graph" id="fullscreen" class="glyphicon glyphicon-fullscreen"></span>
              </div>
            </div>
          </div>
        </div>
      </div> 
    </div>
	</section>


    <!--  <div class="homeTitle2"><b>[@s.text name="dashboard.homepage.title" /] ${(currentUser.firstName)!}!</b></div>    -->
    <!--  <div class="homeDescription2 col-md-12">[@s.text name="dashboard.homepage.description" /]</div>   -->

  

    [#if !action.isAiccra()]
    <div class="homeTitle"><b>[@s.text name="dashboard.decisionTree.title" /]</b></div>
  [/#if]
    [#-- What do you want to do --]
    <div id="decisionTree">

      [#if centerGlobalUnit]
        [#-- CENTER Impact patchway --]
        <div class="flex-container">
          <div id="newImpactPathway" class="option hvr-float">
            <a href="[@s.url action="impactPathway/${centerSession}/programimpacts"][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <p>[@s.text name="dashboard.decisionTree.defineImpact" /]</p>
            </a>
          </div>
        </div>

        [#-- Projects --]
        <div class="flex-container">
          <div id="startMonitoring" class="option hvr-float">
            <a href="[@s.url action="monitoring/${centerSession}/monitoringOutcomesList"][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <p>[@s.text name="dashboard.decisionTree.startMonitoring" /]</p>
            </a>
          </div>
        </div>

        [#-- Summaries --]
        <div class="flex-container">
          <div id="finalDes" class="option hvr-float"">
            <a href="[@s.url namespace="/projects" action='${crpSession}/projectsList'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <p>[@s.text name="dashboard.decisionTree.updateProject" /]</p>
            </a>
          </div>
        </div>

      [#else]

        [#if !aiccra]
          [#-- Add new Project --]
          <div class="flex-container">
          [#assign canAddCoreProject = (action.canAddCoreProject()) && (!crpClosed) && (!reportingActive) && (action.getActualPhase().editable)]
          [#if canAddCoreProject]<a href="[@s.url namespace="/projects" action='${crpSession}/addNewCoreProject'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">[/#if]
            <div id="newProject" class="hvr-float option ${(!canAddCoreProject)?string('disabled','')}" ${(!canAddCoreProject)?string('title="This link is disabled"','')}>
              <p>[@s.text name="dashboard.decisionTree.newProject" /]</p>
            </div>
          [#if canAddCoreProject]</a>[/#if]
          </div>

          [#-- Update an ongoing Project --]
          <div class="flex-container">
          [#assign canUpdateOngoingProjects = !crpClosed && canEditPhase ]
          [#if canUpdateOngoingProjects]<a href="[@s.url namespace="/projects" action='${crpSession}/projectsList'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"> [/#if]
            <div id="updatePlanning" class="hvr-float option ${(!canUpdateOngoingProjects)?string('disabled','')}" ${(!canUpdateOngoingProjects)?string('title="This link is disabled"','')}>
              <p>[@s.text name="dashboard.decisionTree.updateProject" /]</p>
            </div>
          [#if canUpdateOngoingProjects]</a>[/#if]
          </div>

          [#-- Evaluate Project --]
          <div class="flex-container">
            <div id="reportProject" class="option disabled" title="This link is disabled">
              <p>[@s.text name="dashboard.decisionTree.evaluateProject" /]</p>
            </div>
          </div>
        [/#if]

      <div class="clearfix"></div>
    </div>
    [/#if]


    [#-- Shorcuts --]
    <div id="shorcuts"  class="col-md-5">
    [#-- if crpSession?contains("CCAFS")  --]
    [#if false ]
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

        [#if aiccra]
            <!--  <p><h3>What is a Cluster?</h3></p><p>A cluster is defined as the group of AICCRA main activities led by each AICCRA Country Leader (Ghana, Mali, Senegal, Ethiopia, Kenya and Zambia), AICCRA Regional Leaders (Western Africa and Eastern & Southern Africa), and  AICCRA Thematic leaders (Theme 1, Theme 2, Theme 3, and Theme 4). In each cluster, participants are involved as leaders, coordinators and collaborators with specific budget allocations for each AICCRA main activity with a set of deliverables and contributions towards our performance indicators.</p>  -->
        [#else]
            [@s.text name="dashboard.aiccra.instructions" ] [@s.param] <a href="https://docs.google.com/document/d/1hy2yt6E4pJ5orGqHxBSX_ACcr72pPTwaSesQ9P6vHYQ/edit" target="_blank">here</a>.[/@s.param][/@s.text]
            <img src="${baseUrlCdn}/global/images/aiccra-planning.png" width="450">
        [/#if]

    </div>

    [#-- Dashboard --]
    <!--  <div id="dashboardContent" class="col-md-12">
      <div class="homeTitle col-md-12"></div>
      <div class="col-md-12">
      [#if !action.isAiccra()]
        <ul class="nav nav-tabs" role="tablist">
          <li role="presentation" class="active"><a  id="projects" href="#myProjects" aria-controls="myProjects" role="tab" data-toggle="tab">[@s.text name="dashboard.myProjects.title" /]</a></li>
          <li role="presentation" style="display:none;"><a id="impact" href="#impactP" aria-controls="impactP" role="tab" data-toggle="tab">Impact pathway</a></li>
        </ul>
      [#else]
      <ul class="nav nav-tabs" role="tablist">
          <li role="presentation" class="active"><a  id="projects" href="#myProjects" aria-controls="myProjects" role="tab" data-toggle="tab">[@s.text name="dashboard.myProjects.title" /]</a></li>
          <li role="presentation"><a id="deliverables" href="#myDeliverables" aria-controls="myProjects" role="tab" data-toggle="tab">[@s.text name="dashboard.myDeliverables.title" /]</a></li>
          <li role="presentation"><a id="studies" href="#myStudies" aria-controls="myProjects" role="tab" data-toggle="tab">[@s.text name="dashboard.studies.table.title" /]</a></li>
          <li role="presentation"><a id="innovations" href="#myInnovations" aria-controls="myProjects" role="tab" data-toggle="tab">[@s.text name="dashboard.innovations.table.title" /]</a></li>
          <li role="presentation" style="display:none;"><a id="impact" href="#impactP" aria-controls="impactP" role="tab" data-toggle="tab">Impact pathway</a></li>
        </ul>
      [/#if]

        <div class="tab-content">
          <div role="tabpanel" class="tab-pane fade in active" id="myProjects">
            [#if !action.isAiccra()]
                [@projectList.dashboardProjectsList projects=myProjects canValidate=true canEdit=true namespace="/projects" defaultAction="${(crpSession)!}/description" /]
            [#else]
              [@projectList.dashboardProjectsList projects=myProjects canValidate=true canEdit=true namespace="/clusters" defaultAction="${(crpSession)!}/description" /]
            [/#if]
          </div>

          <div role="tabpanel" class="tab-pane fade" id="myDeliverables">
            [@indicatorLists.deliverablesHomeList deliverables=myDeliverables canValidate=true canEdit=true namespace="/clusters" defaultAction="${(crpSession)!}/deliverable" /]
          </div>
          
          <div role="tabpanel" class="tab-pane fade" id="myStudies">
            [@indicatorLists.studiesHomeList studies=myStudies canValidate=true canEdit=true namespace="/clusters" defaultAction="${(crpSession)!}/study" /]
          </div>
          
          <div role="tabpanel" class="tab-pane fade" id="myInnovations">
            [@indicatorLists.innovationsHomeList innovations=myInnovations canValidate=true canEdit=true namespace="/clusters" defaultAction="${(crpSession)!}/innovation" /]
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
              <span title="View full graph" id="fullscreen" class="glyphicon glyphicon-fullscreen"></span>
            </div>
          </div>
        </div>
      </div>
    </div>    -->


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
