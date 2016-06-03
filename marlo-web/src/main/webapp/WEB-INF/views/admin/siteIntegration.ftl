[#ftl]
[#assign title = "Site Integration" /]
[#assign pageLibs = ["select2", "flag-icon-css"] /]
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
        [#assign crpCountries = [{'name': 'Colombia','code': 'co'},{'name':'China','code': 'cn'}] /]
        [#if crpCountries?has_content]
          [#list crpCountries as crpCountry]
          [#assign customNameCountry = "crpCountries[${crpCountry_index}].leaders" /]
          <h5 class="sectionSubTitle" > <span class="flag-icon flag-icon-${crpCountry.code}"></span> ${crpCountry.name}</h5>
          <div class="borderBox"> 
            <div class="crpCountry-block">
              <div class="items-list simpleBox">
                <ul>
                [#assign leaders = [] /]
                [#list leaders as item]
                  [@userItem element=item index=item_index name=customNameCountry /]
                [/#list]
                </ul>
                <p class="text-center" style="display:${(leaders?has_content)?string('none','block')}">There are not users added yet.</p>
              </div>
              <div class="searchUser button-green">
                <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>[@s.text name="form.buttons.addPerson" /]
                <span class="inputName-input" style="display:none">${customNameCountry}</span>
              </div>
            </div>
          </div>
          [/#list]
        [#else]
          <p class="text-center">There are not countries added yet</p>
        [/#if] 
        
        <br />
        [#-- List of countries --]
        [@customForm.select name="" label="Select a country:" i18nkey="" listName="countriesList" keyFieldName="id" displayFieldName="" value="" /]

       
        
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

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro userItem element index name template=false]
  [#assign customName = "${name}[${index}]" /]
  <li id="user-${template?string('template',index)}" class="user" style="display:${template?string('none','block')}">
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
    <span class="name"> ${(element.user.getComposedName()?html)!'Unknown user'}</span>
    <input class="user" type="hidden" name="${customName}.user.id" value="${(element.user.id)!}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    <span class="glyphicon glyphicon-remove pull-right remove-userItem" aria-hidden="true"></span>
  </li>
[/#macro]