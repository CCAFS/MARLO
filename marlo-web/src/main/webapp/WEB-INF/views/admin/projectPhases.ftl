[#ftl]
[#assign title = "Project Phases" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["bootstrap-select","jquery-ui"] /]
[#assign customJS = [ "${baseUrl}/js/admin/projectPhases.js","${baseUrl}/js/global/fieldsValidation.js" ] /]
[#assign customCSS = [ "${baseUrl}/css/admin/projectPhases.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "projectPhases" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"projectPhases", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectPhases.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        <h4 class="sectionTitle">[@s.text name="Project for " /] ${currentCycle} [@s.text name="cycle" /]</h4>
        <div class=" simpleBox row " listname="">
          <div class="col-md-6">
            <label class="text-center col-md-12 tableTitles" for="">All projects</label>
            <div class="clearfix"></div>
            <div id="allProjectList" class="dragProjectList">
            [#list allProjects as project]
              <div id="project-${project.id}" class="borderBox  project" >
                ${project.composedName}
                <input type="hidden" name="" value="${project.id}"/>
              </div>
            [/#list]
            </div>
          </div>
          <div class="col-md-6">
            <label class="text-center col-md-12 tableTitles" for=""> Cycle projects</label>
            <div class="clearfix"></div>
            <div id="phasesProjectList" class="dragProjectList">
            [#list phasesProjects as project]
              <div id="project-${project.id}" class="borderBox  project" >
                ${project.composedName}
                <input type="hidden" name="phasesProjects[${project_index}].id" value="${project.id}"/>
              </div>
            [/#list]
            </div>
          </div>
        </div>
        
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


[#include "/WEB-INF/global/pages/footer.ftl" /]

