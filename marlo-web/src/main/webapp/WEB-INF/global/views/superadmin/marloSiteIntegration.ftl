[#ftl]
[#assign title = "Site Integration" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "flag-icon-css"] /]
[#assign customJS = [
  "${baseUrlCdn}/global/js/usersManagement.js",
  "${baseUrlCdn}/global/js/superadmin/marloSiteIntegration.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ]
/]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/siteIntegration.css", "${baseUrlCdn}/global/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "siteIntegration" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"marloSiteIntegration", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        <h4 class="sectionTitle">[@s.text name="siteIntegration.title" /]</h4>
        <div class="countriesContent" listname="loggedCrp.siteIntegrations">
          [#if loggedCrp.siteIntegrations?has_content]
            [#list loggedCrp.siteIntegrations as siteIntegration]
              [@countryMacro element=siteIntegration index=siteIntegration_index /]
            [/#list]
          [#else]
            <p class="text-center">[@s.text name="siteIntegration.noSites" /]</p>
          [/#if]
        </div>
        <br />

        [@customForm.select name="" i18nkey="siteIntegration.select.title" listName="countriesList"
          keyFieldName="isoAlpha2" displayFieldName="name" value="id" className="countriesList"/]

        <div class="buttons">
          <div class="buttons-content">
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
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
  [@userItem element={} index=0 name="" template=true /]
</ul>

[@countryMacro element={} index=0 template=true /]

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro userItem element index name template=false]
  [#assign customName = "${name}[${index}]" /]
  <li id="user-${template?string('template',index)}" class="user userItem" style="display:${template?string('none','block')}">
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
    <span class="name"> ${(element.user.getComposedName())!'Unknown user'}</span>
    <input class="user" type="hidden" name="${customName}.user.id" value="${(element.user.id)!}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    <span class="glyphicon glyphicon-remove pull-right remove-userItem" aria-hidden="true"></span>
  </li>
[/#macro]

[#macro countryMacro element index template=false]
  [#local customNameCountry = "loggedCrp.siteIntegrations[${index}]" /]
  <div id="country-${template?string('template',index)}" class="borderBox country col-md-12" style="display:${template?string('none','block')}">
    <div class="removeElement removeCountry" title="Remove Country"></div>
    <h5 class="country-title"><i class="flag-icon flag-icon-${(element.locElement.isoAlpha2?lower_case)!}"></i> ${(element.locElement.name)!}</h5>
    <div class="crpCountry-block">
      <div class="items-list simpleBox" listname="${customNameCountry}.siteLeaders">
        <ul>
          [#if element.siteLeaders?has_content]
            [#list element.siteLeaders as item]
              [@userItem element=item index=item_index name="${customNameCountry}.siteLeaders"/]
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
