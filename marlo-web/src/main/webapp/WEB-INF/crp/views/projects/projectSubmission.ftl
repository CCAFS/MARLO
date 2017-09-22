[#ftl]
[#assign title = "Project Submission" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign customJS = [] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "Submission" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectSubmission", "nameSpace":"/projects", "action":""}
] /]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        <h1 class="successfullyTitle">Project Submit</h1>
        <div class="borderBox">
        [#if complete]
          <h2 class="successTitle">The project has been successfully submitted</h2>
          <div class="fullPartBlock">
            <h6>Project title</h6>
            <p>${(project.title)!"Title not defined"}</p>
          </div> 
          <div class="fullPartBlock">
              <h6>Submission date</h6>
              [#if submission]
                  [#assign lastSubmission =action.getProjectSubmissions(projectID)?last /]
              [/#if]
              <p>${(lastSubmission.cycle)!} - ${(lastSubmission.year)!} - ${(lastSubmission.dateTime?date)!} by ${(lastSubmission.user.composedCompleteName)!}</p>
          </div> 
          <div class="fullPartBlock">
            <h6>Download Full Project Report</h6>
            <a href="[@s.url namespace="/projects" action='${(crpSession)!}/reportingSummary'][@s.param name='cycle']${action.getCurrentCycle()}[/@s.param][@s.param name='projectID']${project.id?c}[/@s.param][/@s.url]" class="button-pdf-format" target="__BLANK">PDF Format</a> 
          
          </div> 
        [#else]
          <p>The project is still incomplete, please go to the sections without the green check mark and complete the missing fields before submitting your project.</p>
        [/#if]
        </div>
      </div>
    </div>  
</section>

[#include "/WEB-INF/crp/pages/footer.ftl"]