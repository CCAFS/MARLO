[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro list projects={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="header">
        <th colspan="4">Project information</th>
        <th colspan="1">Actions</th> 
      </tr>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="projectTitles" >[@s.text name="projectsList.projectTitles" /]</th>
        <th id="projectStatus">[@s.text name="projectsList.projectStatus" /]</th>
        <th id="projectLeader" >[@s.text name="projectsList.projectLeader" /]</th>
        <th id="projectDelete">[@s.text name="projectsList.delete" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if projects?has_content]
      [#list projects as project]
        <tr>
        [#-- ID --]
        <td class="projectId">
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">P${project.id}</a>
        </td>
          [#-- Deliverable Title --]
          <td class="left"> 
            [#if project.title?has_content]
              <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]" title="${project.title}">
              [#if project.title?length < 120] ${project.title}</a> [#else] [@utilities.wordCutter string=project.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url] ">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
          </td>
          [#-- Project Status --]
          <td>
            ${(project.statusName)!'none'}
          </td>
          [#-- Project Leader --]
          <td class=""> 
            ${(project.createdBy.composedName)!'Not defined'}
          </td>
          [#-- Delete Project--]
          <td class="text-center">
            [#--if (action.hasProjectPermission("deleteProject", project.id, "manage") && project.isNew(currentPlanningStartDate)) --]
            [#if true]
              <a id="removeDeliverable-${project.id}" class="removeProject" href="[@s.url namespace=namespace action="${(crpSession)!}/deleteCofunded"][@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]" title="">
                <img src="${baseUrl}/images/global/trash.png" title="[@s.text name="projectsList.removeDeliverable" /]" /> 
              </a>
            [#else]
              <img src="${baseUrl}/images/global/trash_disable.png" title="[@s.text name="projectsList.cannotDelete" /]" />
            [/#if]
          </td>
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]