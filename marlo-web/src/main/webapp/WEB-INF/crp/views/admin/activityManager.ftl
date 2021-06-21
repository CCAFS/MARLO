[#ftl]
[#assign title = "Activity Management" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["vanilla-color-picker","intro.js"] /]
[#assign customJS = [
  "${baseUrlCdn}/global/js/usersManagement.js", 
  "${baseUrlMedia}/js/admin/activity.js" ,
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ] 
/]
[#assign currentSection = "admin" /]
[#assign currentStage = "activities" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"activities", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign isCenter = (isCenterGlobalUnit)!false /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="CrpProgram.help" /] </p>
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
        [@s.form action=actionName enctype="multipart/form-data" ] 
        
        <h4 class="sectionTitle">[@s.text name="activityManagement.activity.title" /]</h4>
        <div class="usersBlock borderBox clearfix" listname="loggedCrp.programManagmenTeam">
          [#-- PMU Users List --]
          <div class="users items-list simpleBox">
            <ul>
            [#if activities?has_content]
              [#list activities as item]
                [@activityItem element=item name="" /]
              [/#list]
            [/#if]
            </ul>
            <p class="text-center usersMessage" style="display:${(loggedCrp.programManagmenTeam?has_content)?string('none','block')}">[@s.text name="programManagement.notUsers.span" /]</p>
          </div>
          [#-- Add Person--]
          [#if editable] 
          <div class="text-right">
            <div class="searchUser button-blue"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> [@s.text name="form.buttons.searchUser" /]</div>
          </div>
          [/#if]
          [#-- Hidden Parameters --]
          <span class="usersType" style="display:none">crpUser</span>
          <span class="usersRole" style="display:none">${pmuRol}</span>
        </div>
                 
        <h4 class="sectionTitle">[@s.text name="programManagement.flagship.title" /]</h4>
        <div class="program-block"  listname="activities">
          [#-- Flagships List --]
          
          <div class="flagships items-list">
            <ul class="flagships-list" >
            [#if activities?has_content]
              [#list activities as item]
                [@activityItem2 element=item index=item.id name="activities"/]
              [/#list]
            [/#if]
            </ul>
            <p class="text-center programMessage" style="display:${(activities?has_content)?string('none','block')}">[@s.text name="programManagement.flagship.notFlagship.span" /]</p>
          </div>
        </div>
         [#-- Add Flagship--]
          [#if editable] 
          <div class="text-center">
            <div class="addProgram bigAddButton"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addFlagshipProgram" /]</div>
            <span class="type-input" style="display:none">1</span>
            <span class="inputName-input" style="display:none">activities</span>
          </div>
          [/#if]
        </div>

        [#-- confirm popup --]
        <div id="dialog-confirm"  style="display:none;">
          <p><span class="glyphicon glyphicon-warning-sign" style="float:left; margin:0 7px 20px 0;"></span><strong> Are you sure?</strong></p>
        </div>
        
        [#-- Section Buttons--]
        [#include "/WEB-INF/crp/views/admin/buttons-admin.ftl" /]
        
        [/@s.form]
      </div>
    </div>
  </div>
</section>

[#-- Search users Interface --]
[#import "/WEB-INF/global/macros/usersPopup.ftl" as usersForm/]
[@usersForm.searchUsers/]

[#-- Program template --]
[@activityItem2 element={} index=0 name="" template=true /]

<ul style="display:none">
  [#-- User template --]
  [@userItem element={} index=0 name="" userRole="-1" template=true /]
</ul>



[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro userItem element index name userRole template=false]
  [#local customName = "${name}[${index}]" /]
  <li id="user-${template?string('template',index)}" class="user userItem" style="display:${template?string('none','block')}">
    [#-- User Name --]
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span><span class="name"> ${(element.user.getComposedName()?html)!'Unknown user'}</span>
    [#-- Hidden inputs --]
    <input class="user" type="hidden" name="${customName}.user.id" value="${(element.user.id)!}"/>
    <input class="role" type="hidden" name="${customName}.role.id" value="${userRole}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    [#-- Remove Button --]
    [#if editable]
      [#if template || action.canBeDeleted(element.id, element.class.name)!false]
        <span class="glyphicon glyphicon-remove pull-right remove-userItem" aria-hidden="true"></span>
      [#else]
        <span class="glyphicon glyphicon-remove pull-right" style="color:#ccc" aria-hidden="true" title="Can not be deleted"></span>
      [/#if]
    [/#if]
  </li>
[/#macro]

[#macro activityItem element name template=false]
  [#local customName = "${name}[${element.id}]" /]
  <li id="user-${template?string('template',element.id)}" class="user userItem" style="display:${template?string('none','block')}">
    [#-- Activity Name --]
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span><span class="name"> ${(element.title?html)!'Unknown actibity'}</span>
    [#-- Hidden inputs --]
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    [#-- Remove Button --]
    [#if editable]
      [#if template || action.canBeDeleted(element.id, element.class.name)!false]
        <span class="glyphicon glyphicon-remove pull-right remove-userItem" aria-hidden="true"></span>
      [#else]
        <span class="glyphicon glyphicon-remove pull-right" style="color:#ccc" aria-hidden="true" title="Can not be deleted"></span>
      [/#if]
    [/#if]
  </li>
[/#macro]


[#macro activityItem2 element index name template=false]
  [#local customName = "${name}[${index}]" /]
  <li id="program-${template?string('template',index)}" class="program borderBox" style="display:${template?string('none','block')}">
    [#-- Remove Button  --]
    [#if editable]
      [#if template || action.canBeDeleted(element.id, element.class.name)!false]
      <div class="remove-activityItem2 removeElement" title="Remove activity"></div>
      [/#if]
    [/#if]
    <div class="leftHead">
      [#assign globalFlagship][@s.text name="global.flagship${isCenter?string('Center','')}"/][/#assign]
      <span class="index">${index}</span>
      <span class="elementId">${(element.title)!}</span>
    </div>
    <br />
    [#-- Program Acronym & Name --]
    <div class="form-group"> 
      <div class="row">
        <div class="col-sm-9">[@customForm.input name="${customName}.title" type="text"  i18nkey="CrpProgram.inputName" placeholder="CrpProgram.inputName.placeholder" className="title" required=true editable=editable /]</div>
      </div>
    </div>
    [#-- Hidden inputs  --]
    <input class="id" type="hidden" name="${customName}.id" value="${(index)!}"/>
    
    
  </li>
[/#macro]
