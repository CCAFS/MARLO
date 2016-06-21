[#ftl]
[#assign title = "Site Integration" /]
[#assign pageLibs = ["select2", "flat-flags"] /]
[#assign customJS = ["${baseUrl}/js/global/usersManagement.js", "${baseUrl}/js/admin/siteIntegration.js" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "siteIntegration" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"siteIntegration", "nameSpace":"", "action":""}
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
        
        <h4 class="sectionTitle">Site Integration</h4>
        <div class="borderBox"> 
        
        [#if loggedCrp.siteIntegrations?has_content]
          [#list loggedCrp.siteIntegrations as crpCountry]

            [@countryMacro element=crpCountry index=crpCountry_index name='countries'  /]

          [/#list]
          
        [#else]
          <p class="text-center">There are not countries added yet</p>
        [/#if] 
        </div>
        <br />
        [#-- List of countries --]
        [@customForm.select name="" label="Select a country:" i18nkey="" listName="countriesList" keyFieldName="isoAlpha2" displayFieldName="name" value="id"  /]

       
        
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

<ul style="display:none">
  [#-- User Item template --]
  [@userItem element={} index=0 name="" template=true /]
</ul>

[#-- Country Item template --]
  [@countryMacro element={} index=0 name="" template=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro userItem element index name template=false]
  [#assign customName = "${name}[${index}]" /]
  <li id="user-${template?string('template',index)}" class="user userItem" style="display:${template?string('none','block')}">
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
    <span class="name"> ${(element.user.getComposedName()?html)!'Unknown user'}</span>
    <input class="user" type="hidden" name="${customName}.user.id" value="${(element.user.id)!}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    <span class="glyphicon glyphicon-remove pull-right remove-userItem" aria-hidden="true"></span>
  </li>
[/#macro]

[#macro countryMacro element index name template=false]
[#assign customNameCountry = "loggedCrp.siteIntegrations[${index}].locElement" /]
  <div id="country-${template?string('template','')}" class="country col-md-12" style="display:${template?string('none','block')}">
    <h5 class="country-title"><i class="flag-sm flag-sm-${(element.locElement.isoAlpha2?upper_case)!}"></i>  ${(element.locElement.name)!}</h5>
    <div class="crpCountry-block">
      <div class="items-list simpleBox">
        <ul>
        [#if loggedCrp.siteIntegrations.siteLeaders?has_content]
          [#list loggedCrp.siteIntegrations.siteLeaders as item]
          [@userItem element=item index=item_index name=customNameCountry /]
          [/#list]
        [/#if] 
        </ul>
        <p class="text-center" style="display:${(loggedCrp.siteIntegrations.siteLeaders?has_content)?string('none','block')}">There are not users added yet.</p>
      </div>
      <div class="text-center">
        <div class="searchUser button-green">
          <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>[@s.text name="form.buttons.addPerson" /]
          <span class="inputName-input" style="display:none">${customNameCountry}</span>
        </div>
      </div>
    </div>
    <input class="Id" type="hidden" name="${customNameCountry}.id" value="${(element.id)!}"/>
     <input class="isoAlpha" type="hidden" name="${customNameCountry}.isoAlpha2" value="${(element.locElement.isoAlpha2)!}"/>
  </div>  
[/#macro]