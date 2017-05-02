[#ftl]
[#assign title = "Site Integration" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2", "flat-flags"] /]
[#assign customJS = ["${baseUrl}/js/global/usersManagement.js", "${baseUrl}/js/admin/siteIntegration.js","${baseUrl}/js/global/fieldsValidation.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/admin/siteIntegration.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "users" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"users", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="siteIntegration.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]  
        
        [#--Users tables --]
        <h4 class="sectionTitle">[@s.text name="crpUsers.title" /]</h4>
        <div class="borderBox">
          <table id="marloUsersTable" class="display table table-striped table-hover" width="100%">
            <thead>
              <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Username</th>
                  <th>Email</th>
                  <th>Active</th>
                  <th>AutoSave</th>
                  <th>Last Login</th>
              </tr>
            </thead> 
          </table>
        </div>
        
        [/@s.form]
      </div>
    </div>
  </div>
</section>


[#include "/WEB-INF/global/pages/footer.ftl" /]

