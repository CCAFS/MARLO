[#ftl]
[#import "/WEB-INF/center/global/macros/utils.ftl" as utilities/]
[#macro deliverableList deliverables={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="projectDeliverable"]
  <table class="deliverablesList" id="deliverables">
    <thead>
      <tr class="header">
        <th id="ids">[@s.text name="deliverableList.deliverablesids" /]</th>
        <th id="deliverablesName" >[@s.text name="deliverableList.deliverablesName" /]</th>
        <th id="deliverablesType" >[@s.text name="deliverableList.deliverablesType" /]</th>
        <th id="deliverablesStartDate">[@s.text name="deliverableList.deliverablesStartDate" /]</th>
        <th id="deliverablesEndDate">[@s.text name="deliverableList.deliverablesEndDate" /]</th>
        <th id="deliverablesRF">[@s.text name="deliverableList.missingFields" /]</th>
        <th id="deliverableDelete">[@s.text name="deliverableList.deliverablesRemove" /]</th> 
      </tr>
    </thead>
    <tbody>
    [#if deliverables?has_content]
      [#list deliverables as deliverable]         
        [#local deliverableUrl][@s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url][/#local]
        <tr>
        [#-- ID --]
        <td class="deliverableId">
          <a href="${deliverableUrl}"> P${deliverable.id}</a>
        </td>
          [#-- deliverable Title --]
          <td class="left">
            [#if deliverable.name?has_content]
              <a href="${deliverableUrl}" title="${deliverable.name}">
              [#if deliverable.name?length < 120] ${deliverable.name}</a> [#else] [@utilities.wordCutter string=deliverable.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="${deliverableUrl}">
                [@s.text name="deliverableList.none" /]
              </a>
            [/#if]
          </td>
          [#-- deliverable type --]
          <td class=""> 
            [#if deliverable.deliverableType?has_content]
              ${(deliverable.deliverableType.name)!}
            [/#if]
          </td>
          [#-- start date --]
          <td>
           [#if deliverable.startDate?has_content]${(deliverable.startDate)!""}[#else][@s.text name="deliverableList.none" /][/#if]
          </td>
          [#-- end date --]
          <td>
           [#if deliverable.endDate?has_content]${(deliverable.endDate)!""}[#else][@s.text name="deliverableList.none" /][/#if]
          </td>
          
          [#-- Deliverable required fields --]
          <td class="text-center">
            [#if action.getCenterDeliverableStatus(deliverable.id)??]
              [#if !((action.getCenterDeliverableStatus(deliverable.id)).missingFields)?has_content]
                <span class="icon-20 icon-check" title="Complete"></span>
              [#else]
                <span class="icon-20 icon-uncheck" title=""></span> 
              [/#if]
            [#else]
                <span class="icon-20 icon-uncheck" title=""></span>
            [/#if]
          </td>
          
          [#-- Delete Deliverable--]
          <td class="text-center">
            [#if canEdit && action.centerCanBeDeleted(deliverable.id, deliverable.class.name)!false]
              <a id="removeDeliverable-${deliverable.id}" class="removeDeliverable" href="#" title="">
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