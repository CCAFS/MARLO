[#ftl]
[#assign title = "Management" /]
[#assign pageLibs = [] /]
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
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]  
        
        <h4 class="sectionTitle">Program Management Team</h4>
        <div class="borderBox clearfix">
          [#-- PMU Users List --]
          <div class="users items-list simpleBox">
            <ul>
            [#if rolePmu.userRoles?has_content]
              [#list rolePmu.userRoles as item]
                [@userItem element=item index=item_index name="rolePmu.userRoles" /]
              [/#list]
            [/#if]
            </ul>
            <p class="text-center" style="display:${(programManagmentTeam?has_content)?string('none','block')}">There are not users added yet.</p>
          </div>
          [#-- Add Person--] 
          <div class="searchUser button-blue pull-right"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> [@s.text name="form.buttons.searchUser" /]</div>
        </div>
        
        <h4 class="sectionTitle">Flagships</h4>
        <div class="program-block borderBox">
          [#-- Flagships List --]
          <div class="flagships items-list simpleBox">
           <ul>
            [#list 1..2 as item]
              [@programItem element={} index=item_index name="programs"/]
            [/#list]
           </ul>
           <p class="text-center">There are not Flagships added yet.</p>
          </div>
          [#-- Add Flagship--] 
          <div class="row">
            <div class="col-sm-2"><input type="text" class="acronym-input form-control" placeholder="Acronym"></div>
            <div class="col-sm-8"><input type="text" class="name-input form-control" placeholder="Flagship name"></div>
            <div class="col-sm-2">
              <div class="addProgram button-blue"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.add" /]</div>
              <span class="type-input" style="display:none">1</span>
            </div>
          </div>
        </div>
        
        <h4 class="sectionTitle">Regional program Managers</h4>
        <div class="program-block borderBox">
          [#-- Regions List --]
          <div class="flagships items-list simpleBox">
           <ul>
            [#list 1..2 as item]
              [@programItem element={} index=item_index name="programs"/]
            [/#list]
           </ul>
           <p class="text-center">There are not Flagships added yet.</p>
          </div>
          [#-- Add Region--]
          <div class="row">
            <div class="col-sm-2"><input type="text" class="acronym-input form-control" placeholder="Acronym"></div>
            <div class="col-sm-8"><input type="text" class="name-input form-control" placeholder="Region name"></div>
            <div class="col-sm-2">
              <div class="addProgram button-blue"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.add" /]</div>
              <span class="type-input" style="display:none">2</span>
            </div>
          </div>
        </div>

        <div class="buttons">
          [@s.submit type="button" name="save" cssClass=""][@s.text name="form.buttons.save" /][/@s.submit]
        </div>
        
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
  [#-- Program template --]
  [@programItem element={} index=0 name="programs" template=true /]
</ul>

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro userItem element index name template=false]
  [#assign customName = "${name}[${index}]" /]
  <li id="user-${template?string('template',index)}" class="user" style="display:${template?string('none','block')}">
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
    <span class="name"> ${(element.user.composedName?html)!'Unknown user'}</span>
    <input class="id" type="hidden" name="${customName}.user" value="${(element.user)!}"/>
     <input class="id" type="hidden" name="${customName}.role" value="${(element.role)!}"/>
    <span class="glyphicon glyphicon-remove pull-right" aria-hidden="true"></span>
  </li>
[/#macro]

[#macro programItem element index name template=false]
  [#assign customName = "${name}[${index}]" /]
  <li id="program-${template?string('template',index)}" class="program" style="display:${template?string('none','block')}">
    <span class="composedName">${(element.acronym)!'Unknown acronym'} - ${(element.name)!'Unknown name'}</span>
    <input class="acronym" type="hidden" name="${customName}.acronym" value="${(element.acronym)!'Unknown acronym'}"/>
    <input class="name" type="hidden" name="${customName}.name" value="${(element.name)!'Unknown name'}"/>
    <input class="type" type="hidden" name="${customName}.type" value="${(element.type)!'-1'}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!'-1'}"/>
    <span class="glyphicon glyphicon-remove pull-right" aria-hidden="true"></span>
  </li>
[/#macro]