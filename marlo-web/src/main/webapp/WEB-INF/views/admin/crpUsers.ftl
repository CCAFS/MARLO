[#ftl]
[#assign title = "CRP Users" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [ "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
                      "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
                      "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
                      "${baseUrl}/js/admin/crpUsers.js" 
                    ] /]
[#assign customCSS = [ "${baseUrl}/css/admin/crpUsers.css" ] /]
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
        
        
        [#--Users tables --]
        <h4 class="sectionTitle">[@s.text name="crpUsers.title" /]</h4>
        <div class="">
          <!-- Nav tabs -->
          <ul class="nav nav-tabs" role="tablist">
            [#list rolesCrp as role]
              <li role="" class="[#if role?is_first]active[/#if]"><a href="#role-${role.id}" aria-controls="home" role="tab" data-toggle="tab">${role.acronym}</a></li>
            [/#list]
          </ul>
        
          <!-- Tab panes -->
          <div class="tab-content">
            [#list rolesCrp as role]
              <div role="tabpanel" class="tab-pane [#if role?is_first]active[/#if]" id="role-${role.id}">
                <h4>${role.description}</h4>
                <table class="display table table-striped table-hover usersTable" width="100%">
                  <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Active</th>
                        <th>Last Login</th>
                    </tr>
                  </thead>
                  <tbody>
                    [#list action.getUsersByRole(role.id) as user]
                    <tr>
                      <td>${user.id}</td>
                      <td>${(user.composedCompleteName)!}</td>
                      <td>${(user.username)!}</td>
                      <td>${(user.email)!}</td>
                      <td><div class="text-center"><img src="${baseUrl}/images/global/checked-${user.active?string}.png" alt="${user.active?string}" /></div></td>
                      <td>${(user.lastLogin)!}</td>
                    </tr>
                    [/#list]
                  </tbody>
                </table>
              </div>
            [/#list]
          </div>
        </div>
        
        [@s.form action=actionName enctype="multipart/form-data" ]  
        [/@s.form]
      </div>
    </div>
  </div>
</section>


[#include "/WEB-INF/global/pages/footer.ftl" /]

