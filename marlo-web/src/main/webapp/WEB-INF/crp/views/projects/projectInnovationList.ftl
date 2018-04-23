[#ftl]
[#assign title = "Project Innovations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#-- TODO: Remove unused pageLibs--]
[#assign pageLibs = ["select2","font-awesome","dropzone","blueimp-file-upload","jsUri"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectInnovations.js",
  "${baseUrl}/global/js/autoSave.js",
  "${baseUrl}/global/js/fieldsValidation.js"
] /]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectInnovations.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "innovations" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"P${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"innovationsList", "nameSpace":"/projects", "action":"${(crpSession)!}/innovations" ,"param":"projectID=${projectID}"},
  {"label":"innovationInformation", "nameSpace":"/projects", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="container">
  <div class="row">
    [#-- Project Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
    </div>
    [#-- Project Section Content --]
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
  
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]

        <h3 class="headTitle">[@s.text name="projectInnovations" /]</h3>

        [#-- Innovations List Table --]
        [@innovationsTableMacro /]

        [#-- Add Innovation Button --]
        [#if canEdit]
        <div class="buttons">
          <div class="buttons-content">
            <div class="addInnovation button-blue"><a  href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/addNewInnovation'][@s.param name="projectID"]${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              [@s.text name="form.buttons.addInnovation" /]
            </a></div>
            <div class="clearfix"></div>
          </div>
        </div>
        [/#if]

      [/@s.form]
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro innovationsTableMacro]
  <table class="table-innovations table-border-powb" id="table-innovations">
    <thead>
      <tr class="subHeader">
        <th id="tb-id" width="9%">[@s.text name="projectInnovations.table.id" /]</th>
        <th id="tb-title" width="43%">[@s.text name="projectInnovations.table.title" /]</th>
        <th id="tb-type" width="22%">[@s.text name="projectInnovations.table.type" /]</th>
        <th id="tb-stage" width="13%">[@s.text name="projectInnovations.table.stage" /]</th>
        <th id="tb-year" width="9%">[@s.text name="projectInnovations.table.year" /]</th>
        <th id="tb-remove" width="4%"></th>
      </tr>
    </thead>
    <tbody>
    [#if project.innovations?has_content]
      [#list project.innovations as innovation]
        [#local tsURL][@s.url namespace="/projects" action="${(crpSession)!}/innovation"][@s.param name='innovationID']${(innovation.id)!''}[/@s.param][@s.param name='projectID']${(innovation.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
          [#-- ID --]
          <td class="tb-id text-center">
            <a href="${tsURL}" target="_blank">${(innovation.id)!}</a>
          </td>
          [#-- Title --]
          <td class="tb-title">
            [#if innovation.projectInnovationInfo.title?has_content]
                <span>${(innovation.projectInnovationInfo.title)!''}</span>
            [#else]
                <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
            [/#if]
          </td>
          [#-- Type --]
          <td>
          [#if innovation.projectInnovationInfo.repIndInnovationType?has_content]
            <span>${(innovation.projectInnovationInfo.repIndInnovationType.name)!''}</span>
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Stage --]
          <td class="text-center">
          [#if innovation.projectInnovationInfo.repIndStageInnovation?has_content]
            ${innovation.projectInnovationInfo.repIndStageInnovation.name!''}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Stage --]
          <td class="text-center">
          [#if innovation.year?has_content]
            ${innovation.year!''}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Remove --]
          <td class="remove" title="Remove">
          
          </td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="6">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]