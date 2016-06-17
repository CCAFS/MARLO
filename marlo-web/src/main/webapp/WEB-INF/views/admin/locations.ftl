[#ftl]
[#assign title = "Locations" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrl}/js/admin/locations.js" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "locations" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"locations", "nameSpace":"", "action":""}
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
        
        <h4 class="sectionTitle">Locations</h4>
        <div class="row">
          
        </div>
        
        <div class="buttons">
          [@s.submit type="button" name="save" cssClass=""][@s.text name="form.buttons.save" /][/@s.submit]
        </div>
        
        [/@s.form]
      </div>
    </div>
  </div>
</section>



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
