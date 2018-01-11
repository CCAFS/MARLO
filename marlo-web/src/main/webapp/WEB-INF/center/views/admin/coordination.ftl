[#ftl]
[#assign title = "Coordination" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign customJS = ["${baseUrl}/global/js/usersManagement.js", "${baseUrlMedia}/js/admin/management.js" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "coordination" /]

[#assign breadCrumb = [
  {"label":"superadmin.admin", "nameSpace":"/admin", "action":"coordination"},
  {"label":"superadmin.coordination", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/center/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]  
        
        <h4 class="sectionTitle form-group">[@s.text name="programCoordination.title" /]</h4>
        
        <label for="">Program coordinators:</label>
        <div class="users-block">
        
        <div class="items-list simpleBox" listname="coordinators">
          <ul>
          [#if coordinators?has_content]
            [#list coordinators as item]
              [@programCoordinator element=item name="coordinators" index=item_index /]
            [/#list]
          [/#if] 
          </ul>
          <p class="emptyMessage text-center usersMessage" style="display:${(coordinators?has_content)?string('none','block')}">No assigned a coordinators yet.</p>
        </div>
        [#-- if editable--]
        <div class="text-center">
          <div class="searchUser button-green">
            <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addPerson" /]
          </div>
        </div>
        [#--if--]
      </div>
      
      [#-- Section Buttons--]
        <div class="buttons">
          <div class="buttons-content">
          [#if editable]
            <a href="[@s.url][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> [@s.text name="form.buttons.back" /]</a>
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
          [#else]
            [#if canEdit]
              <a href="[@s.url][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
            [/#if]
          [/#if]
          </div>
        </div>
                
        [/@s.form]
      </div>
    </div>
  </div>
</section>

[#-- Search users Interface --]
[#import "/WEB-INF/global/macros/usersPopup.ftl" as usersForm/]
[@usersForm.searchUsers/]

[@programCoordinator element={} name="coordinators" index=-1 template=true/]

[#include "/WEB-INF/center/pages/footer.ftl" /]

[#macro programCoordinator element name index template=false]
  [#local customName = "${name}[${index}]" /]
  <li id="user-${template?string('template',index)}" class="user userItem" style="display:${template?string('none','block')}">
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
    <span class="name"> ${(element.user.getComposedName()?html)!'Unknown user'}</span>
    <input class="user" type="hidden" name="${customName}.user.id" value="${(element.user.id)!}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    [#-- Remove Button --]
    [#if editable]
      <span class="glyphicon glyphicon-remove pull-right remove-userItem" aria-hidden="true"></span>
    [/#if]
  </li>
[/#macro]
