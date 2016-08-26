[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

[#macro deliverablesList deliverables={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction=""]
  <table class="deliverableList" id="deliverables">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="deliverableTitles" >Deliverable Name</th>
        <th id="deliverableType">Type</th>
        <th id="deliverableEDY">Expected delivery year</th>
        <th id="deliverableFC">FAIR compliance</th>
        <th id="deliverableStatus">Status</th>
        <th id="deliverableRF">Required Fields</th>
        <th id="deliverableDelete">[@s.text name="projectsList.delete" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if deliverables?has_content]
      [#list deliverables as deliverable]
        <tr>
        [#-- ID --]
        <td class="projectId">
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">P${deliverable.id}</a>
        </td>
          [#-- Deliverable Title --]
          <td class="left"> 
            [#if deliverable.title?has_content]
              <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='deliverableID']${deliverable.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]" title="${deliverable.title}">
              [#if deliverable.title?length < 120] ${deliverable.title}</a> [#else] [@utilities.wordCutter string=deliverable.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='deliverableID']${deliverable.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url] ">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
          </td>
          [#-- Deliverable Type --]
          <td>
            ${(deliverable.deliverableType.name?lower_case)!'none'}
          </td>
          [#-- Deliverable Year --]
          <td>
            ${(deliverable.year)!'none'}
          </td>
          [#-- Deliverable FAIR compliance --]
          <td class="fair">
            <div class="col-md-2 active">F</div>
            <div class="col-md-2 active">A</div>
            <div class="col-md-2 ">I</div>
            <div class="col-md-2 ">R</div>
          </td>
          [#-- Deliverable Status --]
          <td>
            ${(deliverable.status)!'none'}
          </td>
          [#-- Deliverable required fields --]
          <td>
            {TODO}
          </td>
          [#-- Delete Deliverable--]
          <td>
            [#--if (action.hasProjectPermission("deleteProject", project.id, "manage") && project.isNew(currentPlanningStartDate)) --]
            [#if true]
              <a id="removeDeliverable-${deliverable.id}" class="removeDeliverable" href="#" title="">
                <img src="${baseUrl}/images/global/trash.png" title="Delete deliverable" /> 
              </a>
            [#else]
              <img src="${baseUrl}/images/global/trash_disable.png" title="Delete deliverable" />
            [/#if]
          </td>
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]