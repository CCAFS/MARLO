[#ftl]
[#assign title = "Users" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js",
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlMedia}/js/admin/crpUsers.js?20260219v4" 
  ] 
/]
[#assign customCSS = [
  "https://cdn.datatables.net/buttons/1.3.1/css/buttons.dataTables.min.css",
  "${baseUrlMedia}/css/admin/crpUsers.css"
] /]
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
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="crpUsers.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
      
        [#-- Create User Guest --]
        <h4 class="sectionTitle">Create Guest User</h4>
        <div class="borderBox">
          <p id="guestUserMessage" class="note" style="display:none"></p>
          [@s.form action=actionName enctype="multipart/form-data" ]
          <div class="form-group">
            <div class="form-group row">
              <div class="col-md-10">
                [@customForm.input name="user.email" i18nkey="guestUsers.email" value="${(user.email)!}" className="userEmail" type="text"  required=true editable=true /]
              </div>
              <div class="col-md-5">
                [#if namespace?contains('superadmin')]
                  [@customForm.select name="selectedGlobalUnitID" className="selectedGlobalUnitID" i18nkey="guestUsers.globalUnit" listName="crps" keyFieldName="id"  displayFieldName="acronym" required=true editable=true/]
                [#else]
                  <input type="hidden" name="selectedGlobalUnitID" class="selectedGlobalUnitID" value="${(crpSession)!}" />
                [/#if]
              </div>
            </div>
            <div class="firstLastName form-group row">
              <div class="col-md-5 ">
                [@customForm.input name="user.firstName" help="Not required for CGIAR emails"  i18nkey="guestUsers.firstName" value="${(user.firstName)!}" className="userFirstName" type="text"  required=(isCgiarUser)!true editable=true /]
              </div>
              <div class="col-md-5">
                [@customForm.input name="user.lastName" help="Not required for CGIAR emails" i18nkey="guestUsers.lastName" value="${(user.lastName)!}" className="userLastName" type="text"  required=(isCGIARUser)!true  editable=true /]
              </div>
            </div>
            </br>
            [#-- SEND EMAIL --]
            [#-- 
            <div class="pull-left">
              [@customForm.checkmark  id="sendEmail"  i18nkey="guestUsers.sendEmail" name="emailSend" value="true" checked=true editable=true /]
            </div>
             --]
          </div>

          
          <div class="buttons">
            <div class="buttons-content">
              [@s.submit type="button" name="save" cssClass="button-save disabled"]<span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add User [/@s.submit]
            </div>
          </div>
          [/@s.form]
        </div>
        
        
        [#--Users tables --]
        <h4 class="sectionTitle">[@s.text name="crpUsers.title" /]</h4>
        <div class="">
          <!-- Nav tabs -->
          <ul class="nav nav-tabs" role="tablist">
            [#-- All Users Tab --]
            [#assign allUsersList = (action.getAllUsersWithRoles())![] /]
            <li role="" class="active"><a href="#all-users" aria-controls="all-users" role="tab" data-toggle="tab" title="All Users (${allUsersList?size})">All Users</a></li>
            
            [#-- Active Users Tab (users with login) --]
            [#assign activeUsersList = allUsersList?filter(u -> u.lastLogin??) /]
            <li role=""><a href="#active-users" aria-controls="active-users" role="tab" data-toggle="tab" title="Users with Login (${activeUsersList?size})">Active Users</a></li>
            
            [#-- Role-specific tabs --]
            [#list rolesCrp as role]
              [#assign usersList = (action.getUsersByRole(role.id))![] /]
              [#if role.description?substring(0, 3) != "API"]
                [#if usersList?has_content]
                [#if action.isAiccra()]
                  <li role=""><a href="#role-${role.id}" aria-controls="role-${role.id}" role="tab" data-toggle="tab" title="${role.description} (${usersList?size})">${role.aiccraAcronymDimanic}</a></li>
                [#else] 
                   <li role=""><a href="#role-${role.id}" aria-controls="role-${role.id}" role="tab" data-toggle="tab" title="${role.description} (${usersList?size})">${role.acronymDimanic}</a></li>
                [/#if]
                [/#if]
              [/#if]
            [/#list]
          </ul>
        
          <!-- Tab panes -->
          <div class="tab-content">
            [#-- All Users Tab Content --]
            <div role="tabpanel" class="tab-pane active" id="all-users">
              <h4 class="sectionSubTitle">All Users</h4>
              <p class="help-block">Complete list of users with their assigned roles in the system</p>
              <table class="display table table-striped table-hover usersTable" width="100%">
                <thead>
                  <tr>
                      <th>ID</th>
                      <th>Name</th>
                      <th>Roles</th>
                      <th>Email</th>
                      <th>Last Login (UTC)</th>
                  </tr>
                </thead>
                <tbody>
                  [#list allUsersList as user]
                  [#assign userRoles = (action.getUserRoles(user.id))! /]
                  <tr>
                    <td>${user.id}</td>
                    <td><strong>${(user.composedCompleteName)!}</strong></td>
                    <td>
                      [#if userRoles?has_content]
                        [#assign rolesList = userRoles?split(", ") /]
                        [#list rolesList as role]
                          <span class="label label-info role-badge" style="margin-right: 4px; font-size: 0.85em;">${role}</span>
                        [/#list]
                      [#else]
                        <span class="text-muted"><em>No roles</em></span>
                      [/#if]
                    </td>
                    <td><a href="mailto:${(user.email)!}" style="text-decoration: none;">${(user.email)!}</a></td>
                    <td>[#if user.lastLogin??]${user.lastLogin}[#else]<i><span class="glyphicon glyphicon-time" style="font-size: 0.8em;"></span> Never logged in</i>[/#if]</td>
                  </tr>
                  [/#list]
                </tbody>
              </table>
            </div>
            
            [#-- Active Users Tab Content (users with login) --]
            <div role="tabpanel" class="tab-pane" id="active-users">
              <h4 class="sectionSubTitle">Active Users (with Login History)</h4>
              <p class="help-block">${activeUsersList?size} user(s) have logged into the system at least once</p>
              <table class="display table table-striped table-hover usersTable" width="100%">
                <thead>
                  <tr>
                      <th>ID</th>
                      <th>Name</th>
                      <th>Roles</th>
                      <th>Email</th>
                      <th>Last Login (UTC)</th>
                  </tr>
                </thead>
                <tbody>
                  [#list activeUsersList as user]
                  [#assign userRoles = (action.getUserRoles(user.id))! /]
                  <tr>
                    <td>${user.id}</td>
                    <td><strong>${(user.composedCompleteName)!}</strong></td>
                    <td>
                      [#if userRoles?has_content]
                        [#assign rolesList = userRoles?split(", ") /]
                        [#list rolesList as role]
                          <span class="label label-default role-badge" style="margin-right: 4px; font-size: 0.85em;">${role}</span>
                        [/#list]
                      [#else]
                        <span class="text-muted"><em>No roles</em></span>
                      [/#if]
                    </td>
                    <td><a href="mailto:${(user.email)!}" style="text-decoration: none;">${(user.email)!}</a></td>
                    <td><strong>${user.lastLogin}</strong></td>
                  </tr>
                  [/#list]
                </tbody>
              </table>
            </div>
            
            [#-- Role-specific Tab Content --]
            [#list rolesCrp as role]
              [#assign usersList = (action.getUsersByRole(role.id))![] /]
              [#if usersList?has_content]
                <div role="tabpanel" class="tab-pane" id="role-${role.id}">
                [#if action.isAiccra()]
                  <h4 class="sectionSubTitle ">${role.aiccraAcronymDimanic}</h4>
                [#else]
                  <h4 class="sectionSubTitle ">${role.description}</h4>
                [/#if ]
                  <p class="help-block">${usersList?size} user(s) with this role</p>
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
                          <th>Last Login (UTC)</th>
                      </tr>
                    </thead>
                    <tbody>
                      [#list usersList as user]
                      <tr>
                        <td>${user.id}</td>
                        <td><strong>${(user.composedCompleteName)!}</strong></td>
                        [#-- <td>${(user.username)!'<i>No Username</i>'}</td> --]
                        [#if (action.hasRelations(role.acronym))??]
                        <td>
                          [#assign relationsStr = (action.getRelations(user.id, role.id))! /]
                          [#if relationsStr?has_content]
                            [#assign relationsList = relationsStr?replace("[", "")?replace("]", "")?split(", ") /]
                            [#list relationsList as relation]
                              [#if relation?trim?has_content]
                                <span class="label label-default role-badge" style="margin-right: 4px; font-size: 0.85em;">${relation}</span>
                              [/#if]
                            [/#list]
                          [/#if]
                        </td>
                        [/#if]
                        <td><a href="mailto:${(user.email)!}" style="text-decoration: none;">${(user.email)!}</a></td>
                        <td>[#if user.lastLogin??]${user.lastLogin}[#else]<i><span class="glyphicon glyphicon-time" style="font-size: 0.8em;"></span> Never logged in</i>[/#if]</td>
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

