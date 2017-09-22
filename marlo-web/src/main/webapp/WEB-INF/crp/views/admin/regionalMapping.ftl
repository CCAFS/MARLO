[#ftl]
[#assign title = "Regional Mapping" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2", "flat-flags", "vanilla-color-picker"] /]
[#assign customJS = [
  "${baseUrl}/global/js/usersManagement.js", 
  "${baseUrlMedia}/js/admin/regionalMapping.js",
  "${baseUrl}/global/js/fieldsValidation.js" 
  ] 
/]
[#assign currentSection = "admin" /]
[#assign currentStage = "regionalMapping" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"regionalMapping", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="regionalMapping.help" /] </p>
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
        [@s.form action=actionName enctype="multipart/form-data"]
        
        <h4 class="sectionTitle">[@s.text name="regionalMapping.title"/]</h4>
        <div class="program-block">
          [#-- Regions List --]
          <div class="regions items-list" listname="regionsPrograms">
            <ul class="regions-list">
            [#if regionsPrograms?has_content]
              [#list regionsPrograms as item]
                [@programItem element=item index=item_index name="regionsPrograms"/]
              [/#list] 
            [#else] [@programItem element={} index=0 name="regionsPrograms"/] 
            [/#if]
            </ul>
            [#if !regionsPrograms?has_content && !editable]
              <p class="text-center programMessage" style="display:${(regionsPrograms?has_content)?string('none','block')}">
                [@s.text name="regionalMapping.notRegions.span"/]
              </p>
            [/#if]
          </div>
          [#-- Add Regions--]
          [#if editable]
          <div class="text-center">
            <div class="addProgram bigAddButton"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addRegionProgram"/]</div>
            <span class="type-input" style="display:none">2</span>
            <span class="inputName-input" style="display:none">regionsPrograms</span>
          </div>
          [/#if]
        </div>

        [#-- Section Buttons--]
        <div class="buttons">
          <div class="buttons-content">
          [#if editable]
            <a href="[@s.url][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> [@s.text name="form.buttons.back" /]</a>
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
          [#else]
            [#if canEdit]
              <a href="[@s.url][@s.param name="edit" value="true"/][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
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

[#-- Program template --]
[@programItem element={} index=0 name="" template=true /]

<ul style="display:none">
  [#-- User template --]
  [@userItem element={} index=0 name="" userRole="-1" template=true /]
</ul>

[#include "/WEB-INF/crp/pages/footer.ftl" /]

[#macro userItem element index name userRole template=false]
  [#assign userCustomName = "${name}[${index}]" /]
  <li id="user-${template?string('template',index)}" class="user userItem" style="display:${template?string('none','block')}">
    [#-- User Name --]
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span> <span class="name"> ${(element.user.getComposedName()?html)!'Unknown user'}</span>
    [#-- Hidden inputs --]
    <input class="user" type="hidden" name="${userCustomName}.user.id" value="${(element.user.id)!}"/>
    <input class="role" type="hidden" name="${userCustomName}.role.id" value="${userRole}"/>
    <input class="id" type="hidden" name="${userCustomName}.id" value="${(element.id)!}"/>
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
  [#assign customName = "${name}[${index}]" /]
  <li id="program-${template?string('template',index)}" class="program borderBox" style="display:${template?string('none','block')}">
    [#-- Remove Button  --]
    [#if editable]
      [#if template || !element?has_content || action.canBeDeleted(element.id, element.class.name)!false]
      <div class="remove-programItem removeElement" title="Remove program"></div>
      [/#if]
    [/#if]
    <div class="leftHead">
      <span class="index">${index+1}</span>
      <span class="elementId">${(element.composedName)!'Regional Program'}</span>
    </div>
    <br />
    [#-- Program Acronym & Name --]
    <div class="form-group">
    
      <div class="row">
        <div class="col-sm-2">
          <label class="abbreviation" for="">[@s.text name="CrpProgram.inputAcronym"/]<span class="red">*</span></label>
          [@customForm.input name="${customName}.acronym" type="text" i18nkey="regionalMapping.CrpProgram.name" showTitle=false placeholder="regionalMapping.CrpProgram.inputAcronym.placeholder" className="acronym-input" required=true editable=editable /]
        </div>
        
        <div class="col-sm-9">
          <label class="regionalName" for="">Name<span class="red">*</span></label>
          [@customForm.input name="${customName}.name" type="text" showTitle=false placeholder="regionalMapping.CrpProgram.inputName.placeholder" className="name-input" required=true editable=editable /]
        </div>
        
        <div class="col-sm-1">
          <label class="color" for="">Color</label>
          <div class="color-picker" style="background:${(element.color)!};"><input type="hidden" name="${customName}.color" value="${(element.color)!}"></div>
        </div>
      </div>
    </div>
    [#-- Hidden inputs  --]
    <input class="type" type="hidden" name="${customName}.programType" value="${(element.programType)!'-1'}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    [#-- Region Leaders  --]
    <label for="">[@s.text name="regionalMapping.CrpProgram.leaders"/]:[@customForm.req required=true /]</label>
    <div class="usersBlock leaders form-group simpleBox" listname="${customName}.leaders">
      [#-- List --]
      <div class="items-list" listname="regionsPrograms[${index}].leaders" >
        <ul>
        [#if element.leaders?has_content]
          [#list element.leaders as leader]
            [@userItem element=leader index=leader_index name="${customName}.leaders" userRole=rplRole.id/]
          [/#list]
        [/#if]
        </ul>
        <p class="text-center usersMessage" style="display:${(element.leaders?has_content)?string('none','block')}">[@s.text name="regionalMapping.CrpProgram.notLeaders.span"/]</p>
      </div>
      [#-- Add person Button --]
      [#if editable]
      <div class="text-center">
        <div class="searchUser button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addPerson" /]</div>
      </div>
      [/#if]
      [#-- Hidden Parameters --]
      <span class="usersType" style="display:none">programUser</span>
      <span class="usersRole" style="display:none">${rplRole.id}</span>
    </div>
    
    [#-- Region Managers  --]
    <label for="">[@s.text name="regionalMapping.CrpProgram.managers"/]:[@customForm.req required=true /]</label>
    <div class="usersBlock managers form-group simpleBox" listname="${customName}.leaders">
      [#-- List --]
      <div class="items-list" listname="regionsPrograms[${index}].leaders" >
        <ul>
        [#if element.managers?has_content]
          [#list element.managers as leader]
            [@userItem element=leader index=leader_index name="${customName}.managers" userRole=rpmRole.id/]
          [/#list]
        [/#if]
        </ul>
        <p class="text-center usersMessage" style="display:${(element.leaders?has_content)?string('none','block')}">[@s.text name="regionalMapping.CrpProgram.notManagers.span"/]</p>
      </div>
      [#-- Add person Button --]
      [#if editable]
      <div class="text-center">
        <div class="searchUser button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addPerson" /]</div>
      </div>
      [/#if]
      [#-- Hidden Parameters --]
      <span class="usersType" style="display:none">programUser</span>
      <span class="usersRole" style="display:none">${rpmRole.id}</span>
    </div>
    
    [#-- Countries  --]
    <div class="countriesBlock form-group" title="Select Countries clicking here">
      [#-- Countries List --]
      [@customForm.select name="${customName}.selectedCountries" label=""  i18nkey="regionalMapping.CrpProgram.countries" listName="countriesList" keyFieldName="isoAlpha2"  displayFieldName="name" value="${customName}.selectedCountries" multiple=true required=true  className="countriesSelect form-control input-sm" disabled=!editable/]              
      [#-- Hidden Parameters --]
      <span class="usersType" style="display:none">programUser</span>
      <span class="usersRole" style="display:none">{rpRol}</span>
    </div>
  </li>
[/#macro]