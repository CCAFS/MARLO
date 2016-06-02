[#ftl]
[#assign title = "Leaders" /]
[#assign pageLibs = [] /]
[#assign customJS = ["${baseUrl}/js/global/usersManagement.js", "${baseUrl}/js/admin/leaders.js" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "leaders" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"leaders", "nameSpace":"", "action":""}
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
        
        <h4 class="sectionTitle">(Flagships / Regions) Leaders</h4>
        <div class="row">
          [#if programs?has_content]
            [#list programs as program]
            [#assign customNameProgram = "programs[${program_index}].leaders" /]
            <div class="col-md-6">
              <h5 class="sectionSubTitle" > ${program.name} (${program.acronym})</h5>
              <div class="program-block borderBox">
                <div class="items-list">
                  <ul>
                  [#list program.leaders as item]
                    [@userItem element=item index=item_index name=customNameProgram /]
                  [/#list]
                  </ul>
                  <p class="text-center" style="display:${(program.leaders?has_content)?string('none','block')}">There are not users added yet.</p>
                </div>
                <div class="searchUser button-green">
                  <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>[@s.text name="form.buttons.addPerson" /]
                  <span class="inputName-input" style="display:none">${customNameProgram}</span>${customNameProgram}
                </div>
              </div>
            </div>
            [#if (program_index%2) = 1] <div class="clearfix"></div> [/#if]
            [/#list]
          [#else]
            <p class="text-center">There are not programs added yet.</p>
          [/#if]
          
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
