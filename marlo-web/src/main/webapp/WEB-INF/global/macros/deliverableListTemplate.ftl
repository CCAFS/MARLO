[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

[#macro deliverablesList deliverables={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="deliverables"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="deliverableTitles" >Deliverable Name</th>
        <th id="deliverableType">[@s.text name="projectsList.projectType" /]</th>
        <th id="deliverableEDY">Expected delivery year</th>
        <th id="deliverableFC">FAIR compliance</th>
        <th id="deliverableStatus">Status</th>
        <th id="deliverableRF">Required Fields</th>
        <th id="deliverableDelete">[@s.text name="projectsList.delete" /]</th>
      </tr>
    </thead>
    <tbody>
      [#list deliverables as deliverable]
        <tr>
        [#-- ID --]
        <td class="projectId">
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${deliverable.id?c}[/@s.param][/@s.url]"> P${deliverable.id}</a>
        </td>
          [#-- Project Title --]
          <td class="left"> 
            [#if project.title?has_content]
              <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='projectID']${deliverable.id?c}[/@s.param][/@s.url]" title="${deliverable.title}">
              [#if project.title?length < 120] ${deliverable.title}</a> [#else] [@utilities.wordCutter string=project.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='projectID']${deliverable.id?c}[/@s.param][/@s.url] ">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
          </td>
          [#-- Project Type --]
          <td>
            [@s.text name="project.type.${(project.type?lower_case)!'none'}" /]
          </td>
        </tr>  
      [/#list]
    </tbody>
  </table>
[/#macro]