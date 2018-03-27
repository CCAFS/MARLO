[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro capdevList capdevs={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
    <table class="capDevList" id="capdevs">
		<thead >
		  <tr class="header">
		    <th style="width: 3%">ID</th>
		    <th style="width: 21%">Title</th> 
		    <th style="width: 7%">Type</th>
		    <th style="width: 7%">Start date</th>
		    <th style="width: 7%">End date</th>
		    <th style="width: 7%">Research Area</th>
		    <th style="width: 7%">Research Program</th>
		    <th style="width: 7%">Annexes</th>
		    <th style="width: 1%">Remove</th>
		  </tr>
	  	</thead>
	  	<tbody >
		  	[#if capdevs?has_content]
			  	[#list capdevs as i]
				  	[#local capdevUrl][@s.url namespace=namespace action=defaultAction][@s.param name='capdevID']${i.id?c}[/@s.param][@s.param name='projectID']${projectID?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url][/#local]
				  	[#if i.active]
					  <tr>
					    <td><a href="${capdevUrl}">C${i.id}</a></td>
					     
					    <td>
						    [#if i.title?has_content]
						    	<a href="${capdevUrl}">${i.title}</a>
						    [#else]
						    	<a href="${capdevUrl}">Not defined</a>
						    [/#if]
					    </td> 
					    <td>
						    [#if i.capdevType??]
						    	${i.capdevType.name}
						    [#else]
						    	Not defined
						    [/#if]
					    </td>
					     <td>
						    [#if i.startDate??]
						    	${i.startDate}
						    [#else]
						    	Not defined
						    [/#if]
					    </td>
					    
					    <td>
						    [#if i.endDate??]
						    	${i.endDate}
						    [#else]
						    	Not defined
						    [/#if]
					    </td>
					    <td>
						    [#if i.researchArea??] 
						    	${i.researchArea.name}
						    [#else]
						    	Not defined
						    [/#if]
					    </td>
					    <td>
						    [#if i.researchProgram??]
						    	${i.researchProgram.name}
						    [#else]
						    	Not defined
						    [/#if]
					    </td>
					    <td>
					    	<div class="icon">
					    		
				    			<img src="${baseUrl}/center/images/capdev/participants.png" class="[#if i.capdevParticipant?has_content && i.capdevParticipant?size  > 1]capDevIconEnable [#else]capDevIconDisable[/#if]" title="List of participants" />
					    		
						    	<img src="${baseUrl}/center/images/capdev/deliverable.png" class="[#if i.deliverables?has_content] capDevIconEnable [#else]capDevIconDisable[/#if]" title="Supporting documents" />
					    	</div>
				    		
					    </td>
					    <td class="removeCol">
					    	[#if action.centerCanBeDeleted(i.id, i.class.name)!false]
					    	<a id="removeCapdev-${i.id}" class="removeCapdev" href="#"  data-toggle="modal" data-target="#confirm-delete-capdev-${i.id}">
				               <img src="${baseUrl}/global/images/trash.png" title="[@s.text name="capdev.removeCapdev" /]" /> 
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
				            
				            	<img src="${baseUrl}/global/images/trash_disable.png" title="[@s.text name="capdev.removeCapdev" /]" /> 
				            [/#if]
					    </td>
					  </tr>
					  
				  	[/#if]
			  	[/#list]
		  	[/#if]
	  	</tbody>
	</table>
[/#macro]