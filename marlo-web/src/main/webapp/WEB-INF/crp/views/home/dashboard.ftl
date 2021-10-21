[#ftl]
[#assign title = "Welcome to MARLO" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["jQuery-Timelinr","cytoscape","cytoscape-panzoom","cytoscape-qtip","qtip2","datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "https://www.gstatic.com/charts/loader.js",
  "${baseUrlMedia}/js/home/dashboard.js?20211021a",
  "${baseUrlCdn}/global/js/impactGraphic.js"
  ] 
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/home/dashboard.css?20211021a",
  "${baseUrlCdn}/global/css/customDataTable.css",
  "${baseUrlCdn}/global/css/impactGraphic.css",
  "${baseUrlCdn}/global/css/global.css?20210827a"
  ] 
/]
[#assign currentSection = "home" /]
[#assign breadCrumb = [
  {"label":"home", "nameSpace":"", "action":""}
]/]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
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

<div id="loadingScreen" style="position: absolute; width: 100%; height: 200vw; background: white; z-index: 999">
  <div class="loadingBlock"></div>
</div>
<section class="marlo-content">
  <div class="container">

    [#--  Home Graphs  --]
    <div class="col-md-12">
    <div class="homeTitle"><b>[@s.text name="dashboard.homepage.title" /] ${(currentUser.firstName)!}!</b></div>
    <div class="homeDescription col-md-12">[@s.text name="dashboard.homepage.description" /]</div>
    </div>

    [#-- Dashboard --]   
    <span id="actualPhase" style="display: none;">${action.isSelectedPhaseAR2021()?c}</span>
    <div id="dashboardContent" class="col-md-12">
      <div class="toggleBtnGraphs">
        <span class="glyphicon glyphicon-eye-open icon-show"></span>
        <p class="toggleTxtGraphs">Hide/Show Graphs<p>
      </div>
      <div class="col-md-12">
        <ul class="nav nav-tabs" role="tablist">
          <li role="presentation" class="active"><a  id="homeProjects" href="#myProjects" aria-controls="myProjects" role="tab" data-toggle="tab">[@s.text name="dashboard.projects.table.title" /]</a></li>
          <li role="presentation"><a id="deliverables" href="#myDeliverables" aria-controls="myDeliverables" role="tab" data-toggle="tab">[@s.text name="dashboard.deliverables.table.title" /]</a></li>
          <li role="presentation"><a id="oicrs" href="#myOicrs" aria-controls="myOicrs" role="tab" data-toggle="tab">[@s.text name="dashboard.oicrs.table.title" /]</a></li>
          <li role="presentation"><a id="melias" href="#myMelias" aria-controls="myMelias" role="tab" data-toggle="tab">[@s.text name="dashboard.melias.table.title" /]</a></li>
          <li role="presentation"><a id="innovations" href="#myInnovations" aria-controls="myInnovations" role="tab" data-toggle="tab">[@s.text name="dashboard.innovations.table.title" /]</a></li>
          <li role="presentation"><a id="policies" href="#myPolicies" aria-controls="myPolicies" role="tab" data-toggle="tab">[@s.text name="dashboard.policies.table.title" /]</a></li>
          <li role="presentation" style="display:none;"><a id="impact" href="#impactP" aria-controls="impactP" role="tab" data-toggle="tab">Impact pathway</a></li>
        </ul>
        
        [#if (action.canAcessCrpAdmin() || action.isRole("DM") || action.isRole("FPL") || action.isRole("FPM") || action.isRole("ML") || action.isRole("PMU") || action.isRole("RPL") || action.isRole("RPM") || action.canAccessSuperAdmin())!false]
          <div class="homeGraphs col-md-12">
            <div class="col-md-6">
              <div id="chartHome1" class="chartBox chartSimpleBox" style="height: 250px;"></div>
            </div>

            <div class="col-md-6">
              <div id="chartHome2" class="chartBox chartSimpleBox" style="height: 250px;"></div>
            </div>
          </div>
        [/#if]
        
        <div class="tab-content">
          <div role="tabpanel" class="tab-pane fade in active" id="myProjects">
            [@indicatorLists.projectsHomeList projects=myProjects canValidate=true canEdit=true namespace="/projects" defaultAction="${(crpSession)!}/description" /]
          </div>
          
          <div role="tabpanel" class="tab-pane fade" id="myDeliverables">
            [@indicatorLists.deliverablesHomeList deliverables=myDeliverables canValidate=true canEdit=true namespace="/projects" defaultAction="${(crpSession)!}/deliverable" /]
          </div>

          <div role="tabpanel" class="tab-pane fade" id="myOicrs">
            [@indicatorLists.studiesHomeList studies=myOicrs canValidate=true canEdit=true namespace="/projects" defaultAction="${(crpSession)!}/study" /]
          </div>

          <div role="tabpanel" class="tab-pane fade" id="myMelias">
            [@indicatorLists.studiesMeliasHomeList studies=myMelias canValidate=true canEdit=true namespace="/projects" defaultAction="${(crpSession)!}/study" /]
          </div>

          <div role="tabpanel" class="tab-pane fade" id="myInnovations">
            [@indicatorLists.innovationsHomeList innovations=myInnovations canValidate=true canEdit=true namespace="/projects" defaultAction="${(crpSession)!}/innovation" /]
          </div>

          <div role="tabpanel" class="tab-pane fade" id="myPolicies">
            [@indicatorLists.policiesHomeList policies=myPolicies canValidate=true canEdit=true namespace="/projects" defaultAction="${(crpSession)!}/policy" /]
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