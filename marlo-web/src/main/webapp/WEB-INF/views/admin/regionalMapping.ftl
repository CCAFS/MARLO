[#ftl]
[#assign title = "Regional Mapping" /]
[#assign pageLibs = [] /]
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
        
        <h4 class="sectionTitle">Regional Mapping</h4>
        [#-- Regions List --]
        <div class="regions items-list simpleBox">
         <ul>
         [#if regionsPrograms?has_content]
          [#list regionsPrograms as item]
            [@programItem element=item index=item_index name="regionsPrograms"/]
          [/#list]
         [/#if]
         </ul>
        <p class="text-center" style="display:${(regionsPrograms?has_content)?string('none','block')}">There are not regions added yet.</p>
        </div>
        [#-- Add Region--]
        <div class="row">
          <div class="col-sm-2"><input type="text" class="acronym-input form-control" placeholder="Acronym"></div>
          <div class="col-sm-8"><input type="text" class="name-input form-control" placeholder="Region name"></div>
          <div class="col-sm-2">
            <div class="addProgram button-blue"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.add" /]</div>
            <span class="type-input" style="display:none">2</span>
            <span class="inputName-input" style="display:none">regionsPrograms</span>
          </div>
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
