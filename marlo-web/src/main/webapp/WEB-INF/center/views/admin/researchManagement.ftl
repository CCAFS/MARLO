[#ftl]
[#assign title = "Research Areas" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign customJS = [
  "${baseUrl}/global/js/usersManagement.js",
  "${baseUrlMedia}/js/admin/researchManagement.js"
] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "researchManagement" /]

[#assign breadCrumb = [
  {"label":"superadmin.admin", "nameSpace":"/admin", "action":"objectives"},
  {"label":"superadmin.researchManagement", "nameSpace":"", "action":""}
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
          <h4 class="sectionTitle form-group">[@s.text name="researchManagement.title" /]</h4>
          <div class="researchAreas-block">
            <div class="researchAreas-list" listname="centerAreas">
              [#if centerAreas?has_content]
                [#list centerAreas as item]
                  [@researchAreaMacro element=item name="centerAreas" index=item_index /]
                [/#list]
              [/#if] 
              <p class="emptyMessage text-center usersMessage" style="display:${(centerAreas?has_content)?string('none','block')}">No Research Areas added yet.</p>
            </div>
            [#-- Add Research Area Button --]
            <div class="text-center">
              <div class="addResearchArea bigAddButton"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="researchManagement.addResearchArea"/]</div>
            </div>
          </div>
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/center/views/admin/buttons-admin.ftl" /]
          
        [/@s.form]
      </div>
    </div>
  </div>
</section>


[@researchAreaMacro element={} name="centerAreas" index=-1 template=true/]

[@userItemMacro element={} name="centerAreas[-1].leaders" index=-1 id="userItemAreaLeader" template=true/]

[@programMacro element={} name="centerAreas[-1].programs" index=-1 template=true/]

[@userItemMacro element={} name="centerAreas[-1].programs[-1].leaders" index=-1 id="userItemProgramLeader" template=true/]


[#-- Search users Interface --]
[#import "/WEB-INF/global/macros/usersPopup.ftl" as usersForm/]
[@usersForm.searchUsers/]

[#include "/WEB-INF/center/pages/footer.ftl" /]

[#macro researchAreaMacro element name index id="researchArea" template=false]
  [#local customName = "${name}[${index}]" /]
  <div id="${id}-${template?string('template',index)}" class="${id} borderBox" style="display:${template?string('none','block')}">
    [#-- Hidden inputs --]
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    
    [#-- Head --]
    <div class="leftHead">
      <span class="index">${index+1}</span>
      <span class="elementId"> ${(element.acronym)!} - [@s.text name="researchManagement.researchArea"/]</span>
    </div>
    <br />
    
    [#-- Remove Button --]
    <div class="removeArea removeElement" title="[@s.text name="researchManagement.removeArea"/]"></div>
    
    [#-- Basic Information --]
    <div class="form-group row">
      <div class="col-md-2">[@customForm.input name="${customName}.acronym" i18nkey="researchArea.acronym" className="" /]</div>
      <div class="col-md-10">[@customForm.input name="${customName}.name" i18nkey="researchArea.name" className="" /]</div>
    </div>
    
    [#-- Leaders --]
    <div class="form-group">
      <h5 class="sectionSubTitle" for="">[@s.text name="researchArea.leaders" /]</h5>
      <div class="simpleBox researchAreas-leaders-block items-list">
        <ul>
          [#if element.leaders?has_content]
            [#list element.leaders as item]
              [@userItemMacro element=item name="${customName}.leaders" index=item_index /]
            [/#list]
          [#else]
          [/#if]
        </ul>
        <div class="text-center">
          <div class="searchUser button-green clone-userItemAreaLeader"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addPerson" /]</div>
        </div>
      </div>
    </div>
    
    [#-- Programs --]
    <div class="form-group">
      <h5 class="sectionSubTitle" for="">[@s.text name="researchArea.programs" /]:</h5>
      <div class="programs-list">
        [#if element.programs?has_content]
          [#list element.programs as item]
            [@programMacro element=item name="${customName}.programs" index=item_index /]
          [/#list]
        [#else]
        [/#if]
      </div>
      [#-- Add Research Program --]
      <div class="text-right">
        <div class="addResearchProgram button-blue"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="researchManagement.addResearchProgram"/]</div>
      </div>
    </div>
     
  </div>
[/#macro]

[#macro programMacro element name index id="researchProgram" template=false]
  [#local customName = "${name}[${index}]" /]
  <div id="${id}-${template?string('template',index)}" class="${id} simpleBox" style="display:${template?string('none','block')}">
    [#-- Hidden inputs --]
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    
    [#-- Head --]
    <div class="leftHead blue sm">
      <span class="index">${index+1}</span>
      <span class="elementId">${(element.acronym)!} - [@s.text name="researchManagement.program"/]</span>
    </div>
    <br />
    
    [#-- Remove Button --]
    <div class="removeProgram removeElement sm" title="[@s.text name="researchManagement.removeProgram"/]"></div>
    
    [#-- Basic Information --]
    <div class="form-group row">
      <div class="col-md-2">[@customForm.input name="${customName}.acronym" i18nkey="programs.acronym" className="" /]</div>
      <div class="col-md-10">[@customForm.input name="${customName}.name" i18nkey="programs.name" className="" /]</div>
    </div>
    
    [#-- Leaders --]
    <div class="form-group">
      <label for="">[@s.text name="programs.leaders" /]:</label>
      <div class="simpleBox researchAreas-leaders-block items-list">
        <ul>
          [#if element.leaders?has_content]
            [#list element.leaders as item]
              [@userItemMacro element=item name="${customName}.leaders" index=item_index /]
            [/#list]
          [#else]
          [/#if]
        </ul>
        <div class="text-center">
          <div class="searchUser button-green clone-userItemProgramLeader"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addPerson" /]</div>
        </div>
      </div>
    </div>
    
  </div>
[/#macro]

[#macro userItemMacro element name index id="userItem" template=false]
  [#local customName = "${name}[${index}]" /]
  <li id="${id}-${template?string('template',index)}" class="user userItem ${id}" style="display:${template?string('none','block')}">
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
    <span class="name"> ${(element.user.getComposedName()?html)!'Unknown user'}</span>
    <input class="user" type="hidden" name="${customName}.user.id" value="${(element.user.id)!}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    [#-- Remove Button --]
    <span class="glyphicon glyphicon-remove pull-right removeUserItem" aria-hidden="true"></span>
  </li>
[/#macro]
