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
         [#list 1..4 as program]
          <div class="col-md-6">
            <h5 class="sectionSubTitle" > Flagship ${program}</h5>
            <div class="simpleBox">
              <div class="list">
                <ul></ul>
                <p class="text-center">There are not users added yet.</p>
              </div>
              <div class="searchUser button-green"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="form.buttons.addPerson" /]</div>
            </div>
          </div>
         [/#list]
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

[#include "/WEB-INF/global/pages/footer.ftl" /]