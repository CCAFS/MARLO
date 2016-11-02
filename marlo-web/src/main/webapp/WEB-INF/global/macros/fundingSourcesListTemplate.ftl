[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro list projects={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="header">
        <th colspan="6">Funding Source information</th>
        <th colspan="1">Actions</th> 
      </tr>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="projectTitles" >[@s.text name="projectsList.fundingTitle" /]</th>
        <th id="projectBudgetType" >[@s.text name="projectsList.projectBudgetType" /]</th>
        <th id="projectStatus">[@s.text name="projectsList.projectStatus" /]</th>
        <th id="projectStatus">CGIAR lead center</th>
        <th id="projectDonor" >[@s.text name="projectsList.projectDonor" /]</th>
        <th id="projectDelete">[@s.text name="projectsList.delete" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if projects?has_content]
      [#list projects as project]
        <tr>
        [#-- ID --]
        <td class="projectId">
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">F${project.id}</a>
        </td>
          [#-- Funding source Title --]
          <td class="left"> 
            [#if project.description?has_content]
              <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='fundingSourceID']${project.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]" title="${project.description}">
              [#if project.description?length < 120] ${project.description}</a> [#else] [@utilities.wordCutter string=project.description maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url] ">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
          </td>
          [#-- Project Budget --]
          <td class=""> 
            ${(project.budgetType.name)!'Not defined'}
          </td>
          [#-- Project Status --]
          <td>
            ${(project.statusName)!'none'}
          </td>
          [#-- Center Lead --]
          <td class=""> 
            ${(project.leader.composedName)!'Not defined'}
          </td>
           [#-- Donor --]
          <td class=""> 
            ${(project.institution.composedName)!'Not defined'}
          </td>
          [#-- Delete Project--]
          <td class="text-center">
            [#if action.canBeDeleted(project.id, project.class.name)]
              <a id="removeDeliverable-${project.id}" class="removeProject" href="[@s.url namespace=namespace action="${(crpSession)!}/deleteFundingSources"][@s.param name='fundingSourceID']${project.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]" title="">
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