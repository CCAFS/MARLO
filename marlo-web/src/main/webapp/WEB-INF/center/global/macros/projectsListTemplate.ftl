[#ftl]
[#import "/WEB-INF/center/global/macros/utils.ftl" as utilities/]
[#macro projectsList projects={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="header">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="projectTitles" >[@s.text name="projectsList.projectTitles" /]</th>
        <th id="projectOutputs" >[@s.text name="projectsList.projectOutputs" /]</th>
        <th id="projectLeader">[@s.text name="projectsList.projectLeader" /]</th>
        <th id="projectStartDate">[@s.text name="projectsList.startDate" /]</th>
        <th id="projectEndDate">[@s.text name="projectsList.endDate" /]</th>
        <th id="projectDelete">[@s.text name="projectsList.delete" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if projects?has_content]
      [#list projects as project]         
        [#local projectUrl][@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url][/#local]
        <tr>
        [#-- ID --]
        <td class="projectId">
          <a href="${projectUrl}"> P${project.id}</a>
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
          [#-- start date --]
          <td>
           [#if project.startDate?has_content]${(project.starDateFormat)!""}[#else][@s.text name="projectsList.none" /][/#if]
          </td>
          [#-- end date --]
          <td>
           [#if project.endDate?has_content]${(project.endDateFormat)!""}[#else][@s.text name="projectsList.none" /][/#if]
          </td>
          [#-- Delete project--]
          <td class="text-center">
            [#if canEdit && action.centerCanBeDeleted(project.id, project.class.name)!false]
              <a id="removeProject-${project.id}" class="removeProject" href="#" title="">
                <img src="${baseUrlMedia}/images/global/trash.png" title="[@s.text name="projectsList.removeProject" /]" /> 
              </a>
            [#else]
              <img src="${baseUrlMedia}/images/global/trash_disable.png" title="[@s.text name="projectsList.cannotDelete" /]" />
            [/#if]
          </td> 
        </tr>  
      [/#list]
    [/#if]
    </tbody>
  </table>
[/#macro]

