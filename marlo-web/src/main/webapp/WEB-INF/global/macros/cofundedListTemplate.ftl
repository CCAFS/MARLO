[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro cofundedList projects={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="header">
        <th colspan="7">Project information</th>
        <th colspan="3">Actions</th> 
      </tr>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="projectTitles" >[@s.text name="projectsList.projectTitles" /]</th>
        <th id="projectStatus">[@s.text name="projectsList.projectStatus" /]</th>
        <th id="projectLeader" >[@s.text name="projectsList.projectLeader" /]</th>
        <th id="projectType">[@s.text name="projectsList.projectType" /]</th>
        <th id="projectDelete">[@s.text name="projectsList.delete" /]</th>
        [#if isPlanning]
          <th id="projectBudget">[@s.text name="planning.projects.completion" /]</th>
        [/#if]
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
            [#if project.title?has_content]
              <a href="${projectUrl}" title="${project.title}">
              [#if project.title?length < 120] ${project.title}</a> [#else] [@utilities.wordCutter string=project.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="${projectUrl}">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
          </td>
          [#-- Project Status --]
          <td>
            [#if project.status?has_content]${(project.status)!'none'}[#else][@s.text name="projectsList.title.none" /][/#if]
          </td>
          [#-- Project Leader --]
          <td class=""> 
            [#if project.leader?has_content]${(project.leader.institution.acronym)!project.leader.institution.name}[#else][@s.text name="projectsList.title.none" /][/#if]
          </td>
          [#-- Project Type --]
          <td>
            [@s.text name="project.type.${(project.type?lower_case)!'none'}" /]
          </td>
          
          [#-- Delete Project--]
          <td>
            [#--if (action.hasProjectPermission("deleteProject", project.id, "manage") && project.isNew(currentPlanningStartDate)) --]
            [#if true]
              <a id="removeProject-${project.id}" class="removeProject" href="#" title="">
                <img src="${baseUrl}/images/global/trash.png" title="[@s.text name="projectsList.deleteProject" /]" /> 
              </a>
            [#else]
              <img src="${baseUrl}/images/global/trash_disable.png" title="[@s.text name="projectsList.cantDeleteProject" /]" />
            [/#if]
          </td>
        </tr>  
      [/#list]
    [/#if]
    </tbody>
  </table>
[/#macro]