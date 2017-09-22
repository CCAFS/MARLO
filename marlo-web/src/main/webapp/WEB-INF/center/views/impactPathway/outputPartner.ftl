[#ftl]
[#assign title = "Outcome" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${programID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrl}/global/js/usersManagement.js", 
  "${baseUrlMedia}/js/impactPathway/output.js", 
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = [
  "${baseUrl}/global/css/customDataTable.css",
  "${baseUrlMedia}/css/impactPathway/outputList.css"
  ] 
/]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "outputs" /]
[#assign currentSubStage = "partners" /]

[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"impactPathway", "action":"programImpacts"},
  {"label":"outputsList", "nameSpace":"${centerSession}/impactPathway", "action":"outputsList"},
  {"label":"output", "nameSpace":"", "action":""}
]/]
[#assign leadersName = "leaders"/]
[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#import "/WEB-INF/center/views/impactPathway/outputListTemplate.ftl" as outcomesList /]

[#assign outputCustomName= "output" /]

[#--  marlo cluster of activities--]
<section class="marlo-content">
  <div class="container"> 
    
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/center/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/center/views/impactPathway/messages-impactPathway-output.ftl" /]
        

        <span id="programSelected" class="hidden">${(selectedProgram.id)!}</span>
        
        [#-- Title --]
          <div class="col-md-12">
            <h3 class="subTitle headTitle outcomeListTitle">${selectedProgram.name} - Outputs</h3>
            <hr />
          </div><div class="clearfix"></div>
        
        [#-- Back --]
        <small class="pull-right">
          <a href="[@s.url action='${centerSession}/outputsList'][@s.param name="programID" value=programID /][@s.param name="outcomeID" value=outcomeID /][@s.param name="edit" value=true /][/@s.url]">
            <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the outputs list
          </a>
        </small>
        
        <div class="clearfix"></div>
        
        [#-- Output submenu --]
        [#include "/WEB-INF/center/views/impactPathway/submenu-impactPathway-output.ftl" /]
        
        [@s.form action=actionName enctype="multipart/form-data" ]

        
        <h3 class="headTitle"> Partners </h3>
        <div class="parntersBlock">
          <div class="partnersList" listname="partners">
            [#if output.partners?has_content]
              [#list output.partners as partner]
                [@partnerMacro element=partner name="output.partners" index=partner_index /]
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
          
        [#-- Section Buttons--]
        [#include "/WEB-INF/center/views/impactPathway/buttons-impactPathway-output.ftl" /]
        
        [#-- Hidden inputs --]
        <input type="hidden" name="outputID" value="${outputID}" />
          
        [/@s.form]
        
      </div>
    </div>
    
  </div>
</section>

[#-- Search users Interface --]
[#import "/WEB-INF/global/macros/usersPopup.ftl" as usersForm/]
[@usersForm.searchUsers/]

[#-- Partner Template --]
[@partnerMacro element={} name="${outputCustomName}.partners" index="-1" isTemplate=true /]

[#-- UserItem Template --]
[@userItem element={} name="${outputCustomName}.partners[-1].users" index="-1" template=true /]

[#include "/WEB-INF/center/pages/footer.ftl" /]

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
      <div class="pull-right">[@s.radio label="outputPartner.mode" name="${customName}.internal" list="partnerModes" value="${(element.internal?c)!}" /]</div>
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