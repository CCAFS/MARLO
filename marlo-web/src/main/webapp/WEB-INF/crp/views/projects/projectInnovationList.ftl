[#ftl]
[#assign title = "Project Innovations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectInnovationsList.js",
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
        <div class="simpleBox">
          [@innovationsTableMacro /]
        </div>

        [#-- Add Innovation Button --]
        [#if canEdit]
        <div class="buttons">
          <div class="buttons-content">
            <div class="addInnovation button-blue"><a  href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/addNewInnovation'][@s.param name="projectID"]${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addInnovation" /]
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
  <table id="table-innovations" class="table table-striped table-hover">
    <thead>
      <tr class="subHeader">
        <th id="tb-id" width="9%">[@s.text name="projectInnovations.table.id" /]</th>
        <th id="tb-title" width="40%">[@s.text name="projectInnovations.table.title" /]</th>
        <th id="tb-type" width="22%">[@s.text name="projectInnovations.table.type" /]</th>
        <th id="tb-stage" width="15%">[@s.text name="projectInnovations.table.stage" /]</th>
        <th id="tb-year" width="8%">[@s.text name="projectInnovations.table.year" /]</th>
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
            <a href="${tsURL}" >${(innovation.id)!}</a>
          </td>
          [#-- Title --]
          <td class="tb-title">
            <a href="${tsURL}">
              [#if innovation.projectInnovationInfo.title?has_content]${(innovation.projectInnovationInfo.title)!''}[#else][@s.text name="global.untitled"/] [/#if]
            </a>
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
          [#if innovation.projectInnovationInfo.year?has_content]
            ${innovation.projectInnovationInfo.year!''}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Remove --]
          <td class="text-center">
            [#if canEdit ]
              <a id="remove-innovation" class="remove-innovation" href="[@s.url namespace="/projects" action="${(crpSession)!}/deleteInnovation"][@s.param name='innovationID']${(innovation.id)!''}[/@s.param][@s.param name='projectID']${(innovation.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" title="" >
                <img src="${baseUrl}/global/images/trash.png" title="[@s.text name="projectInnovations.table.remove" /]" /> 
              </a>
            [#else]
              <img src="${baseUrl}/global/images/trash_disable.png" title="[@s.text name="projectInnovations.table.cantDelete" /]" />
            [/#if]
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