[#ftl]
[#assign title = "Welcome to MARLO" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2","jQuery-Timelinr"] /]
[#assign customJS = ["${baseUrl}/js/home/dashboard.js","${baseUrl}/js/global/timeline.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/global/timeline.css","${baseUrl}/css/home/dashboard.css" ] /]
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
    
    [#-- Shorcuts --]
    <div class="homeTitle col-md-7"><strong>Shorcuts</strong></div>
    <div class="homeTitle col-md-5 "><strong>Dashboard</strong></div>
    
    <div id="shorcuts" class="borderBox col-md-5">   
      <div id="timeline">
        <ul id="dates">
          <li><a href="#1900">1900</a></li>
          <li><a href="#1700">1700</a></li>
          <li><a href="#1600">1600</a></li>
          <li><a href="#1400">1400</a></li>
          <li><a href="#1300">1300</a></li>
          <li><a href="#1200">1200</a></li>
        </ul>
        <ul id="issues">
          <li id="1900">
            <h1>1900</h1>
            <p>Donec semper quam scelerisque tortor dictum gravida. In hac habitasse platea dictumst. Nam pulvinar, odio sed rhoncus suscipit, sem diam ultrices mauris, eu consequat purus metus eu velit. Proin metus odio, aliquam eget molestie nec, gravida ut sapien. Phasellus quis est sed turpis sollicitudin venenatis sed eu odio. Praesent eget neque eu eros interdum malesuada non vel leo. Sed fringilla porta ligula.</p>
          </li>
          <li id="1700">
            <h1>1700</h1>
            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Repudiandae maxime autem expedita laborum aliquid quibusdam deleniti in quisquam assumenda est officia modi ipsam laboriosam possimus blanditiis reprehenderit adipisci mollitia officiis.</p>
          </li>
          <li id="1600">
            <h1>1600</h1>
            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Repudiandae maxime autem expedita laborum aliquid quibusdam deleniti in quisquam assumenda est officia modi ipsam laboriosam possimus blanditiis reprehenderit adipisci mollitia officiis.</p>
          </li>
          <li id="1400">
            <h1>1400</h1>
            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Repudiandae maxime autem expedita laborum aliquid quibusdam deleniti in quisquam assumenda est officia modi ipsam laboriosam possimus blanditiis reprehenderit adipisci mollitia officiis.</p>
          </li>
          <li id="1300">
            <h1>1300</h1>
            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Repudiandae maxime autem expedita laborum aliquid quibusdam deleniti in quisquam assumenda est officia modi ipsam laboriosam possimus blanditiis reprehenderit adipisci mollitia officiis.</p>
          </li>
          <li id="1200">
            <h1>1200</h1>
            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Repudiandae maxime autem expedita laborum aliquid quibusdam deleniti in quisquam assumenda est officia modi ipsam laboriosam possimus blanditiis reprehenderit adipisci mollitia officiis.</p>
          </li>
        </ul> 
      </div>
    </div>     
    <div id="dashboardContent" class="borderBox col-md-5 ">
      <ul class="nav nav-tabs" role="tablist">
        <li role="presentation" class="active"><a href="#myProjects" aria-controls="myProjects" role="tab" data-toggle="tab">My projects</a></li>
        <li role="presentation"><a href="#impactP" aria-controls="impactP" role="tab" data-toggle="tab">Impact pathway</a></li>
      </ul>
      
      <div class="tab-content">
        <div role="tabpanel" class="tab-pane fade in active" id="myProjects">Lorem ipsum dolor sit amet, consectetur adipisicing elit. Architecto quisquam qui delectus nemo odit reprehenderit itaque non perferendis cupiditate cum illo quasi minus ducimus repellendus accusantium eveniet ullam repudiandae eligendi. Lorem ipsum dolor sit amet, consectetur adipisicing elit. Nostrum provident voluptas assumenda hic accusantium eum similique ratione quaerat facere quam tempore atque perspiciatis consequatur omnis aliquam eveniet repudiandae! Quasi delectus.</div>
        <div role="tabpanel" class="tab-pane fade" id="impactP">Lorem ipsum dolor sit amet, consectetur adipisicing elit. Doloremque tenetur optio iure hic officiis itaque dicta earum placeat nam aspernatur sint similique quibusdam quisquam aliquam eaque tempore minus incidunt quo.</div>
      </div>      
    </div>
  </div>
  
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]