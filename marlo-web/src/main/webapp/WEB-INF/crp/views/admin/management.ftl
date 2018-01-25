[#ftl]
[#assign title = "Management" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["vanilla-color-picker","intro.js"] /]
[#assign customJS = [
  "${baseUrl}/global/js/usersManagement.js", 
  "${baseUrlMedia}/js/admin/management.js" ,
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign currentSection = "admin" /]
[#assign currentStage = "management" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"management", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
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
        
        <h4 class="sectionTitle">[@s.text name="programManagement.title" /]</h4>
        <div class="usersBlock borderBox clearfix" listname="loggedCrp.programManagmenTeam">
          [#-- PMU Users List --]
          <div class="users items-list simpleBox">
            <ul>
            [#if loggedCrp.programManagmenTeam?has_content]
              [#list loggedCrp.programManagmenTeam as item]
                [@userItem element=item index=item_index name="loggedCrp.programManagmenTeam" userRole=pmuRol /]
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
        <div class="program-block"  listname="flagshipsPrograms">
          [#-- Flagships List --]
          <div class="flagships items-list">
            <ul class="flagships-list" >
            [#if flagshipsPrograms?has_content]
              [#list flagshipsPrograms as item]
                [@programItem element=item index=item_index name="flagshipsPrograms"/]
              [/#list]
            [/#if]
            </ul>
            <p class="text-center programMessage" style="display:${(flagshipsPrograms?has_content)?string('none','block')}">[@s.text name="programManagement.flagship.notFlagship.span" /]</p>
          </div>
          [#-- Add Flagship--]
          [#if editable] 
          <div class="text-center">
            <div class="addProgram bigAddButton"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addFlagshipProgram" /]</div>
            <span class="type-input" style="display:none">1</span>
            <span class="inputName-input" style="display:none">flagshipsPrograms</span>
          </div>
          [/#if]
        </div>
        
        <h4 class="sectionTitle">[@s.text name="programManagement.regionalProgram.title"/]</h4>
        <div class="program-block borderBox">
          [#assign canChangeRegionaloption = !(regionsPrograms?has_content && loggedCrp.hasRegions)]
          [#if !canChangeRegionaloption]
            <div class="note">This option can no be changed due there are regions already added.</div>
          [/#if]
          [#-- Does your CRP have regional program managers?  --]
          [@customForm.yesNoInput name="loggedCrp.hasRegions" label="programManagement.regionalProgram.question" editable=editable && canChangeRegionaloption inverse=false value="${loggedCrp.hasRegions?string}" cssClass="text-left" /]
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
[@programItem element={} index=0 name="" template=true /]

<ul style="display:none">
  [#-- User template --]
  [@userItem element={} index=0 name="" userRole="-1" template=true /]
</ul>



[#include "/WEB-INF/crp/pages/footer.ftl" /]

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

[#macro programItem element index name template=false]
  [#local customName = "${name}[${index}]" /]
  <li id="program-${template?string('template',index)}" class="program borderBox" style="display:${template?string('none','block')}">
    [#-- Remove Button  --]
    [#if editable]
      [#if template || action.canBeDeleted(element.id, element.class.name)!false]
      <div class="remove-programItem removeElement" title="Remove program"></div>
      [/#if]
    [/#if]
    <div class="leftHead">
      <span class="index">${index+1}</span>
      <span class="elementId">${(element.composedName)!'Flagship'}</span>
    </div>
    <br />
    [#-- Program Acronym & Name --]
    <div class="form-group"> 
      <div class="row">
        <div class="col-sm-2">[@customForm.input name="${customName}.acronym" type="text"  i18nkey="CrpProgram.inputAcronym" placeholder="CrpProgram.inputAcronym.placeholder" className="acronym" required=true editable=editable /]</div>
        <div class="col-sm-9">[@customForm.input name="${customName}.name" type="text"  i18nkey="CrpProgram.inputName" placeholder="CrpProgram.inputName.placeholder" className="name" required=true editable=editable /]</div>
        <div title="This color will be used in some section of the platform to represent your Flagship (e.g. Impact Pathway graph, Your projects, etc.). Please try to pick up a color that differs from the other Flagships." class="col-sm-1">
          <label  for="">Color:</label>
          <div class="color-picker" style="background:${(element.color)!};"><input type="hidden" name="${customName}.color" value="${(element.color)!}"></div>
        </div>
      </div>
    </div>
    [#-- Hidden inputs  --]
    <input class="type" type="hidden" name="${customName}.programType" value="${(element.programType)!'-1'}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    [#-- Leaders  --]
    <label for="">[@s.text name="CrpProgram.leaders"/]</label>
    <div class="usersBlock leaders simpleBox" listname="flagshipsPrograms[${index}].leaders">
      [#-- Leaders List --]
      <div class="items-list" >
        <ul>
        [#if element.leaders?has_content]
          [#list element.leaders as leader]
            [@userItem element=leader index=leader_index name="${customName}.leaders" userRole=fplRole.id /]
          [/#list]
        [/#if]
        </ul>
        <p class="text-center usersMessage" style="display:${(element.leaders?has_content)?string('none','block')}">[@s.text name="CrpProgram.notLeaders.span"/]</p>
      </div>
      [#-- Add person Button --]
      [#if editable]
      <div class="text-center">
        <div class="searchUser button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addFlagshipLeader" /]</div>
      </div>
      [/#if]
      [#-- Hidden Parameters --]
      <span class="usersType" style="display:none">programUser</span>
      <span class="usersRole" style="display:none">${fplRole.id}</span> 
    </div>
    
    [#-- Managers  --]
    <div class="form-group">
      <label for="">[@s.text name="CrpProgram.managers"/]</label>
      <div class="usersBlock managers simpleBox" listname="flagshipsPrograms[${index}].managers">
        [#-- Managers List --]
        <div class="items-list" >
          <ul>
          [#if element.managers?has_content]
            [#list element.managers as leader]
              [@userItem element=leader index=leader_index name="${customName}.managers" userRole=fpmRole.id /]
            [/#list]
          [/#if]
          </ul>
          <p class="text-center usersMessage" style="display:${(element.managers?has_content)?string('none','block')}">[@s.text name="CrpProgram.notManagers.span"/]</p>
        </div>
        [#-- Add person Button --]
        [#if editable]
        <div class="text-center">
          <div class="searchUser button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> 
          [@s.text name="form.buttons.addFlagshipManager" ]
            [@s.param ]mkyong[/@s.param]
          [/@s.text]
          </div>
        </div>
        [/#if]
        [#-- Hidden Parameters --]
        <span class="usersType" style="display:none">programUser</span>
        <span class="usersRole" style="display:none">${fpmRole.id}</span> 
      </div>
    </div>
    
    [#if action.hasSpecificities('crp_baseline_indicators')]
    <div class="form-group simpleBox">
      [@customForm.checkBoxFlat id="${customName}.baseLine" name="${customName}.baseLine" label="CrpProgram.allowBaseline" editable=editable value="true" checked=(element.baseLine)!false cssClass="" /]
    </div>
    [/#if]
  </li>
[/#macro]