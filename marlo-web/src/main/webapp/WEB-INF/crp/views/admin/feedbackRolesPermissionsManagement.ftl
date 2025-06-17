[#ftl]
[#assign title = "Feedback Role Permissions Management" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [ "${baseUrlMedia}/js/admin/feedbackRolesPermissionsManagement.js"
 ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/feedbackRolesPermissionsManagement.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "feedbackRolesPermissionsManagement" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"marloBoard"},
  {"label":"feedbackRolesPermissionsManagement", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

 
<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        
        [#-- Feedback Permissions --]
        <h4 class="sectionTitle">[@s.text name="feedbackPermissions.title" /]</h4>
        <div class="slos-list">
        [#if feedbackRolesPermissions?has_content]
          [#list feedbackRolesPermissions as frp]
            [@feedbackCommentFieldsMacro element=frp name="feedbackRolesPermissions[${frp_index}]" index=frp_index  /]
          [/#list]
        [/#if]
        </div>
        [#-- Add Feedback Permission Button --]
        <div class="addSlo bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addFeedbackPermission"/]</div>
        
        [#-- Section Buttons--]
        <div class="buttons">
          <div class="buttons-content">
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
          </div>
        </div>
        
        [/@s.form]
        
      </div>
    </div>
  </div>
</section>

[#-- Feedback Comments fields Template --]
[@feedbackCommentFieldsMacro element={} name="feedbackRolesPermissions[-1]" index=-1 isTemplate=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro feedbackCommentFieldsMacro element name index isTemplate=false]
  <div id="srfSlo-${isTemplate?string('template',index)}" class="srfSlo borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeElement sm" title="Remove"></div>
    
    [#-- Description --]
    <div class="blockTitle closed">
      <strong>Feedback Field ${index+1}: </strong>${(element.description)!''} - ${(element.description[0..*200])!'Feeckback Fields'}
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      [#-- feedback role permission ID  --]
      <input type="hidden" name="${name}.id" value="${(element.id)!}"/>
      [#--  feedback role Description  --]
      <div class="form-group">
        [@customForm.input name="${name}.description" i18nkey="feedbackPermissions.description" className="description limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>
      [#--  Feedback Permissions  --]
      <div class="form-group">
        [@customForm.select name="${name}.feedbackPermission.id" className="feedbackPermission" i18nkey="feedbackPermissions.permission"  disabled=!editable  listName="feedbackPermissionsList" keyFieldName="id"  displayFieldName="description" required=true editable=editable /]
      </div>
      <div class="clearfix"></div>
      [#--  Role --]
      <div class="form-group">
        [@customForm.select name="${name}.role.id" className="role" i18nkey="feedbackPermissions.role"  disabled=!editable  listName="roleList" keyFieldName="id"  displayFieldName="displayLabel" required=true editable=editable /]
      </div>
      <div class="clearfix"></div>
      [#--  Cluster Type  --]
      <div class="form-group">
        [@customForm.select name="${name}.clusterType.id" className="clusterType" i18nkey="feedbackPermissions.clusterType"  disabled=!editable  listName="clusterTypeList" keyFieldName="id"  displayFieldName="name" required=true editable=editable /]
      </div>
      <div class="clearfix"></div>
        
    </div>
  </div>
[/#macro]
