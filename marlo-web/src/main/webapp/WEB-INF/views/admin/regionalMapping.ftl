[#ftl]
[#assign title = "Regional Mapping" /]
[#assign pageLibs = ["select2", "flat-flags"] /]
[#assign customJS = ["${baseUrl}/js/global/usersManagement.js", "${baseUrl}/js/admin/regionalMapping.js" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "regionalMapping" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"regionalMapping", "nameSpace":"", "action":""}
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
        [@s.form action=actionName enctype="multipart/form-data"]
        
        <h4 class="sectionTitle">[@s.text name="regionalMapping.title"/]</h4>
        <div class="program-block">
          [#-- Regions List --]
          <div class="regions items-list">
            <ul class="regions-list">
            [#if regionsPrograms?has_content]
              [#list regionsPrograms as item]
                [@programItem element=item index=item_index name="regionsPrograms"/]
              [/#list] 
            [/#if]
            </ul>
            [#if !regionsPrograms?has_content]
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
          [#if editable]
            <a href="[@s.url][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> [@s.text name="form.buttons.back" /]</a>
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-save" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
          [#else]
            [#if canEdit]
              <a href="[@s.url][@s.param name="edit" value="true"/][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
            [/#if]
          [/#if]
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
    <span class="glyphicon glyphicon-remove pull-right remove-userItem" aria-hidden="true"></span>
    [/#if]
  </li>
[/#macro]

[#macro programItem element index name template=false]
  [#assign customName = "${name}[${index}]" /]
  <li id="program-${template?string('template',index)}" class="program borderBox" style="display:${template?string('none','block')}">
    [#-- Remove Button  --]
    [#if editable]
      <div class="remove-programItem removeElement" title="Remove program"></div>
    [/#if]
    [#-- Program Acronym & Name --]
    <div class="form-group">
      <label for="">[@s.text name="regionalMapping.CrpProgram.name"/]</label>
      <div class="row">
        <div class="col-sm-2">[@customForm.input name="${customName}.acronym" type="text" showTitle=false placeholder="regionalMapping.CrpProgram.inputAcronym.placeholder" className="acronym-input" required=true editable=true /]</div>
        <div class="col-sm-9">[@customForm.input name="${customName}.name" type="text" showTitle=false placeholder="regionalMapping.CrpProgram.inputName.placeholder" className="name-input" required=true editable=true /]</div>
      </div>
    </div>
    [#-- Hidden inputs  --]
    <input class="type" type="hidden" name="${customName}.programType" value="${(element.programType)!'-1'}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    [#-- Leaders  --]
    <label for="">[@s.text name="regionalMapping.CrpProgram.leaders"/]</label>
    <div class="usersBlock simpleBox">
      [#-- Leaders List --]
      <div class="items-list">
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
      <span class="usersRole" style="display:none">{rplRole.id}</span>
    </div>
    
    [#-- Countries  --]
    <label for="">[@s.text name="regionalMapping.CrpProgram.countries"/]</label>
    <div class="countriesBlock form-group">
      [#-- Countries List --]
      [@customForm.select name="${customName}.selectedCountries" label="" i18nkey="" listName="countriesList" keyFieldName="isoAlpha2"  displayFieldName="name" value="${customName}.selectedCountries" multiple=true   className="countriesSelect form-control input-sm" editable=editable/]              
      [#-- Hidden Parameters --]
      <span class="usersType" style="display:none">programUser</span>
      <span class="usersRole" style="display:none">{rpRol}</span>
    </div>
  </li>
[/#macro]