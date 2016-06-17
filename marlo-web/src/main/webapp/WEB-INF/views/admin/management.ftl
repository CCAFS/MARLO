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
        <div class="usersBlock clearfix">
          [#-- PMU Users List --]
          <div class="users items-list borderBox">
            <ul>
            [#if loggedCrp.programManagmenTeam?has_content]
              [#list loggedCrp.programManagmenTeam as item]
                [@userItem element=item index=item_index name="loggedCrp.programManagmenTeam" userRole=pmuRol /]
              [/#list]
            [/#if]
            </ul>
            <p class="text-center usersMessage" style="display:${(loggedCrp.programManagmenTeam?has_content)?string('none','block')}">There are not users added yet.</p>
          </div>
          [#-- Add Person--]
          <div class="text-right">
            <div class="searchUser button-blue"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> [@s.text name="form.buttons.searchUser" /]</div>
          </div>
          [#-- Hidden Parameters --]
          <span class="usersType" style="display:none">crpUser</span>
          <span class="usersRole" style="display:none">${pmuRol}</span>
        </div>
        
        <h4 class="sectionTitle">Flagships</h4>
        <div class="program-block">
          [#-- Flagships List --]
          <div class="flagships items-list">
            <ul class="flagships-list">
            [#if flagshipsPrograms?has_content]
              [#list flagshipsPrograms as item]
                [@programItem element=item index=item_index name="flagshipsPrograms"/]
              [/#list]
            [/#if]
            </ul>
            <p class="text-center programMessage" style="display:${(flagshipsPrograms?has_content)?string('none','block')}">There are not flagships added yet.</p>
          </div>
          [#-- Add Flagship--] 
          <div class="text-center">
            <div class="addProgram bigAddButton"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.add" /] Flagship Program</div>
            <span class="type-input" style="display:none">1</span>
            <span class="inputName-input" style="display:none">flagshipsPrograms</span>
          </div>
        </div>
        
        <h4 class="sectionTitle">Regional program Managers</h4>
        <div class="program-block borderBox">
          [#-- Does your CRP have regional program managers?  --]
          [@customForm.yesNoInput name="loggedCrp.hasRegions" label="Does your CRP have regional program managers?" editable=true inverse=false value="${loggedCrp.hasRegions?string}" cssClass="text-left" /]
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

[#-- Program template --]
[@programItem element={} index=0 name="" template=true /]

<ul style="display:none">
  [#-- User template --]
  [@userItem element={} index=0 name="" userRole="-1" template=true /]
</ul>

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro userItem element index name userRole template=false]
  [#assign customName = "${name}[${index}]" /]
  <li id="user-${template?string('template',index)}" class="user userItem" style="display:${template?string('none','block')}">
    [#-- User Name --]
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span><span class="name"> ${(element.user.getComposedName()?html)!'Unknown user'}</span>
    [#-- Hidden inputs --]
    <input class="user" type="hidden" name="${customName}.user.id" value="${(element.getUser().id)!}"/>
    <input class="role" type="hidden" name="${customName}.role.id" value="${userRole}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    [#-- Remove Button --]
    <span class="glyphicon glyphicon-remove pull-right remove-userItem" aria-hidden="true"></span>
  </li>
[/#macro]

[#macro programItem element index name template=false]
  [#assign customName = "${name}[${index}]" /]
  <li id="program-${template?string('template',index)}" class="program borderBox" style="display:${template?string('none','block')}">
    [#-- Remove Button  --]
    <div class="remove-programItem removeElement" title="Remove program"></div>
    [#-- Program Acronym & Name --]
    <div class="form-group">
      <label for="">Program Name:</label>
      <div class="row">
        <div class="col-sm-2">[@customForm.input name="${customName}.acronym" type="text" showTitle=false placeholder="Acronym" className="acronym-input" required=true editable=true /]</div>
        <div class="col-sm-9">[@customForm.input name="${customName}.name" type="text" showTitle=false placeholder="Flagship Name" className="name-input" required=true editable=true /]</div>
      </div>
    </div>
    [#-- Hidden inputs  --]
    <input class="type" type="hidden" name="${customName}.programType" value="${(element.programType)!'-1'}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    [#-- Leaders  --]
    <label for="">Program Leaders:</label>
    <div class="usersBlock simpleBox">
      [#-- Leaders List --]
      <div class="items-list">
        <ul>
        [#if element.leaders?has_content]
          [#list element.leaders as leader]
            [@userItem element=leader index=leader_index name="${customName}.leaders" userRole=fplRole.id /]
          [/#list]
        [/#if]
        </ul>
        <p class="text-center usersMessage" style="display:${(element.leaders?has_content)?string('none','block')}">There are not leaders belong to this flagship yet.</p>
      </div>
      [#-- Add person Button --]
      <div class="text-center">
        <div class="searchUser button-green"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> [@s.text name="form.buttons.searchUser" /]</div>
      </div>
      [#-- Hidden Parameters --]
      <span class="usersType" style="display:none">programUser</span>
      <span class="usersRole" style="display:none">{fpRole.id}</span>
    </div>
  </li>
[/#macro]