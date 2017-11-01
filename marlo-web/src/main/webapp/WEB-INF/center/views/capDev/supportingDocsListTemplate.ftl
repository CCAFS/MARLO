[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro deliverableList deliverables={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="projectDeliverable"]
  <table class="supportDocsList" id="deliverables">
    <thead>
      <tr class="header">
        <th id="ids">[@s.text name="deliverableList.deliverablesids" /]</th>
        <th id="deliverablesName" >[@s.text name="deliverableList.deliverablesName" /]</th>
        <th id="deliverablesType" >[@s.text name="deliverableList.deliverablesType" /]</th>
        <th id="deliverablesStartDate">[@s.text name="deliverableList.deliverablesStartDate" /]</th>
        <th id="deliverablesEndDate">[@s.text name="deliverableList.deliverablesEndDate" /]</th>
        <th id="deliverableDelete">[@s.text name="deliverableList.deliverablesRemove" /]</th> 
      </tr>
    </thead>
    <tbody>
    [#if deliverables?has_content]
      [#list deliverables as deliverable]         
        [#local deliverableUrl][@s.url namespace=namespace action=defaultAction][@s.param name='capdevID']${capdevID?c}[/@s.param][@s.param name='projectID']${projectID?c}[/@s.param][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url][/#local]
        <tr>
        [#-- ID --]
        <td class="deliverableId">
          <a href="${deliverableUrl}"> S${deliverable.id}</a>
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
            [#else][@s.text name="deliverableList.none" /]
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
          
          
          
          [#-- Delete Deliverable--]
          <td class="text-center">
            [#if editable]
              <a  class="deleteDoc" href="#" data-href="[@s.url action='${centerSession}/deleteSupportingDoc'][@s.param name='capdevID']${capdev.id}[/@s.param] [@s.param name='deliverableID']${deliverable.id?c}[/@s.param] [@s.param name='edit' value="true" /][/@s.url]" data-toggle="modal" data-target="#confirm-delete">
                 <img src="${baseUrl}/global/images/trash.png" title="[@s.text name="capdev.removeCapdev" /]" /> 
              </a>
            [#else]
              <img src="${baseUrl}/global/images/trash_disable.png" title="[@s.text name="capdev.removeCapdev" /]" /> 
            [/#if]
          </td>
        </tr>  
        
      [/#list]
    [/#if]
    </tbody>
  </table>

  <div class="modal fade" id="confirm-delete" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
            
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">Confirm Delete</h4>
                </div>
            
                <div class="modal-body">
                    <p>You are about to delete one track, this procedure is irreversible.</p>
                    <p>Do you want to proceed?</p>
                    <p class="debug-url"></p>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <a class="btn btn-danger btn-ok">Delete</a>
                </div>
            </div>
        </div>
      </div>
[/#macro]