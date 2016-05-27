[#ftl]
[#assign title = "Management" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/home/dashboard.js" ] /]
[#assign currentSection = "admin" /]
[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"management", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="marlo-content">
  <div class="container">
    [#include "/WEB-INF/global/pages/breadCrumb.ftl" /]
  </div>
  <div class="container">
    <h4>CRP Admin</h4>
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        <h5>Program Management Team</h5>
        <div class="borderBox">Content</div>
        
        <h5>Flagships</h5>
        <div class="borderBox">Content</div>
        
        <h5>Regional program Managers</h5>
        <div class="borderBox">Content</div>
      </div>
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]