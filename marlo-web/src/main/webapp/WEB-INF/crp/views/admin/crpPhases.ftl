[#ftl]
[#assign title = "Project Phases" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["bootstrap-select","jquery-ui"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/admin/projectPhases.js",
  "${baseUrlMedia}/global/js/fieldsValidation.js" 
  ] 
/]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/projectPhases.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "crpPhases" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"crpPhases", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectPhases.help" /] </p>
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
        [@s.form action=actionName enctype="multipart/form-data" ]
        
        <h4 class="sectionTitle">CRP Phases</h4>
        <div class="borderBox row ">
          [#if phases?size > 1]
            [#list phases as phase]
              <div class="simpleBox">
                <h4> ${(phase.description)!} ${(phase.year)!}</h4>
                
                <p><strong>editable:</strong> ${phase.editable?string}</p>
                <p><strong>visible:</strong> ${phase.visible?string}</p>
                
                <div class="form-group row">
                  <div class="col-md-6">
                    <strong>From: </strong>${phase.startDate}
                  </div>
                  <div class="col-md-6">
                    <strong>Until: </strong>${phase.endDate}
                  </div>
                </div>
                 
              </div>
            [/#list]
          [#else]
            <p>No Phases in the system</p>
          [/#if]
        </div>
        
        [#-- Section Buttons--]
        [#include "/WEB-INF/crp/views/admin/buttons-admin.ftl" /]
        
        [/@s.form]
        
      </div>
    </div>
  </div>
</section>


[#include "/WEB-INF/crp/pages/footer.ftl" /]

