[#ftl]
[#assign title = "Welcome to MiLE" /]
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

<section class="marlo-content">
  <div class="container">
    [#-- What do you want to do --]
    <div class="homeTitle"><b>[@s.text name="dashboard.decisionTree.title" /]</b></div>
    <div id="decisionTree" class="borderBox">
      <div id="newImpactPathway" class="option">
        <a href="[@s.url action="impactPathway/${centerSession}/programimpacts"][@s.param name="edit" value="true"/][/@s.url]">
          <p>[@s.text name="dashboard.decisionTree.defineImpact" /]</p>
        </a>
      </div>
      
      <div id="startMonitoring" class="option ${action.canAccessSuperAdmin()?string('','disabled')}">
  [#if action.canAccessSuperAdmin()]
      <a href="[@s.url action="monitoring/${centerSession}/projectList"][@s.param name="edit" value="true"/][/@s.url]">
        <p>[@s.text name="dashboard.decisionTree.startMonitoring" /]</p>
      </a>  
  [#else]
      <p>[@s.text name="dashboard.decisionTree.startMonitoring" /]</p>
  [/#if]        
      </div>
    
      <div id="finalDes" class="option disabled" title="This link is disabled"><p>[@s.text name="dashboard.decisionTree.finishDes" /]</p></div>
      <div class="clearfix"></div>
    </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]