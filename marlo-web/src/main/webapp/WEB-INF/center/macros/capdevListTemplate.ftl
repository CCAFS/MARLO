[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro capdevList capdevs={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
  <table class="capDevList" id="capdevs">
    <thead >
      <tr class="header">
        <th style="width: 2%">ID</th>
        <th style="width: 21%">Title</th>
        <th style="width: 7%">Program</th>
        <th style="width: 7%">Type</th>
        <th style="width: 7%">Created By</th>
        <th style="width: 7%">Annexes</th>
        <th style="width: 7%">Status</th>
        <th style="width: 1%"></th>
        <th style="width: 1%">Remove</th>
      </tr>
    </thead>
    <tbody >
        [#if capdevs?has_content]
          [#list capdevs as i]
            [#assign submission = action.isSubmitCapDev(i.id) /]
            [#assign isCompleted = (action.isCompleteCapDev(i.id))!false /]
            [#local capdevUrl][@s.url namespace=namespace action=defaultAction][@s.param name='capdevID']${i.id?c}[/@s.param][@s.param name='projectID']${projectID?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url][/#local]
            [#if i.active]
            <tr>
              <td class="text-center">
                <a href="${capdevUrl}">C${i.id}</a></td>
              <td>
                [#-- Tags --]
                [#if i.category == 1]<span class="label label-success">[@s.text name="Individual" /]</span>[/#if]
                [#if i.category == 2]<span class="label label-primary">[@s.text name="Grupal" /]</span>[/#if]
                [#-- Title --]
                <a href="${capdevUrl}">
                  [#if i.title?has_content]${i.title}[#else]Not defined[/#if]
                </a>
              </td>
              [#-- Program --]
              <td class="text-center">
                [#if (i.researchProgram??)!false]
                  <span class="programTag" style="border-color:${(i.researchProgram.color)!'#fff'}">${(i.researchProgram.composedName)!}</span>
                [#else]
                  Not defined
                [/#if]
              </td>
              <td>
                [#if i.capdevType??]${i.capdevType.name}[#else]Not defined[/#if]
              </td> 
              <td>
                [#if i.createdBy??]${i.createdBy.composedCompleteName}[#else]Not defined[/#if]
              </td>
              <td class="text-center">
                [#if ((i.capdevParticipants?size > 1)!false) || ((i.deliverables?has_content)!false)]
                  [#-- Number of Participants --]
                  [#if (i.capdevParticipants?size > 1)!false]
                    <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-participants-${i_index}">
                      <span class="icon-20 outcomesCont"></span> <strong>${i.capdevParticipants?size}</strong>
                    </button>
                  [/#if]
                  [#-- Number of Deliverables --]
                  [#if (i.deliverables?has_content)!false]
                    <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-deliverables-${i_index}">
                      <span class="icon-20 deliverable"></span> <strong>${i.deliverables?size}</strong>
                    </button>
                  [/#if]
                [#else]
                  <p>No Annexes</p>
                [/#if]
              </td>
              <td class="text-center">
                [#if !submission]
                  <span>Not Submitted</span>
                [#else]
                  <strong title="Submitted">Submitted</strong>
                [/#if]  
              </td>
              [#-- Missing fields --]
              <td>
                [#if isCompleted]
                  <span class="icon-20 icon-check" title="Complete"></span>
                [#else]
                  <span class="icon-20 icon-uncheck" title="Required fields still incompleted"></span> 
                [/#if]
              </td>
              <td class="removeCol">
                [#if action.centerCanBeDeleted(i.id, i.class.name)!false]
                  <a id="removeCapdev-${i.id}" class="removeCapdev" href="#"  data-toggle="modal" data-target="#confirm-delete-capdev-${i.id}">
                    <img src="${baseUrlCdn}/global/images/trash.png" title="[@s.text name="capdev.removeCapdev" /]" /> 
                  </a>
                  <div class="modal fade" id="confirm-delete-capdev-${i.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                      <div class="modal-content">
                        <div class="modal-header">
                          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                          <h4 class="modal-title" id="myModalLabel">Confirm Delete</h4>
                        </div>
                        <div class="modal-body">
                          <p>You are about to delete the track <b>C${i.id} - [#if i.title?has_content]${i.title}[#else]Not defined[/#if]</b>, this procedure is irreversible.</p>
                          <p>Do you want to proceed?</p>
                        </div>
                        <div class="modal-footer">
                          <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                          <a class="btn btn-danger btn-ok modal-button-delete" href="[@s.url namespace='/capdev' action='${centerSession}/deleteCapdev'][@s.param name='projectID']${projectID}[/@s.param][@s.param name='capdevID']${i.id}[/@s.param] [/@s.url]" >Delete</a>
                        </div>
                      </div>
                    </div>
                  </div>
                [#else]
                  <img src="${baseUrlCdn}/global/images/trash_disable.png" title="[@s.text name="capdev.removeCapdev" /]" /> 
                [/#if]
              </td>
            </tr>
            [/#if]
          [/#list]
        [/#if]
    </tbody>
  </table>
[/#macro]