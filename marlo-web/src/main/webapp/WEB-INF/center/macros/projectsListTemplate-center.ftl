[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro projectsList projects={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="header">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="sync">[@s.text name="projectsList.projectSync" /]</th>
        <th id="projectTitles" >[@s.text name="projectsList.projectTitles" /]</th>
        <th id="projectOutputs" >[@s.text name="projectsList.projectOutputs" /]</th>
        <th id="projectLeader">[@s.text name="projectsList.projectLeader" /]</th>
        <th id="projectStatus">[@s.text name="projectsList.status" /]</th>
        <th id="projectDelete">[@s.text name="projectsList.delete" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if projects?has_content]
      [#list projects as project]         
        [#local projectUrl][@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
        [#-- ID --]
        <td class="projectId">
          <a href="${projectUrl}"> P${project.id}</a>
        </td>
        [#-- Sync Project --]
        <td class="projectId">
          <a href="${projectUrl}"> ${action.getProjectSyncCode(project.id)}</a>
        </td>
          [#-- Project Title --]
          <td class="left">
            [#if project.name?has_content]
              <a href="${projectUrl}" title="${project.name}">
              [#if project.name?length < 120] ${project.name}</a> [#else] [@utilities.wordCutter string=project.name maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="${projectUrl}">
                [@s.text name="projectsList.none" /]
              </a>
            [/#if]
          </td>
          [#-- Project Outputs --]
          <td class=""> 
            [#if project.projectOutputs?has_content]
              [#list project.projectOutputs as output]
                [#if output.active]
                  <span>O${(output.id)!''}</span>
                [/#if]
              [/#list]
            [#else]
              [@s.text name="projectsList.none" /]
            [/#if]
          </td>
          [#-- Contact person--]
          <td>
           [#if project.projectLeader?has_content]${(project.projectLeader.composedName)!""}[#else][@s.text name="projectsList.none" /][/#if]
          </td>
          [#-- Status --]
          <td>
           [#if project.projectStatus?has_content]${(project.projectStatus.name)!""}[#else][@s.text name="projectsList.none" /][/#if]
          </td>
          [#-- Delete project--]
          <td class="text-center">
            [#if canEdit && action.centerCanBeDeleted(project.id, project.class.name)!false]
              <a id="removeProject-${project.id}" class="removeProject" href="#" title="">
                <img src="${baseUrl}/global/images/trash.png" title="[@s.text name="projectsList.removeProject" /]" /> 
              </a>
            [#else]
              <img src="${baseUrl}/global/images/trash_disable.png" title="[@s.text name="projectsList.cannotDelete" /]" />
            [/#if]
          </td> 
        </tr>  
      [/#list]
    [/#if]
    </tbody>
  </table>
[/#macro]

