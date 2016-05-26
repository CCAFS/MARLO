[#ftl]
[#assign title = "Welcome to MARLO" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/home/dashboard.js" ] /]
[#assign currentSection = "home" /]
[#assign breadCrumb = [
  {"label":"home", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="marlo-content">
  <div class="container">
    [#include "/WEB-INF/global/pages/breadCrumb.ftl" /]
  </div>
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
        <a href="[@s.url namespace="/planning" action='addNewCoreProject'/]"><div class="addProject"><p>[@s.text name="dashboard.decisionTree.coreProject" /]</p></div></a>
        <a href="[@s.url namespace="/planning" action='addNewBilateralProject'/]"><div class="addProject"><p>[@s.text name="dashboard.decisionTree.bilateralProject" /]</p></div></a>
        [#--<p>[@s.text name="dashboard.decisionTree.notPermissions" /]</p>--]
      </div>
    </div>  
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]