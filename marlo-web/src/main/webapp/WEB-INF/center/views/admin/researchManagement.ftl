[#ftl]
[#assign title = "Strategic Objectives" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign customJS = ["${baseUrlMedia}/js/global/usersManagement.js"] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "researchManagement" /]

[#assign breadCrumb = [
  {"label":"superadmin.admin", "nameSpace":"/admin", "action":"objectives"},
  {"label":"superadmin.researchManagement", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/center/global/pages/header.ftl" /]
[#include "/WEB-INF/center/global/pages/main-menu.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/center/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]  
          <h4 class="sectionTitle form-group">[@s.text name="researchManagement.title" /]</h4>
          <div class="researchAreas-block">
            <div class="items-list" listname="centerAreas">
              [#if centerAreas?has_content]
                [#list centerAreas as item]
                  [@researchAreaMacro element=item name="centerAreas" index=item_index /]
                [/#list]
              [/#if] 
              <p class="emptyMessage text-center usersMessage" style="display:${(centerAreas?has_content)?string('none','block')}">No Research Areas added yet.</p>
            </div>
          </div>
          [#-- Add Research Area Button --]
          <div class="text-right">
            <div class=" button-blue"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="Add Research Area"/]</div>
          </div>
        [/@s.form]
      </div>
    </div>
  </div>
</section>


[@researchAreaMacro element={} name="centerAreas" index=-1 template=true/]

[#include "/WEB-INF/center/global/pages/footer.ftl" /]

[#macro researchAreaMacro element name index template=false]
  [#local customName = "${name}[${index}]" /]
  <div id="researchArea-${template?string('template',index)}" class="researchArea borderBox" style="display:${template?string('none','block')}">
    [#-- Hidden inputs --]
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    
    [#-- Basic Information --]
    <div class="form-group row">
      <div class="col-md-3">[@customForm.input name="${customName}.acronym" i18nkey="researchArea.acronym" className="" /]</div>
      <div class="col-md-9">[@customForm.input name="${customName}.name" i18nkey="researchArea.name" className="" /]</div>
    </div>
    
    [#-- Leaders --]
    <div class="form-group">
      <label for="">Leaders:</label>
      <div class="simpleBox researchAreas-leaders-block">
        <ul>
          [#if element.leaders?has_content]
          [#list element.leaders as item]
            [@userItemMacro element=item name="${customName}.leaders" index=item_index /]
          [/#list]
        [#else]
        [/#if]
        </ul>
      </div>
    </div>
    
    [#-- Programs --]
    <div class="form-group">
      <label for="">Research Programs:</label>
      <div class="simpleBox researchAreas-programs-block">
        [#if element.programs?has_content]
          [#list element.programs as item]
            [@programMacro element=item name="${customName}.programs" index=item_index /]
          [/#list]
        [#else]
        [/#if]
      </div>
    </div>
    
    [#-- Remove Button --]
    [#if editable]<span class="glyphicon glyphicon-remove pull-right remove-researchArea" aria-hidden="true"></span>[/#if]
  </div>
[/#macro]

[#macro programMacro element name index template=false]
  [#local customName = "${name}[${index}]" /]
  <div id="program-${template?string('template',index)}" class="program grayBox" style="display:${template?string('none','block')}">
    [#-- Hidden inputs --]
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    
    [#-- Basic Information --]
    <div class="form-group row">
      <div class="col-md-3">[@customForm.input name="${customName}.acronym" i18nkey="programs.acronym" className="" /]</div>
      <div class="col-md-9">[@customForm.input name="${customName}.name" i18nkey="programs.name" className="" /]</div>
    </div>
    
    [#-- Remove Button --]
    [#if editable]<span class="glyphicon glyphicon-remove pull-right remove-researchArea" aria-hidden="true"></span>[/#if]
  </div>
[/#macro]

[#macro userItemMacro element name index template=false]
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
