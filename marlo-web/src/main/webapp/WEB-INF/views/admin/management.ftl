[#ftl]
[#assign title = "Management" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/global/usersManagement.js", "${baseUrl}/js/admin/management.js" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "management" /]

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
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        <h4 class="sectionTitle">Program Management Team</h4>
        <div class="borderBox clearfix">
          [#-- PMU Users List --]
          <div class="users-list simpleBox">
           <ul>
            
           </ul>
           <p class="text-center">There are not users added yet.</p>
          </div>
          [#-- Add Person--]
          <div class="partnerPerson-email userField">
            [#if editable][/#if]<div class="searchUser">[@s.text name="form.buttons.searchUser" /]</div>
          </div>

        </div>
        
        <h4 class="sectionTitle">Flagships</h4>
        <div class="borderBox">Content</div>
        
        <h4 class="sectionTitle">Regional program Managers</h4>
        <div class="borderBox">Content</div>
      </div>
    </div>
  </div>
</section>

[#-- Search users Interface --]
[#import "/WEB-INF/global/macros/usersPopup.ftl" as usersForm/]
[@usersForm.searchUsers/]

[#-- PMU User template --]
<ul style="display:none">
  <li id="user-template" class="user">
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
    <span class="name">{user.name}</span>
    <input class="id" type="hidden" name="user.id" value="{user.ïd}"/>
    <span class="glyphicon glyphicon-remove pull-right" aria-hidden="true"></span>
  </li>
</ul>

[#include "/WEB-INF/global/pages/footer.ftl" /]