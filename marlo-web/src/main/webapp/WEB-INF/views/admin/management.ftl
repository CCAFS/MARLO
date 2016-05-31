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
        [@s.form action=actionName enctype="multipart/form-data" ]  
        
        <h4 class="sectionTitle">Program Management Team</h4>
        <div class="borderBox clearfix">
          [#-- PMU Users List --]
          <div class="users-list simpleBox">
            <ul>
            [#if programManagmentTeam?has_content]
              [#list programManagmentTeam as item]
                [@userItem element=item index=item_index name="programManagmentTeam" /]
              [/#list]
            [/#if]
            </ul>
            <p class="text-center" style="display:${(programManagmentTeam?has_content)?string('none','block')}">There are not users added yet.</p>
          </div>
          [#-- Add Person--] 
          <div class="searchUser button-blue pull-right"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> [@s.text name="form.buttons.searchUser" /]</div>
        </div>
        
        <h4 class="sectionTitle">Flagships</h4>
        <div class="borderBox">
          [#-- Flagships List --]
          <div class="flagships-list simpleBox">
           <ul>
            [#list 1..2 as item]
              [@programItem element={} index=item_index /]
            [/#list]
           </ul>
           <p class="text-center">There are not Flagships added yet.</p>
          </div>
          [#-- Add Flagship--] 
          <div class="row">
            <div class="col-sm-2"><input type="text" id="acronym-input" class="form-control" placeholder="Acronym"></div>
            <div class="col-sm-8"><input type="text" id="acronym-name" class="form-control" placeholder="Flagship name"></div>
            <div class="col-sm-2"><div class="addFlagship button-blue"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.add" /]</div></div>
          </div>
        </div>
        
        <h4 class="sectionTitle">Regional program Managers</h4>
        <div class="borderBox">Content</div>

        [@s.submit type="button" name="save" cssClass="btn btn-success"][@s.text name="form.buttons.save" /][/@s.submit]
        
        [/@s.form]
      </div>
    </div>
  </div>
</section>

[#-- Search users Interface --]
[#import "/WEB-INF/global/macros/usersPopup.ftl" as usersForm/]
[@usersForm.searchUsers/]

<ul style="display:none">
  [#-- PMU User template --]
  [@userItem element={} index=0 name="programManagmentTeam" template=true /]
  [#-- Flagship template --]
  [@programItem element={} index=0 template=true /]
</ul>

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro userItem element index name template=false]
  [#assign customName = "${name}[${index}]" /]
  <li id="user-${template?string('template',index)}" class="user" style="display:${template?string('none','block')}">
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
    <span class="name"> ${(element.composedName?html)!'Unknown user'}</span>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    <span class="glyphicon glyphicon-remove pull-right" aria-hidden="true"></span>
  </li>
[/#macro]

[#macro programItem element index template=false]
  <li id="program-${template?string('template',index)}" class="user" style="display:${template?string('none','block')}">
    <span class="acronym">${(element.acronym)!'Unknown acronym'}</span>
    <span class="name">${(element.name)!'Unknown name'}</span>
    <input class="id" type="hidden" name="user.id" value="${(element.id)!}"/>
    <span class="glyphicon glyphicon-remove pull-right" aria-hidden="true"></span>
  </li>
[/#macro]