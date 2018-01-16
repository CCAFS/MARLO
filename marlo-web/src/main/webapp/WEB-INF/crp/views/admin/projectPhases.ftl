[#ftl]
[#assign title = "Project Phases" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["bootstrap-select","jquery-ui"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/admin/projectPhases.js",
  "${baseUrlMedia}/global/js/fieldsValidation.js" 
  ] 
/]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/projectPhases.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "projectPhases" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"projectPhases", "nameSpace":"", "action":""}
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
        <h4 class="sectionTitle">[@s.text name="Projects for " /] ${currentCycle} (${currentCycleYear}) [@s.text name="cycle" /]</h4>
        [#if editable]
        <div class="note form-group">To enable or disable projects in the current cycle. You can drag the project from one column and place it in another column.</div>
        [/#if]
        <div class=" simpleBox row " listname="">
          <div class="col-md-6">
            <label class="text-center col-md-12 tableTitles" for="">Projects disabled for this cycle</label>
            <div class="clearfix"></div>
            <div id="allProjectList" class="dragProjectList">
            [#list allProjects as project]
              <div id="project-${project.id}" class="borderBox  project" >
                [@utils.wordCutter string=(project.projectInfo.composedName) maxPos=70 substr=" "/]
                <input type="hidden" name="" value="${project.id}"/>
              </div>
            [/#list]
            </div>
          </div>
          <div class="col-md-6">
            <label class="text-center col-md-12 tableTitles" for="">Projects enabled for this cycle</label>
            <div class="clearfix"></div>
            <div id="phasesProjectList" class="dragProjectList">
            [#list phasesProjects as project]
              <div id="project-${project.id}" class="borderBox  project">
                [@utils.wordCutter string=(project.projectInfo.composedName) maxPos=70 substr=" "/]
                <input type="hidden" name="phasesProjects[${project_index}].id" value="${project.id}"/>
              </div>
            [/#list]
            </div>
          </div>
        </div>
        
        [#-- Section Buttons--]
        [#include "/WEB-INF/crp/views/admin/buttons-admin.ftl" /]
        
        [/@s.form]
        
      </div>
    </div>
  </div>
</section>


[#include "/WEB-INF/crp/pages/footer.ftl" /]

