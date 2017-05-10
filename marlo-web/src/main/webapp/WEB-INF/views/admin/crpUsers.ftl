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
    <p class="col-md-10"> [@s.text name="crpUsers.help" /] </p>
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
              [#assign usersList = (action.getUsersByRole(role.id))![] /]
              [#if usersList?has_content]
                <li role="" class="[#if role?is_first]active[/#if]"><a href="#role-${role.id}" aria-controls="home" role="tab" data-toggle="tab" title="${role.description} (${usersList?size})">${role.acronym}</a></li>
              [/#if]
            [/#list]
          </ul>
        
          <!-- Tab panes -->
          <div class="tab-content">
            [#list rolesCrp as role]
              [#assign usersList = (action.getUsersByRole(role.id))![] /]
              [#if usersList?has_content]
                <div role="tabpanel" class="tab-pane [#if role?is_first]active[/#if]" id="role-${role.id}">
                  <h4 class="sectionSubTitle ">${role.description}</h4>
                  <table class="display table table-striped table-hover usersTable" width="100%">
                    <thead>
                      <tr>
                          <th>ID</th>
                          <th>Name</th>
                          [#--  <th>Username</th> --]
                          [#if (action.hasRelations(role.acronym))??]
                          <th>${action.hasRelations(role.acronym)}</th>
                          [/#if]
                          <th>Email</th>
                          <th>Last Login (${timeZone})</th>
                      </tr>
                    </thead>
                    <tbody>
                      [#list usersList as user]
                      <tr>
                        <td>${user.id}</td>
                        <td>${(user.composedCompleteName)!}</td>
                        [#-- <td>${(user.username)!'<i>No Username</i>'}</td> --]
                        [#if (action.hasRelations(role.acronym))??]
                        <td>${(action.getRelations(user.id, role.id))!}</td>
                        [/#if]
                        <td>${(user.email)!}</td>
                        <td>${(user.lastLogin)!'<i> <span class="hide">aaa</span> Never logged in </i>'}</td>
                      </tr>
                      [/#list]
                    </tbody>
                  </table>
                </div>
              [/#if]
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

