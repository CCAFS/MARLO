[#ftl]
[#assign title = "Project Partners" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${programID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrlMedia}/js/global/usersManagement.js", "${baseUrlMedia}/js/impactPathway/output.js", "${baseUrlMedia}/js/global/fieldsValidation.js", "${baseUrlMedia}/js/global/autoSave.js"] /]
[#assign customCSS = ["${baseUrlMedia}/css/impactPathway/outputList.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "projectPartners" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/monitoring", "action":"${(centerSession)!}/projectList"},
  {"label":"projectPartners", "nameSpace":"/monitoring", "action":""}]/]
  
[#assign leadersName = "leaders"/]
[#include "/WEB-INF/center//global/pages/header.ftl" /]
[#include "/WEB-INF/center//global/pages/main-menu.ftl" /]
[#import "/WEB-INF/center//global/macros/utils.ftl" as utils /]
[#-- Help text --]
<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlMedia}/images/global/icon-help.png" />
    <p class="col-md-10"> [@s.text name="projectPartner.help"][/@s.text] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

[#--  marlo cluster of activities--]
<section class="marlo-content">
  <div class="container"> 
    
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/center//views/monitoring/project/menu-projects.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Section Messages --]
        [#-- Projects data information --]
        [#include "/WEB-INF/center//views/monitoring/project/dataInfo-projects.ftl" /]
        <br />

        <span id="programSelected" class="hidden">${(selectedProgram.id)!}</span>
        
        [#-- Title --]
          <div class="col-md-12">
        
        
        <div class="clearfix"></div>
        [@s.form action=actionName enctype="multipart/form-data" ]

        [#-- Back --]
        <div class="pull-right">
          <a href="[@s.url action='${centerSession}/projectList'][@s.param name="programID" value=programID /][@s.param name="projectID" value=projectID /][@s.param name="edit" value=true /][/@s.url]">
            <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the project list
          </a>
        </div>
        <h3 class="headTitle">${selectedProgram.name} - Project Partners </h3>
        <div class="parntersBlock">
          <div class="partnersList" listname="partners">
            [#if project.partners?has_content]
              [#list project.partners as partner]
                [@partnerMacro element=partner name="project.partners" index=partner_index /]
              [/#list]
            [#else] 
              [#-- <p class="emptyMessage text-center">No partners added yet.</p> --]
            [/#if]
          </div>
          [#-- Select a organization/institution  --]
          [#if editable]
          <div class="partnerSelect"> 
            <div class="pull-left"> <span class="glyphicon glyphicon-plus"></span>  &nbsp</div>
            [@customForm.select name="" label="" disabled=!canEdit i18nkey="output.selectInstitution"  listName="institutions" keyFieldName="id" displayFieldName="composedName" className="" value="" /]
          </div>
          [/#if] 
        </div>
          
        [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/center//views/monitoring/project/buttons-projects.ftl" /]
        
          
        [/@s.form]
        
      </div>
    </div>
    
  </div>
</section>

[#-- Search users Interface --]
[#import "/WEB-INF/center//global/macros/usersPopup.ftl" as usersForm/]
[@usersForm.searchUsers/]

[#-- Partner Template --]
[@partnerMacro element={} name="project.partners" index="-1" isTemplate=true /]

[#-- UserItem Template --]
[@userItem element={} name="project.partners[-1].users" index="-1" template=true /]

[#include "/WEB-INF/center//global/pages/footer.ftl" /]

[#macro partnerMacro element name index isTemplate=false]
  <div id="partner-${isTemplate?string('template', index)}" class="partner borderBox" style="display:${isTemplate?string('none','block')}">
    [#local customName = "${name}[${index}]"]
    
    [#-- Remove Button --]
    [#if editable]<div class="removeIcon removePartner" title="Remove"></div>[/#if]

    [#-- Hidden Inputs --]
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <input class="partnerInstitutionId" type="hidden" name="${customName}.institution.id" value="${(element.institution.id)!}" />
    
    [#-- Partner Title --]    
    <div class="form-group">
      <h5 class="sectionSubTitle title">${(element.institution.composedName)!'Undefined'}</h5> 
      <div class="clearfix"></div>
    </div>
    
    [#-- Contact persons --]
    <div class="users-block">
      <div class="items-list simpleBox" listname="${customName}.users">
        <ul>
        [#if element.users?has_content]
          [#list element.users as item]
            [@userItem element=item name="${customName}.users" index=item_index /]
          [/#list]
        [/#if] 
        </ul>
        <p class="emptyMessage text-center usersMessage" style="display:${(element.users?has_content)?string('none','block')}">No assigned a partner contact yet.</p>
      </div>
      [#if editable]
      <div class="text-center">
        <div class="searchUser button-green">
          <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addPerson" /]
        </div>
      </div>
      [/#if]
    </div>
  </div>
[/#macro]


[#macro userItem element name index template=false]
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