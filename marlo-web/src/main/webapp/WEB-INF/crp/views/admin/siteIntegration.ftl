[#ftl]
[#assign title = "Site Integration" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2", "flat-flags"] /]
[#assign customJS = [
  "${baseUrl}/global/js/usersManagement.js", 
  "${baseUrlMedia}/js/admin/siteIntegration.js",
  "${baseUrl}/global/js/fieldsValidation.js" 
  ] 
/]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/siteIntegration.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "siteIntegration" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"siteIntegration", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="siteIntegration.help" /] </p>
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
      
      [#--  <div class="text-center col-md-12  alert alert-info"><span> This section is provisional for it to be available when there are more details defined. </span></div>--]
      
      
        [@s.form action=actionName enctype="multipart/form-data" ]  
        
        <h4 class="sectionTitle">[@s.text name="siteIntegration.title" /]</h4>
        <div class="countriesContent" listname="loggedCrp.siteIntegrations"> 
        
        [#if loggedCrp.siteIntegrations?has_content]
          [#list loggedCrp.siteIntegrations as crpCountry]
            [@countryMacro element=crpCountry index=crpCountry_index name='countries'  /]
          [/#list]
        [#else]
          <p class="text-center">[@s.text name="siteIntegration.noSites" /]</p>
        [/#if] 
        </div>
        <br />
        
        [#-- List of countries --]
        [#if editable]
          [@customForm.select name=""  i18nkey="siteIntegration.select.title" listName="countriesList" keyFieldName="isoAlpha2" displayFieldName="name" value="id" className="countriesList"/]
        [/#if]
        
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

<ul style="display:none">
  [#-- User Item template --]
  [@userItem element={} index=0 name="" template=true /]
</ul>

[#-- Country Item template --]
  [@countryMacro element={} index=0 name="" template=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro userItem element index name template=false hasRegions=false]
  
  [#assign customName = "${name}[${index}]" /]
  <li id="user-${template?string('template',index)}" class="user userItem" style="display:${template?string('none','block')}">
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
    <span class="name"> ${(element.user.getComposedName()?html)!'Unknown user'}</span>
    <input class="user" type="hidden" name="${customName}.user.id" value="${(element.user.id)!}"/>
    <input class="role" type="hidden" name="${customName}.role.id" value="${(slRole.id)!}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    [#-- Remove Button --]
    [#if editable && !hasRegions]
      <span class="glyphicon glyphicon-remove pull-right remove-userItem" aria-hidden="true"></span>
    [/#if]
  </li>
[/#macro]

[#macro countryMacro element index name template=false]
[#local customNameCountry = "loggedCrp.siteIntegrations[${index}]" /]
[#local hasRegions = element.programName?has_content /]
  <div id="country-${template?string('template','')}" class="borderBox country col-md-12" style="display:${template?string('none','block')}">
    [#-- Remove Button --]
    [#if editable && !hasRegions]
      <div class=" removeElement removeCountry" title="Remove Country"></div>
    [/#if]
    <h5 class="country-title"><i class="flag-sm flag-sm-${(element.locElement.isoAlpha2?upper_case)!}"></i>  ${(element.locElement.name)!}   [#if hasRegions][#list element.programName as regName]- ${regName!} [/#list][/#if] </h5>
    <div class="crpCountry-block">
      <div class="items-list simpleBox" listname="${customNameCountry}.siteLeaders">
        <ul>
        [#if element.siteLeaders?has_content]
          [#list element.siteLeaders as item]
          [@userItem element=item index=item_index name="${customNameCountry}.siteLeaders" hasRegions=item.regional/]
          [/#list]
        [/#if] 
        </ul>
        <p class="text-center" style="display:${(element.siteLeaders?has_content)?string('none','block')}">[@s.text name="siteIntegration.notUsers" /]</p>
      </div>
      <div class="text-center">
        <div class="searchUser button-green">
          <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>[@s.text name="form.buttons.addPerson" /]
          <span class="inputName-input" style="display:none">${customNameCountry}</span>
        </div>
      </div>
    </div>
    <input class="id" type="hidden" name="${customNameCountry}.id" value="${(element.id)!}"/>
    <input class="isoAlpha2" type="hidden" name="${customNameCountry}.locElement.isoAlpha2" value="${(element.locElement.isoAlpha2)!}"/>
  </div>  
[/#macro]