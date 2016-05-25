[#ftl]
[#assign title = "Welcome to MARLO" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/home/login.js" ] /]
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
    <div class="homeTitle"><b>What do you want to do ?</b></div>
    <div id="decisionTree" class="borderBox">
      <div id="newProject" class="option"><p>Enter a new project</p></div>
      <div id="updatePlanning" class="option disabled" title="This link is disabled"><p>Update an ongoing project</p></div>
      <div id="reportProject" class="option disabled" title="This link is disabled"><p>Evaluate a submitted project</p></div>
      <div class="clearfix"></div>
      <div class="addProjectButtons clearfix" style="display:none">
        <p class="title">What type of project do you want to enter?</p>
        <a href="[@s.url namespace="/planning" action='addNewCoreProject'/]"><div class="addProject"><p>CCAFS <br />Core Project</p></div></a>
        <a href="[@s.url namespace="/planning" action='addNewBilateralProject'/]"><div class="addProject"><p>Bilateral <br />Project</p></div></a>
        [#-- <p>You don't have sufficient permissions to add a project</p> --]

      </div>
    </div>
    
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]